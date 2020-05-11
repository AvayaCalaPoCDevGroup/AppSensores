package com.example.appsensores.Models;

public class Sensor {
    public int id;
    public String name;
    public Double value;
    public Sensor(int id, String name, Double value){
        this.id = id;
        this.name = name;
        this.value = value;
    }
}
