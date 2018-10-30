package com.lafferty.jai.trackie;

import java.io.Serializable;

public class Weight implements Serializable {

    private float _weight;
    private long _date;

    public Weight(long date, float weight) {
        this._weight = weight;
        this._date = date;
    }

    public float get_weight() {
        return _weight;
    }

    public long get_date() {
        return _date;
    }

}
