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

public class BarangAdapter extends BaseAdapter {
    Context context;
    ArrayList<String[]> list;
    LayoutInflater inflter;

    public BarangAdapter(Context applicationContext, ArrayList<String[]> barang_list){
        this.context = applicationContext;
        this.list = barang_list;
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
            view = inflter.inflate(R.layout.list_barang, null);
            TextView txt_nama = (TextView)view.findViewById(R.id.txt_list_barang_nama);
            TextView txt_desc = (TextView)view.findViewById(R.id.txt_list_barang_desc);
            TextView txt_harga = (TextView)view.findViewById(R.id.txt_list_barang_harga);
            TextView txt_supplier = (TextView)view.findViewById(R.id.txt_list_barang_supllier);
            txt_nama.setText(list.get(i)[1]+" ("+list.get(i)[5]+")");
            txt_desc.setText(list.get(i)[0]+" | "+list.get(i)[2]+" | "+list.get(i)[6]+" | "+list.get(i)[7]);

            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

            Double hrd = Double.parseDouble(list.get(i)[3]);
            String hd = formatRupiah.format((double)hrd);
            Double hrj = Double.parseDouble(list.get(i)[4]);
            String hj = formatRupiah.format((double)hrj);
            txt_harga.setText("Harga Beli : "+hd+" | Harga Jual : "+hj);
            txt_supplier.setText("Supplier : "+list.get(i)[9]);
        }
        return view;
    }
}
