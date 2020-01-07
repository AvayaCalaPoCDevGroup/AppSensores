package com.example.appsensores.Models;

/***
 * Clase para encapsular las variables que necesita la api de tago.io example:
 * [
 *   {
 *     "variable": "temperature",
 *     "value": 17
 *   },
 *   {
 *   	"variable": "nombre",
 *     "value": "SENSORPUCK"
 *   }
 * ]
 */
public class ValuesTago {

    public ValuesTago(String variable, String value){
        this.variable = variable;
        this.value = value;
    }

    public String variable;
    public String value;
}
