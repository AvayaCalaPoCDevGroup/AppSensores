package com.example.appsensores.Models;

public class Parametros {
    /***
     * Clase Modelo para los Parametros requeridos por el endpoint de Avaya
     */
    public Parametros(){}

    /***
     * Constructor con los campos necesarios para el request test de AVAYA
     * @param mail Correo Electronico
     * @param param1 Nombre del boton
     * @param param2 Param2
     */
    public Parametros(String mail, String param1, String param2){
        correoElectronico = mail;
        Param1 = param1;
        Param2 = param2;
    }

    private String correoElectronico;
    private String Param1;
    private String Param2;
    private String Param3;
    private String Param4;
    private String Param5;
    private String Param6;
    private String Param7;
    private String Param8;
    private String Param9;
    private String Param10;

    public void setCorreoElectronico(String correoElectronico) {        this.correoElectronico = correoElectronico;    }
    public void setParam1(String param1) {        Param1 = param1;    }
    public void setParam2(String param2) {        Param2 = param2;    }
    public void setParam3(String param3) {        Param3 = param3;    }
    public void setParam4(String param4) {        Param4 = param4;    }
    public void setParam5(String param5) {        Param5 = param5;    }
    public void setParam6(String param6) {        Param6 = param6;    }
    public void setParam7(String param7) {        Param7 = param7;    }
    public void setParam8(String param8) {        Param8 = param8;    }
    public void setParam9(String param9) {        Param9 = param9;    }
    public void setParam10(String param10) {        Param10 = param10;    }

    public String getCorreoElectronico() {        return correoElectronico;    }
    public String getParam1() {        return Param1;    }
    public String getParam2() {        return Param2;    }
    public String getParam3() {        return Param3;    }
    public String getParam4() {        return Param4;    }
    public String getParam5() {        return Param5;    }
    public String getParam6() {        return Param6;    }
    public String getParam7() {        return Param7;    }
    public String getParam8() {        return Param8;    }
    public String getParam9() {        return Param9;    }
    public String getParam10() {        return Param10;    }
}

