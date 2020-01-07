package com.example.appsensores.Clases;

import com.example.appsensores.Models.Dispositivos.DispoSensorPuck;

import java.util.Arrays;

public class Utils {

    public static final String AVAYA_SHARED_PREFERENCES = "AVAYA_PREFERENCES";
    public static final String AVAYA_INTERVALO = "AVAYA_INTERVALO";

    public static final int ENVIRONMENTAL_MODE = 0;
    public static final int BIOMETRIC_MODE = 1;

    public static final int HRM_SAMPLE_COUNT = 5;

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
}
