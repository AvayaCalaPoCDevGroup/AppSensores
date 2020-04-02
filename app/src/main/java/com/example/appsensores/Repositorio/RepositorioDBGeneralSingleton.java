package com.example.appsensores.Repositorio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.appsensores.DBHelpers.DBGeneralHandler;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Rule;

import java.util.ArrayList;

public class RepositorioDBGeneralSingleton {

    private static RepositorioDBGeneralSingleton ourInstance = null;
    private Context mContext;
    private DBGeneralHandler dbGeneralHandler;
    private SQLiteDatabase db;

    public static RepositorioDBGeneralSingleton getInstance(Context context) {
        if(ourInstance == null)
            ourInstance = new RepositorioDBGeneralSingleton(context);
        return ourInstance;
    }

    private RepositorioDBGeneralSingleton(Context context) {
        mContext = context;
        dbGeneralHandler = new DBGeneralHandler(mContext);
    }

    /***
     * Agregar un nuevo dispositivo a la tabla de dispositivos
     * @param device Dispositivo a agregar
     */
    public synchronized void addDevice(BaseDispositivo device){
        db = dbGeneralHandler.getWritableDatabase();

        ContentValues dispoValues = new ContentValues();
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_NOMBRE,device.Nombre);
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_MACADDRESS,device.MacAddress.toUpperCase());
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_TOKEN,device.Token);
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_TIPODISPOSITIVO,device.TipoDispositivo);

        db.insert(DBGeneralHandler.TABLE_DISPOSITIVOS,null, dispoValues);
        db.close();
    }

    /***
     * Update valores de un dispositivo
     * @param device Dispositivo a agregar, el cual debe contener un Id existente en la base de datos
     */
    public synchronized void updateDevice(BaseDispositivo device){
        db = dbGeneralHandler.getWritableDatabase();

        ContentValues dispoValues = new ContentValues();
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_NOMBRE,device.Nombre);
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_MACADDRESS,device.MacAddress.toUpperCase());
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_TOKEN,device.Token);
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_TIPODISPOSITIVO,device.TipoDispositivo);

        db.update(DBGeneralHandler.TABLE_DISPOSITIVOS, dispoValues, DBGeneralHandler.KEY_DISPOSITIVOS_ID + "=?", new String[] {""+device.id});
        db.close();
    }

    /***
     * Obtiene todos los dispositivos que estan en la tabla Dispositivos
     * @return
     */
    public synchronized ArrayList<BaseDispositivo> getDevices(){
        ArrayList<BaseDispositivo> dispositivos = new ArrayList<BaseDispositivo>();

        db = dbGeneralHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DBGeneralHandler.TABLE_DISPOSITIVOS + " ORDER BY " + DBGeneralHandler.KEY_DISPOSITIVOS_ID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                BaseDispositivo dispositivo = new BaseDispositivo();

                dispositivo.id = Integer.parseInt(cursor.getString(0));
                dispositivo.Nombre = cursor.getString(1);
                dispositivo.MacAddress = cursor.getString(2);
                dispositivo.Token = cursor.getString(3);
                dispositivo.TipoDispositivo = cursor.getInt(4);

                dispositivos.add(dispositivo);
            } while (cursor.moveToNext());
        }
        db.close();

        return dispositivos;
    }

    /***
     * Metodo para borrar un dispositivo
     * @param id ID del dispositivo a borrar
     * @return true si es borrado, false en otro caso
     */
    public synchronized boolean deleteDevice(int id){
        boolean resp = false;

        db = dbGeneralHandler.getWritableDatabase();
        try{
            //Log.d("DELETEFON","DELETE FROM " + DBTicketsHandler.TABLE_CELULARES + " WHERE "+ DBTicketsHandler.KEY_NUMERO_CEL + " = '" + phoneNumber + "'");
            db.execSQL("DELETE FROM " + dbGeneralHandler.TABLE_DISPOSITIVOS + " WHERE "+ dbGeneralHandler.KEY_DISPOSITIVOS_ID + " = " + id );
            resp = true;
        } catch (Exception e){
            Log.e("deleteDevice","Error al borrar dispositivo " + e.toString());
        }

        db.close();

        return resp;
    }

    /***
     * Metodo para recuperar un dispositivo pasando como parametro su ID
     * @param idDispositivo
     * @return el objeto {@link BaseDispositivo}, null si ni se encuentra en la DB
     */
    public BaseDispositivo getDeviceById(int idDispositivo) {
        BaseDispositivo dispositivo = null;

        db = dbGeneralHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DBGeneralHandler.TABLE_DISPOSITIVOS + "  WHERE " + dbGeneralHandler.KEY_DISPOSITIVOS_ID + " = " + idDispositivo + " LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                dispositivo = new BaseDispositivo();
                dispositivo.id = Integer.parseInt(cursor.getString(0));
                dispositivo.Nombre = cursor.getString(1);
                dispositivo.MacAddress = cursor.getString(2);
                dispositivo.Token = cursor.getString(3);
                dispositivo.TipoDispositivo = cursor.getInt(4);
            } while (cursor.moveToNext());
        }
        db.close();
        return dispositivo;
    }

    /***
     * Metodo para obtener un device a traves de su MAC ADDRESS
     * @param mac_address
     * @return el oobjeto {@link BaseDispositivo} , null si no se encuentra en la DB
     */
    public BaseDispositivo getDeviceByMAC(String mac_address) {
        BaseDispositivo dispositivo = null;

        db = dbGeneralHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DBGeneralHandler.TABLE_DISPOSITIVOS + "  WHERE " + dbGeneralHandler.KEY_DISPOSITIVOS_MACADDRESS + " = '" + mac_address + "' LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                dispositivo = new BaseDispositivo();
                dispositivo.id = Integer.parseInt(cursor.getString(0));
                dispositivo.Nombre = cursor.getString(1);
                dispositivo.MacAddress = cursor.getString(2);
                dispositivo.Token = cursor.getString(3);
                dispositivo.TipoDispositivo = cursor.getInt(4);
            } while (cursor.moveToNext());
        }
        db.close();
        return dispositivo;
    }

    /***
     * Metodo para obtener un device a traves de su TOKEN
     * @param token
     * @return el oobjeto {@link BaseDispositivo} , null si no se encuentra en la DB
     */
    public BaseDispositivo getDeviceToken(String token) {
        BaseDispositivo dispositivo = null;

        db = dbGeneralHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DBGeneralHandler.TABLE_DISPOSITIVOS + "  WHERE " + dbGeneralHandler.KEY_DISPOSITIVOS_TOKEN + " = '" + token + "' LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                dispositivo = new BaseDispositivo();
                dispositivo.id = Integer.parseInt(cursor.getString(0));
                dispositivo.Nombre = cursor.getString(1);
                dispositivo.MacAddress = cursor.getString(2);
                dispositivo.Token = cursor.getString(3);
                dispositivo.TipoDispositivo = cursor.getInt(4);
            } while (cursor.moveToNext());
        }
        db.close();
        return dispositivo;
    }

    public synchronized ArrayList<Rule> getAllRules(){
        ArrayList<Rule> rules = new ArrayList<Rule>();

        db = dbGeneralHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DBGeneralHandler.TABLE_RULES + " ORDER BY " + DBGeneralHandler.KEY_RULES_ID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Rule rule = new Rule();

                rule.id = Integer.parseInt(cursor.getString(0));
                rule.DispositivoId = cursor.getInt(1);
                rule.RuleId = cursor.getInt(2);
                rule.SensorId = cursor.getInt(3);
                rule.Value1 = cursor.getFloat(4);
                rule.Value2 = cursor.getFloat(5);
                rule.IsEnabled = cursor.getInt(6) != 0;
                rule.LastDate = Long.parseLong(cursor.getString(7));

                rules.add(rule);
            } while (cursor.moveToNext());
        }
        db.close();

        return rules;
    }

    public synchronized ArrayList<Rule> getRulesByDispositivo(int DispoId){
        ArrayList<Rule> rules = new ArrayList<Rule>();

        db = dbGeneralHandler.getWritableDatabase();
        String query = "SELECT * FROM " + DBGeneralHandler.TABLE_RULES + " WHERE " + DBGeneralHandler.KEY_RULES_DISPOID + " = " + DispoId + " ORDER BY " + DBGeneralHandler.KEY_RULES_ID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Rule rule = new Rule();

                rule.id = Integer.parseInt(cursor.getString(0));
                rule.DispositivoId = cursor.getInt(1);
                rule.RuleId = cursor.getInt(2);
                rule.SensorId = cursor.getInt(3);
                rule.Value1 = cursor.getFloat(4);
                rule.Value2 = cursor.getFloat(5);
                rule.IsEnabled = cursor.getInt(6) != 0;
                rule.LastDate = Long.parseLong(cursor.getString(7));

                rules.add(rule);
            } while (cursor.moveToNext());
        }
        db.close();

        return rules;
    }

    public synchronized void addRule(Rule rule){
        db = dbGeneralHandler.getWritableDatabase();

        ContentValues dispoValues = new ContentValues();
        dispoValues.put(DBGeneralHandler.KEY_RULES_DISPOID,rule.DispositivoId);
        dispoValues.put(DBGeneralHandler.KEY_RULES_SENSORID,rule.SensorId);
        dispoValues.put(DBGeneralHandler.KEY_RULES_RULEID,rule.RuleId);
        dispoValues.put(DBGeneralHandler.KEY_RULES_VALUE1,rule.Value1);
        dispoValues.put(DBGeneralHandler.KEY_RULES_VALUE2,rule.Value2);
        dispoValues.put(DBGeneralHandler.KEY_RULES_ISENABLED,rule.IsEnabled);
        dispoValues.put(DBGeneralHandler.KEY_RULES_LASTDATE,""+rule.LastDate);

        db.insert(DBGeneralHandler.TABLE_RULES,null, dispoValues);
        db.close();
    }

    public synchronized boolean deleteRule(int id){
        boolean resp = false;

        db = dbGeneralHandler.getWritableDatabase();
        try{
            //Log.d("DELETEFON","DELETE FROM " + DBTicketsHandler.TABLE_CELULARES + " WHERE "+ DBTicketsHandler.KEY_NUMERO_CEL + " = '" + phoneNumber + "'");
            db.execSQL("DELETE FROM " + dbGeneralHandler.TABLE_RULES + " WHERE "+ dbGeneralHandler.KEY_RULES_ID + " = " + id );
            resp = true;
        } catch (Exception e){
            Log.e("deleteRule","Error al borrar regla " + e.toString());
        }

        db.close();

        return resp;
    }

    public synchronized void updateRule(Rule rule){
        db = dbGeneralHandler.getWritableDatabase();

        ContentValues dispoValues = new ContentValues();
        dispoValues.put(DBGeneralHandler.KEY_RULES_DISPOID,rule.DispositivoId);
        dispoValues.put(DBGeneralHandler.KEY_RULES_RULEID,rule.RuleId);
        dispoValues.put(DBGeneralHandler.KEY_RULES_SENSORID,rule.SensorId);
        dispoValues.put(DBGeneralHandler.KEY_RULES_VALUE1,rule.Value1);
        dispoValues.put(DBGeneralHandler.KEY_RULES_VALUE2,rule.Value2);
        dispoValues.put(DBGeneralHandler.KEY_RULES_ISENABLED, rule.IsEnabled);

        db.update(DBGeneralHandler.TABLE_RULES, dispoValues, DBGeneralHandler.KEY_RULES_ID + "=?", new String[] {""+rule.id});
        db.close();
    }

    public synchronized void updateLastUpdateRule(long now, int idDispo){
        db = dbGeneralHandler.getWritableDatabase();

        ContentValues dispoValues = new ContentValues();

        dispoValues.put(DBGeneralHandler.KEY_RULES_LASTDATE, ""+now);

        db.update(DBGeneralHandler.TABLE_RULES, dispoValues, DBGeneralHandler.KEY_RULES_ID + "=?", new String[] {""+idDispo});
        db.close();
    }
}

