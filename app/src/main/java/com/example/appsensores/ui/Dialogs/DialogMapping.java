package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.appsensores.Models.Sensor;
import com.example.appsensores.Models.SensorEndpointMapping;
import com.example.appsensores.R;

import java.util.ArrayList;

public class DialogMapping extends Dialog {

    public SensorEndpointMapping mSensorEndpointMapping = null;

    ArrayList<Sensor> currentSensors;
    ArrayList<String> currentSensorListString = new ArrayList<>();
    ArrayList<String> currentParams;

    private Spinner spnr_dialog_mapping_param;
    private Spinner spnr_dialog_mapping_value;
    private Button btn_dialog_mapping_ok;

    public DialogMapping(@NonNull Context context, ArrayList<Sensor> currentSensors, ArrayList<String> currentParams) {
        super(context, R.style.custom_dialog);
        this.currentSensors = currentSensors;
        this.currentParams = currentParams;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_agregar_mapping);
        setViews();
    }

    private void setViews() {
        spnr_dialog_mapping_param = findViewById(R.id.spnr_dialog_mapping_param);
        spnr_dialog_mapping_value = findViewById(R.id.spnr_dialog_mapping_value);
        btn_dialog_mapping_ok = findViewById(R.id.btn_dialog_mapping_ok);

        for (Sensor sensor : currentSensors ) {
            currentSensorListString.add(sensor.name);
        }

        ArrayAdapter<String> adapterParam = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, currentParams);
        ArrayAdapter<String> adapterSensor = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, currentSensorListString);

        spnr_dialog_mapping_param.setAdapter(adapterParam);
        spnr_dialog_mapping_value.setAdapter(adapterSensor);

        btn_dialog_mapping_ok.setOnClickListener(v -> {
            mSensorEndpointMapping = new SensorEndpointMapping();
            mSensorEndpointMapping.idSensor = currentSensors.get(spnr_dialog_mapping_value.getSelectedItemPosition()).id;
            mSensorEndpointMapping.map = currentParams.get(spnr_dialog_mapping_param.getSelectedItemPosition());
            dismiss();
        });
    }
}
