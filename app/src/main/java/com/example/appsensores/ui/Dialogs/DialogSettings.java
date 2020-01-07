package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appsensores.Clases.Utils;
import com.example.appsensores.R;

public class DialogSettings extends Dialog {

    private EditText et_dialog_settings_token;
    private EditText et_dialog_settings_intervalo;
    private Button btn_dialog_settings_ok;

    private SharedPreferences sharedPreferencesAvaya;

    public DialogSettings(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings);

        sharedPreferencesAvaya = getContext().getSharedPreferences(Utils.AVAYA_SHARED_PREFERENCES,0);

        setViews();
    }

    private void setViews() {
        et_dialog_settings_token = findViewById(R.id.et_dialog_settings_token);
        et_dialog_settings_intervalo = findViewById(R.id.et_dialog_settings_intervalo);
        btn_dialog_settings_ok = findViewById(R.id.btn_dialog_settings_ok);

        btn_dialog_settings_ok.setOnClickListener(v -> {
            if (et_dialog_settings_token.getText().toString().equals("")) {
                toastMessage(getContext().getResources().getString(R.string.dialog_settins_adv_token));
            } else if (Integer.parseInt(et_dialog_settings_intervalo.getText().toString()) < 5) {
                toastMessage(getContext().getResources().getString(R.string.dialog_settins_adv_intervalo));
            } else {
                //guardamos en Shared preferences
                SharedPreferences.Editor editor = sharedPreferencesAvaya.edit();
                int intervalo = Integer.parseInt(et_dialog_settings_intervalo.getText().toString());
                editor.putInt(Utils.AVAYA_INTERVALO,intervalo*1000);
                editor.commit();
                dismiss();
            }
        });
    }

    private void toastMessage(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
