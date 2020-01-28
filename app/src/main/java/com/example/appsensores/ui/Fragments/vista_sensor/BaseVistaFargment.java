package com.example.appsensores.ui.Fragments.vista_sensor;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.location.Location;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.appsensores.Clases.Enums.EnumTipoDispo;
import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Dispositivos.DispoSensorPuck;
import com.example.appsensores.Models.Dispositivos.DispoTelefono;
import com.example.appsensores.Models.Dispositivos.DispoThunderBoard;
import com.example.appsensores.Models.LocationTago;
import com.example.appsensores.Models.Parametros;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.Models.ValuesTago;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.example.appsensores.WebMethods.WebMethods;
import com.example.appsensores.ui.Dialogs.DialogSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.journeyapps.barcodescanner.Util;

import java.util.ArrayList;
import java.util.HashMap;

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
    SharedPreferences sharedPreferencesAvaya;

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
        sharedPreferencesAvaya = getContext().getSharedPreferences(Utils.AVAYA_SHARED_PREFERENCES,0);
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
            if(unit.isEnabled()) //Checamos si el sensor esta disponible en el dispositivo, ya que si no existe el sensor el sw no se habilita
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

            LocationTago loc = new LocationTago("location", "myLoc");
            loc.location = new LocationTago.LocationValues();
            loc.location.lat = Double.valueOf(lists[0][lists[0].length-2].value);
            loc.location.lng = Double.valueOf(lists[0][lists[0].length-1].value);
            String jsonLocation = new Gson().toJson(loc);
            resp = WebMethods.getStringPOSTmethodTago(WebMethods.IP_SERVER, token,jsonLocation);

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
        private boolean[] switchStatus = {false, false, false, false, false};

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Leemos el status de los switch en esta variable local por que no podemos acceder directo a la UI desde InBackGround
            for(int i = 0; i < 5; i++){
                switchStatus[i] = listSwitches.get(i).isChecked();
            }
        }

        @Override
        protected String doInBackground(BaseDispositivo... baseDispositivos) {
            String resp = "-1";
            ArrayList<Rule> rules = RepositorioDBGeneralSingleton.getInstance(getContext()).getRulesByDispositivo(baseDispositivos[0].getId());
            float[] values = {0,0,0,0,0};

            String message, sensorName, sensortype, ruletype, valor1, valor2, valor;

            sensorName = baseDispositivos[0].getNombre();

            //Filtro para saber de que clase se llamo el asynctask
            if( DispoTelefono.class.isInstance(baseDispositivos[0]) ){
                values[0] = ((DispoTelefono)baseDispositivos[0]).Temperature;
                values[1] = ((DispoTelefono)baseDispositivos[0]).Humidity;
                values[2] = ((DispoTelefono)baseDispositivos[0]).AmbientLight;
                switchStatus[3] = false; //hardcode el switch de UV para el telefono, ya que no tiene el sensor
                values[4] = ((DispoTelefono)baseDispositivos[0]).Voltaje;
            } else if (DispoThunderBoard.class.isInstance(baseDispositivos[0])){
                values[0] = ((DispoThunderBoard)baseDispositivos[0]).Temperature;
                values[1] = ((DispoThunderBoard)baseDispositivos[0]).Humidity;
                values[2] = ((DispoThunderBoard)baseDispositivos[0]).AmbientLight;
                values[3] = ((DispoThunderBoard)baseDispositivos[0]).UV_Index;
                values[4] = ((DispoThunderBoard)baseDispositivos[0]).batteryLevel;
            } else if (DispoSensorPuck.class.isInstance(baseDispositivos[0])){
                values[0] = ((DispoSensorPuck)baseDispositivos[0]).Temperature;
                values[1] = ((DispoSensorPuck)baseDispositivos[0]).Humidity;
                values[2] = ((DispoSensorPuck)baseDispositivos[0]).AmbientLight;
                values[3] = ((DispoSensorPuck)baseDispositivos[0]).UV_Index;
                values[4] = ((DispoSensorPuck)baseDispositivos[0]).Battery;
            } else {
                return ""+resp;
            }

            //Iteramos sobre las reglas y checamos si alguna se cimple para mandar la alerta
            for (Rule unit : rules) {
                if(unit.IsEnabled && !checkLastRuleSend(unit.LastDate)) { //Si la regla esta deshabilitada no mandamos la alerta y si ya paso mas de un minuto desde la ultima regla
                    if (unit.RuleId == SensorTypes.MAYOR) {
                        if (values[unit.SensorId] > unit.Value1) {
                            if (switchStatus[unit.SensorId]) { //Verificamos si el switch del sensor esta habilitado
                                sensortype = SensorTypes.getSensorAmbientList(getContext())[unit.SensorId];
                                ruletype = SensorTypes.getRuleTypes(getContext())[unit.RuleId];
                                valor1 = String.format("%.2f", unit.Value1);
                                valor = String.format("%.2f", values[unit.SensorId]);
                                message = "Alert " + sensorName + " -> " + sensortype + " " + valor + " " + ruletype + " " + valor1;
                                resp = sendNotification(message, unit.id);
                            }
                        }
                    } else if (unit.RuleId == SensorTypes.MENOR) {
                        if (values[unit.SensorId] < unit.Value1) {
                            if (switchStatus[unit.SensorId]) { //Verificamos si el switch del sensor esta habilitado
                                sensortype = SensorTypes.getSensorAmbientList(getContext())[unit.SensorId];
                                ruletype = SensorTypes.getRuleTypes(getContext())[unit.RuleId];
                                valor1 = String.format("%.2f", unit.Value1);
                                valor = String.format("%.2f", values[unit.SensorId]);
                                message = "Alert " + sensorName + " -> " + sensortype + " " + valor + " " + ruletype + " " + valor1;
                                resp = sendNotification(message, unit.id);
                            }
                        }
                    }
                    if (unit.RuleId == SensorTypes.ENTRE) {
                        if (values[unit.SensorId] > unit.Value1 && values[unit.SensorId] < unit.Value2) {
                            if (switchStatus[unit.SensorId]) { //Verificamos si el switch del sensor esta habilitado
                                sensortype = SensorTypes.getSensorAmbientList(getContext())[unit.SensorId];
                                ruletype = SensorTypes.getRuleTypes(getContext())[unit.RuleId];
                                valor1 = String.format("%.2f", unit.Value1);
                                valor2 = String.format("%.2f", unit.Value2);
                                valor = String.format("%.2f", values[unit.SensorId]);
                                message = "Alert " + sensorName + " -> " + sensortype + " " + valor + " " + ruletype + " " + valor1 + " - " + valor2;
                                resp = sendNotification(message, unit.id);
                            }
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
     * Metodo para checar si ya paso un minuto despues de la ultima notificacion
     * @param lastDate
     * @return True si aun no pasa el minuto, false si ya
     */
    private boolean checkLastRuleSend(long lastDate){
        long diff = System.currentTimeMillis() - lastDate;
        long secs = diff / 1000;
        //long min = secs/60;
        int minInterval = sharedPreferencesAvaya.getInt(Utils.AVAYA_SHARED_MIN_INTERVAL_BETWEEN_RULES, 60);
        boolean resp = secs < minInterval;

        if(resp){
            Log.e("BaseVistaFragment", "ChekLastRuleSend no se manda la regla por que solo han pasado " + secs + " seg., intervalo min : " + minInterval);
        }

        return resp;
    }

    /**
     * Metodo para mandar la alerta al endPoint de Avaya
     * @param message
     * @return
     */
    private String sendNotification(String message, int idRule) {
        String _mail = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_MAIL,"");
        String _family = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_FAMILY,"");
        String _type = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_TYPE,"");
        String _version = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_VERSION,"");
        String _url = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_URL,"");

        String _from = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_FROM, "");
        String _to = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_TO, "");
        String _zurl = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_ZURL, "");
        String _zurlparam = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_ZURLPARAM, "");

        int endPoint = sharedPreferencesAvaya.getInt(Utils.AVAYA_SHARED_ENPOINT, 0);

        Parametros params = new Parametros();
        params.setCorreoElectronico(_mail);
        params.setParam1(message);

        RepositorioDBGeneralSingleton.getInstance(getContext()).updateLastUpdateRule(System.currentTimeMillis(), idRule); //guardamos la ultima hora de la regla en la base

        if(endPoint == Utils.ENDPOINT_BREEZE){
            return ""+WebMethods.requestPostMethodAvayaEndpoint(params, _url, _family, _type, _version);
        } else if (endPoint == Utils.ENDPOINT_ZANG){
            HashMap<String, String> parametros = new HashMap<>();
            parametros.put("From", _from);
            parametros.put("To", _to);
            parametros.put("Url", _zurlparam);
            //return ""+WebMethods.getStringPOSTmethodZang(_zurl, parametros, "ACbf889084ad63b77ddf614ddda88d2aa9","85af708098464422a6f70d3a36b2abb9");
            return  ""+WebMethods.postDataZang();
        } else {
            return "Invalid endpoint";
        }
    }

    /***
     * Metodo para agregar Switches que se veran afectados por el Swtich del encabezado general
     * @param sw
     */
    protected void addSwitchToList(Switch sw){
        this.listSwitches.add(sw);
    }
}
