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

    public static final int TEMPERATURA = 0;
    public static final int HUMEDAD = 1;
    public static final int LUZ = 2;
    public static final int UV = 3;
    public static final int VOLTAJE = 4;

    public static final int MENOR = 0;
    public static final int MAYOR = 1;
    public static final int ENTRE = 2;



    public static ArrayList<String> getEndPointParameters(Context context){
        ArrayList<String> params = new ArrayList<>();

        SharedPreferences shared = context.getSharedPreferences(Utils.AVAYA_SHARED_PREFERENCES,0);
        String endpoint_json = shared.getString(Utils.AVAYA_SHARED_JSON,"{}");

        try {
            JSONObject jsonObject = new JSONObject(endpoint_json);

            if(jsonObject.has("title") && jsonObject.has("type") && jsonObject.has("properties")){
                //el json es del formato en el que se extraen de engagement designer
                jsonObject = jsonObject.getJSONObject("properties");
            }

            Iterator<String> iterator = jsonObject.keys();
            while(iterator.hasNext()){
                params.add(iterator.next());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params;
    }

    public static String[] getSensorAmbientList(Context context){
        Resources res = context.getResources();
        String[] resp = {
                res.getString(R.string.measure_temperatura),
                res.getString(R.string.measure_humedad),
                res.getString(R.string.measure_lux),
                res.getString(R.string.measure_uv),
                res.getString(R.string.measure_voltaje),
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
