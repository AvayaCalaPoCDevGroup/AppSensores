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

import com.example.appsensores.Clases.STimer;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.example.appsensores.ui.Activities.MainActivity;
import com.google.zxing.integration.android.IntentIntegrator;

public class DialogSettings extends Dialog implements MainActivity.IScanListener {

    private EditText et_dialog_settings_token;
    private EditText et_dialog_settings_intervalo;
    private Button btn_dialog_settings_ok;
    private Button btn_dialog_settings_scan;
    private MainActivity mMainActivity;

    private SharedPreferences sharedPreferencesAvaya;
    public static IsettinsListener mIsettinsListener;

    public DialogSettings(@NonNull Context context, MainActivity mainActivity) {
        super(context);
        this.mMainActivity = mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings);

        sharedPreferencesAvaya = getContext().getSharedPreferences(Utils.AVAYA_SHARED_PREFERENCES,0);

        setViews();
        mMainActivity.setOnScanListener(this);
    }

    public static void setOnSettingsChangedListener(IsettinsListener l){
        mIsettinsListener = l;
    }

    private void setViews() {
        et_dialog_settings_token = findViewById(R.id.et_dialog_settings_token);
        et_dialog_settings_intervalo = findViewById(R.id.et_dialog_settings_intervalo);
        btn_dialog_settings_ok = findViewById(R.id.btn_dialog_settings_ok);
        btn_dialog_settings_scan = findViewById(R.id.btn_dialog_settings_scan);

        //Obtenemos la informacion del telefono, por default siempre es el id 1
        BaseDispositivo deviceTel = RepositorioDBGeneralSingleton.getInstance(getContext()).getDeviceById(1);
        et_dialog_settings_token.setText(deviceTel.getToken());
        et_dialog_settings_intervalo.setText("" + (sharedPreferencesAvaya.getInt(Utils.AVAYA_INTERVALO, 3000)/1000));

        btn_dialog_settings_ok.setOnClickListener(v -> {
            if (et_dialog_settings_token.getText().toString().equals("")) {
                toastMessage(getContext().getResources().getString(R.string.dialog_settins_adv_token));
            } else if (Integer.parseInt(et_dialog_settings_intervalo.getText().toString()) < Utils.MIN_INTERVAL_TAGO) {
                toastMessage(String.format(getContext().getResources().getString(R.string.dialog_settins_adv_intervalo), Utils.MIN_INTERVAL_TAGO));
            } else {
                //guardamos en Shared preferences
                SharedPreferences.Editor editor = sharedPreferencesAvaya.edit();
                int intervalo = Integer.parseInt(et_dialog_settings_intervalo.getText().toString());
                editor.putInt(Utils.AVAYA_INTERVALO,intervalo*1000);
                STimer.CURRENT_PERIOD = intervalo * 1000;
                editor.commit();
                deviceTel.setToken(et_dialog_settings_token.getText().toString());
                RepositorioDBGeneralSingleton.getInstance(getContext()).updateDevice(deviceTel);
                if(mIsettinsListener != null)
                    mIsettinsListener.onSettingsChanged();
                dismiss();
            }
        });

        btn_dialog_settings_scan.setOnClickListener(v -> {
            new IntentIntegrator(mMainActivity).initiateScan();
        });
    }

    private void toastMessage(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScanResult(String msg) {
        et_dialog_settings_token.setText(msg);
    }

    public interface IsettinsListener {
        /**
         * Callback para notificar los cambios en los settings
         */
        void onSettingsChanged();
    }
}
