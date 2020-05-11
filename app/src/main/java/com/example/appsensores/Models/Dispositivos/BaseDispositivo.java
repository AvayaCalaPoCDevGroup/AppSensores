package com.example.appsensores.Models.Dispositivos;

import android.content.Context;

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Models.Sensor;

import java.util.ArrayList;

public class BaseDispositivo {
    public Context context;
    public BaseDispositivo(Context context){
        this.context = context;
    }
    public int id;
    public String Nombre;
    public String MacAddress;
    public String Token;
    public int TipoDispositivo;

    public float  Temperature;
    public float  Humidity;
    public int    AmbientLight;
    public int    UV_Index;
    public float  Battery;

    public ArrayList<Sensor> sensors = new ArrayList<>();
    public Sensor GetSensorById(int idSensor){
        Sensor resp = null;
        for (Sensor sensor : sensors) {
            if(sensor.id == idSensor){
                resp = sensor;
                break;
            }
        }
        return resp;
    }
    public int GetSensorIndexById(int idSensor){
        int index = 0;
        for (Sensor sensor : sensors) {
            if(sensor.id == idSensor){
                break;
            }
            index++;
        }
        return index;
    }
}
