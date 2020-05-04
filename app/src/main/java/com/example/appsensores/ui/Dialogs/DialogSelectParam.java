package com.example.appsensores.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appsensores.R;

import java.util.ArrayList;

public class DialogSelectParam extends Dialog {

    private Spinner spnr_dialog_param;
    private Button btn_select_param_ok;
    private ArrayList<String> params;
    private ArrayAdapter<String> adapter;

    public String resp = "";

    public DialogSelectParam(@NonNull Context context, int themeResId, ArrayList<String> params) {
        super(context, themeResId);
        this.params = params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_param);

        setViews();
    }

    private void setViews() {
        btn_select_param_ok = findViewById(R.id.btn_select_param_ok);
        spnr_dialog_param = findViewById(R.id.spnr_dialog_param);
        adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item,params);
        spnr_dialog_param.setAdapter(adapter);

        btn_select_param_ok.setOnClickListener(v -> {
            if(spnr_dialog_param.getSelectedItemPosition() == 0){
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.dialog_select_param_adv), Toast.LENGTH_SHORT).show();
                return;
            }
            resp = params.get(spnr_dialog_param.getSelectedItemPosition());
            dismiss();
        });
    }
}
