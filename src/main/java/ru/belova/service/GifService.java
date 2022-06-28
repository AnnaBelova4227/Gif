package ru.belova.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.belova.client_interface.GifClient;

import java.util.Map;

/**
 * Сервис для работы с Giphy.com
 */

@Service
public class GifService {

    private GifClient  gifClient;
    @Value("${gif.api.key}")
    private String key;

    @Autowired
    public GifService(GifClient gifClient){
        this.gifClient = gifClient;
    }

    /**
     * Ответ от Giphy.com просто перекидывается клиенту
     * в виде ResponseEntity
     * лишь с небольшой модификацией- добавляется compareResult
     * для удобства визуальной проверки результата.
     *
     * @param tag
     * @return
     */

    public ResponseEntity<Map> getGif(String tag){
        ResponseEntity<Map> result = gifClient.getRandomGif(this.key, tag);
        result.getBody().put("compareResult", tag);
        return result;
    }
}
