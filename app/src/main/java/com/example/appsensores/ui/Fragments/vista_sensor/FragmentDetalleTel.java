package com.example.appsensores.ui.Fragments.vista_sensor;


import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appsensores.R;

import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDetalleTel extends BaseVistaFargment {

    private TextView tv_fragmentdetalle_tel_sensores;

    public FragmentDetalleTel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_detalle_tel, container, false);
    }

    @Override
    protected void setControles(View view) {
        tv_fragmentdetalle_tel_sensores = view.findViewById(R.id.tv_fragmentdetalle_tel_sensores);
        SensorManager sensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);

        List<Sensor> listaSensores = sensorManager.getSensorList(Sensor.TYPE_ALL);

        String msg = "Sensores: ";
        for(Sensor sensor: listaSensores) {
            msg += sensor.getName() + "\n";
        }

        tv_fragmentdetalle_tel_sensores.setText(msg);
    }

    @Override
    protected void onToogleControles(Boolean opcion) {

    }

}
