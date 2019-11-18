package uts.harveys.com.harveys.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import uts.harveys.com.harveys.R;

public class LaporanDetailAdapter extends BaseAdapter {
    Context context;
    ArrayList<String[]> list;
    LayoutInflater inflter;

    public LaporanDetailAdapter(Context applicationContext, ArrayList<String[]> laporan_list){
        this.context = applicationContext;
        this.list = laporan_list;
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
            view = inflter.inflate(R.layout.list_laporan_detail, null);
            TextView txt_lap_barang = (TextView)view.findViewById(R.id.txt_lap_barang);
            TextView txt_id_lap = (TextView)view.findViewById(R.id.txt_id_lap);
            TextView txt_desc_lap = (TextView)view.findViewById(R.id.txt_desc_lap);
            TextView txt_tot_lap = (TextView)view.findViewById(R.id.txt_tot_lap);


            txt_id_lap.setText(list.get(i)[1]);
            txt_lap_barang.setText(list.get(i)[6]);


            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

            Double hrj = Double.parseDouble(list.get(i)[5]);
            String hj = formatRupiah.format((double)hrj);

            Double hrg = Double.parseDouble(list.get(i)[3]);
            String hg = formatRupiah.format((double)hrg);

            txt_desc_lap.setText(list.get(i)[4]+" | "+hg);
            txt_tot_lap.setText(hj);
        }
        return view;
    }
}
