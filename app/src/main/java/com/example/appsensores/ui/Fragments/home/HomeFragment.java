package com.example.appsensores.ui.Fragments.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.appsensores.Clases.Enums.EnumTipoDispo;
import com.example.appsensores.Models.Dispositivos.BaseDispositivo;
import com.example.appsensores.Models.Dispositivos.DispoTelefono;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.example.appsensores.ui.Dialogs.DialogAgregarDispositivo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.example.appsensores.Clases.Enums.EnumTipoDispo.TELEFONO;

public class HomeFragment extends Fragment {

    /***
     * Este Array Contiene los dispositivos guardados en la app
     */
    private ArrayList<BaseDispositivo> listDispositivos = new ArrayList<BaseDispositivo>();
    private ArrayList<String> lisDispoString = new ArrayList<String>();

    ListView lv_fragment_home;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        listDispositivos = RepositorioDBGeneralSingleton.getInstance(getContext()).getDevices();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iniciarListas();

        final ArrayAdapter adapterDispositivos = new ArrayAdapter(getContext(),R.layout.list_unit_dispositivos,lisDispoString);

        lv_fragment_home = view.findViewById(R.id.lv_fragment_home);
        lv_fragment_home.setAdapter(adapterDispositivos);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                DialogAgregarDispositivo dialogAgregarDispositivo = new DialogAgregarDispositivo(getContext(), getActivity());
                dialogAgregarDispositivo.setCancelable(true);
                dialogAgregarDispositivo.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface v) {
                        iniciarListas();
                        adapterDispositivos.notifyDataSetChanged();
                    }
                });
                dialogAgregarDispositivo.show();
            }
        });

        lv_fragment_home.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getResources().getString(R.string.fragment_home_Eliminar))
                        .setPositiveButton(getResources().getString(R.string.fragment_home_Eliminar_si), (dialog, which) -> {
                            deleteDevice(position);
                            adapterDispositivos.notifyDataSetChanged();
                        })
                        .setNegativeButton(getResources().getString(R.string.fragment_home_Eliminar_no), (dialog, which) -> {}).show();
                return true;
            }
        });

        lv_fragment_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseDispositivo dispo = listDispositivos.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("idSensor", dispo.getId());
                String caso = (EnumTipoDispo.values()[dispo.getTipoDispositivo()]).toString();
                switch (EnumTipoDispo.valueOf(caso)){
                    case TELEFONO :
                        Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_fragmentDetalleTel,bundle);
                        break;
                    case THUNDERBOARD:
                        Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_fragmentThunderBoard,bundle);
                        break;
                    case SENSOR_PUCK:
                        Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_fragmentDetalleSensorPuck,bundle);
                        break;
                }
            }
        });
    }

    /***
     * Iniciar o reiniciar listas con los cambios hechos. Notar que
     * el adaptador de la lista debe ser notificado con NotifyDatasetChanged();
     */
    private void iniciarListas(){
        listDispositivos.clear();
        lisDispoString.clear();
        listDispositivos.addAll(RepositorioDBGeneralSingleton.getInstance(getContext()).getDevices());
        for (BaseDispositivo unit : listDispositivos) {
            lisDispoString.add(unit.getNombre() + " - " + ((EnumTipoDispo.values()[unit.getTipoDispositivo()])));
        }
    }

    /***
     * Metodo para eliminar un device de la base
     * @param index index del device en la lista de la UI
     */
    private void deleteDevice(int index){
        BaseDispositivo device = listDispositivos.get(index);
        if(device.getId() != 1) {
            RepositorioDBGeneralSingleton.getInstance(getContext()).deleteDevice(device.getId());
            iniciarListas();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.fragment_home_EliminarTelefono),Toast.LENGTH_SHORT).show();
        }
    }
}