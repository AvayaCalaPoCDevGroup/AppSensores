package com.example.appsensores.Clases.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.appsensores.Clases.Enums.SensorTypes;
import com.example.appsensores.Models.Rule;
import com.example.appsensores.R;
import com.example.appsensores.Repositorio.RepositorioDBGeneralSingleton;
import com.example.appsensores.ui.Dialogs.DialogRuleDetails;

import java.util.ArrayList;

public class AdapterRules extends ArrayAdapter<Rule> {

    private ArrayList<Rule> mRuleList;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tvRule;
        Switch swRule;
        Button btnDelete;
    }

    public AdapterRules(@NonNull Context context, int resource, @NonNull ArrayList<Rule> objects) {
        super(context, resource, objects);
        mContext = context;
        mRuleList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Rule rule = mRuleList.get(position);

        ViewHolder viewHolder;

        final View result;

        if(convertView == null){

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_unit_rules,null);
            viewHolder.tvRule = convertView.findViewById(R.id.tv_unit_rule);
            viewHolder.swRule = convertView.findViewById(R.id.sw_unit_enabled);
            viewHolder.btnDelete = convertView.findViewById(R.id.btn_unit_delete);

            result = convertView;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        String[] sensorAmbient = SensorTypes.getSensorAmbientList(getContext());
        String[] ruleTypes = SensorTypes.getRuleTypes(getContext());

        String ruleText = "";
        if(rule.RuleId == SensorTypes.ENTRE){
            ruleText = sensorAmbient[rule.SensorId] + " " + ruleTypes[rule.RuleId] + " " + rule.Value1 + " - " + rule.Value2;
        } else {
            ruleText = sensorAmbient[rule.SensorId] + " " + ruleTypes[rule.RuleId] + " " + rule.Value1;
        }
        viewHolder.tvRule.setText(ruleText);
        viewHolder.swRule.setChecked(rule.IsEnabled);
        viewHolder.btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(mContext.getResources().getString(R.string.dialog_rule_settings_delete))
                    .setPositiveButton(mContext.getString(R.string.fragment_home_Eliminar_si), (dialog, which) -> {
                        RepositorioDBGeneralSingleton.getInstance(mContext).deleteRule(mRuleList.get(position).id);
                        UpdateList(mRuleList.get(position).DispositivoId);
                    })
                    .setNegativeButton(mContext.getString(R.string.fragment_home_Eliminar_no), (dialog, which) -> {}).show();
        });
        viewHolder.swRule.setOnCheckedChangeListener((buttonView, isChecked) -> {

            mRuleList.get(position).IsEnabled = isChecked;
            RepositorioDBGeneralSingleton.getInstance(mContext).updateRule(mRuleList.get(position));

        });
        viewHolder.tvRule.setOnClickListener(v -> {
            DialogRuleDetails dlogDetails = new DialogRuleDetails(getContext(), R.style.custom_dialog, mRuleList.get(position));
            dlogDetails.show();
        });

        return convertView;
    }

    private void UpdateList(int idDispositivo){
        mRuleList.clear();
        mRuleList.addAll(RepositorioDBGeneralSingleton.getInstance(mContext).getRulesByDispositivo(idDispositivo));
        notifyDataSetChanged();
    }
}
