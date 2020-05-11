package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.Models.SensorEndpointMapping;
import com.example.appsensores.R;
import com.google.gson.Gson;

import org.w3c.dom.Text;

public class DialogRuleDetails extends Dialog {

    private Rule rule;

    private TextView tv_dlog_ruledetails_endpoint;
    private TextView tv_dlog_ruledetails_sensor;
    private TextView tv_dlog_ruledetails_ruletype;
    private TextView tv_dlog_ruledetails_val1;
    private TextView tv_dlog_ruledetails_val2;
    private TextView tv_dlog_ruledetails_mapping;


    public DialogRuleDetails(@NonNull Context context, int themeResId, Rule rule) {
        super(context, themeResId);
        this.rule = rule;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.diaog_rule_details);
        setViews();
    }

    private void setViews() {
        tv_dlog_ruledetails_endpoint = findViewById(R.id.tv_dlog_ruledetails_endpoint);
        tv_dlog_ruledetails_sensor = findViewById(R.id.tv_dlog_ruledetails_sensor);
        tv_dlog_ruledetails_ruletype = findViewById(R.id.tv_dlog_ruledetails_ruletype);
        tv_dlog_ruledetails_val1 = findViewById(R.id.tv_dlog_ruledetails_val1);
        tv_dlog_ruledetails_val2 = findViewById(R.id.tv_dlog_ruledetails_val2);
        tv_dlog_ruledetails_mapping = findViewById(R.id.tv_dlog_ruledetails_mapping);

        tv_dlog_ruledetails_endpoint.setText(Utils.getEndPoints(getContext())[rule.EndpointId]);
        tv_dlog_ruledetails_sensor.setText(SensorTypes.getSensorAmbientList(getContext())[rule.SensorId]);
        tv_dlog_ruledetails_ruletype.setText(SensorTypes.getRuleTypes(getContext())[rule.RuleId]);
        tv_dlog_ruledetails_val1.setText(""+rule.Value1);
        tv_dlog_ruledetails_val2.setText(""+rule.Value2);
        String mappingText = "";
        SensorEndpointMapping[] mapList = new Gson().fromJson(rule.jsonParams, SensorEndpointMapping[].class);
        for (SensorEndpointMapping sepm: mapList) {
            mappingText += sepm.map + "   ->   " + SensorTypes.getSensorAmbientList(getContext())[sepm.idSensor] + "\n";
        }
        tv_dlog_ruledetails_mapping.setText(mappingText);
    }
}
