package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appsensores.Clases.Enums.EnumTipoDispo;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;

import java.util.ArrayList;
import java.util.Arrays;

/***
 * Dialog para escanear dispositivos ble THUNDER y PUCK
 */
public class DialogSearchDevices extends Dialog {

    private ArrayList<BaseDispositivo> listDispoScaneados = new ArrayList<>();
    private ArrayList<String> lisDispoString = new ArrayList<>();
    public BaseDispositivo DeviceSelected;
    private ArrayAdapter adapterDispositivos;

    private BluetoothAdapter Adapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private ListView lv_dialogSearchDevices;

    public DialogSearchDevices(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.diaog_search_devices);

        lv_dialogSearchDevices = findViewById(R.id.lv_dialogSearchDevices);
        adapterDispositivos = new ArrayAdapter(getContext(),R.layout.list_unit_dispositivos,lisDispoString);
        lv_dialogSearchDevices.setAdapter(adapterDispositivos);
        lv_dialogSearchDevices.setOnItemClickListener((parent, view, position, id) -> {
            //establecemos el device seleccionado, este sera consultado en Ondismiss() de la clase que llamo este dialog
            DeviceSelected = listDispoScaneados.get(position);
            dismiss();
        });


        /* Iniciar el Bluetooth*/
        BluetoothManager Manager = (BluetoothManager)getContext().getSystemService( Context.BLUETOOTH_SERVICE );
        Adapter = Manager.getAdapter();

    }

    @Override
    protected void onStart() {
        super.onStart();
        /* Iniciar el escaneo de BLE advertisements */
        if ( Adapter!=null && Adapter.isEnabled() ) {
            //
            bluetoothLeScanner = Adapter.getBluetoothLeScanner();
            ArrayList<ScanFilter> filters = new ArrayList<>();
            //ScanFilter filter = new ScanFilter.Builder().setDeviceAddress("D4:81:CA:E1:7A:DC").build(); //SensorPuck
            //ScanFilter filter = new ScanFilter.Builder().setDeviceAddress("74:1C:E4:5E:4E:32").build();
            //filters.add(filter);
            ScanSettings scansattings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).setReportDelay(0).build();
            bluetoothLeScanner.startScan(filters,scansattings,ScanCallback);
            //bluetoothLeScanner.startScan(ScanCallback);
        }
    }

    /* Esto se llama cuando se recive un Advertisement de cualquier dispositivo BLE */
    private android.bluetooth.le.ScanCallback ScanCallback = new android.bluetooth.le.ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            ScanRecord sr = result.getScanRecord();
            String name = result.getDevice().getName();
            String mac_address = result.getDevice().getAddress();
            byte[] scanRecord = sr.getBytes();

            for ( int x=0; x<scanRecord.length && scanRecord[x]!=0; x+=scanRecord[x]+1 ){
                //Utils.onAdvertisingData( mDispoSensorPuck, scanRecord[x+1], Arrays.copyOfRange(scanRecord,x+2,x+scanRecord[x]+1));
                if ( (scanRecord[x+1]==(-1)) && (scanRecord[x+2]==0x35) && (scanRecord[x+3]==0x12) )
                {
                    Log.d("dialogSearch", "PUCK Name: " + name + " Address: " + mac_address);
                    checkDevice(mac_address, EnumTipoDispo.SENSOR_PUCK);
                } else if ((scanRecord[x+1]==(-1)) && (scanRecord[x+2]==0x47) && (scanRecord[x+3]==0x00)){
                    Log.d("dialogSearch", "THUNDER Name: " + name + " Address: " + mac_address);
                    checkDevice(mac_address, EnumTipoDispo.THUNDERBOARD);
                }
            }

        }
    };

    /***
     * Metodo para validar que el dispositivo no exista ya en la lista o la DB
     * @param mac_address
     * @param enumTipoDispo
     */
    private void checkDevice(String mac_address, EnumTipoDispo enumTipoDispo) {
        if(!isInCurrentList(mac_address) && !isInDatabase(mac_address)){
            //el dispositivo es nuevo tanto en la lista como en la base
            BaseDispositivo newDevice = new BaseDispositivo();
            newDevice.setNombre(EnumTipoDispo.values()[enumTipoDispo.ordinal()].toString());
            newDevice.setMacAddress(mac_address);
            newDevice.setTipoDispositivo(enumTipoDispo.ordinal());
            listDispoScaneados.add(newDevice);
            lisDispoString.clear();
            for (BaseDispositivo unit : listDispoScaneados) {
                lisDispoString.add(unit.getMacAddress() + " - " + ((EnumTipoDispo.values()[unit.getTipoDispositivo()])));
            }
            adapterDispositivos.notifyDataSetChanged();
        }
    }

    /***
     * Metodo para verificar si el dispositivo escaneado ya existe en la lista actual
     * @param mac_address mac address del dispositivo
     * @return true si ya esta en la ista, false si no
     */
    private boolean isInCurrentList(String mac_address){
        boolean resp = false;
        for (String unit : lisDispoString ) {
            if(unit.contains(mac_address)){
                resp = true;
                break;
            }
        }
        return resp;
    }

    /***
     * Metodo para verificar que el dispositivo escaneado existe en la DB
     * @param macAddres
     * @return tru si ya existe en la DB, false si no
     */
    private boolean isInDatabase(String macAddres){
        BaseDispositivo device = RepositorioDBGeneralSingleton.getInstance(getContext()).getDeviceByMAC(macAddres);
        if(device == null)
            return false;
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        /* Stop scanning for BLE advertisements */
        if ( Adapter!=null && Adapter.isEnabled() )
            bluetoothLeScanner.stopScan( ScanCallback );

        lisDispoString.clear();
        listDispoScaneados.clear();
    }
}
