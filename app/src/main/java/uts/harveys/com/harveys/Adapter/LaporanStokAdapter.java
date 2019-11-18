package uts.harveys.com.harveys.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import uts.harveys.com.harveys.R;

public class LaporanStokAdapter extends BaseExpandableListAdapter {
    Context context;
    List<String[]> list;
    HashMap<String[], List<String[]>> haslist;
    LayoutInflater inflter;

    public LaporanStokAdapter(Context applicationContext, List<String[]> stok_list, HashMap<String[], List<String[]>> has_stok_list){
        this.context = applicationContext;
        this.list = stok_list;
        this.haslist = has_stok_list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 0;//16;//haslist.get(list.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    public Object getChild(int i, int j) {
        return haslist.get(list.get(i)).get(j);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int j) {
        return j;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup vg) {
        if(!list.isEmpty()){
            view = inflter.inflate(R.layout.list_stok, null);
            TextView g_barang = (TextView)view.findViewById(R.id.txt_stok_barang);
            TextView g_stok = (TextView)view.findViewById(R.id.txt_stok_jumlah);
            TextView g_penjualan = (TextView)view.findViewById(R.id.txt_stok_penjualan);
            TextView g_pembelian = (TextView)view.findViewById(R.id.txt_stok_pembelian);

            g_barang.setText(list.get(i)[1]);
            g_stok.setText(list.get(i)[2]);
            g_penjualan.setText(list.get(i)[3]);
            g_pembelian.setText(list.get(i)[4]);
        }
        return view;
    }

    @Override
    public View getChildView(int i, int j, boolean b, View view, ViewGroup vg) {
        if(!haslist.get(list.get(i)).isEmpty()){
            view = inflter.inflate(R.layout.list_stok_detail, null);
            TextView c_barang = (TextView)view.findViewById(R.id.txt_stok_barang_detail);
            TextView c_stok = (TextView)view.findViewById(R.id.txt_stok_jumlah_detail);
            TextView c_penjualan = (TextView)view.findViewById(R.id.txt_stok_penjualan_detail);
            TextView c_pembelian = (TextView)view.findViewById(R.id.txt_stok_pembelian_detail);

            c_barang.setText(haslist.get(list.get(i)).get(j)[1]);
            c_stok.setText(haslist.get(list.get(i)).get(j)[2]);
            c_penjualan.setText(haslist.get(list.get(i)).get(j)[3]);
            c_pembelian.setText(haslist.get(list.get(i)).get(j)[4]);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
