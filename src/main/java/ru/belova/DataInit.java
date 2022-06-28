package ru.belova;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.belova.service.OpenExchangeRatesService;

import javax.annotation.PostConstruct;

@Component
public class DataInit {

    private OpenExchangeRatesService exchangeRatesService;

    @Autowired
    public DataInit(OpenExchangeRatesService exchangeRatesService){
        this.exchangeRatesService = exchangeRatesService;
    }

    @PostConstruct
    public void firstInit(){
        exchangeRatesService.refreshRates();
    }
}
