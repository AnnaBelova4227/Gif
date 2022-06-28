package ru.belova.client_interface;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.belova.model.Model;

@org.springframework.cloud.openfeign.FeignClient(name = "OERClient", url = "${openmodelrates.url.general}")
public interface FeignOpenModelClient extends OpenModelClient {

    @Override
    @GetMapping(name = "last.json")
    Model getLastRates(
            @RequestParam("id") String id
    );

    @Override
    @GetMapping (name = "history/{date}.json")
    Model getHistoryRates(
            @PathVariable String data,
            @RequestParam("id") String id
    );
}
