package com.example.appsensores.ui.Fragments.vista_sensor;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Clases.GPSGoogleClient;
import com.example.appsensores.Clases.STimer;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.DispoTelefono;
import com.example.appsensores.Models.ValuesTago;
import com.example.appsensores.R;
import com.example.appsensores.ui.Activities.MainActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

import static android.content.Context.SENSOR_SERVICE;



/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDetalleTel extends BaseVistaFargment implements MqttCallbackExtended {

    private SensorManager mSensorManager;
    private DispoTelefono mDispoTelefono;
    //private GPSClient gpsClient;
    private GPSGoogleClient gpsGoogleClient;
    private CameraManager camManager;
    private String cameraId = null;

    private TextView tv_fragmentdetalle_tel_temperatura;
    private TextView tv_fragmentdetalle_tel_humedad;
    private TextView tv_fragmentdetalle_tel_lux;
    private TextView tv_fragmentdetalle_tel_proximity;
    private TextView tv_fragmentdetalle_tel_voltaje;
    private TextView tv_fragmentdetalle_tel_ox;
    private TextView tv_fragmentdetalle_tel_oy;
    private TextView tv_fragmentdetalle_tel_oz;
    private TextView tv_fragmentdetalle_tel_ax;
    private TextView tv_fragmentdetalle_tel_ay;
    private TextView tv_fragmentdetalle_tel_az;
    private TextView tv_fragmentdetalle_tel_lat;
    private TextView tv_fragmentdetalle_tel_lng;

    private Switch sw_fragmentdetalle_tel_temperatura;
    private Switch sw_fragmentdetalle_tel_humedad;
    private Switch sw_fragmentdetalle_tel_lux;
    private Switch sw_fragmentdetalle_tel_proximity;
    private Switch sw_fragmentdetalle_tel_voltaje;
    private Switch sw_fragmentdetalle_tel_ox;
    private Switch sw_fragmentdetalle_tel_oy;
    private Switch sw_fragmentdetalle_tel_oz;
    private Switch sw_fragmentdetalle_tel_ax;
    private Switch sw_fragmentdetalle_tel_ay;
    private Switch sw_fragmentdetalle_tel_az;
    private Switch sw_fragmentdetalle_tel_lat;
    private Switch sw_fragmentdetalle_tel_lng;

    private ToggleButton tb_fragmentdetalle_thunder_flash;

    private STimer mSTimer;

    public FragmentDetalleTel() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDispoTelefono = new DispoTelefono(getContext());
        mDispoTelefono.id = dispositivoBase.id;
        mDispoTelefono.Nombre = dispositivoBase.Nombre;
        mDispoTelefono.MacAddress = dispositivoBase.MacAddress;
        mDispoTelefono.Token = dispositivoBase.Token;
        mDispoTelefono.TipoDispositivo = dispositivoBase.TipoDispositivo;

        // Inflate the layout for this fragment
        mSensorManager = (SensorManager)getContext().getSystemService(SENSOR_SERVICE);

        //Iniciamos el cliente GPS
        //gpsClient = new GPSClient(getContext());
        gpsGoogleClient = new GPSGoogleClient(getContext());
        gpsGoogleClient.startLocationUpdates();

        //Instanciamos la camara
        camManager = (CameraManager)getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = camManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        //Iniciamos el timer
        mSTimer = new STimer();
        mSTimer.setOnAlarmListener( OnPuckTick );
        mSTimer.setPeriod( STimer.CURRENT_PERIOD );

        mSTimer.start();

        return inflater.inflate(R.layout.fragment_fragment_detalle_tel, container, false);
    }

    @Override
    public void onUpdateUI() {
        //obtenemos locacion
        //Location location = gpsClient.getLastLocation();
        Location location = gpsGoogleClient.getCurrentLocaton();
        double lat = location == null ? 0 : location.getLatitude();
        double lng = location == null ? 0 : location.getLongitude();
        //Log.e("FragmentdetalleTel", "GPS (LAT,LNG): " + lat + " ," + lng);
        mDispoTelefono.GetSensorById(SensorTypes.SENSOR_LAT).value = sw_fragmentdetalle_tel_lat.isChecked() ?  lat : 0;
        mDispoTelefono.GetSensorById(SensorTypes.SENSOR_LNG).value = sw_fragmentdetalle_tel_lng.isChecked() ?  lng : 0;

        //obtenemos Temperatura batteria
        mDispoTelefono.GetSensorById(SensorTypes.SENSOR_TEMPERATURA).value = sw_fragmentdetalle_tel_temperatura.isChecked() ? Utils.batteryTemperature(getContext()) : 0.0;

        //obtenemos battery level
        mDispoTelefono.GetSensorById(SensorTypes.SENSOR_VOLTAJE).value = sw_fragmentdetalle_tel_voltaje.isChecked() ? Utils.batteryLevel(getContext()) : 0.0;

        UpdateUI();
    }

    @Override
    public void setListenerForRulesButton(Button button) {
        button.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("idSensor", dispositivoBase.id);

            Navigation.findNavController(getView()).navigate(R.id.action_fragmentDetalleTel_to_fragmentRules,bundle);

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //Nos Suscribimos a los topics, movi este codigo a onresume ya que en OnviewCreated se establecia el callback antes de que se creara la conexion con mqtt, y cuando se creaba planchaba este callback
        ((MainActivity)getActivity()).setMqttCalbback(FragmentDetalleTel.this);
    }

    @Override
    protected void setControles(View view) {

        tv_fragmentdetalle_tel_temperatura =    view.findViewById(R.id.tv_fragmentdetalle_tel_temperatura);
        tv_fragmentdetalle_tel_humedad =        view.findViewById(R.id.tv_fragmentdetalle_tel_humedad);
        tv_fragmentdetalle_tel_lux =            view.findViewById(R.id.tv_fragmentdetalle_tel_lux);
        tv_fragmentdetalle_tel_proximity =      view.findViewById(R.id.tv_fragmentdetalle_tel_proximity);
        tv_fragmentdetalle_tel_voltaje =        view.findViewById(R.id.tv_fragmentdetalle_tel_voltaje);
        tv_fragmentdetalle_tel_ox =             view.findViewById(R.id.tv_fragmentdetalle_tel_ox);
        tv_fragmentdetalle_tel_oy =             view.findViewById(R.id.tv_fragmentdetalle_tel_oy);
        tv_fragmentdetalle_tel_oz =             view.findViewById(R.id.tv_fragmentdetalle_tel_oz);
        tv_fragmentdetalle_tel_ax =             view.findViewById(R.id.tv_fragmentdetalle_tel_ax);
        tv_fragmentdetalle_tel_ay =             view.findViewById(R.id.tv_fragmentdetalle_tel_ay);
        tv_fragmentdetalle_tel_az =             view.findViewById(R.id.tv_fragmentdetalle_tel_az);
        tv_fragmentdetalle_tel_lat =            view.findViewById(R.id.tv_fragmentdetalle_tel_lat);
        tv_fragmentdetalle_tel_lng =            view.findViewById(R.id.tv_fragmentdetalle_tel_lng);

        sw_fragmentdetalle_tel_temperatura =    view.findViewById(R.id.sw_fragmentdetalle_tel_temperatura);
        sw_fragmentdetalle_tel_humedad =        view.findViewById(R.id.sw_fragmentdetalle_tel_humedad);
        sw_fragmentdetalle_tel_lux =            view.findViewById(R.id.sw_fragmentdetalle_tel_lux);
        sw_fragmentdetalle_tel_proximity =      view.findViewById(R.id.sw_fragmentdetalle_tel_proximity);
        sw_fragmentdetalle_tel_voltaje=         view.findViewById(R.id.sw_fragmentdetalle_tel_voltaje);
        sw_fragmentdetalle_tel_ox = view.findViewById(R.id.sw_fragmentdetalle_tel_ox);
        sw_fragmentdetalle_tel_oy = view.findViewById(R.id.sw_fragmentdetalle_tel_oy);
        sw_fragmentdetalle_tel_oz = view.findViewById(R.id.sw_fragmentdetalle_tel_oz);
        sw_fragmentdetalle_tel_ax = view.findViewById(R.id.sw_fragmentdetalle_tel_ax);
        sw_fragmentdetalle_tel_ay = view.findViewById(R.id.sw_fragmentdetalle_tel_ay);
        sw_fragmentdetalle_tel_az = view.findViewById(R.id.sw_fragmentdetalle_tel_az);
        sw_fragmentdetalle_tel_lat = view.findViewById(R.id.sw_fragmentdetalle_tel_lat);
        sw_fragmentdetalle_tel_lng = view.findViewById(R.id.sw_fragmentdetalle_tel_lng);

        sw_fragmentdetalle_tel_temperatura.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_humedad.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_lux.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_proximity.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_ox.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_oy.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_oz.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_ax.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_ay.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_az.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_lat.setOnCheckedChangeListener(swListener);
        sw_fragmentdetalle_tel_lng.setOnCheckedChangeListener(swListener);

        tb_fragmentdetalle_thunder_flash = view.findViewById(R.id.tb_fragmentdetalle_thunder_flash);
        tb_fragmentdetalle_thunder_flash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(camManager != null){
                    try {
                        camManager.setTorchMode(cameraId, isChecked);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //Agregamos los switches en la clase padre para que sean afectados con la accion del switch general
        addSwitchToList(sw_fragmentdetalle_tel_temperatura);
        addSwitchToList(sw_fragmentdetalle_tel_humedad);
        addSwitchToList(sw_fragmentdetalle_tel_lux);
        addSwitchToList(sw_fragmentdetalle_tel_proximity);
        addSwitchToList(sw_fragmentdetalle_tel_voltaje);
        addSwitchToList(sw_fragmentdetalle_tel_ox);
        addSwitchToList(sw_fragmentdetalle_tel_oy);
        addSwitchToList(sw_fragmentdetalle_tel_oz);
        addSwitchToList(sw_fragmentdetalle_tel_ax);
        addSwitchToList(sw_fragmentdetalle_tel_ay);
        addSwitchToList(sw_fragmentdetalle_tel_az);
        addSwitchToList(sw_fragmentdetalle_tel_lat);
        addSwitchToList(sw_fragmentdetalle_tel_lng);

        List<Sensor> listaSensores = mSensorManager.getSensorList(Sensor.TYPE_ALL);


        for(Sensor sensor: listaSensores) {
            mSensorManager.registerListener(mSensorListener,sensor, 1000000);
            if(sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) sw_fragmentdetalle_tel_temperatura.setEnabled(true); //Serie de ifs para verificar si los sensores requeridos para las reglas se encuentran disponibles
            else if(sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) sw_fragmentdetalle_tel_humedad.setEnabled(true);
            else if(sensor.getType() == Sensor.TYPE_LIGHT) sw_fragmentdetalle_tel_lux.setEnabled(true);
            else if(sensor.getType() == Sensor.TYPE_PROXIMITY) sw_fragmentdetalle_tel_proximity.setEnabled(true);
            Log.e("FragmentDetalleTel", "available sensor: " + sensor.getName() + " type " + sensor.getType());
        }

        sw_fragmentdetalle_tel_temperatura.setEnabled(true); //habilito el sensor de temperatura por que ya no lo tomo del ambiental si no de la bateria
        sw_fragmnetvista_gral.setChecked(true); //Habilito el switch general para que los valores sean visibles al inicio
        mSTimer.sendTick(); //Justo despues mando el primer tick para llenar info
    }

    @Override
    protected void onToogleControles(Boolean opcion) {

    }

    @Override
    public void onSettingsChanged() {
        if(mSTimer != null)
            mSTimer.setPeriod( STimer.CURRENT_PERIOD );
        //Establecemos de nuevo el callback de mqtt por que al cambiar los settings se reinicia la conexion
        try{
            ((MainActivity)getActivity()).StopMQTT();
            ((MainActivity)getActivity()).StartMQTT();
            ((MainActivity)getActivity()).setMqttCalbback(FragmentDetalleTel.this);
        } catch (Exception e){

        }
    }

    // Create listener
    SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // More code goes here

            //Log.e("FragmentTel", "clase que lanza el evento sensor: " + sensorEvent.sensor.getName()+ " tipo: " + sensorEvent.sensor.getType());
            switch (sensorEvent.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ACCELERATION_X).value = sw_fragmentdetalle_tel_ax.isChecked() ? sensorEvent.values[0] : 0.0;
                    mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ACCELERATION_Y).value = sw_fragmentdetalle_tel_ax.isChecked() ? sensorEvent.values[1] : 0.0;
                    mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ACCELERATION_Z).value = sw_fragmentdetalle_tel_ax.isChecked() ? sensorEvent.values[2] : 0.0;
                    UpdateUI();
                    break;
                case Sensor.TYPE_GYROSCOPE :
                    mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ORIENTATION_X).value = sw_fragmentdetalle_tel_ox.isChecked() ? sensorEvent.values[0] : 0.0;
                    mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ORIENTATION_Y).value = sw_fragmentdetalle_tel_oy.isChecked() ? sensorEvent.values[1] : 0.0;
                    mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ORIENTATION_Z).value = sw_fragmentdetalle_tel_oz.isChecked() ? sensorEvent.values[2] : 0.0;
                    UpdateUI();
                    break;
                case Sensor.TYPE_LIGHT :
                    mDispoTelefono.GetSensorById(SensorTypes.SENSOR_LUZ).value = sw_fragmentdetalle_tel_lux.isChecked() ? (int)sensorEvent.values[0] : 0.0;
                    UpdateUI();
                    break;
                /*case Sensor.TYPE_AMBIENT_TEMPERATURE : //LO COMENTE POR QUE SON POCOS LOS TELEFONO SON SENSOR DE TEMPERATURA AMBIENTE, LO CAMBIE POR LA BATERIA DEL TELEFONO EN EL TICK DEL TIMMER
                    mDispoTelefono.Temperature = sw_fragmentdetalle_tel_temperatura.isChecked() ? sensorEvent.values[0] : 0;
                    UpdateUI();
                    break;*/
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    mDispoTelefono.GetSensorById(SensorTypes.SENSOR_HUMEDAD).value = sw_fragmentdetalle_tel_humedad.isChecked() ? sensorEvent.values[0] : 0.0;
                    UpdateUI();
                    break;
                case Sensor.TYPE_PROXIMITY:
                    mDispoTelefono.GetSensorById(SensorTypes.SENSOR_PROXIMITY).value = sw_fragmentdetalle_tel_proximity.isChecked() ? sensorEvent.values[0] : 0.0;
                    UpdateUI();
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    private void UpdateUI() {
        tv_fragmentdetalle_tel_temperatura.setText(String.format("%.2f",mDispoTelefono.GetSensorById(SensorTypes.SENSOR_TEMPERATURA).value) + "°C");
        tv_fragmentdetalle_tel_humedad.setText(String.format("%.2f",mDispoTelefono.GetSensorById(SensorTypes.SENSOR_HUMEDAD).value)+"%");
        tv_fragmentdetalle_tel_lux.setText( mDispoTelefono.GetSensorById(SensorTypes.SENSOR_LUZ).value + " lux");
        tv_fragmentdetalle_tel_proximity.setText(String.format("%.2f",mDispoTelefono.GetSensorById(SensorTypes.SENSOR_PROXIMITY).value));
        tv_fragmentdetalle_tel_voltaje.setText(""+mDispoTelefono.GetSensorById(SensorTypes.SENSOR_VOLTAJE).value+"%");
        tv_fragmentdetalle_tel_ox.setText(String.format("%.2f",mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ORIENTATION_X).value));
        tv_fragmentdetalle_tel_oy.setText(String.format("%.2f",mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ORIENTATION_Y).value));
        tv_fragmentdetalle_tel_oz.setText(String.format("%.2f",mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ORIENTATION_Z).value));
        tv_fragmentdetalle_tel_ax.setText(String.format("%.2f",mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ACCELERATION_X).value));
        tv_fragmentdetalle_tel_ay.setText(String.format("%.2f",mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ACCELERATION_Y).value));
        tv_fragmentdetalle_tel_az.setText(String.format("%.2f",mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ACCELERATION_Z).value));
        tv_fragmentdetalle_tel_lat.setText(""+mDispoTelefono.GetSensorById(SensorTypes.SENSOR_LAT).value);
        tv_fragmentdetalle_tel_lng.setText(""+mDispoTelefono.GetSensorById(SensorTypes.SENSOR_LNG).value);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("FragmentDetalleTel", "ONDESTROYVIEW");
        mSensorManager.unregisterListener(mSensorListener);
        gpsGoogleClient.stopLocationUpdates();
        mSTimer.stop();
        if(camManager != null){ //Apagamos el flash de la camara si se quedo prendida
            try {
                camManager.setTorchMode(cameraId, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /* Esto se llama una vez por segundo en la UI thread */
    STimer.OnAlarmListener OnPuckTick = new STimer.OnAlarmListener()
    {
        @Override
        public void OnAlarm( STimer source )
        {
            sendData();
            new checkAndSendRules().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mDispoTelefono);
        }
    };

    private void sendData() {
        ValuesTago[] values = {
                new ValuesTago("Temperatura", ""    + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_TEMPERATURA).value),
                new ValuesTago("Humidity", ""       + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_HUMEDAD).value),
                new ValuesTago("AmbientLight", ""   + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_LUZ).value),
                new ValuesTago("Proximity", ""      + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_PROXIMITY).value),
                new ValuesTago("Battery", ""        + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_VOLTAJE).value),
                new ValuesTago("Orientation_x", ""  + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ORIENTATION_X).value),
                new ValuesTago("Orientation_y", ""  + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ORIENTATION_Y).value),
                new ValuesTago("Orientation_z", ""  + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ORIENTATION_Z).value),
                new ValuesTago("Acceleration_x", "" + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ACCELERATION_X).value),
                new ValuesTago("Acceleration_y", "" + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ACCELERATION_Y).value),
                new ValuesTago("Acceleration_z", "" + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_ACCELERATION_Z).value),
                new ValuesTago("lat", "" + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_LAT).value),
                new ValuesTago("lng", "" + mDispoTelefono.GetSensorById(SensorTypes.SENSOR_LNG).value)

        };

        new EnviarInformacionTago(mDispoTelefono.Token).execute(values);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e("FragmentDetalleTel", "Coneccion with broker lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if(topic.equals(Utils.AVAYA_MQTT_TOPIC_FLASH)){
            if(message.toString().equals(Utils.AVAYA__MQTT_PAYLOAD_OFF)){
                tb_fragmentdetalle_thunder_flash.setChecked(false);
            } else if (message.toString().equals(Utils.AVAYA__MQTT_PAYLOAD_ON)){
                tb_fragmentdetalle_thunder_flash.setChecked(true);
            }
            //tb_fragmentdetalle_thunder_ledblue.setChecked(!tb_fragmentdetalle_thunder_ledblue.isChecked());
        }

        //Toast.makeText(getContext(),"MQTT\n topic: " + topic + "\npayload: " + message.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.e("FragmentDetalleTel", "Coneccion with broker complete");
        ((MainActivity)getActivity()).suscribeTopics();
    }
}
