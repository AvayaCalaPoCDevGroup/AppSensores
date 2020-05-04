package com.example.appsensores.Models.Dispositivos;

/***
 * Clase modelo para los sensores de Silicon Labs Puck
 */
public class DispoSensorPuck extends BaseDispositivo {
    /* Sensor data */
    public int    MeasurementMode;
    public int    Sequence;
    /*public float  Humidity;
    public float  Temperature;
    public int    AmbientLight;
    public int    UV_Index;
    public float  Battery;*/
    public int    HRM_State;
    public int    HRM_Rate;
    public int[]  HRM_Sample;
    public int    HRM_PrevSample;

    /* Statistics */
    public int    PrevSequence;
    public int    RecvCount;
    public int    PrevCount;
    public int    UniqueCount;
    public int    LostAdv;
    public int    LostCount;
    public int    IdleCount;
}
