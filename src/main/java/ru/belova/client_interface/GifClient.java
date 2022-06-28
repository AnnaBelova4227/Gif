package ru.belova.client_interface;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface GifClient {

    ResponseEntity<Map> getRandomGif(String key, String tag);
}
