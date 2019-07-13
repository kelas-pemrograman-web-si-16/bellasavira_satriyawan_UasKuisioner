package com.example.kuisioner.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kuisioner.R;
import com.example.kuisioner.data.DataPertanyaan;

import java.util.List;

public class AdapterPertanyaan extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataPertanyaan> item;

    public AdapterPertanyaan(Activity activity, List<DataPertanyaan> item) {
        this.activity = activity;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int location) {
        return item.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.pertanyaan, null);


        TextView kodepertanyaan     = (TextView) convertView.findViewById(R.id.kodepertanyaan);
        TextView pertanyaan         = (TextView) convertView.findViewById(R.id.tekspertanyaan);
        TextView nomor              = (TextView) convertView.findViewById(R.id.nomor);
        TextView radioya            = (TextView) convertView.findViewById(R.id.radioya);
        TextView radiotidak         = (TextView) convertView.findViewById(R.id.radiotidak);


        kodepertanyaan.setText(item.get(position).getKodepertanyaan());
        nomor.setText(item.get(position).getNomor());
        pertanyaan.setText(item.get(position).getPertanyaan());


        return convertView;
    }
}
