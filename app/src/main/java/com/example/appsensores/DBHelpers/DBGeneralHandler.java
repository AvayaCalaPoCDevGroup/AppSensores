package com.example.appsensores.DBHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.R;

public class DBGeneralHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GeneralDB";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;

    //TABLA Dispositivos
    public static String TABLE_DISPOSITIVOS = "Dispositivos";
    public static String KEY_DISPOSITIVOS_ID = "id";
    public static String KEY_DISPOSITIVOS_NOMBRE = "Nombre";
    public static String KEY_DISPOSITIVOS_MACADDRESS = "Macaddress";
    public static String KEY_DISPOSITIVOS_TOKEN = "Token";
    public static String KEY_DISPOSITIVOS_TIPODISPOSITIVO = "TipoDispositivo";

    //TABLA RULES
    public static String TABLE_RULES = "Rules";
    public static String KEY_RULES_ID = "id";
    public static String KEY_RULES_DISPOID = "DispositivoId";
    public static String KEY_RULES_RULEID = "RuleId";
    public static String KEY_RULES_SENSORID = "SensorId";
    public static String KEY_RULES_VALUE1 = "Value1";
    public static String KEY_RULES_VALUE2 = "Value2";
    public static String KEY_RULES_ISENABLED = "IsEnabled";
    public static String KEY_RULES_LASTDATE = "LastDate";

    public DBGeneralHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DISPOSITIVOS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_DISPOSITIVOS + "("
                + KEY_DISPOSITIVOS_ID + " INTEGER PRIMARY KEY,"
                + KEY_DISPOSITIVOS_NOMBRE + " TEXT,"
                + KEY_DISPOSITIVOS_MACADDRESS + " TEXT,"
                + KEY_DISPOSITIVOS_TOKEN + " TEXT,"
                + KEY_DISPOSITIVOS_TIPODISPOSITIVO + " INT"
                + ")";
        db.execSQL(CREATE_DISPOSITIVOS_TABLE);

        String CREATE_RULES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RULES + "("
                + KEY_RULES_ID + " INTEGER PRIMARY KEY,"
                + KEY_RULES_DISPOID + " INTEGER,"
                + KEY_RULES_RULEID + " INTEGER,"
                + KEY_RULES_SENSORID + " INTEGER,"
                + KEY_RULES_VALUE1 + " FLOAT,"
                + KEY_RULES_VALUE2 + " FLOAT,"
                + KEY_RULES_ISENABLED + " INTEGER,"
                + KEY_RULES_LASTDATE + " TEXT"
                + ")";
        db.execSQL(CREATE_RULES_TABLE);

        String thisPhoneName = mContext.getResources().getString(R.string.fragment_detalle_Telefono);

        String SEED = "insert into " + TABLE_DISPOSITIVOS + " ( " +
                KEY_DISPOSITIVOS_NOMBRE + "," +
                KEY_DISPOSITIVOS_MACADDRESS + "," +
                KEY_DISPOSITIVOS_TOKEN + "," +
                KEY_DISPOSITIVOS_TIPODISPOSITIVO +
                ") values('"+thisPhoneName+"', 'abcd', 'token', 0)";
        db.execSQL(SEED);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
