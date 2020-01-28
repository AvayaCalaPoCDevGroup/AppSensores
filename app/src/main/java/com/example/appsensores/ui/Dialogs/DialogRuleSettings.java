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
    private EditText    et_dialog_rule_settings_from;
    private EditText    et_dialog_rule_settings_to;
    private EditText    et_dialog_rule_settings_zurl;
    private EditText    et_dialog_rule_settings_zurlparam;
    private Button      btn_dialog_rule_settings_ok;

    private String _mail;
    private String _family;
    private String _type;
    private String _version;
    private String _url;
    private String _from;
    private String _to;
    private String _zurl;
    private String _zurlparam;

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
        _family = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_FAMILY,"AAADEVRFID");
        _type = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_TYPE,"AAADEVRFIDLOCALIZATION");
        _version = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_VERSION,"1.0");
        _url = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_URL,"http://breeze2-132.collaboratory.avaya.com/services/EventingConnector/events");
        _from = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_FROM, "+19892560890");
        _to = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_TO, "");
        _zurl = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_ZURL, "https://api.zang.io/v2/Accounts/ACbf889084ad63b77ddf614ddda88d2aa9/Calls");
        _zurlparam = sharedPreferencesAvaya.getString(Utils.AVAYA_SHARED_ZURLPARAM, "https://workflow.zang.io/EngagementDesignerZang/wf/Admin/createThalliumInstance/iotmom/9/ACbf889084ad63b77ddf614ddda88d2aa9");

        setViews();

    }

    private void setViews() {
        et_dialog_rule_settings_mail      = findViewById(R.id.et_dialog_rule_settings_mail);
        et_dialog_rule_settings_family    = findViewById(R.id.et_dialog_rule_settings_family);
        et_dialog_rule_settings_type      = findViewById(R.id.et_dialog_rule_settings_type);
        et_dialog_rule_settings_version   = findViewById(R.id.et_dialog_rule_settings_version);
        et_dialog_rule_settings_url      = findViewById(R.id.et_dialog_rule_settings_url);
        et_dialog_rule_settings_from      = findViewById(R.id.et_dialog_rule_settings_from);
        et_dialog_rule_settings_to        = findViewById(R.id.et_dialog_rule_settings_to);
        et_dialog_rule_settings_zurl      = findViewById(R.id.et_dialog_rule_settings_zurl);
        et_dialog_rule_settings_zurlparam = findViewById(R.id.et_dialog_rule_settings_zurlparam);

        btn_dialog_rule_settings_ok       = findViewById(R.id.btn_dialog_rule_settings_ok);

        et_dialog_rule_settings_mail.setText(_mail);
        et_dialog_rule_settings_family.setText(_family);
        et_dialog_rule_settings_type.setText(_type);
        et_dialog_rule_settings_version.setText(_version);
        et_dialog_rule_settings_url.setText(_url);
        et_dialog_rule_settings_from.setText(_from);
        et_dialog_rule_settings_to.setText(_to);
        et_dialog_rule_settings_zurl.setText(_zurl);
        et_dialog_rule_settings_zurlparam.setText(_zurlparam);

        btn_dialog_rule_settings_ok.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferencesAvaya.edit();
            editor.putString(Utils.AVAYA_SHARED_MAIL, et_dialog_rule_settings_mail.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_FAMILY, et_dialog_rule_settings_family.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_TYPE, et_dialog_rule_settings_type.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_VERSION, et_dialog_rule_settings_version.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_URL, et_dialog_rule_settings_url.getText().toString());

            editor.putString(Utils.AVAYA_SHARED_FROM, et_dialog_rule_settings_from.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_TO, et_dialog_rule_settings_to.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_ZURL, et_dialog_rule_settings_zurl.getText().toString());
            editor.putString(Utils.AVAYA_SHARED_ZURLPARAM, et_dialog_rule_settings_zurlparam.getText().toString());
            editor.commit();
            dismiss();
        });
    }
}
