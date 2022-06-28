package ru.belova.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.belova.client_interface.OpenModelClient;
import ru.belova.model.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


/**
 * Сервис для работы с openexchangerates.org
 */
@Service
public class OpenExchangeRatesService {

    private Model lastRates;
    private Model realRates;

    private OpenModelClient openModelClient;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    @Value("${openexchangrates.app.id}")
    private String id;
    @Value("${openexchangrates.base}")
    private String base;

    @Autowired

    public OpenExchangeRatesService(
            OpenModelClient openModelClient,
            @Qualifier("date_bean") SimpleDateFormat dateFormat,
            @Qualifier("time_bean") SimpleDateFormat timeFormat
    ) {
        this.openModelClient = openModelClient;
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
    }

    /**
     * Возвращает список доступных для проверки валют
     *
     * @return
     */
    public List<String> getCharCodes(){
        List<String> result = null;
        if (this.realRates.getCurs() != null){
            result = new ArrayList<>(this.realRates.getCurs().keySet());
        }
        return result;
    }

    /**
     * Проверяет, обновляет курсы,
     * возвращает результат сравнения коэфициентов.
     * Если курсов или коэфициентов нет, то возвращает -101
     * @param charCode
     * @return
     */
    public int getKeyForTag(String charCode){
        this.refreshRates();
        Double prevCoefficient = this.getCoefficient(this.lastRates, charCode);
        Double currentCoefficient = this.getCoefficient(this.realRates, charCode);
        return prevCoefficient != null && currentCoefficient != null
                ? Double.compare(currentCoefficient, prevCoefficient)
                : -101;
    }

    /**
     * Проверка, обновление курсов
     */
    public void refreshRates(){
        long currentTime = System.currentTimeMillis();
        this.refreshCurrentRates(currentTime);
        this.refreshPrevRates(currentTime);
    }

    /**
     * обновление текущих курсов. Проверяется время с точностью
     * до часа, т.к. обновление на openexchangerates.org происходит
     * каждый час.
     * @param time
     */
    private void refreshCurrentRates(long time){
        if (
                this.realRates == null ||
                        !timeFormat.format(Long.valueOf(this.realRates.getTimestamp()) * 1000)
                                .equals(timeFormat.format(time))
        ){
            this.realRates = openModelClient.getLastRates(this.id);
        }
    }

    /**
     * Обновление вчерашних курсов.
     * Проверяется время с точностью до дня.
     * Так же, что бы при каждом запросе не приходилось обращаться к внешнему сервису
     * для обновления курсов- происходит проверка даты текущего prevRates
     * с приведённым к строковому виду YYYY-MM-DD текущей и меньшей на день от текущей дат,
     * т.к. при запросе к к openexchangerates.org//historical/* с указанием
     * вчерашней даты могут вернуться
     * курсы с датой, равной текущей.
     *
     * @param time
     */
    private void refreshPrevRates(long time){
        Calendar prevCalendar = Calendar.getInstance();
        prevCalendar.setTimeInMillis(time);
        String currentDate = dateFormat.format(prevCalendar.getTime());
        prevCalendar.add(Calendar.DAY_OF_YEAR, -1);
        String newPrevDate = dateFormat.format(prevCalendar.getTime());
        if (
                this.lastRates == null ||
                        (!dateFormat.format(Long.valueOf(this.lastRates.getTimestamp()) * 1000)
                                .equals(newPrevDate) &&
                                !dateFormat.format(Long.valueOf(this.lastRates.getTimestamp()) * 1000)
                                .equals(currentDate)
                        )
        ){
            this.lastRates = openModelClient.getHistoryRates(newPrevDate, id);
        }
    }

    /**
     * Формула для подсчётка коэфициента по отношению к установленной в этом приложении валютной базе.
     * (Default_Base / Our_Base) * Target
     * Если на входе оказался несуществующий charCode- то вернёт null
     * Так же при подсчёте результата происходит округление курса до четырёх знаков после запятой.
     *
     * @param rates
     * @param charCode
     */
    private Double getCoefficient(Model rates, String charCode){
        Double result = null;
        Double targetRate = null;
        Double appBaseRate = null;
        Double defaultBaseRate = null;
        Map<String, Double> map = null;
        if (rates != null && rates.getCurs() != null){
            map = rates.getCurs();
            targetRate = map.get(charCode);
            appBaseRate = map.get(this.base);
            defaultBaseRate = map.get(rates.getBase());
        }
        if (targetRate != null && appBaseRate != null && defaultBaseRate != null){
            result = new BigDecimal(
                    (defaultBaseRate / appBaseRate) * targetRate
            )
                    .setScale(4, RoundingMode.UP)
                    .doubleValue();
        }
        return result;
    }
}
