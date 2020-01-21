package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;

public class DialogAddRule extends Dialog {

    private BaseDispositivo dispo;

    private Spinner spnr_dlog_rule_sensortype;
    private Spinner spnr_dlog_rule_ruletype;

    private EditText et_dialog_rule_val1;
    private EditText et_dialog_rule_val2;

    private Button btn_dialog_rule_ok;

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

    private void setViews() {

        ArrayAdapter<String> adapterSensor = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,SensorTypes.getSensorAmbientList(getContext()));
        ArrayAdapter<String> adapterRule = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,SensorTypes.getRuleTypes(getContext()));

        spnr_dlog_rule_sensortype = findViewById(R.id.spnr_dlog_rule_sensortype);
        spnr_dlog_rule_ruletype = findViewById(R.id.spnr_dlog_rule_ruletype);

        spnr_dlog_rule_ruletype.setAdapter(adapterRule);
        spnr_dlog_rule_sensortype.setAdapter(adapterSensor);

        et_dialog_rule_val1 = findViewById(R.id.et_dialog_rule_val1);
        et_dialog_rule_val2 = findViewById(R.id.et_dialog_rule_val2);

        btn_dialog_rule_ok = findViewById(R.id.btn_dialog_rule_ok);
        btn_dialog_rule_ok.setOnClickListener(v -> {
            Rule rule = new Rule();
            rule.DispositivoId = dispo.getId();
            rule.RuleId = spnr_dlog_rule_ruletype.getSelectedItemPosition();
            rule.SensorId = spnr_dlog_rule_sensortype.getSelectedItemPosition();
            rule.Value1 = Float.parseFloat(et_dialog_rule_val1.getText().toString());
            rule.Value2 = Float.parseFloat(et_dialog_rule_val2.getText().toString());
            RepositorioDBGeneralSingleton.getInstance(getContext()).addRule(rule);
            dismiss();
        });
    }
}
