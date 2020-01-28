package com.example.appsensores.Models;

import android.location.Location;

public class LocationTago extends ValuesTago {

    public LocationTago(String variable, String value) {
        super(variable, value);
    }

    public LocationValues location;

    public static class LocationValues {
        public Double lat;
        public Double lng;
    }

}

