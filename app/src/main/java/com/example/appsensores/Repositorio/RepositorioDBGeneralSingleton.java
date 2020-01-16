package com.example.appsensores.Repositorio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.appsensores.DBHelpers.DBGeneralHandler;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;

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
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_NOMBRE,device.getNombre());
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_MACADDRESS,device.getMacAddress().toUpperCase());
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_TOKEN,device.getToken());
        dispoValues.put(DBGeneralHandler.KEY_DISPOSITIVOS_TIPODISPOSITIVO,device.getTipoDispositivo());

        db.insert(DBGeneralHandler.TABLE_DISPOSITIVOS,null, dispoValues);
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

                dispositivo.setId(Integer.parseInt(cursor.getString(0)));
                dispositivo.setNombre(cursor.getString(1));
                dispositivo.setMacAddress(cursor.getString(2));
                dispositivo.setToken(cursor.getString(3));
                dispositivo.setTipoDispositivo(cursor.getInt(4));

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
                dispositivo.setId(Integer.parseInt(cursor.getString(0)));
                dispositivo.setNombre(cursor.getString(1));
                dispositivo.setMacAddress(cursor.getString(2));
                dispositivo.setToken(cursor.getString(3));
                dispositivo.setTipoDispositivo(cursor.getInt(4));
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
                dispositivo.setId(Integer.parseInt(cursor.getString(0)));
                dispositivo.setNombre(cursor.getString(1));
                dispositivo.setMacAddress(cursor.getString(2));
                dispositivo.setToken(cursor.getString(3));
                dispositivo.setTipoDispositivo(cursor.getInt(4));
            } while (cursor.moveToNext());
        }
        db.close();
        return dispositivo;
    }
}
