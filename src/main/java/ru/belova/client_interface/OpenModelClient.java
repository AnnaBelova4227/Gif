package ru.belova.client_interface;

import ru.belova.model.Model;

public interface OpenModelClient {

    Model getLastRates(String id);

    Model getHistoryRates (String data, String id);
}
