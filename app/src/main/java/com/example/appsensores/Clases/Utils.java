package com.example.appsensores.Clases;

import com.example.appsensores.Models.Dispositivos.DispoSensorPuck;

import java.util.Arrays;
import java.util.UUID;

import static java.util.UUID.fromString;

public class Utils {

    public static final String AVAYA_SHARED_PREFERENCES = "AVAYA_PREFERENCES";
    public static final String AVAYA_INTERVALO = "AVAYA_INTERVALO";

    public static final int ENVIRONMENTAL_MODE = 0;
    public static final int BIOMETRIC_MODE = 1;

    public static final int HRM_SAMPLE_COUNT = 5;

    public static final int MIN_INTERVAL_TAGO = 3;

    public static void onAdvertisingData(DispoSensorPuck ThisPuck, byte type, byte[] ADData) {

        /* If the advertisement contains Silabs manufacturer specific data */
        if ( (type==(-1)) && ((ADData[0]==0x34)||(ADData[0]==0x35)) && (ADData[1]==0x12) )
        {
            /* If its a new style advertisement */
            if ( ADData[0] == 0x35 )
            {
                /* If its an environmental advertisement then process it */
                if ( ADData[2] == ENVIRONMENTAL_MODE )
                    onEnvironmentalData( ThisPuck, Arrays.copyOfRange(ADData,3,14) );

                /* If its a biometric advertisement then process it */
                if ( ADData[2] == BIOMETRIC_MODE )
                    onBiometricData( ThisPuck, Arrays.copyOfRange(ADData,3,18) );
            }

            /* Another adverstisement has been received */
            ThisPuck.RecvCount++;

            /* Ignore duplicate advertisements */
            if ( ThisPuck.Sequence != ThisPuck.PrevSequence )
            {
                /* Another unique adverstisement has been received */
                ThisPuck.UniqueCount++;

                /* Calculate the number of lost advertisements */
                if ( ThisPuck.Sequence > ThisPuck.PrevSequence )
                    ThisPuck.LostAdv = ThisPuck.Sequence - ThisPuck.PrevSequence - 1;
                else /* Wrap around */
                    ThisPuck.LostAdv = ThisPuck.Sequence - ThisPuck.PrevSequence + 255;

                /* Big losses means just found a new puck */
                if ( (ThisPuck.LostAdv == 1) || (ThisPuck.LostAdv == 2) )
                    ThisPuck.LostCount += ThisPuck.LostAdv;

                ThisPuck.PrevSequence = ThisPuck.Sequence;
            }
        }
    }

    /* Process environmental data (new style advertisement) */
    static void onEnvironmentalData( DispoSensorPuck ThisPuck, byte Data[] )
    {
        ThisPuck.MeasurementMode = ENVIRONMENTAL_MODE;
        ThisPuck.Sequence        = Int8( Data[0] );
        ThisPuck.Humidity        = ((float)Int16(Data[3],Data[4]))/10;
        ThisPuck.Temperature     = ((float)Int16(Data[5],Data[6]))/10;
        ThisPuck.AmbientLight    = Int16( Data[7], Data[8] )*2;
        ThisPuck.UV_Index        = Int8( Data[9] );
        ThisPuck.Battery         = ((float)Int8(Data[10]))/10;
    }

    /* Process biometric data (new style advertisement) */
    static void onBiometricData( DispoSensorPuck ThisPuck, byte Data[] )
    {
        ThisPuck.MeasurementMode = BIOMETRIC_MODE;
        ThisPuck.Sequence        = Int8( Data[0] );
        ThisPuck.HRM_State       = Int8( Data[3] );
        ThisPuck.HRM_Rate        = Int8( Data[4] );

        ThisPuck.HRM_PrevSample = ThisPuck.HRM_Sample[HRM_SAMPLE_COUNT-1];
        for ( int x=0; x<HRM_SAMPLE_COUNT; x++ )
            ThisPuck.HRM_Sample[x] = Int16( Data[5+(x*2)], Data[6+(x*2)] );
    }

    /* Convertir byte to int */
    static int Int8( byte Data )
    {
        return (int)(((char)Data)&0xFF);
    }

    /* Convertir 2 bytes en int */
    static int Int16( byte LSB, byte MSB )
    {
        return Int8(LSB) + (Int8(MSB)*256);
    }

    /***
     * Retrona el nobre de la caracterustica a partir de su UUID
     * @param uuid UUID de la Caracteristica
     * @return
     */
    static String getCharacteristicName(UUID uuid)
    {
        String resp;
        switch (uuid.toString()){
                case "00002a00-0000-1000-8000-00805f9b34fb": resp = "DEVICE_NAME"; break;
                case "00002a01-0000-1000-8000-00805f9b34fb": resp = "APPEARANCE"; break;
                case "00002a05-0000-1000-8000-00805f9b34fb": resp = "ATTRIBUTE_CHANGED"; break;
                case "00002a23-0000-1000-8000-00805f9b34fb": resp = "SYSTEM_ID"; break;
                case "00002a24-0000-1000-8000-00805f9b34fb": resp = "MODEL_NUMBER"; break;
                case "00002a25-0000-1000-8000-00805f9b34fb": resp = "SERIAL_NUMBER"; break;
                case "00002a26-0000-1000-8000-00805f9b34fb": resp = "FIRMWARE_REVISION"; break;
                case "00002a27-0000-1000-8000-00805f9b34fb": resp = "HARDWARE_REVISION"; break;
                case "00002a29-0000-1000-8000-00805f9b34fb": resp = "MANUFACTURER_NAME"; break;
                case "00002a19-0000-1000-8000-00805f9b34fb": resp = "BATTERY_LEVEL"; break;
                case "EC61A454-ED01-A5E8-B8F9-DE9EC026EC51": resp = "POWER_SOURCE"; break;
                case "00002a55-0000-1000-8000-00805f9b34fb": resp = "CSC_CONTROL_POINT"; break;
                case "00002a5b-0000-1000-8000-00805f9b34fb": resp = "CSC_MEASUREMENT"; break;
                case "00002a5c-0000-1000-8000-00805f9b34fb": resp = "CSC_FEATURE"; break;
                case "9f70a8fc-826c-4c6f-9c72-41b81d1c9561": resp = "CSC_UNKNOWN"; break;
                case "00002a76-0000-1000-8000-00805f9b34fb": resp = "UV_INDEX"; break;
                case "00002a6d-0000-1000-8000-00805f9b34fb": resp = "PRESSURE"; break;
                case "00002a6e-0000-1000-8000-00805f9b34fb": resp = "TEMPERATURE"; break;
                case "00002a6f-0000-1000-8000-00805f9b34fb": resp = "HUMIDITY"; break;
                case "c8546913-bfd9-45eb-8dde-9f8754f4a32e": resp = "AMBIENT_LIGHT_REACT"; break;
                case "c8546913-bf01-45eb-8dde-9f8754f4a32e": resp = "LIGHT_SENSE"; break;
                case "c8546913-bf02-45eb-8dde-9f8754f4a32e": resp = "SOUND_LEVEL"; break;
                case "c8546913-bf03-45eb-8dde-9f8754f4a32e": resp = "ENV_CONTROL_POINT"; break;
                case "efd658ae-c401-ef33-76e7-91b00019103b": resp = "CO2_READING"; break;
                case "efd658ae-c402-ef33-76e7-91b00019103b": resp = "TVOC_READING"; break;
                case "efd658ae-c403-ef33-76e7-91b00019103b": resp = "AIR_QUALITY_CONTROL_POINT"; break;
                case "f598dbc5-2f01-4ec5-9936-b3d1aa4f957f": resp = "HALL_STATE"; break;
                case "f598dbc5-2f02-4ec5-9936-b3d1aa4f957f": resp = "HALL_FIELD_STRENGTH"; break;
                case "f598dbc5-2f03-4ec5-9936-b3d1aa4f957f": resp = "HALL_CONTROL_POINT"; break;
                case "c4c1f6e2-4be5-11e5-885d-feff819cdc9f": resp = "ACCELERATION"; break;
                case "b7c4b694-bee3-45dd-ba9f-f3b5e994f49a": resp = "ORIENTATION"; break;
                case "71e30b8c-4131-4703-b0a0-b0bbba75856b": resp = "CALIBRATE"; break;
                case "fcb89c40-c601-59f3-7dc3-5ece444a401b": resp = "PUSH_BUTTONS"; break;
                case "fcb89c40-c602-59f3-7dc3-5ece444a401b": resp = "LEDS"; break;
                case "fcb89c40-c603-59f3-7dc3-5ece444a401b": resp = "RGB_LEDS"; break;
                case "fcb89c40-c604-59f3-7dc3-5ece444a401b": resp = "UI_CONTROL_POINT"; break;
                case "00002a56-0000-1000-8000-00805f9b34fb": resp = "DIGITAL"; break;
            default : resp = "UNKNOW";
        }

        return resp;
    }

    /***
     * Retrona el nobre de el serivicio a partir de su UUID
     * @param uuid UUID de el Servicio
     * @return
     */
    static String getServiceName(UUID uuid)
    {
        String resp;
        switch (uuid.toString()){
            case "00001800-0000-1000-8000-00805f9b34fb" :  resp = "SERVICE_GENERIC_ACCESS"          ; break;
            case "00001801-0000-1000-8000-00805f9b34fb" :  resp = "SERVICE_GENERIC_ATTRIBUTE"       ; break;
            case "0000180a-0000-1000-8000-00805f9b34fb" :  resp = "SERVICE_DEVICE_INFORMATION"      ; break;
            case "0000180f-0000-1000-8000-00805f9b34fb" :  resp = "SERVICE_BATTERY"                 ; break;
            case "00001815-0000-1000-8000-00805f9b34fb" :  resp = "SERVICE_AUTOMATION_IO"           ; break;
            case "00001816-0000-1000-8000-00805f9b34fb" :  resp = "SERVICE_CSC"                     ; break;
            case "0000181a-0000-1000-8000-00805f9b34fb" :  resp = "SERVICE_ENVIRONMENT_SENSING"     ; break;
            case "a4e649f4-4be5-11e5-885d-feff819cdc9f" :  resp = "SERVICE_ACCELERATION_ORIENTATION"; break;
            case "d24c4f4e-17a7-4548-852c-abf51127368b" :  resp = "SERVICE_AMBIENT_LIGHT"           ; break;
            case "efd658ae-c400-ef33-76e7-91b00019103b" :  resp = "SERVICE_INDOOR_AIR_QUALITY"      ; break;
            case "f598dbc5-2f00-4ec5-9936-b3d1aa4f957f" :  resp = "SERVICE_HALL_EFFECT"             ; break;
            case "fcb89c40-c600-59f3-7dc3-5ece444a401b" :  resp = "SERVICE_USER_INTERFACE"          ; break;
            case "ec61a454-ed00-a5e8-b8f9-de9ec026ec51" :  resp = "SERVICE_POWER_MANAGEMENT"        ; break;
            default : resp = "UNKNOW";
        }

        return resp;
    }
}
