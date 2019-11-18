package uts.harveys.com.harveys.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import uts.harveys.com.harveys.R;

import java.util.ArrayList;


public class KaryawanAdapter extends BaseAdapter {

    Context context;
    ArrayList<String[]> list;
    LayoutInflater inflter;

    public KaryawanAdapter(Context applicationContext, ArrayList<String[]> karyawan_list){
        this.context = applicationContext;
        this.list = karyawan_list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if(!list.isEmpty()){
            view = inflter.inflate(R.layout.list_karyawan, null);
            TextView txt_nama = (TextView)view.findViewById(R.id.txt_list_karyawan_nama);
            TextView txt_desc = (TextView)view.findViewById(R.id.txt_list_karyawan_desc);
            txt_nama.setText(list.get(i)[1]);
            txt_desc.setText(list.get(i)[0]+" | "+list.get(i)[2]+" | "+list.get(i)[3]);
        }
        return view;
    }
}
