package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.R;

import org.w3c.dom.Text;

public class DialogRuleDetails extends Dialog {

    private Rule rule;

    private TextView tv_dlog_ruledetails_sensor;
    private TextView tv_dlog_ruledetails_ruletype;
    private TextView tv_dlog_ruledetails_val1;
    private TextView tv_dlog_ruledetails_val2;
    private TextView tv_dlog_ruledetails_mail;
    private TextView tv_dlog_ruledetails_msg;
    private TextView tv_dlog_ruledetails_temp;
    private TextView tv_dlog_ruledetails_humidity;
    private TextView tv_dlog_ruledetails_lux;
    private TextView tv_dlog_ruledetails_uv;
    private TextView tv_dlog_ruledetails_battery;


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
        tv_dlog_ruledetails_sensor = findViewById(R.id.tv_dlog_ruledetails_sensor);
        tv_dlog_ruledetails_ruletype = findViewById(R.id.tv_dlog_ruledetails_ruletype);
        tv_dlog_ruledetails_val1 = findViewById(R.id.tv_dlog_ruledetails_val1);
        tv_dlog_ruledetails_val2 = findViewById(R.id.tv_dlog_ruledetails_val2);
        tv_dlog_ruledetails_mail = findViewById(R.id.tv_dlog_ruledetails_mail);
        tv_dlog_ruledetails_msg = findViewById(R.id.tv_dlog_ruledetails_msg);
        tv_dlog_ruledetails_temp = findViewById(R.id.tv_dlog_ruledetails_temp);
        tv_dlog_ruledetails_humidity = findViewById(R.id.tv_dlog_ruledetails_humidity);
        tv_dlog_ruledetails_lux = findViewById(R.id.tv_dlog_ruledetails_lux);
        tv_dlog_ruledetails_uv = findViewById(R.id.tv_dlog_ruledetails_uv);
        tv_dlog_ruledetails_battery = findViewById(R.id.tv_dlog_ruledetails_battery);

        tv_dlog_ruledetails_sensor.setText(SensorTypes.getSensorAmbientList(getContext())[rule.SensorId]);
        tv_dlog_ruledetails_ruletype.setText(SensorTypes.getRuleTypes(getContext())[rule.RuleId]);
        tv_dlog_ruledetails_val1.setText(""+rule.Value1);
        tv_dlog_ruledetails_val2.setText(""+rule.Value2);
        tv_dlog_ruledetails_mail.setText(rule.emailParam);
        tv_dlog_ruledetails_msg.setText(rule.messageParam);
        tv_dlog_ruledetails_temp.setText(rule.temperatureParam);
        tv_dlog_ruledetails_humidity.setText(rule.humidityParam);
        tv_dlog_ruledetails_lux.setText(rule.luxParam);
        tv_dlog_ruledetails_uv.setText(rule.uvParam);
        tv_dlog_ruledetails_battery.setText(rule.batteryParam);
    }
}
