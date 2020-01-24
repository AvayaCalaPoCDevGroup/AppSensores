package com.example.appsensores.ui.Fragments.vista_sensor;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.appsensores.Clases.GattClient;
import com.example.appsensores.Clases.STimer;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.DispoThunderBoard;
import com.example.appsensores.Models.ValuesTago;
import com.example.appsensores.R;
import com.example.appsensores.ui.Activities.MainActivity;
import com.journeyapps.barcodescanner.Util;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class FragmentThunderBoard extends BaseVistaFargment implements MqttCallback {

    private BluetoothAdapter Adapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private DispoThunderBoard mDispoThunderBoard;

    private STimer mSTimer;

    private GattClient mGattClient;

    private TextView tv_fragmentdetalle_thunder_temperatura;
    private TextView tv_fragmentdetalle_thunder_humedad;
    private TextView tv_fragmentdetalle_thunder_lux;
    private TextView tv_fragmentdetalle_thunder_uv;
    private TextView tv_fragmentdetalle_thunder_voltaje;
    private TextView tv_fragmentdetalle_thunder_ox;
    private TextView tv_fragmentdetalle_thunder_oy;
    private TextView tv_fragmentdetalle_thunder_oz;
    private TextView tv_fragmentdetalle_thunder_ax;
    private TextView tv_fragmentdetalle_thunder_ay;
    private TextView tv_fragmentdetalle_thunder_az;
    private TextView tv_fragmentdetalle_thunder_sw0;
    private TextView tv_fragmentdetalle_thunder_sw1;

    private Switch sw_fragmentdetalle_thunder_temperatura;
    private Switch sw_fragmentdetalle_thunder_humedad;
    private Switch sw_fragmentdetalle_thunder_lux;
    private Switch sw_fragmentdetalle_thunder_uv;
    private Switch sw_fragmentdetalle_thunder_voltaje;
    private Switch sw_fragmentdetalle_thunder_ox;
    private Switch sw_fragmentdetalle_thunder_oy;
    private Switch sw_fragmentdetalle_thunder_oz;
    private Switch sw_fragmentdetalle_thunder_ax;
    private Switch sw_fragmentdetalle_thunder_ay;
    private Switch sw_fragmentdetalle_thunder_az;

    private ToggleButton tb_fragmentdetalle_thunder_ledblue;
    private ToggleButton tb_fragmentdetalle_thunder_ledgreen;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detalle_thunder, container, false);

        mDispoThunderBoard = new DispoThunderBoard();
        mDispoThunderBoard.setId(dispositivoBase.getId());
        mDispoThunderBoard.setNombre(dispositivoBase.getNombre());
        mDispoThunderBoard.setMacAddress(dispositivoBase.getMacAddress());
        mDispoThunderBoard.setToken(dispositivoBase.getToken());
        mDispoThunderBoard.setTipoDispositivo(dispositivoBase.getTipoDispositivo());

        /* Iniciar el Bluetooth*/
        BluetoothManager Manager = (BluetoothManager)getContext().getSystemService( Context.BLUETOOTH_SERVICE );
        Adapter = Manager.getAdapter();
        if ( Adapter==null || !Adapter.isEnabled() )
        {
            /* Solicitar al usuario encender el Bluetooth */
            Intent EnableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( EnableIntent, 1 );
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

            Navigation.findNavController(getView()).navigate(R.id.action_fragmentThunderBoard_to_fragmentRules,bundle);

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void setControles(View view) {
        mGattClient = new GattClient(mDispoThunderBoard);

        tv_fragmentdetalle_thunder_temperatura =    view.findViewById(R.id.tv_fragmentdetalle_thunder_temperatura);
        tv_fragmentdetalle_thunder_humedad =        view.findViewById(R.id.tv_fragmentdetalle_thunder_humedad);
        tv_fragmentdetalle_thunder_lux =            view.findViewById(R.id.tv_fragmentdetalle_thunder_lux);
        tv_fragmentdetalle_thunder_uv =             view.findViewById(R.id.tv_fragmentdetalle_thunder_uv);
        tv_fragmentdetalle_thunder_voltaje =        view.findViewById(R.id.tv_fragmentdetalle_thunder_voltaje);
        tv_fragmentdetalle_thunder_ox =             view.findViewById(R.id.tv_fragmentdetalle_thunder_ox);
        tv_fragmentdetalle_thunder_oy =             view.findViewById(R.id.tv_fragmentdetalle_thunder_oy);
        tv_fragmentdetalle_thunder_oz =             view.findViewById(R.id.tv_fragmentdetalle_thunder_oz);
        tv_fragmentdetalle_thunder_ax =             view.findViewById(R.id.tv_fragmentdetalle_thunder_ax);
        tv_fragmentdetalle_thunder_ay =             view.findViewById(R.id.tv_fragmentdetalle_thunder_ay);
        tv_fragmentdetalle_thunder_az =             view.findViewById(R.id.tv_fragmentdetalle_thunder_az);
        tv_fragmentdetalle_thunder_sw0 =            view.findViewById(R.id.tv_fragmentdetalle_thunder_sw0);
        tv_fragmentdetalle_thunder_sw1 =            view.findViewById(R.id.tv_fragmentdetalle_thunder_sw1);

        sw_fragmentdetalle_thunder_temperatura =    view.findViewById(R.id.sw_fragmentdetalle_thunder_temperatura);
        sw_fragmentdetalle_thunder_humedad =        view.findViewById(R.id.sw_fragmentdetalle_thunder_humedad);
        sw_fragmentdetalle_thunder_lux =            view.findViewById(R.id.sw_fragmentdetalle_thunder_lux);
        sw_fragmentdetalle_thunder_uv =             view.findViewById(R.id.sw_fragmentdetalle_thunder_uv);
        sw_fragmentdetalle_thunder_voltaje =        view.findViewById(R.id.sw_fragmentdetalle_thunder_voltaje);
        sw_fragmentdetalle_thunder_ox = view.findViewById(R.id.sw_fragmentdetalle_thunder_ox);
        sw_fragmentdetalle_thunder_oy = view.findViewById(R.id.sw_fragmentdetalle_thunder_oy);
        sw_fragmentdetalle_thunder_oz = view.findViewById(R.id.sw_fragmentdetalle_thunder_oz);
        sw_fragmentdetalle_thunder_ax = view.findViewById(R.id.sw_fragmentdetalle_thunder_ax);
        sw_fragmentdetalle_thunder_ay = view.findViewById(R.id.sw_fragmentdetalle_thunder_ay);
        sw_fragmentdetalle_thunder_az = view.findViewById(R.id.sw_fragmentdetalle_thunder_az);

        sw_fragmentdetalle_thunder_temperatura.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_humedad.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_lux.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_uv.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_voltaje.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_ox.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_oy.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_oz.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_ax.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_ay.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_thunder_az.setOnCheckedChangeListener(swListener);

        tb_fragmentdetalle_thunder_ledblue = view.findViewById(R.id.tb_fragmentdetalle_thunder_ledblue);
        tb_fragmentdetalle_thunder_ledgreen = view.findViewById(R.id.tb_fragmentdetalle_thunder_ledgreen);

        tb_fragmentdetalle_thunder_ledblue.setOnCheckedChangeListener(listener_tb);
        tb_fragmentdetalle_thunder_ledgreen.setOnCheckedChangeListener(listener_tb);

        //Agregamos los switches en la clase padre para que sean afectados con la accion del switch general
        addSwitchToList(sw_fragmentdetalle_thunder_temperatura);
        addSwitchToList(sw_fragmentdetalle_thunder_humedad);
        addSwitchToList(sw_fragmentdetalle_thunder_lux);
        addSwitchToList(sw_fragmentdetalle_thunder_uv);
        addSwitchToList(sw_fragmentdetalle_thunder_voltaje);
        addSwitchToList(sw_fragmentdetalle_thunder_ox);
        addSwitchToList(sw_fragmentdetalle_thunder_oy);
        addSwitchToList(sw_fragmentdetalle_thunder_oz);
        addSwitchToList(sw_fragmentdetalle_thunder_ax);
        addSwitchToList(sw_fragmentdetalle_thunder_ay);
        addSwitchToList(sw_fragmentdetalle_thunder_az);

        dialogCargando.show();
        ((TextView)dialogCargando.findViewById(R.id.dialog_loading_msg)).setText(R.string.dialog_loading_msg_conectando);

        mGattClient.onCreate(getContext(), mDispoThunderBoard.getMacAddress(), new GattClient.OnReadListener() {
            @Override
            public void onReadValues(final DispoThunderBoard dispo) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(dispo);
                    }
                });
            }

            @Override
            public void onConnected(final boolean success) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogCargando.dismiss();
                        ((MainActivity)getActivity()).setMqttCalbback(FragmentThunderBoard.this);
                    }
                });
            }

            @Override
            public void onFailedConnection() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), getResources().getString(R.string.fragment_thunder_noConnection), Toast.LENGTH_SHORT).show();
                        dialogCargando.dismiss();
                        getActivity().onBackPressed();
                    }
                });
            }
        });
    }

    CompoundButton.OnCheckedChangeListener listener_tb = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int comando = 0;
            if(buttonView == tb_fragmentdetalle_thunder_ledblue){
                if (isChecked) comando = 1;
                if (tb_fragmentdetalle_thunder_ledgreen.isChecked()) comando |= 4;
            } else if (buttonView == tb_fragmentdetalle_thunder_ledgreen){
                if (isChecked) comando = 4;
                if (tb_fragmentdetalle_thunder_ledblue.isChecked()) comando |= 1;
            }

            ProgressDialog dialog = ProgressDialog.show(getContext(), getResources().getString(R.string.dialog_wait_title), getResources().getString(R.string.dialog_wait_msg), true);
            mGattClient.sendDataLeds(comando, new Handler(msg -> {
                if(msg.what == -1)
                    dialog.dismiss();
                return true;
            }));

        }
    };

    /***
     * Metodo para actualizar la UI con los valores actuales del dispositivo
     * @param dispo
     */
    private void updateUI(DispoThunderBoard dispo) {
        tv_fragmentdetalle_thunder_temperatura.setText((sw_fragmentdetalle_thunder_temperatura.isChecked() ? dispo.Temperature : 0) + "°C");
        tv_fragmentdetalle_thunder_humedad.setText(( sw_fragmentdetalle_thunder_humedad.isChecked() ? dispo.Humidity : 0) + "%");
        tv_fragmentdetalle_thunder_lux.setText(( sw_fragmentdetalle_thunder_lux.isChecked() ? dispo.AmbientLight : 0) + " lux");
        tv_fragmentdetalle_thunder_uv.setText(""+ ( sw_fragmentdetalle_thunder_uv.isChecked() ? dispo.UV_Index : 0));
        tv_fragmentdetalle_thunder_voltaje.setText(( sw_fragmentdetalle_thunder_voltaje.isChecked() ? dispo.batteryLevel : 0) + " %");
        tv_fragmentdetalle_thunder_ox.setText(""+(sw_fragmentdetalle_thunder_ox.isChecked() ? dispo.Orientation_x : 0));
        tv_fragmentdetalle_thunder_oy.setText(""+(sw_fragmentdetalle_thunder_oy.isChecked() ? dispo.Orientation_y : 0));
        tv_fragmentdetalle_thunder_oz.setText(""+(sw_fragmentdetalle_thunder_oz.isChecked() ? dispo.Orientation_z : 0));
        tv_fragmentdetalle_thunder_ax.setText(""+(sw_fragmentdetalle_thunder_ax.isChecked() ? dispo.Acelereation_x : 0));
        tv_fragmentdetalle_thunder_ay.setText(""+(sw_fragmentdetalle_thunder_ay.isChecked() ? dispo.Acelereation_y : 0));
        tv_fragmentdetalle_thunder_az.setText(""+(sw_fragmentdetalle_thunder_az.isChecked() ? dispo.Acelereation_z : 0));


        try{
            Drawable drawOn = getActivity().getDrawable(R.drawable.ic_lights_on_red);
            Drawable drawOff = getActivity().getDrawable(R.drawable.ic_lights_off);
            tv_fragmentdetalle_thunder_sw0.setBackground(dispo.sw0 == 0 ? drawOff : drawOn);
            tv_fragmentdetalle_thunder_sw1.setBackground(dispo.sw1 == 0 ? drawOff : drawOn);
        } catch (Exception ex) {
            Log.e("FragmentThunder", "Error -> " + ex.getMessage());
        }

    }

    @Override
    protected void onToogleControles(Boolean opcion) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGattClient.onDestroy();
        mSTimer.stop();
    }

    /* Esto se llama una vez por segundo en la UI thread */
    STimer.OnAlarmListener OnPuckTick = new STimer.OnAlarmListener()
    {
        @Override
        public void OnAlarm( STimer source )
        {
            mGattClient.requestDataThunderBoard();
            sendData();
            new checkAndSendRules().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mDispoThunderBoard);
        }
    };

    private void sendData() {
        ValuesTago[] values = {
                new ValuesTago("Temperatura", ""    +( sw_fragmentdetalle_thunder_temperatura.isChecked() ? mDispoThunderBoard.Temperature : 0)),
                new ValuesTago("Humidity", ""       +( sw_fragmentdetalle_thunder_humedad.isChecked() ? mDispoThunderBoard.Humidity : 0)),
                new ValuesTago("AmbientLight", ""   +( sw_fragmentdetalle_thunder_lux.isChecked() ? mDispoThunderBoard.AmbientLight : 0)),
                new ValuesTago("UV_Index", ""       +( sw_fragmentdetalle_thunder_uv.isChecked() ? mDispoThunderBoard.UV_Index : 0)),
                new ValuesTago("BatteryLevel", ""   +( sw_fragmentdetalle_thunder_voltaje.isChecked() ? mDispoThunderBoard.batteryLevel : 0)),
                new ValuesTago("Orientation_x", ""  +( sw_fragmentdetalle_thunder_ox.isChecked() ? mDispoThunderBoard.Orientation_x : 0)),
                new ValuesTago("Orientation_y", ""  +( sw_fragmentdetalle_thunder_oy.isChecked() ? mDispoThunderBoard.Orientation_y : 0)),
                new ValuesTago("Orientation_z", ""  +( sw_fragmentdetalle_thunder_oz.isChecked() ? mDispoThunderBoard.Orientation_z : 0)),
                new ValuesTago("Acceleration_x", ""  +( sw_fragmentdetalle_thunder_ax.isChecked() ? mDispoThunderBoard.Acelereation_x : 0)),
                new ValuesTago("Acceleration_y", ""  +( sw_fragmentdetalle_thunder_ay.isChecked() ? mDispoThunderBoard.Acelereation_y : 0)),
                new ValuesTago("Acceleration_z", ""  +( sw_fragmentdetalle_thunder_az.isChecked() ? mDispoThunderBoard.Acelereation_z : 0)),
                new ValuesTago("SW0", ""  + mDispoThunderBoard.sw0),
                new ValuesTago("SW1", ""  + mDispoThunderBoard.sw1)

        };

        new EnviarInformacionTago(mDispoThunderBoard.getToken()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,values);

    }

    @Override
    public void onSettingsChanged() {
        if(mSTimer != null)
            mSTimer.setPeriod( STimer.CURRENT_PERIOD );

        //Establecemos de nuevo el callback de mqtt por que al cambiar los settings se reinicia la conexion
        ((MainActivity)getActivity()).StopMQTT();
        ((MainActivity)getActivity()).StartMQTT();
        ((MainActivity)getActivity()).setMqttCalbback(FragmentThunderBoard.this);
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception {

        String message = msg.toString();

        if(topic.equals(Utils.AVAYA_MQTT_TOPIC_BLUE)){
            if(message.equals(Utils.AVAYA__MQTT_PAYLOAD_OFF)){
                tb_fragmentdetalle_thunder_ledblue.setChecked(false);
            } else if (message.equals(Utils.AVAYA__MQTT_PAYLOAD_ON)){
                tb_fragmentdetalle_thunder_ledblue.setChecked(true);
            }
            //tb_fragmentdetalle_thunder_ledblue.setChecked(!tb_fragmentdetalle_thunder_ledblue.isChecked());
        } else if(topic.equals(Utils.AVAYA_MQTT_TOPIC_GREEN)){
            if(message.equals(Utils.AVAYA__MQTT_PAYLOAD_OFF)){
                tb_fragmentdetalle_thunder_ledgreen.setChecked(false);
            } else if (message.equals(Utils.AVAYA__MQTT_PAYLOAD_ON)){
                tb_fragmentdetalle_thunder_ledgreen.setChecked(true);
            }
            //tb_fragmentdetalle_thunder_ledblue.setChecked(!tb_fragmentdetalle_thunder_ledblue.isChecked());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
