package com.example.appsensores.ui.Fragments.vista_sensor;

import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;

import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;

import com.example.appsensores.Clases.STimer;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.DispoSensorPuck;
import com.example.appsensores.Models.ValuesTago;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class FragmentDetalleSensorPuck extends BaseVistaFargment {

    private BluetoothAdapter Adapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private DispoSensorPuck mDispoSensorPuck;

    private STimer mSTimer;

    private TextView tv_fragmentdetalle_puck_temperatura;
    private TextView tv_fragmentdetalle_puck_humedad;
    private TextView tv_fragmentdetalle_puck_lux;
    private TextView tv_fragmentdetalle_puck_uv;
    private TextView tv_fragmentdetalle_puck_voltaje;
    private TextView tv_fragmentdetalle_puck_hrmrate;

    //TV de pruebas para monitorear el numero de advertisements y secuencias
    private TextView tv_advcount;
    private TextView tv_secuencecount;

    private Switch sw_fragmentdetalle_puck_temperatura;
    private Switch sw_fragmentdetalle_puck_humedad;
    private Switch sw_fragmentdetalle_puck_lux;
    private Switch sw_fragmentdetalle_puck_uv;
    private Switch sw_fragmentdetalle_puck_voltaje;
    private Switch sw_fragmentdetalle_puck_hrmrate;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detalle_sensorpuck, container, false);

        mDispoSensorPuck = new DispoSensorPuck();
        mDispoSensorPuck.setId(dispositivoBase.getId());
        mDispoSensorPuck.setNombre(dispositivoBase.getNombre());
        mDispoSensorPuck.setMacAddress(dispositivoBase.getMacAddress());
        mDispoSensorPuck.setToken(dispositivoBase.getToken());
        mDispoSensorPuck.setTipoDispositivo(dispositivoBase.getTipoDispositivo());
        mDispoSensorPuck.HRM_Sample = new int[Utils.HRM_SAMPLE_COUNT];

        /* Iniciar el Bluetooth*/
        BluetoothManager Manager = (BluetoothManager)getContext().getSystemService( Context.BLUETOOTH_SERVICE );
        Adapter = Manager.getAdapter();
        if ( Adapter==null || !Adapter.isEnabled() )
        {
            /* Solicitar al usuario encender el Bluetooth */
            Intent EnableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( EnableIntent, 1 );
        }

        /* Iniciar el escaneo de BLE advertisements */
        if ( Adapter!=null && Adapter.isEnabled() ) {
            //
            bluetoothLeScanner = Adapter.getBluetoothLeScanner();
            ArrayList<ScanFilter> filters = new ArrayList<>();
            //ScanFilter filter = new ScanFilter.Builder().setDeviceAddress("D4:81:CA:E1:7A:DC").build(); //SensorPuck
            ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(mDispoSensorPuck.getMacAddress()).build();
            filters.add(filter);
            ScanSettings scansattings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).setReportDelay(0).build();
            bluetoothLeScanner.startScan(filters,scansattings,ScanCallback);
            //bluetoothLeScanner.startScan(ScanCallback);
        }

        //Iniciamos el timer
        mSTimer = new STimer();
        mSTimer.setOnAlarmListener( OnPuckTick );
        mSTimer.setPeriod( STimer.CURRENT_PERIOD );

        mSTimer.start();

        return root;
    }

    @Override
    public void setListenerForRulesButton(Button button) {
        button.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("idSensor", dispositivoBase.getId());

            Navigation.findNavController(getView()).navigate(R.id.action_fragmentDetalleSensorPuck_to_fragmentRules,bundle);

        });
    }

    @Override
    protected void setControles(View view) {
        tv_fragmentdetalle_puck_temperatura = view.findViewById(R.id.tv_fragmentdetalle_puck_temperatura);
        tv_fragmentdetalle_puck_humedad = view.findViewById(R.id.tv_fragmentdetalle_puck_humedad);
        tv_fragmentdetalle_puck_lux = view.findViewById(R.id.tv_fragmentdetalle_puck_lux);
        tv_fragmentdetalle_puck_uv = view.findViewById(R.id.tv_fragmentdetalle_puck_uv);
        tv_fragmentdetalle_puck_voltaje = view.findViewById(R.id.tv_fragmentdetalle_puck_voltaje);
        tv_fragmentdetalle_puck_hrmrate = view.findViewById(R.id.tv_fragmentdetalle_puck_hrmrate);

        //Pruebas
        tv_advcount = view.findViewById(R.id.tv_advcount);
        tv_secuencecount = view.findViewById(R.id.tv_secuencecount);

        sw_fragmentdetalle_puck_temperatura = view.findViewById(R.id.sw_fragmentdetalle_puck_temperatura);
        sw_fragmentdetalle_puck_humedad = view.findViewById(R.id.sw_fragmentdetalle_puck_humedad);
        sw_fragmentdetalle_puck_lux = view.findViewById(R.id.sw_fragmentdetalle_puck_lux);
        sw_fragmentdetalle_puck_uv = view.findViewById(R.id.sw_fragmentdetalle_puck_uv);
        sw_fragmentdetalle_puck_voltaje = view.findViewById(R.id.sw_fragmentdetalle_puck_voltaje);
        sw_fragmentdetalle_puck_hrmrate = view.findViewById(R.id.sw_fragmentdetalle_puck_hrmrate);

        sw_fragmentdetalle_puck_temperatura.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_puck_humedad.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_puck_lux.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_puck_uv.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_puck_voltaje.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_puck_hrmrate.setOnCheckedChangeListener(swListener);

        //Agregamos los switches en la clase padre para que sean afectados con la accion del switch general
        addSwitchToList(sw_fragmentdetalle_puck_temperatura);
        addSwitchToList(sw_fragmentdetalle_puck_humedad);
        addSwitchToList(sw_fragmentdetalle_puck_lux);
        addSwitchToList(sw_fragmentdetalle_puck_uv);
        addSwitchToList(sw_fragmentdetalle_puck_voltaje);
        addSwitchToList(sw_fragmentdetalle_puck_hrmrate);
    }

    @Override
    protected void onToogleControles(Boolean opcion) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /* Stop scanning for BLE advertisements */
        if ( Adapter!=null && Adapter.isEnabled() )
            bluetoothLeScanner.stopScan( ScanCallback );

        mSTimer.stop();
    }

    /* Esto se llama cuando se recive un Advertisement de cualquier dispositivo BLE */
    private android.bluetooth.le.ScanCallback ScanCallback = new android.bluetooth.le.ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            ScanRecord sr = result.getScanRecord();
            byte[] scanRecord = sr.getBytes();

            for ( int x=0; x<scanRecord.length && scanRecord[x]!=0; x+=scanRecord[x]+1 )
                Utils.onAdvertisingData( mDispoSensorPuck, scanRecord[x+1], Arrays.copyOfRange(scanRecord,x+2,x+scanRecord[x]+1));

            showData();
        }
    };

    private void showData() {
        tv_fragmentdetalle_puck_temperatura.setText((sw_fragmentdetalle_puck_temperatura.isChecked() ? mDispoSensorPuck.Temperature : 0) + "°C");
        tv_fragmentdetalle_puck_humedad.setText(( sw_fragmentdetalle_puck_humedad.isChecked() ? mDispoSensorPuck.Humidity : 0) + "%");
        tv_fragmentdetalle_puck_lux.setText(( sw_fragmentdetalle_puck_lux.isChecked() ? mDispoSensorPuck.AmbientLight : 0) + " lux");
        tv_fragmentdetalle_puck_uv.setText(""+ ( sw_fragmentdetalle_puck_uv.isChecked() ? mDispoSensorPuck.UV_Index : 0));
        tv_fragmentdetalle_puck_voltaje.setText(( sw_fragmentdetalle_puck_voltaje.isChecked() ? mDispoSensorPuck.Battery : 0) + " volts");

        tv_fragmentdetalle_puck_hrmrate.setText(""+(sw_fragmentdetalle_puck_hrmrate.isChecked() ? mDispoSensorPuck.HRM_Rate : 0) + " bpm");

        //Pruebas
        tv_advcount.setText("AdvCount: " + mDispoSensorPuck.RecvCount);
        tv_secuencecount.setText("Secuence: " + mDispoSensorPuck.Sequence);
    }

    /* Esto se llama una vez por segundo en la UI thread */
    STimer.OnAlarmListener OnPuckTick = new STimer.OnAlarmListener()
    {
        @Override
        public void OnAlarm( STimer source )
        {
            boolean PuckNamesChanged = false;


            /* If an advertisement was not received within the last second */
            if ( mDispoSensorPuck.RecvCount == mDispoSensorPuck.PrevCount )
            {
                /* If the puck is idle for too long */
                /*if ( ++mDispoSensorPuck.IdleCount == MAX_IDLE_COUNT )
                {
                    PuckNamesChanged = true;

                    *//* Delete the puck *//*
                    DeletePuck( x-- );
                }*/
               mDispoSensorPuck.Temperature = 0;
               mDispoSensorPuck.Humidity = 0;
               mDispoSensorPuck.AmbientLight = 0;
               mDispoSensorPuck.UV_Index = 0;
               mDispoSensorPuck.Battery = 0;
               mDispoSensorPuck.HRM_Rate = 0;
               showData();


            }
            else /* An advertisment was received within the last second */
            {
                mDispoSensorPuck.PrevCount = mDispoSensorPuck.RecvCount;
                mDispoSensorPuck.IdleCount = 0;
            }
            sendData();
            new checkAndSendRules().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mDispoSensorPuck);
        }
    };

    private void sendData() {
        ValuesTago[] values = {
                new ValuesTago("Temperatura", ""+(sw_fragmentdetalle_puck_temperatura.isChecked() ? mDispoSensorPuck.Temperature : 0)),
                new ValuesTago("Humidity", ""+( sw_fragmentdetalle_puck_humedad.isChecked() ? mDispoSensorPuck.Humidity : 0)),
                new ValuesTago("AmbientLight", ""+( sw_fragmentdetalle_puck_lux.isChecked() ? mDispoSensorPuck.AmbientLight : 0)),
                new ValuesTago("UV_Index", ""+( sw_fragmentdetalle_puck_uv.isChecked() ? mDispoSensorPuck.UV_Index : 0)),
                new ValuesTago("Battery", ""+( sw_fragmentdetalle_puck_voltaje.isChecked() ? mDispoSensorPuck.Battery : 0)),
                new ValuesTago("HRM_Rate", ""+( sw_fragmentdetalle_puck_hrmrate.isChecked() ? mDispoSensorPuck.HRM_Rate : 0))
        };

        new EnviarInformacionTago(mDispoSensorPuck.getToken()).execute(values);
    }


    @Override
    public void onSettingsChanged() {
        if(mSTimer != null)
            mSTimer.setPeriod( STimer.CURRENT_PERIOD );
    }
}