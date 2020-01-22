package com.example.appsensores.ui.Fragments.vista_sensor;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.appsensores.Clases.Enums.EnumTipoDispo;
import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Dispositivos.DispoSensorPuck;
import com.example.appsensores.Models.Dispositivos.DispoTelefono;
import com.example.appsensores.Models.Dispositivos.DispoThunderBoard;
import com.example.appsensores.Models.Parametros;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.Models.ValuesTago;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.example.appsensores.WebMethods.WebMethods;
import com.example.appsensores.ui.Dialogs.DialogSettings;
import com.google.gson.Gson;

import java.util.ArrayList;


public abstract class BaseVistaFargment extends Fragment implements DialogSettings.IsettinsListener {

    protected BaseDispositivo dispositivoBase;

    /***
     * Dialog para mostrar en las conexiones
     */
    protected AlertDialog dialogCargando;

    private TextView tv_fragmentvista_nombre;
    private Switch sw_fragmnetvista_gral;

    private TextView tv_fragment_base_dispo_tipo;
    private TextView tv_fragment_base_dispo_mac;
    private TextView tv_fragment_base_dispo_token;

    private Button btn_fragmentvista_rules;

    private ArrayList<Switch> listSwitches = new ArrayList<>();

    /***
     * Bandera para diferenciar entre set del switch por usuario o por codigo
     */
    private boolean PROGRAMATICALLY_SET_CHEQUED = false;
    /***
     * Bandera para diferenciar entre set del switch general por usuario o por codigo
     */
    private boolean PROGRAMATICALLY_SET_CHEQUED_GRAL = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("baseVistaFragent", "Paso por Oncreate");
        int idDispositivo = getArguments().getInt("idSensor");
        dispositivoBase = RepositorioDBGeneralSingleton.getInstance(getContext()).getDeviceById(idDispositivo);
        DialogSettings.setOnSettingsChangedListener(this);
    }

    @Nullable
    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_fragmentvista_nombre = view.findViewById(R.id.tv_fragmentvista_nombre);
        sw_fragmnetvista_gral = view.findViewById(R.id.sw_fragmnetvista_gral);

        sw_fragmnetvista_gral.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(PROGRAMATICALLY_SET_CHEQUED_GRAL){
                    PROGRAMATICALLY_SET_CHEQUED_GRAL = false;
                } else {
                    onToogleControles(isChecked);
                    toogleControles(isChecked);
                }
            }
        });

        tv_fragment_base_dispo_tipo = view.findViewById(R.id.tv_fragment_base_dispo_tipo);
        tv_fragment_base_dispo_mac = view.findViewById(R.id.tv_fragment_base_dispo_mac);
        tv_fragment_base_dispo_token = view.findViewById(R.id.tv_fragment_base_dispo_token);
        btn_fragmentvista_rules = view.findViewById(R.id.btn_fragmentvista_rules);

        tv_fragmentvista_nombre.setText(dispositivoBase.getNombre());
        tv_fragment_base_dispo_tipo.setText((EnumTipoDispo.values()[dispositivoBase.getTipoDispositivo()]).toString());
        tv_fragment_base_dispo_mac.setText(dispositivoBase.getMacAddress());
        tv_fragment_base_dispo_token.setText(dispositivoBase.getToken());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_loading);
        dialogCargando = builder.create();
        setControles(view);
        setListenerForRulesButton(btn_fragmentvista_rules);
    }

    /**
     * Metodo abstracto para que las clases que heredan establezcan el id de navegacion hacia el fragment rules
     * @param button
     */
    public abstract void setListenerForRulesButton(Button button);

    /***
     * Aqui se deben inicializar los controles propios de la clase que hereda de BaseVistaFragment
     * @param view View general del fragment.
     */
    protected abstract void setControles(View view);

    /***
     * Aqui se debe implementar la logica para apagar o encender los sensores del dispositio actual
     * @param opcion
     */
    protected abstract void onToogleControles(Boolean opcion);

    /***
     * Metodo para cambiar el estado de los switch de la UI dependiendo del estado del switch general
     * @param isChecked
     */
    private void toogleControles(boolean isChecked) {
        for (Switch unit : listSwitches ) {
            PROGRAMATICALLY_SET_CHEQUED = true;
            unit.setChecked(isChecked);
        }
    }

    /***
     * Metodo para checar el estado de todos los SW, si alguno esta en ON, el sw general se pone en ON, si no se pone en OFF
     */
    private void checkSwitches(){
        if(PROGRAMATICALLY_SET_CHEQUED)
            PROGRAMATICALLY_SET_CHEQUED = false;
        else {
            for (Switch unit : listSwitches ) {

                if(unit.isChecked()){
                    //si ya esta activo no tenemos que activarlo de nuevo
                    //if(!sw_fragmnetvista_gral.isChecked()) {
                    PROGRAMATICALLY_SET_CHEQUED_GRAL = true;
                    sw_fragmnetvista_gral.setChecked(true);
                    //}
                    return;
                }

            }
            PROGRAMATICALLY_SET_CHEQUED_GRAL = true;
            sw_fragmnetvista_gral.setChecked(false);
        }
    }

    /***
     * Listener para todos los switches de la UI, es asi por que todos checan el estatus del SW general.
     */
    protected CompoundButton.OnCheckedChangeListener swListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            checkSwitches();
        }
    };

    /***
     * Async TASK para mandar informacion a Tago.io
     */
    protected class EnviarInformacionTago extends AsyncTask<ValuesTago[],Void,String >{

        String token = "";

        public EnviarInformacionTago(String token){
            this.token = token;
        }

        @Override
        protected String doInBackground(ValuesTago[]... lists) {
            String json = new Gson().toJson(lists[0]);
            //String resp = WebMethods.getStringPOSTmethodTago(WebMethods.IP_SERVER, " 8e8d61d2-a77c-4313-9472-a5492674939a",json);
            String resp = WebMethods.getStringPOSTmethodTago(WebMethods.IP_SERVER, token,json);
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("BaseVista", "Respuesta del envio a tago: " + s);
        }
    }

    protected class checkAndSendRules extends AsyncTask <BaseDispositivo, Void, String>{

        /**
         * Array booleano de los status de los switch de sensores, asi evitamos mandar una alerta de un sensor desactivado
         */
        private boolean[] switchStatus = {false, false, false, false};

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Leemos el status de los switch en esta variable local por que no podemos acceder directo a la UI desde InBackGround
            for(int i = 0; i < 4; i++){
                switchStatus[i] = listSwitches.get(i).isChecked();
            }
        }

        @Override
        protected String doInBackground(BaseDispositivo... baseDispositivos) {
            int resp = -1;
            ArrayList<Rule> rules = RepositorioDBGeneralSingleton.getInstance(getContext()).getRulesByDispositivo(baseDispositivos[0].getId());
            float[] values = {0,0,0,0};

            String message, sensortype, ruletype, valor1, valor2, valor;

            //Filtro para saber de que clase se llamo el asynctask
            if( DispoTelefono.class.isInstance(baseDispositivos[0]) ){
                values[0] = ((DispoTelefono)baseDispositivos[0]).Temperature;
                values[1] = ((DispoTelefono)baseDispositivos[0]).Humidity;
                values[2] = ((DispoTelefono)baseDispositivos[0]).AmbientLight;
                switchStatus[3] = false; //hardcode el switch de UV para el telefono, ya que no tiene el sensor
            } else if (DispoThunderBoard.class.isInstance(baseDispositivos[0])){
                values[0] = ((DispoThunderBoard)baseDispositivos[0]).Temperature;
                values[1] = ((DispoThunderBoard)baseDispositivos[0]).Humidity;
                values[2] = ((DispoThunderBoard)baseDispositivos[0]).AmbientLight;
                values[3] = ((DispoThunderBoard)baseDispositivos[0]).UV_Index;
            } else if (DispoSensorPuck.class.isInstance(baseDispositivos[0])){
                values[0] = ((DispoSensorPuck)baseDispositivos[0]).Temperature;
                values[1] = ((DispoSensorPuck)baseDispositivos[0]).Humidity;
                values[2] = ((DispoSensorPuck)baseDispositivos[0]).AmbientLight;
                values[3] = ((DispoSensorPuck)baseDispositivos[0]).UV_Index;
            } else {
                return ""+resp;
            }

            //Iteramos sobre las reglas y checamos si alguna se cimple para mandar la alerta
            for (Rule unit : rules) {
                 if(unit.RuleId == SensorTypes.MAYOR){
                    if(values[unit.SensorId] > unit.Value1){
                        if(switchStatus[unit.SensorId]) {
                            sensortype = SensorTypes.getSensorAmbientList(getContext())[unit.SensorId];
                            ruletype = SensorTypes.getRuleTypes(getContext())[unit.RuleId];
                            valor1 = String.format("%.2f", unit.Value1);
                            valor = String.format("%.2f", values[unit.SensorId]);
                            message = "Alert -> " + sensortype + " " + valor + " " + ruletype + " " + valor1;
                            resp = sendNotification(message);
                        }
                    }
                 } else if(unit.RuleId == SensorTypes.MENOR){
                     if(values[unit.SensorId] < unit.Value1){
                         if(switchStatus[unit.SensorId]) {
                             sensortype = SensorTypes.getSensorAmbientList(getContext())[unit.SensorId];
                             ruletype = SensorTypes.getRuleTypes(getContext())[unit.RuleId];
                             valor1 = String.format("%.2f", unit.Value1);
                             valor = String.format("%.2f", values[unit.SensorId]);
                             message = "Alert -> " + sensortype + " " + valor + " " + ruletype + " " + valor1;
                             resp = sendNotification(message);
                         }
                     }
                 } if(unit.RuleId == SensorTypes.ENTRE){
                    if(values[unit.SensorId] > unit.Value1 && values[unit.SensorId] < unit.Value2){
                        if(switchStatus[unit.SensorId]) {
                            sensortype = SensorTypes.getSensorAmbientList(getContext())[unit.SensorId];
                            ruletype = SensorTypes.getRuleTypes(getContext())[unit.RuleId];
                            valor1 = String.format("%.2f", unit.Value1);
                            valor2 = String.format("%.2f", unit.Value2);
                            valor = String.format("%.2f", values[unit.SensorId]);
                            message = "Alert -> " + sensortype + " " + valor + " " + ruletype + " " + valor1 + " - " + valor2;
                            resp = sendNotification(message);
                        }
                    }
                }
            }

            return ""+resp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("checkAndSendRules", "postExecute response -> " + s);
        }
    }

    /**
     * Metodo para mandar la alerta al endPoint de Avaya
     * @param message
     * @return
     */
    private int sendNotification(String message) {
        SharedPreferences sharedPreferencesAvaya = getContext().getSharedPreferences(Utils.AVAYA_SHARED_PREFERENCES,0);

        String _mail = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_MAIL,"");
        String _family = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_FAMILY,"");
        String _type = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_TYPE,"");
        String _version = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_VERSION,"");
        String _url = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_URL,"");

        Parametros params = new Parametros();
        params.setCorreoElectronico(_mail);
        params.setParam1(message);

        return WebMethods.requestPostMethodAvayaEndpoint(params, _url, _family, _type, _version);
    }

    /***
     * Metodo para agregar Switches que se veran afectados por el Swtich del encabezado general
     * @param sw
     */
    protected void addSwitchToList(Switch sw){
        this.listSwitches.add(sw);
    }
}
