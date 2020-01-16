package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;

import java.util.ArrayList;

public class DialogAgregarDispositivo extends Dialog {
    public DialogAgregarDispositivo(@NonNull Context context) {
        super(context);
    }

    private Spinner spnr_dialog_agregar_dispositivo_tipo;
    private EditText et_dialog_agregar_dispositivo_nombre;
    private EditText et_dialog_agregar_dispositivo_mac;
    private EditText et_dialog_agregar_dispositivo_token;
    private Button btn_dialog_agregar_dispositivo_ok;
    private Button btn_dialog_agregar_dispositivo_search;

    private ArrayList<String> lisTipoDispo = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_agregar_dispositivo);
        
        //Dispositivos en duro, ya que son pocos.
        lisTipoDispo.add("Elige una opcion.");
        lisTipoDispo.add("SENSOR PUCK");
        lisTipoDispo.add("THUNDERBOARD");
        setViews();
    }

    private void setViews() {
        spnr_dialog_agregar_dispositivo_tipo = findViewById(R.id.spnr_dialog_agregar_dispositivo_tipo);
        et_dialog_agregar_dispositivo_nombre = findViewById(R.id.et_dialog_agregar_dispositivo_nombre);
        et_dialog_agregar_dispositivo_mac = findViewById(R.id.et_dialog_agregar_dispositivo_mac);
        et_dialog_agregar_dispositivo_token = findViewById(R.id.et_dialog_agregar_dispositivo_token);
        btn_dialog_agregar_dispositivo_ok = findViewById(R.id.btn_dialog_agregar_dispositivo_ok);
        btn_dialog_agregar_dispositivo_search = findViewById(R.id.btn_dialog_agregar_dispositivo_search);

        spnr_dialog_agregar_dispositivo_tipo.setAdapter(new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,lisTipoDispo));
        spnr_dialog_agregar_dispositivo_tipo.setEnabled(false);

        btn_dialog_agregar_dispositivo_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ValidarDispositivo()){
                    return;
                }
                BaseDispositivo dispositivo = new BaseDispositivo();
                dispositivo.setNombre(et_dialog_agregar_dispositivo_nombre.getText().toString());
                dispositivo.setMacAddress(et_dialog_agregar_dispositivo_mac.getText().toString());
                dispositivo.setToken(et_dialog_agregar_dispositivo_token.getText().toString());
                dispositivo.setTipoDispositivo(spnr_dialog_agregar_dispositivo_tipo.getSelectedItemPosition());

                RepositorioDBGeneralSingleton.getInstance(getContext()).addDevice(dispositivo);
                dismiss();
            }
        });

        DialogSearchDevices dialogSearchDevices = new DialogSearchDevices(getContext());
        dialogSearchDevices.setOnDismissListener(dialog -> {
            String macAddress = ((DialogSearchDevices)dialog).DeviceSelected.getMacAddress();
            int position = ((DialogSearchDevices)dialog).DeviceSelected.getTipoDispositivo();
            et_dialog_agregar_dispositivo_mac.setText(macAddress);
            spnr_dialog_agregar_dispositivo_tipo.setSelection(position);
        });
        btn_dialog_agregar_dispositivo_search.setOnClickListener(v -> {
            dialogSearchDevices.show();
        });
    }

    private boolean ValidarDispositivo() {
        String msg = "";
        boolean resp = true;
        if (et_dialog_agregar_dispositivo_nombre.getText().toString().equals("")) {
            msg = "El Nombre es requerido";
            resp = false;
        } else if (et_dialog_agregar_dispositivo_mac.getText().toString().equals("")) {
            msg = "El Mac addres es requerido";
            resp = false;
        } else if (et_dialog_agregar_dispositivo_mac.getText().toString().length() != 17) {
            msg = "El Mac Addres debe tener 17 caracteres";
            resp = false;
        } else if (et_dialog_agregar_dispositivo_token.getText().toString().equals("")) {
            msg = "El Token es requerido";
            resp = false;
        } else if (spnr_dialog_agregar_dispositivo_tipo.getSelectedItemPosition() == 0) {
            msg = "Debes elegir un tipo de dispositivo";
            resp = false;
        }

        if (!resp)
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

        return resp;
    }
}
