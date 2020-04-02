package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.example.appsensores.ui.Activities.MainActivity;

import java.util.ArrayList;

public class DialogAddRule extends Dialog {

    private BaseDispositivo dispo;

    private Spinner spnr_dlog_rule_sensortype;
    private Spinner spnr_dlog_rule_ruletype;

    private EditText et_dialog_rule_val1;
    private EditText et_dialog_rule_val2;

    private Button btn_dialog_rule_ok;

    /**
     * Clase auxiliar que despues se utiiza para modelar los sensores disponibles para el dispositivo de este dialog
     */
    private class SensorModel {
        public SensorModel(int id, String Name){
            this.id = id;
            this.Name = Name;
        }
        protected int id;
        protected String Name;
    }

    /***
     * Lista de sensores {@link SensorModel} que estan disponibles, se llena en {@code setViews()}
     */
    private ArrayList<SensorModel> sensorAvailableList = new ArrayList<>();

    public DialogAddRule(@NonNull Context context, BaseDispositivo dispo) {
        super(context);
        this.dispo = dispo;
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
        ArrayList<String> sensorList = obtainAvailableSensors();

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

        ArrayAdapter<String> adapterSensor = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,sensorList);
        ArrayAdapter<String> adapterRule = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,SensorTypes.getRuleTypes(getContext()));

        spnr_dlog_rule_ruletype.setAdapter(adapterRule);
        spnr_dlog_rule_sensortype.setAdapter(adapterSensor);

        btn_dialog_rule_ok.setOnClickListener(v -> {
            Rule rule = new Rule();
            rule.DispositivoId = dispo.id;
            rule.RuleId = spnr_dlog_rule_ruletype.getSelectedItemPosition();
            rule.SensorId = sensorAvailableList.get(spnr_dlog_rule_sensortype.getSelectedItemPosition()).id;
            rule.Value1 = Float.parseFloat(et_dialog_rule_val1.getText().toString());
            rule.Value2 = Float.parseFloat(et_dialog_rule_val2.getText().toString());
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

    private void setViews() {


        spnr_dlog_rule_sensortype = findViewById(R.id.spnr_dlog_rule_sensortype);
        spnr_dlog_rule_ruletype = findViewById(R.id.spnr_dlog_rule_ruletype);


        et_dialog_rule_val1 = findViewById(R.id.et_dialog_rule_val1);
        et_dialog_rule_val2 = findViewById(R.id.et_dialog_rule_val2);

        btn_dialog_rule_ok = findViewById(R.id.btn_dialog_rule_ok);

    }

    /**
     * Metodo para iniciar las listas de los spinners con los sensores que quedan disponibles para este dispositivo
     * @return ArrayList<String> con los sensores disponibles
     */
    private ArrayList<String> obtainAvailableSensors() {
        String[] allSensors = SensorTypes.getSensorAmbientList(getContext());
        ArrayList<Rule> sensorRulesDevice = RepositorioDBGeneralSingleton.getInstance(getContext()).getRulesByDispositivo(dispo.id);
        ArrayList<String> resp = new ArrayList<>();
        int i = 0;
        for (String unit : allSensors ) {
            if(!isSensorIdInactualdevice(i, sensorRulesDevice)){
                resp.add(unit);
                sensorAvailableList.add(new SensorModel(i,unit));
            }
            i++;
        }

        return resp;
    }

    /**
     * Metodo para determinar si el id del sensor actual se encuantra en las reglas del dispositivo actual
     * @param i SensorID
     * @param sensorRulesDevice Lista de Rules del dispositivo
     * @return true si el sensor ya existe en las reglas, false si no
     */
    private boolean isSensorIdInactualdevice(int i, ArrayList<Rule> sensorRulesDevice) {
        for (Rule unit : sensorRulesDevice ) {
            if(unit.SensorId == i) return true;
        }
        return false;
    }


}
