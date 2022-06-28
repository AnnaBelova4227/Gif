package ru.belova.client_interface;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign клиент для получения рандомной гифки от giphy.com
 */

@org.springframework.cloud.openfeign.FeignClient(name = "gifClient", url = "$gif.url.general")
public interface FeignGifClient extends GifClient{

    @Override
    ResponseEntity<Map> getRandomGif(
            @RequestParam("key") String key,
            @RequestParam("tag") String tag
    );
}
