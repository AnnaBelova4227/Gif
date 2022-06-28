package ru.belova.model;

import java.util.Map;

public class Model {
    private String refusal;
    private Integer timestamp;
    private String base;
    private Map<String, Double> curs;

    public Model() {
    }

    public String getRefusal() {
        return refusal;
    }

    public void setRefusal(String refusal) {
        this.refusal = refusal;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, Double> getCurs() {
        return curs;
    }

    public void setCurs(Map<String, Double> curs) {
        this.curs = curs;
    }
}
