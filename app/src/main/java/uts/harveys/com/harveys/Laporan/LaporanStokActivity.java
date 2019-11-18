package uts.harveys.com.harveys.Laporan;

import android.app.ExpandableListActivity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import uts.harveys.com.harveys.Adapter.LaporanAdapter;
import uts.harveys.com.harveys.Adapter.LaporanStokAdapter;
import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.R;

public class LaporanStokActivity extends AppCompatActivity {

    SQLiteDatabase db;

    ExpandableListView list_laporan;
    List<String[]> Stok = new ArrayList<String[]>();;
    HashMap<String[], List<String[]>> detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_stok);
        list_laporan = (ExpandableListView)findViewById(R.id.list_laporan);
    }

    private void initDB(){
        try {
            db = openOrCreateDatabase(DbHelper.DBNAME, MODE_PRIVATE, null);
            db.execSQL(DbHelper.sql_barang);
            db.execSQL(DbHelper.sql_pembelian);
            db.execSQL(DbHelper.sql_pembelian_detail);
            db.execSQL(DbHelper.sql_penjualan);
            db.execSQL(DbHelper.sql_penjualan_detail);
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan init Database!", Toast.LENGTH_SHORT).show();
            Log.e("SQL ERROR", e.getMessage());
        }
    }

    private void get_data(){

        /*if(!tgl_awal.getText().toString().equals("") || !tgl_akhir.getText().toString().equals("")){
            sql += " WHERE TANGGAL_PEMBELIAN BETWEEN '"+tgl_awal.getText().toString()+"' AND '"+tgl_akhir.getText().toString()+"' ";
        }*/

        String sql = "SELECT " +
                                " a.ID_BARANG, " +
                                " a.NAMA_BARANG, " +
                                " a.STOK, " +
                                " (SELECT SUM(b.JUMLAH) FROM Penjualan_detail b WHERE b.ID_BARANG = a.ID_BARANG) AS PENJUALAN, " +
                                " (SELECT SUM(c.JUMLAH) FROM Pembelian_detail c WHERE c.ID_BARANG = a.ID_BARANG) AS PEMBELIAN " +
                                " FROM Barang a";
        Log.e("SQL", sql);
        try{
            Cursor c = db.rawQuery(sql, null);
            Stok.clear();
            int i= 0;

            List<String[]> dd = new ArrayList<String[]>();
            while (c.moveToNext()){
                String[] ret = new String[5];
                ret[0] = c.getString(c.getColumnIndex("ID_BARANG"));
                ret[1] = c.getString(c.getColumnIndex("NAMA_BARANG"));
                ret[2] = c.getString(c.getColumnIndex("STOK"));
                ret[3] = c.getString(c.getColumnIndex("PENJUALAN"));
                ret[4] = c.getString(c.getColumnIndex("PEMBELIAN"));
                Stok.add(ret);
                /*detail = new HashMap<String[], List<String[]>>();

                dd.clear();
                for(int ii = 0; ii<=5; ii++){
                    String[] aret = new String[5];
                    aret[0] = String.valueOf(ii)+"\n"+ret[0];
                    aret[1] = String.valueOf(ii + Math.random());
                    aret[2] = String.valueOf(ii + Math.random());;
                    aret[3] = String.valueOf(ii + Math.random());;
                    aret[4] = String.valueOf(ii + Math.random());;
                    dd.add(aret);
                }
                detail.put(Stok.get(i), dd);
                String a = detail.get(Stok.get(i)).get(2)[0];
                ///Log.e("A", a);
                String sql_det = "SELECT" +
                        " TANGGAL_PENJUALAN, " +
                        " , " +
                        "";
                Log.e("INI "+String.valueOf(i), String.valueOf(detail.get(Stok.get(i)).size()));
                Log.e("SIZE", detail.toString());
                Log.e("PARENT", Stok.get(i).toString());*/
                i++;
            }


            LaporanStokAdapter ca = new LaporanStokAdapter(getApplicationContext(), Stok, detail);
            list_laporan.setAdapter(ca);
        }catch (SQLException e){
            Log.e("SQL STOK", sql);
            Log.e("SQL STOK", e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDB();
        get_data();
    }
}
