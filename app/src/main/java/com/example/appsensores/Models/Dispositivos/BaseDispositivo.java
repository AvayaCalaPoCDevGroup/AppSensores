package com.example.appsensores.Models.Dispositivos;

public class BaseDispositivo {
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
}
