package com.example.appsensores.Clases.Enums;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.appsensores.Clases.Utils;
import com.example.appsensores.R;
import com.journeyapps.barcodescanner.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SensorTypes {

    public static final int SENSOR_TEMPERATURA = 0;
    public static final int SENSOR_HUMEDAD = 1;
    public static final int SENSOR_LUZ = 2;
    public static final int SENSOR_UV = 3;
    public static final int SENSOR_VOLTAJE = 4;
    public static final int SENSOR_PROXIMITY = 5;
    public static final int SENSOR_ORIENTATION_X = 6;
    public static final int SENSOR_ORIENTATION_Y = 7;
    public static final int SENSOR_ORIENTATION_Z = 8;
    public static final int SENSOR_ACCELERATION_X = 9;
    public static final int SENSOR_ACCELERATION_Y = 10;
    public static final int SENSOR_ACCELERATION_Z = 11;
    public static final int SENSOR_LAT = 12;
    public static final int SENSOR_LNG = 13;
    public static final int SENSOR_SW0 = 14;
    public static final int SENSOR_SW1 = 15;
    public static final int SENSOR_HEART_RATE = 16;
    public static final int SENSOR_MESAGE = 17; //Este es mas bien un comodin para aqui guardar el mensaje de la alerta.

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
                res.getString(R.string.measure_voltaje),
                res.getString(R.string.measure_proximidad),
                res.getString(R.string.measure_orientation_x),
                res.getString(R.string.measure_orientation_y),
                res.getString(R.string.measure_orientation_z),
                res.getString(R.string.measure_aceleration_x),
                res.getString(R.string.measure_aceleration_y),
                res.getString(R.string.measure_aceleration_z),
                res.getString(R.string.measure_lat),
                res.getString(R.string.measure_lng),
                "SW0",
                "SW1",
                res.getString(R.string.measure_hrmrate),
                res.getString(R.string.param_msg)
        };

        return resp;
    }

    public static String[] getRuleTypes(Context context){
        Resources res = context.getResources();
        String[] resp = {
                res.getString(R.string.rule_type_smaller),
                res.getString(R.string.rule_type_greater),
                res.getString(R.string.rule_type_between)
        };

        return resp;
    }

}
