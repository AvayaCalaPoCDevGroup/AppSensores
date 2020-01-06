package com.example.appsensores.ui.Fragments.vista_sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.DispoSensorPuck;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;

import java.util.Arrays;

public class FragmentDetalleSensorPuck extends BaseVistaFargment {

    private BluetoothAdapter Adapter;
    private DispoSensorPuck mDispoSensorPuck;

    private TextView tv_fragmentdetalle_puck_temperatura;
    private TextView tv_fragmentdetalle_puck_humedad;
    private TextView tv_fragmentdetalle_puck_lux;
    private TextView tv_fragmentdetalle_puck_uv;
    private TextView tv_fragmentdetalle_puck_voltaje;

    private Switch sw_fragmentdetalle_puck_temperatura;
    private Switch sw_fragmentdetalle_puck_humedad;
    private Switch sw_fragmentdetalle_puck_lux;
    private Switch sw_fragmentdetalle_puck_uv;
    private Switch sw_fragmentdetalle_puck_voltaje;

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

        /* Iniciar el Bluetooth*/
        BluetoothManager Manager = (BluetoothManager)getContext().getSystemService( Context.BLUETOOTH_SERVICE );
        Adapter = Manager.getAdapter();
        if ( Adapter==null || !Adapter.isEnabled() )
        {
            /* Solicitar al usuario encender el Bluetooth */
            Intent EnableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( EnableIntent, 1 );
        }
        boolean resp;
        /* Iniciar el escaneo de BLE advertisements */
        if ( Adapter!=null && Adapter.isEnabled() ) {
            resp = Adapter.startLeScan(ScanCallback);
        }
        return root;
    }

    @Override
    protected void setControles(View view) {
        tv_fragmentdetalle_puck_temperatura = view.findViewById(R.id.tv_fragmentdetalle_puck_temperatura);
        tv_fragmentdetalle_puck_humedad = view.findViewById(R.id.tv_fragmentdetalle_puck_humedad);
        tv_fragmentdetalle_puck_lux = view.findViewById(R.id.tv_fragmentdetalle_puck_lux);
        tv_fragmentdetalle_puck_uv = view.findViewById(R.id.tv_fragmentdetalle_puck_uv);
        tv_fragmentdetalle_puck_voltaje = view.findViewById(R.id.tv_fragmentdetalle_puck_voltaje);

        sw_fragmentdetalle_puck_temperatura = view.findViewById(R.id.sw_fragmentdetalle_puck_temperatura);
        sw_fragmentdetalle_puck_humedad = view.findViewById(R.id.sw_fragmentdetalle_puck_humedad);
        sw_fragmentdetalle_puck_lux = view.findViewById(R.id.sw_fragmentdetalle_puck_lux);
        sw_fragmentdetalle_puck_uv = view.findViewById(R.id.sw_fragmentdetalle_puck_uv);
        sw_fragmentdetalle_puck_voltaje = view.findViewById(R.id.sw_fragmentdetalle_puck_voltaje);
    }

    @Override
    protected void toogleControles(Boolean opcion) {
        sw_fragmentdetalle_puck_temperatura.setChecked(opcion);
        sw_fragmentdetalle_puck_humedad.setChecked(opcion);
        sw_fragmentdetalle_puck_lux.setChecked(opcion);
        sw_fragmentdetalle_puck_uv.setChecked(opcion);
        sw_fragmentdetalle_puck_voltaje.setChecked(opcion);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /* Stop scanning for BLE advertisements */
        if ( Adapter!=null && Adapter.isEnabled() )
            Adapter.stopLeScan( ScanCallback );
    }

    /* Esto se llama cuando se recive un Advertisement de cualquier dispositivo BLE */
    private BluetoothAdapter.LeScanCallback ScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord )
        {
            /* Crear un nuevo objeto Advertisement */
            /*Advertisement Adv = new Advertisement();
            Adv.Address = device.getAddress();
            Adv.Data    = scanRecord;*/

            /* Send the advertisement to the sensor handler */
            /*Message Msg = Message.obtain();
            Msg.obj = Adv;
            SensorHandler.sendMessage( Msg );*/

            if(device.getAddress().equals("D4:81:CA:E1:7A:DC")){
                for ( int x=0; x<scanRecord.length && scanRecord[x]!=0; x+=scanRecord[x]+1 )
                    Utils.onAdvertisingData( mDispoSensorPuck, scanRecord[x+1], Arrays.copyOfRange(scanRecord,x+2,x+scanRecord[x]+1));

                showData();
            }

        }
    };

    private void showData() {
        tv_fragmentdetalle_puck_temperatura.setText(mDispoSensorPuck.Temperature + "°C");
        tv_fragmentdetalle_puck_humedad.setText(mDispoSensorPuck.Humidity + "%");
        tv_fragmentdetalle_puck_lux.setText(mDispoSensorPuck.AmbientLight + "lux");
        tv_fragmentdetalle_puck_uv.setText(""+mDispoSensorPuck.UV_Index);
        tv_fragmentdetalle_puck_voltaje.setText(mDispoSensorPuck.Battery + " volts");
    }
}