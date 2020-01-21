package com.example.appsensores.Clases.Enums;

import android.content.Context;
import android.content.res.Resources;

import com.example.appsensores.R;

public class SensorTypes {

    public static final int TEMPERATURA = 0;
    public static final int HUMEDAD = 1;
    public static final int LUZ = 2;
    public static final int UV = 3;

    public static final int MENOR = 0;
    public static final int MAYOR = 1;
    public static final int ENTRE = 2;


    public static String[] getSensorAmbientList(Context context){
        Resources res = context.getResources();
        String[] resp = {
                res.getString(R.string.measure_temperatura),
                res.getString(R.string.measure_humedad),
                res.getString(R.string.measure_lux),
                res.getString(R.string.measure_uv),
        };

        return resp;
    }

    public static String[] getRuleTypes(Context context){
        Resources res = context.getResources();
        String[] resp = {
                "Menor que",
                "Mayor que",
                "Entre"
        };

        return resp;
    }
}
