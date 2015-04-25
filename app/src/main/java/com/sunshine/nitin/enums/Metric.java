package com.sunshine.nitin.enums;

/**
 * Created by nitin on 4/20/15.
 */
public enum Metric {
    CELCIUS("C"),
    FARENHEIT("F");

    private String code;

    private Metric(String code) {
        this.code = code;
    }

    public static Metric getByCode(String code) throws Exception {
        for(Metric m : Metric.values()) {
            if(m.code.equals(code))
                return m;
        }

        throw new Exception("Invalid units");
    }

    public String getCode() {
        return this.code;
    }
}
