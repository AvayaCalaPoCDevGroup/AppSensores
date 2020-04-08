package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.example.appsensores.ui.Activities.MainActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DialogAddRule extends Dialog {

    private BaseDispositivo dispo;

    private Spinner spnr_dlog_rule_sensortype;
    private Spinner spnr_dlog_rule_ruletype;

    private EditText et_dialog_rule_val1;
    private EditText et_dialog_rule_val2;

    private Button btn_dialog_rule_ok;

    private TextView tv_mail_param;
    private TextView tv_msg_param;
    private TextView tv_temperature_param;
    private TextView tv_humidity_param;
    private TextView tv_lux_param;
    private TextView tv_uv_param;
    private TextView tv_battery_param;

    private Button btn_mail_param;
    private Button btn_msg_param;
    private Button btn_temperature_param;
    private Button btn_humidity_param;
    private Button btn_lux_param;
    private Button btn_uv_param;
    private Button btn_battery_param;

    private Button btn_mail_param_del;
    private Button btn_msg_param_del;
    private Button btn_temperature_param_del;
    private Button btn_humidity_param_del;
    private Button btn_lux_param_del;
    private Button btn_uv_param_del;
    private Button btn_battery_param_del;

    private String[] mParametros = new String[]{
            "", //0 correo
            "", //1 mensaje
            "", //2 Temperature
            "", //3 Humedad
            "", //4 Lux
            "", //5 UV
            ""  //4 battery
    };

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
        super(context, R.style.custom_dialog);
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

        ArrayList<String> parameters = SensorTypes.getEndPointParameters(getContext());

        ArrayAdapter<String> adapterSensor = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,sensorList);
        ArrayAdapter<String> adapterRule = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,SensorTypes.getRuleTypes(getContext()));

        spnr_dlog_rule_ruletype.setAdapter(adapterRule);
        spnr_dlog_rule_sensortype.setAdapter(adapterSensor);

        btn_dialog_rule_ok.setOnClickListener(v -> {

            if(tv_mail_param.getText().toString().equals("") || tv_msg_param.getText().toString().equals("")){
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.dialog_addrule_adv), Toast.LENGTH_SHORT).show();
                return;
            }

            Rule rule = new Rule();
            rule.DispositivoId = dispo.id;
            rule.RuleId = spnr_dlog_rule_ruletype.getSelectedItemPosition();
            rule.SensorId = sensorAvailableList.get(spnr_dlog_rule_sensortype.getSelectedItemPosition()).id;
            rule.Value1 = Float.parseFloat(et_dialog_rule_val1.getText().toString());
            rule.Value2 = Float.parseFloat(et_dialog_rule_val2.getText().toString());
            rule.emailParam = tv_mail_param.getText().toString();
            rule.messageParam = tv_msg_param.getText().toString();
            rule.temperatureParam = tv_temperature_param.getText().toString();
            rule.humidityParam = tv_humidity_param.getText().toString();
            rule.luxParam = tv_lux_param.getText().toString();
            rule.uvParam = tv_uv_param.getText().toString();
            rule.batteryParam = tv_battery_param.getText().toString();

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

        //nuevos controles para parametrizacion breeze
        tv_mail_param           = findViewById(R.id.tv_mail_param       );
        tv_msg_param            = findViewById(R.id.tv_msg_param        );
        tv_temperature_param    = findViewById(R.id.tv_temperature_param);
        tv_humidity_param       = findViewById(R.id.tv_humidity_param   );
        tv_lux_param            = findViewById(R.id.tv_lux_param        );
        tv_uv_param             = findViewById(R.id.tv_uv_param         );
        tv_battery_param        = findViewById(R.id.tv_battery_param    );

        btn_mail_param              = findViewById(R.id.btn_mail_param           );
        btn_msg_param               = findViewById(R.id.btn_msg_param            );
        btn_temperature_param       = findViewById(R.id.btn_temperature_param    );
        btn_humidity_param          = findViewById(R.id.btn_humidity_param       );
        btn_lux_param               = findViewById(R.id.btn_lux_param            );
        btn_uv_param                = findViewById(R.id.btn_uv_param             );
        btn_battery_param           = findViewById(R.id.btn_battery_param        );

        btn_mail_param_del          = findViewById(R.id.btn_mail_param_del       );
        btn_msg_param_del           = findViewById(R.id.btn_msg_param_del        );
        btn_temperature_param_del   = findViewById(R.id.btn_temperature_param_del);
        btn_humidity_param_del      = findViewById(R.id.btn_humidity_param_del   );
        btn_lux_param_del           = findViewById(R.id.btn_lux_param_del        );
        btn_uv_param_del            = findViewById(R.id.btn_uv_param_del         );
        btn_battery_param_del       = findViewById(R.id.btn_battery_param_del    );

        btn_mail_param       .setOnClickListener(new Param_click_listener(tv_mail_param       ,0));
        btn_msg_param        .setOnClickListener(new Param_click_listener(tv_msg_param        , 1));
        btn_temperature_param.setOnClickListener(new Param_click_listener(tv_temperature_param, 2));
        btn_humidity_param   .setOnClickListener(new Param_click_listener(tv_humidity_param   , 3));
        btn_lux_param        .setOnClickListener(new Param_click_listener(tv_lux_param        , 4));
        btn_uv_param         .setOnClickListener(new Param_click_listener(tv_uv_param         , 5));
        btn_battery_param    .setOnClickListener(new Param_click_listener(tv_battery_param    , 6));

        btn_mail_param_del       .setOnClickListener(new Delete_param_click_listener(btn_mail_param       ,0));
        btn_msg_param_del        .setOnClickListener(new Delete_param_click_listener(btn_msg_param        , 1));
        btn_temperature_param_del.setOnClickListener(new Delete_param_click_listener(btn_temperature_param, 2));
        btn_humidity_param_del   .setOnClickListener(new Delete_param_click_listener(btn_humidity_param   , 3));
        btn_lux_param_del        .setOnClickListener(new Delete_param_click_listener(btn_lux_param        , 4));
        btn_uv_param_del         .setOnClickListener(new Delete_param_click_listener(btn_uv_param         , 5));
        btn_battery_param_del    .setOnClickListener(new Delete_param_click_listener(btn_battery_param    , 6));
    }

    private class Param_click_listener implements View.OnClickListener {

        TextView tv;
        int position = -1;

        public Param_click_listener(TextView tv, int position){
            this.tv = tv;
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            DialogSelectParam dlog = new DialogSelectParam(getContext(), R.style.custom_dialog, getAvailableParams());
            dlog.setOnDismissListener(dialog -> {
                String resp = dlog.resp;
                if(!resp.equals("")){
                    tv.setText(resp);
                    v.setVisibility(View.GONE);
                    mParametros[position] = resp;
                }
            });
            dlog.show();
        }
    }

    private class Delete_param_click_listener implements View.OnClickListener {

        Button tv;
        int position = -1;

        public Delete_param_click_listener(Button tv, int position){
            this.tv = tv;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            mParametros[position] = "";
            tv.setVisibility(View.VISIBLE);
        }
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

    /**
     * Metodo para devolver los parametros que un no se usan en otras variables
     * @return
     */
    private ArrayList<String> getAvailableParams(){
        ArrayList<String> resp = new ArrayList<>();
        resp.add(getContext().getResources().getString(R.string.dialog_select_select));
        for (String param : SensorTypes.getEndPointParameters(getContext())) {
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
        for (String mparam : mParametros) {
            if(param.equals(mparam)) {
                return true;
            }
        }
        return false;
    }


}
