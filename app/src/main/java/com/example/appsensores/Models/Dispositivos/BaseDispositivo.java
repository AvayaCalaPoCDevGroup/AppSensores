package com.example.appsensores.Models.Dispositivos;

public class BaseDispositivo {
    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public int getTipoDispositivo() {
        return TipoDispositivo;
    }

    public void setTipoDispositivo(int tipoDispositivo) {
        TipoDispositivo = tipoDispositivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    private int id;
    private String Nombre;
    private String MacAddress;
    private String Token;
    private int TipoDispositivo;
}
