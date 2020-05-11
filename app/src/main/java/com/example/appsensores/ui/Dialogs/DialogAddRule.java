package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.Models.Sensor;
import com.example.appsensores.Models.SensorEndpointMapping;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DialogAddRule extends Dialog {

    private BaseDispositivo dispo;

    private Spinner spnr_dlog_rule_sensortype;
    private Spinner spnr_dlog_rule_ruletype;

    private EditText et_dialog_rule_val1;
    private EditText et_dialog_rule_val2;

    private ListView lv_dialog_addrule_map;

    private Button btn_dialog_rule_map;
    private Button btn_dialog_rule_ok;

    ArrayList<Sensor> sensorList;
    ArrayList<String> sensorListString;
    ArrayList<SensorEndpointMapping> mapList = new ArrayList<>();
    ArrayList<String> mapListString = new ArrayList<>();
    ArrayAdapter<String> adapterMapList;

    private float _density;
    private int _endpoint;

    public DialogAddRule(@NonNull Context context, BaseDispositivo dispo, int endpoint) {
        super(context, R.style.custom_dialog);
        this.dispo = dispo;
        _density = context.getResources().getDisplayMetrics().density;
        _endpoint = endpoint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_addrule);
        setViews();
    }



    @Override
    protected void onStart() {
        super.onStart();
        sensorList = obtainAvailableSensors();
        sensorListString = SensorListToSTringLIst(sensorList);

        //aqui me quede, ya lleno los sensores para la regla disponibles, falta mapear los parametros para el endpoint

        if(sensorList.size() == 0){
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.dialog_addrule_norules), Toast.LENGTH_SHORT).show();
            AsyncTask.execute(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dismiss();
            });
        }

        ArrayAdapter<String> adapterSensor = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,sensorListString);
        ArrayAdapter<String> adapterRule = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,SensorTypes.getRuleTypes(getContext()));

        spnr_dlog_rule_ruletype.setAdapter(adapterRule);
        spnr_dlog_rule_sensortype.setAdapter(adapterSensor);

        btn_dialog_rule_map.setOnClickListener(v -> {
            DialogMapping dlomapping = new DialogMapping(getContext(), getAvailableSensorForParams(), getAvailableParams());
            dlomapping.setOnDismissListener(dialog -> {
                SensorEndpointMapping sensorEndpointMapping = ((DialogMapping)dialog).mSensorEndpointMapping;
                if(sensorEndpointMapping != null){
                    mapList.add(sensorEndpointMapping);
                    refreshparamsList();
                }
            });
            dlomapping.show();
        });

        btn_dialog_rule_ok.setOnClickListener(v -> {

            Rule rule = new Rule();
            rule.DispositivoId = dispo.id;
            rule.EndpointId = _endpoint;
            rule.RuleId = spnr_dlog_rule_ruletype.getSelectedItemPosition();
            rule.SensorId = sensorList.get(spnr_dlog_rule_sensortype.getSelectedItemPosition()).id;
            rule.Value1 = Float.parseFloat(et_dialog_rule_val1.getText().toString());
            rule.Value2 = Float.parseFloat(et_dialog_rule_val2.getText().toString());
            rule.jsonParams = new Gson().toJson(mapList);

            RepositorioDBGeneralSingleton.getInstance(getContext()).addRule(rule);
            dismiss();
        });

        spnr_dlog_rule_ruletype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == SensorTypes.ENTRE){
                    et_dialog_rule_val2.setEnabled(true);
                } else {
                    et_dialog_rule_val2.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void refreshparamsList() {
        mapListString.clear();
        ArrayList<String> actmapliststring = new ArrayList<>();
        for (SensorEndpointMapping sepm : mapList ) {
            actmapliststring.add(sepm.map+ "  -  " +dispo.GetSensorById(sepm.idSensor).name);
        }
        if(actmapliststring.size()>0){
            mapListString.addAll(actmapliststring);
        }
        lv_dialog_addrule_map.getLayoutParams().height = (int)_density*50*mapListString.size();
        adapterMapList.notifyDataSetChanged();
    }

    private ArrayList<String> SensorListToSTringLIst(ArrayList<Sensor> sensorList) {
        ArrayList<String> resp = new ArrayList<>();
        for (Sensor sensor : sensorList ) {
            resp.add(sensor.name);
        }
        return resp;
    }

    private void setViews() {
        spnr_dlog_rule_sensortype = findViewById(R.id.spnr_dlog_rule_sensortype);
        spnr_dlog_rule_ruletype = findViewById(R.id.spnr_dlog_rule_ruletype);


        et_dialog_rule_val1 = findViewById(R.id.et_dialog_rule_val1);
        et_dialog_rule_val2 = findViewById(R.id.et_dialog_rule_val2);

        lv_dialog_addrule_map = findViewById(R.id.lv_dialog_addrule_map);
        adapterMapList = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,mapListString);
        lv_dialog_addrule_map.setAdapter(adapterMapList);

        btn_dialog_rule_map = findViewById(R.id.btn_dialog_rule_map);
        btn_dialog_rule_ok = findViewById(R.id.btn_dialog_rule_ok);
    }

    /**
     * Metodo para iniciar las listas de los spinners con los sensores que quedan disponibles para este dispositivo
     * @return ArrayList<String> con los sensores disponibles
     */
    private ArrayList<Sensor> obtainAvailableSensors() {
        ArrayList<Sensor> allSensors = dispo.sensors;
        ArrayList<Rule> sensorRulesDevice = RepositorioDBGeneralSingleton.getInstance(getContext()).getRulesByDispositivo(dispo.id);
        ArrayList<Sensor> resp = new ArrayList<>();
        for (Sensor unit : allSensors ) {
            if(!isSensorIdInactualdevice(unit.id, sensorRulesDevice)){
                resp.add(unit);
            }
        }

        return resp;
    }

    /**
     * Metodo para determinar si el id del sensor actual se encuantra en las reglas del dispositivo actual
     * @param id SensorID
     * @param sensorRulesDevice Lista de Rules del dispositivo
     * @return true si el sensor ya existe en las reglas, false si no
     */
    private boolean isSensorIdInactualdevice(int id, ArrayList<Rule> sensorRulesDevice) {
        for (Rule unit : sensorRulesDevice ) {
            if(unit.SensorId == id) return true;
        }
        return false;
    }

    /**
     * Metodo para devolver los parametros que un no se usan en otras variables
     * @return
     */
    private ArrayList<String> getAvailableParams(){
        ArrayList<String> resp = new ArrayList<>();
        //resp.add(getContext().getResources().getString(R.string.dialog_select_select));
        for (String param : Utils.GetEndpointParameters(getContext(), _endpoint)) {
            if(!isParamInParams(param)){
                resp.add(param);
            }
        }

        return resp;
    }

    /**
     * Metodo para verificar si un parametro se encuentra en la lista de parametros actuales
     * @param param
     * @return
     */
    private boolean isParamInParams(String param){
        for (SensorEndpointMapping map : mapList) {
            if(map.map.equals(param)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Sensor> getAvailableSensorForParams(){
        ArrayList<Sensor> resp = new ArrayList<>();
        //resp.add(getContext().getResources().getString(R.string.dialog_select_select));
        for (Sensor sensor : dispo.sensors) {
            if(!isParamInParams(sensor)){
                resp.add(sensor);
            }
        }

        return resp;
    }

    private boolean isParamInParams(Sensor sensor){
        for (SensorEndpointMapping map : mapList) {
            if(map.idSensor == sensor.id) {
                return true;
            }
        }
        return false;
    }


}
