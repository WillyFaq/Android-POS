package uts.harveys.com.harveys.Transaksi;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import uts.harveys.com.harveys.Adapter.BarangAdapter;
import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.R;

public class PembelianBarangActivity extends AppCompatActivity {

    SQLiteDatabase db;

    ArrayList<String[]> dataBarang = new ArrayList<String[]>();
    ListView list_barang;
    EditText cari_barang_nama;
    String id_supplier = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembelian_barang);
        list_barang = (ListView)findViewById(R.id.list_barang_penjualan);
        cari_barang_nama = (EditText)findViewById(R.id.cari_barang_nama);

        Intent itnt = getIntent();
        id_supplier = itnt.getStringExtra("id_supplier");
    }

    private void initDB(){
        try {
            db = openOrCreateDatabase(DbHelper.DBNAME, MODE_PRIVATE, null);
            db.execSQL(DbHelper.sql_barang);
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan init Database!", Toast.LENGTH_SHORT).show();
            Log.e("SQL ERROR", e.getMessage());
        }
    }


    private void get_data(String cari){
        Log.e("ID SUP", id_supplier);
        String sql = "SELECT * FROM Barang a JOIN Supplier b ON a.ID_SUPPLIER = b.ID_SUPPLIER" +
                " WHERE a.ID_SUPPLIER = '"+id_supplier+"' AND (NAMA_BARANG LIKE '%"+cari+"%' OR JENIS_BARANG LIKE '%"+cari+"%')";
        Cursor c = db.rawQuery(sql, null);
        dataBarang.clear();
        while (c.moveToNext()){
            String[] ret = new String[10];
            ret[0] = c.getString(c.getColumnIndex("ID_BARANG"));
            ret[1] = c.getString(c.getColumnIndex("NAMA_BARANG"));
            ret[2] = c.getString(c.getColumnIndex("JENIS_BARANG"));
            ret[3] = c.getString(c.getColumnIndex("HARGA_BELI"));
            ret[4] = c.getString(c.getColumnIndex("HARGA_JUAL"));
            ret[5] = c.getString(c.getColumnIndex("SIZE"));
            ret[6] = c.getString(c.getColumnIndex("WARNA"));
            ret[7] = c.getString(c.getColumnIndex("STOK"));
            ret[8] = c.getString(c.getColumnIndex("ID_SUPPLIER"));
            ret[9] = c.getString(c.getColumnIndex("NAMA_SUPPLIER"));
            dataBarang.add(ret);
        }
        BarangAdapter ca = new BarangAdapter(getApplicationContext(), dataBarang);
        list_barang.setAdapter(ca);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDB();
        get_data("");

        cari_barang_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                get_data(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        list_barang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                final String idb = dataBarang.get(i)[0];
                final String nmb = dataBarang.get(i)[1]+" ("+dataBarang.get(i)[5]+")";
                final Double hrg = Double.parseDouble(dataBarang.get(i)[3]);
                final int stk = Integer.parseInt(dataBarang.get(i)[7]);
                final AlertDialog.Builder builder = new AlertDialog.Builder(PembelianBarangActivity.this);
                LayoutInflater inflter = getLayoutInflater();
                final View DialogView = inflter.inflate(R.layout.tambah_barang_penjualan, null);
                builder.setView(DialogView)
                        .setCancelable(true)
                        .setTitle("Tambah Barang")
                        .setNegativeButton("Batal", null);
                final EditText txt_jumlah_barang = (EditText)DialogView.findViewById(R.id.txt_jumlah_barang);
                builder.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(txt_jumlah_barang.getText().toString().equals("") || txt_jumlah_barang.getText().toString().equals(0)){
                            Toast.makeText(getApplicationContext(), "Silahkan isikan jumlah", Toast.LENGTH_LONG).show();
                        }else{
                            String jmlh = txt_jumlah_barang.getText().toString();
                            int jml = Integer.parseInt(jmlh);
                            int ca = cek_ada(idb);
                            if(ca>=0){
                                int j = jml + Integer.parseInt(DbHelper.detailPenjualan.get(ca)[3]);
                                Double st = hrg*j;
                                String[] det = new String[5];
                                det[0] = idb;
                                det[1] = nmb;
                                det[2] = String.valueOf(hrg);
                                det[3] = String.valueOf(j);
                                det[4] = String.valueOf(st);
                                DbHelper.detailPembelian.set(ca, det);
                                finish();
                            }else{
                                Double st = hrg*jml;
                                String[] det = new String[5];
                                det[0] = idb;
                                det[1] = nmb;
                                det[2] = String.valueOf(hrg);
                                det[3] = jmlh;
                                det[4] = String.valueOf(st);
                                DbHelper.detailPembelian.add(det);
                                finish();
                            }
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    private int cek_ada(String id){
        for(int i=0;i<DbHelper.detailPembelian.size();i++){
            String idb = DbHelper.detailPembelian.get(i)[0];
            if(id.equals(idb)){
                return i;
            }
        }
        return -1;
    }
}
