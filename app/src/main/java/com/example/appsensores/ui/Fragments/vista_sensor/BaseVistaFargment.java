package com.example.appsensores.ui.Fragments.vista_sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.appsensores.Clases.Enums.EnumTipoDispo;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;

import org.w3c.dom.Text;

public abstract class BaseVistaFargment extends Fragment {

    protected BaseDispositivo dispositivoBase;

    private TextView tv_fragmentvista_nombre;
    private Switch sw_fragmnetvista_gral;

    private TextView tv_fragment_base_dispo_tipo;
    private TextView tv_fragment_base_dispo_mac;
    private TextView tv_fragment_base_dispo_token;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("baseVistaFragent", "Paso por Oncreate");
        int idDispositivo = getArguments().getInt("idSensor");
        dispositivoBase = RepositorioDBGeneralSingleton.getInstance(getContext()).getDeviceById(idDispositivo);


    }

    @Nullable
    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_fragmentvista_nombre = view.findViewById(R.id.tv_fragmentvista_nombre);
        sw_fragmnetvista_gral = view.findViewById(R.id.sw_fragmnetvista_gral);

        sw_fragmnetvista_gral.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toogleControles(isChecked);
            }
        });

        tv_fragment_base_dispo_tipo = view.findViewById(R.id.tv_fragment_base_dispo_tipo);
        tv_fragment_base_dispo_mac = view.findViewById(R.id.tv_fragment_base_dispo_mac);
        tv_fragment_base_dispo_token = view.findViewById(R.id.tv_fragment_base_dispo_token);

        tv_fragmentvista_nombre.setText(dispositivoBase.getNombre());
        tv_fragment_base_dispo_tipo.setText((EnumTipoDispo.values()[dispositivoBase.getTipoDispositivo()]).toString());
        tv_fragment_base_dispo_mac.setText(dispositivoBase.getMacAddress());
        tv_fragment_base_dispo_token.setText(dispositivoBase.getToken());

        setControles(view);
    }

    /***
     * Aqui se deben inicializar los controles propios de la clase que hereda de BaseVistaFragment
     * @param view View general del fragment.
     */
    protected abstract void setControles(View view);

    /***
     * Aqui se debe implementar la logica para apagar o encender los sensores del dispositio actual
     * @param opcion
     */
    protected abstract void toogleControles(Boolean opcion);
}
