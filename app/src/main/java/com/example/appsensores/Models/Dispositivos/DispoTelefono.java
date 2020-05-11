package com.example.appsensores.Models.Dispositivos;

import android.content.Context;

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Models.Sensor;

public class DispoTelefono extends BaseDispositivo {
    //Orientation Service
    public float Orientation_x;
    public float Orientation_y;
    public float Orientation_z;
    public float Acelereation_x;
    public float Acelereation_y;
    public float Acelereation_z;
    //public float Humidity;
    //public float Temperature;
    public float Proximidad;
    //public float AmbientLight;
    //public float Voltaje;

    public double Lat;
    public double Lng;
    public DispoTelefono(Context context) {
        super(context);
        sensors.add(new Sensor(SensorTypes.SENSOR_TEMPERATURA,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_TEMPERATURA],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_HUMEDAD,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_HUMEDAD],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_LUZ,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_LUZ],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_PROXIMITY,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_PROXIMITY],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_VOLTAJE,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_VOLTAJE],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_ORIENTATION_X,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_ORIENTATION_X],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_ORIENTATION_Y,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_ORIENTATION_Y],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_ORIENTATION_Z,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_ORIENTATION_Z],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_ACCELERATION_X,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_ACCELERATION_X],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_ACCELERATION_Y,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_ACCELERATION_Y],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_ACCELERATION_Z,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_ACCELERATION_Z],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_LAT,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_LAT],0.0));
        sensors.add(new Sensor(SensorTypes.SENSOR_LNG,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_LNG],0.0));
        //Al final siempre añado el valor donde se mapeara el mensaje de la alerta
        sensors.add(new Sensor(SensorTypes.SENSOR_MESAGE,SensorTypes.getSensorAmbientList(context)[SensorTypes.SENSOR_MESAGE],0.0));
    }
}
