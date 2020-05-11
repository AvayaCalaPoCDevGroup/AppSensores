package com.example.appsensores.ui.Fragments.Rules;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.appsensores.Clases.Adapters.AdapterRules;
import com.example.appsensores.Clases.Enums.EnumTipoDispo;
import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.example.appsensores.ui.Activities.MainActivity;
import com.example.appsensores.ui.Dialogs.DialogAddRule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.Util;

import java.util.ArrayList;

public class FragmentRules extends Fragment {
    private BaseDispositivo dispositivoBase;
    private ArrayList<Rule> rulesList = new ArrayList<>();

    private TextView    tv_fragmentvista_nombre;
    private Switch      sw_fragmnetvista_gral;

    private TextView    tv_fragment_base_dispo_tipo;
    private TextView    tv_fragment_base_dispo_mac;
    private TextView    tv_fragment_base_dispo_token;
    private Button btn_fragmentvista_rules;

    private ListView lv_fragment_rules;
    private FloatingActionButton fab_fragment_rules;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int idDispositivo = getArguments().getInt("idSensor");
        dispositivoBase = RepositorioDBGeneralSingleton.getInstance(getContext()).getDeviceById(idDispositivo);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rules, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews(view);
    }

    private void setViews(View view) {
        tv_fragmentvista_nombre = view.findViewById(R.id.tv_fragmentvista_nombre);
        sw_fragmnetvista_gral = view.findViewById(R.id.sw_fragmnetvista_gral);
        sw_fragmnetvista_gral.setVisibility(View.GONE);
        tv_fragment_base_dispo_tipo = view.findViewById(R.id.tv_fragment_base_dispo_tipo);
        tv_fragment_base_dispo_mac = view.findViewById(R.id.tv_fragment_base_dispo_mac);
        tv_fragment_base_dispo_token = view.findViewById(R.id.tv_fragment_base_dispo_token);
        btn_fragmentvista_rules = view.findViewById(R.id.btn_fragmentvista_rules);
        btn_fragmentvista_rules.setVisibility(View.GONE);
        lv_fragment_rules = view.findViewById(R.id.lv_fragment_rules);
        fab_fragment_rules = view.findViewById(R.id.fab_fragment_rules);

        tv_fragmentvista_nombre.setText(dispositivoBase.Nombre);
        tv_fragment_base_dispo_tipo.setText((EnumTipoDispo.values()[dispositivoBase.TipoDispositivo]).toString());
        tv_fragment_base_dispo_mac.setText(dispositivoBase.MacAddress);
        tv_fragment_base_dispo_token.setText(dispositivoBase.Token);

        iniciarListas();
        final AdapterRules adapterRules = new AdapterRules(getContext(),R.layout.list_unit_dispositivos, rulesList);
        lv_fragment_rules.setAdapter(adapterRules);
        fab_fragment_rules.setOnClickListener(v -> {
            final int[] endpoint = {-1};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.dialog_endpoint_title));
            builder.setMessage(getResources().getString(R.string.dialog_endpoint_mesage));
            builder.setPositiveButton(Utils.getEndPoints(getContext())[Utils.ENDPOINT_BREEZE], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dlog, int which) {
                    endpoint[0] = Utils.ENDPOINT_BREEZE;
                    DialogAddRule dialog = new DialogAddRule(getContext(), dispositivoBase, endpoint[0]);
                    dialog.setOnDismissListener(dialog1 -> {
                        iniciarListas();
                        adapterRules.notifyDataSetChanged();
                    });
                    dialog.show();
                    dlog.dismiss();
                }
            });
            builder.setNegativeButton(Utils.getEndPoints(getContext())[Utils.ENDPOINT_ZANG], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dlog, int which) {
                    endpoint[0] = Utils.ENDPOINT_ZANG;
                    DialogAddRule dialog = new DialogAddRule(getContext(), dispositivoBase, endpoint[0]);
                    dialog.setOnDismissListener(dialog1 -> {
                        iniciarListas();
                        adapterRules.notifyDataSetChanged();
                    });
                    dialog.show();
                    dlog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();



        });

        lv_fragment_rules.setOnItemLongClickListener((parent, view1, position, id) -> {
            RepositorioDBGeneralSingleton.getInstance(getContext()).deleteRule(rulesList.get(position).id);
            iniciarListas();
            adapterRules.notifyDataSetChanged();
            return true;
        });
    }

    /***
     * Iniciar o reiniciar listas con los cambios hechos. Notar que
     * el adaptador de la lista debe ser notificado con NotifyDatasetChanged();
     */
    private void iniciarListas(){
        rulesList.clear();
        rulesList.addAll(RepositorioDBGeneralSingleton.getInstance(getContext()).getRulesByDispositivo(dispositivoBase.id));
    }
}
