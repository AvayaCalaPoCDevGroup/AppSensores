package com.example.appsensores.Models;

public class Rule {
    public int id;
    public int DispositivoId;
    public int RuleId;
    public int SensorId;
    public float Value1;
    public float Value2;
    public boolean IsEnabled;
    public long LastDate;

    public String emailParam;
    public String messageParam;
    public String temperatureParam;
    public String humidityParam;
    public String luxParam;
    public String uvParam;
    public String batteryParam;
}
