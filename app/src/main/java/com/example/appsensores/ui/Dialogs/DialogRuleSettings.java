package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.appsensores.Clases.Utils;
import com.example.appsensores.R;

public class DialogRuleSettings extends Dialog {

    private SharedPreferences sharedPreferencesAvaya;

    private EditText    et_dialog_rule_settings_mail;
    private EditText    et_dialog_rule_settings_family;
    private EditText    et_dialog_rule_settings_type;
    private EditText    et_dialog_rule_settings_version;
    private EditText    et_dialog_rule_settings_url;
    private Button      btn_dialog_rule_settings_ok;

    private String _mail;
    private String _family;
    private String _type;
    private String _version;
    private String _url;

    public DialogRuleSettings(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rule_settings);

        sharedPreferencesAvaya = getContext().getSharedPreferences(Utils.AVAYA_SHARED_PREFERENCES,0);

        _mail = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_MAIL,"");
        _family = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_FAMILY,"");
        _type = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_TYPE,"");
        _version = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_VERSION,"");
        _url = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_URL,"");

        setViews();

    }

    private void setViews() {
        et_dialog_rule_settings_mail      = findViewById(R.id.et_dialog_rule_settings_mail);
        et_dialog_rule_settings_family    = findViewById(R.id.et_dialog_rule_settings_family);
        et_dialog_rule_settings_type      = findViewById(R.id.et_dialog_rule_settings_type);
        et_dialog_rule_settings_version   = findViewById(R.id.et_dialog_rule_settings_version);
        et_dialog_rule_settings_url       = findViewById(R.id.et_dialog_rule_settings_url);
        btn_dialog_rule_settings_ok       = findViewById(R.id.btn_dialog_rule_settings_ok);

        et_dialog_rule_settings_mail.setText(_mail);
        et_dialog_rule_settings_family.setText(_family);
        et_dialog_rule_settings_type.setText(_type);
        et_dialog_rule_settings_version.setText(_version);
        et_dialog_rule_settings_url.setText(_url);

        btn_dialog_rule_settings_ok.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferencesAvaya.edit();
            editor.putString(Utils.AVAYA_SHARED_MAIL, et_dialog_rule_settings_mail.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_FAMILY, et_dialog_rule_settings_family.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_TYPE, et_dialog_rule_settings_type.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_VERSION, et_dialog_rule_settings_version.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_URL, et_dialog_rule_settings_url.getText().toString());
            editor.commit();
            dismiss();
        });
    }
}
