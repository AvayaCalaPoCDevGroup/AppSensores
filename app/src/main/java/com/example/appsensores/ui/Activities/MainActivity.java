package com.example.appsensores.ui.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.appsensores.BuildConfig;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.R;
import com.example.appsensores.ui.Dialogs.DialogAddRule;
import com.example.appsensores.ui.Dialogs.DialogRuleSettings;
import com.example.appsensores.ui.Dialogs.DialogSettings;
import com.example.appsensores.ui.Fragments.acercade.AcercaDeFragment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Properties;

public class MainActivity extends AppCompatActivity implements AcercaDeFragment.OnFragmentInteractionListener {

    private AppBarConfiguration mAppBarConfiguration;

    String[] topic = {"home/avaya/thunder", "home/avaya/thunderg"};
    int[] qos = {1,1};

    public interface IScanListener {
        void onScanResult(String msg);
    }
    private IScanListener mIScanListener;

    protected MqttAndroidClient client;
    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e("MainActivity", "Mensage arrivado a callback de onactivity : " + topic);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_acercade)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        ((TextView)(navigationView.getHeaderView(0).findViewById(R.id.tv_navheader_version))).setText("Ver: " + BuildConfig.VERSION_NAME);

        CheckPermissions();
        //StartMQTT();
        //client.setCallback(mqttCallback);
    }

    public void setMqttCalbback(MqttCallback l){
        client.setCallback(l);
    }

    public void StartMQTT() {

        SharedPreferences sharedPreferences = getSharedPreferences(Utils.AVAYA_SHARED_PREFERENCES,0);
        String PassWord = sharedPreferences.getString(Utils.AVAYA_SHARED_BORKERTOKEN, "ecfd35e5-2f4b-4e8a-bc62-8ee3b10d5d1f");

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://mqtt.tago.io:1883", clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        String password = PassWord;
        options.setUserName("token");
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //Toast.makeText(getApplicationContext(), "Connected with tago broker", Toast.LENGTH_SHORT).show();
                    Log.e("MainActivity", "SartMQTT Succes");
                    suscribeTopics();
                    //client.setCallback(mqttCallback);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.e("MainActivity", "SartMQTT Fail -> " + exception.getCause());
                    Toast.makeText(getApplicationContext(), "Fail conection with tago broker", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //Asignamos el callback para los topics
        client.setCallback(mqttCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StartMQTT();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StopMQTT();
    }

    public void StopMQTT() {
        try {
            if(!client.isConnected()) return;

            IMqttToken disconToken = client.disconnect();

            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("MainActivity", "StopMQTT Succes");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.e("MainActivity", "StopMQTT Fail: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo para suscribirse a los topics necesarios para encender los leds de thunderboard
     */
    private void suscribeTopics(){

        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //Toast.makeText(getApplicationContext(), "Suscrito al topic", Toast.LENGTH_SHORT).show();
                    Log.e("MainActivity", "SartMQTT TOPICS Succes");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Toast.makeText(getApplicationContext(), "Error al suscribir", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private void CheckPermissions() {
        chkLocationPermission();
        BluetoothManager Manager = (BluetoothManager)getSystemService( Context.BLUETOOTH_SERVICE );
        BluetoothAdapter Adapter = Manager.getAdapter();
        if(Adapter==null){
            Toast.makeText(this, "Bluetooth is required", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!Adapter.isEnabled() )
        {
            /* Solicitar al usuario encender el Bluetooth */
            Intent EnableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( EnableIntent, 123 );
        }
    }

    private void chkLocationPermission(){
        if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            chkCameraPermission();
        }
    }

    private void chkCameraPermission(){
        if (getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, 2);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, 2);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 123:
                if (resultCode == Activity.RESULT_OK) {

                } else {
                    finish();
                }
                break;
            case 49374:
                if (resultCode == Activity.RESULT_OK) {
                    if(mIScanListener != null){
                        mIScanListener.onScanResult(data.getStringExtra("SCAN_RESULT"));
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        chkCameraPermission();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 2:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                DialogSettings dialogSettings = new DialogSettings(this, MainActivity.this);
                /*dialogSettings.setOnDismissListener(dialog -> {
                    MainActivity.this.StopMQTT();
                    MainActivity.this.StartMQTT();
                });*/
                dialogSettings.show();
                break;
            case R.id.action_rule_settings:
                DialogRuleSettings dialogRuleSettings = new DialogRuleSettings(this);
                dialogRuleSettings.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /***
     * Metodo para establecer el callback que manejara las lecturas de el lector de qrs
     * @param l
     */
    public void setOnScanListener(IScanListener l){
        mIScanListener = l;
    }
}
