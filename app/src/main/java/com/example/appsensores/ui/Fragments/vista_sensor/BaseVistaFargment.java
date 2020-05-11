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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.appsensores.Clases.Enums.EnumTipoDispo;
import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Clases.STimer;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Dispositivos.DispoSensorPuck;
import com.example.appsensores.Models.Dispositivos.DispoTelefono;
import com.example.appsensores.Models.Dispositivos.DispoThunderBoard;
import com.example.appsensores.Models.LocationTago;
import com.example.appsensores.Models.Parametros;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.Models.SensorEndpointMapping;
import com.example.appsensores.Models.ValuesTago;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.example.appsensores.WebMethods.WebMethods;
import com.example.appsensores.ui.Dialogs.DialogSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.journeyapps.barcodescanner.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public abstract class BaseVistaFargment extends Fragment implements DialogSettings.IsettinsListener {

    protected BaseDispositivo dispositivoBase;

    /***
     * Dialog para mostrar en las conexiones
     */
    protected AlertDialog dialogCargando;

    private TextView tv_fragmentvista_nombre;
    protected Switch sw_fragmnetvista_gral;

    private TextView tv_fragment_base_dispo_tipo;
    private TextView tv_fragment_base_dispo_mac;
    private TextView tv_fragment_base_dispo_token;

    private Button btn_fragmentvista_rules;

    private ArrayList<Switch> listSwitches = new ArrayList<>();
    SharedPreferences sharedPreferencesAvaya;

    protected STimer timmerUI;

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
    public void onDestroy() {
        super.onDestroy();
        timmerUI.stop();
    }

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

        tv_fragmentvista_nombre.setText(dispositivoBase.Nombre);
        tv_fragment_base_dispo_tipo.setText((EnumTipoDispo.values()[dispositivoBase.TipoDispositivo]).toString());
        tv_fragment_base_dispo_mac.setText(dispositivoBase.MacAddress);
        tv_fragment_base_dispo_token.setText(dispositivoBase.Token);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_loading);
        dialogCargando = builder.create();
        listSwitches.clear(); //Limpio por si acaso antes de set views
        setControles(view);
        setListenerForRulesButton(btn_fragmentvista_rules);

        onUpdateUI(); //primer update justo al crear
        timmerUI = new STimer();
        timmerUI.setPeriod(2500);
        timmerUI.setOnAlarmListener(source -> {
            //Log.e("BaseVistaFragment", "update ui...");
            onUpdateUI();
        });
        timmerUI.start();
    }

    /**
     * Metodo que se ejecutara cada tick del timmer, las clases que heredan deben actualizar su UI aqui.
     */
    public abstract void onUpdateUI();

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
        private boolean[] switchStatus =    { //hasta hoy 2020-05-06 el maximo de switch es de 16, por los que este array de 25 es suficiente para hacer una copia temporal
                true, true, true, true, true, //si agrego un dispositivo de mas de 25 sensores tengo que umentar este array
                true, true, true, true, true,
                true, true, true, true, true,
                true, true, true, true, true,
                true, true, true, true, true};

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Leemos el status de los switch en esta variable local por que no podemos acceder directo a la UI desde InBackGround
            for(int i = 0; i < listSwitches.size(); i++){
                switchStatus[i] = listSwitches.get(i).isChecked();
            }
        }

        @Override
        protected String doInBackground(BaseDispositivo... baseDispositivos) {
            String resp = "-1";
            ArrayList<Rule> rules = RepositorioDBGeneralSingleton.getInstance(getContext()).getRulesByDispositivo(baseDispositivos[0].id);
            float[] values = {0,0,0,0,0};

            String message, sensorName, sensortype, ruletype, valor1, valor2, valor;

            sensorName = baseDispositivos[0].Nombre;

            //Filtro para saber de que clase se llamo el asynctask
            if( DispoTelefono.class.isInstance(baseDispositivos[0]) ){
                switchStatus[3] = false; //hardcode el switch de UV para el telefono, ya que no tiene el sensor y e este lugar esta el de proximidad
            }

            values[0] = baseDispositivos[0].Temperature;
            values[1] = baseDispositivos[0].Humidity;
            values[2] = baseDispositivos[0].AmbientLight;
            values[3] = baseDispositivos[0].UV_Index;
            values[4] = baseDispositivos[0].Battery;

            //Iteramos sobre las reglas y checamos si alguna se cimple para mandar la alerta
            for (Rule unit : rules) {
                if(unit.IsEnabled && !checkLastRuleSend(unit.LastDate)) { //Si la regla esta deshabilitada no mandamos la alerta y si ya paso mas de un minuto desde la ultima regla
                    if (unit.RuleId == SensorTypes.MAYOR) {
                        if (baseDispositivos[0].GetSensorById(unit.SensorId).value > unit.Value1) {
                        //if (values[unit.SensorId] > unit.Value1) {
                            if (switchStatus[baseDispositivos[0].GetSensorIndexById(unit.SensorId)]) { //Verificamos si el switch del sensor esta habilitado
                                sensortype = SensorTypes.getSensorAmbientList(getContext())[unit.SensorId];
                                ruletype = SensorTypes.getRuleTypes(getContext())[unit.RuleId];
                                valor1 = String.format("%.2f", unit.Value1);
                                valor = String.format("%.2f", baseDispositivos[0].GetSensorById(unit.SensorId).value);
                                message = "Alert " + sensorName + " -> " + sensortype + " " + valor + " " + ruletype + " " + valor1;
                                resp = sendNotification(message, unit, baseDispositivos[0]);
                            }
                        }
                    } else if (unit.RuleId == SensorTypes.MENOR) {
                        if (switchStatus[baseDispositivos[0].GetSensorIndexById(unit.SensorId)]) { //Verificamos si el switch del sensor esta habilitado
                            if (switchStatus[unit.SensorId]) { //Verificamos si el switch del sensor esta habilitado
                                sensortype = SensorTypes.getSensorAmbientList(getContext())[unit.SensorId];
                                ruletype = SensorTypes.getRuleTypes(getContext())[unit.RuleId];
                                valor1 = String.format("%.2f", unit.Value1);
                                valor = String.format("%.2f", baseDispositivos[0].GetSensorById(unit.SensorId).value);
                                message = "Alert " + sensorName + " -> " + sensortype + " " + valor + " " + ruletype + " " + valor1;
                                resp = sendNotification(message, unit, baseDispositivos[0]);
                            }
                        }
                    }
                    if (unit.RuleId == SensorTypes.ENTRE) {
                        if (baseDispositivos[0].GetSensorById(unit.SensorId).value > unit.Value1 && values[unit.SensorId] < unit.Value2) {
                            if (switchStatus[baseDispositivos[0].GetSensorIndexById(unit.SensorId)]) { //Verificamos si el switch del sensor esta habilitado
                                sensortype = SensorTypes.getSensorAmbientList(getContext())[unit.SensorId];
                                ruletype = SensorTypes.getRuleTypes(getContext())[unit.RuleId];
                                valor1 = String.format("%.2f", unit.Value1);
                                valor2 = String.format("%.2f", unit.Value2);
                                valor = String.format("%.2f", baseDispositivos[0].GetSensorById(unit.SensorId).value);
                                message = "Alert " + sensorName + " -> " + sensortype + " " + valor + " " + ruletype + " " + valor1 + " - " + valor2;
                                resp = sendNotification(message, unit, baseDispositivos[0]);
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
        int minInterval = sharedPreferencesAvaya.getInt(Utils.AVAYA_SHARED_MIN_INTERVAL_BETWEEN_RULES, 180);
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
    private String sendNotification(String message, Rule rule, BaseDispositivo baseDispositivo) {
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

        RepositorioDBGeneralSingleton.getInstance(getContext()).updateLastUpdateRule(System.currentTimeMillis(), rule.id); //guardamos la ultima hora de la regla en la base

        //Verificamos si se enviara por breeze o por zang
        if(rule.EndpointId == Utils.ENDPOINT_BREEZE){

            String jsonEndPoint = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_JSON,"{}");

            JsonObject jsonObject = new Gson().fromJson(jsonEndPoint,JsonObject.class);
            SensorEndpointMapping[] sepmList = new Gson().fromJson(rule.jsonParams,SensorEndpointMapping[].class);
            for (SensorEndpointMapping sepm : sepmList ) {
                if(sepm.idSensor == SensorTypes.SENSOR_MESAGE){
                    jsonObject.addProperty(sepm.map, message);
                } else {
                    jsonObject.addProperty(sepm.map, baseDispositivo.GetSensorById(sepm.idSensor).value);
                }
            }

            //jsonObject.addProperty("phone", "17863310405");

            String json = jsonObject.toString();


            HashMap<String, String> eparams = new HashMap<>();
            eparams.put("family", _family);
            eparams.put("type", _type);
            eparams.put("version", _version);
            eparams.put("eventBody", json);

            String response = WebMethods.requestPostMethodAvayaEndpoint(eparams, _url);

            return ""+response;
        } else if (rule.EndpointId == Utils.ENDPOINT_ZANG){
            String jsonEndPoint = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_ZJOSN,"{}");
            String json = "{}";
            try {
                JSONObject jsonObject = new JSONObject(jsonEndPoint);
                if(jsonObject.has("imessage")){
                    jsonObject = new JSONObject(jsonObject.getString("imessage"));
                }

                SensorEndpointMapping[] sepmList = new Gson().fromJson(rule.jsonParams,SensorEndpointMapping[].class);
                for (SensorEndpointMapping sepm : sepmList) {
                    if(sepm.idSensor == SensorTypes.SENSOR_MESAGE){
                        jsonObject.put(sepm.map, message);
                    } else {
                        jsonObject.put(sepm.map, ""+baseDispositivo.GetSensorById(sepm.idSensor).value);
                    }
                }

                JSONObject jsonToSend = new JSONObject();
                jsonToSend.put("imessage", jsonObject.toString());

                json = jsonToSend.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }
                    //{"imessage": "{"Phone":"5563556882","Email":"martinez71@avaya.com","Name":"Alberto","Message":"test Android"}"}
            return  ""+WebMethods.postBodyDataZang("https://workflow.zang.io/EngagementDesignerZang/wf/Admin/createThalliumInstance/PostToAvayaSpaces/1/ACf674eb32816d08d783f148299249fffd", json);
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
