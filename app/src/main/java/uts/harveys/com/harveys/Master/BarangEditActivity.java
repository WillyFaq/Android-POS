package uts.harveys.com.harveys.Master;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import uts.harveys.com.harveys.Adapter.SupplierCariAdapter;
import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.R;

public class BarangEditActivity extends AppCompatActivity {

    SQLiteDatabase db;

    ArrayList<String[]> dataSupplier = new ArrayList<String[]>();
    EditText txt_frm_barang_kode, txt_frm_barang_nama, txt_frm_barang_jenis, txt_frm_barang_warna, txt_frm_barang_size, txt_frm_barang_beli, txt_frm_barang_jual, txt_frm_barang_stok, txt_frm_barang_supplier, txt_frm_barang_id_supplier;
    Button btn_edit_barang, btn_hapus_barang, txt_frm_barang_btn_supplier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_edit);
        txt_frm_barang_kode = (EditText)findViewById(R.id.txt_frm_barang_kode);
        txt_frm_barang_nama = (EditText)findViewById(R.id.txt_frm_barang_nama);
        txt_frm_barang_jenis = (EditText)findViewById(R.id.txt_frm_barang_jenis);
        txt_frm_barang_warna = (EditText)findViewById(R.id.txt_frm_barang_warna);
        txt_frm_barang_size = (EditText)findViewById(R.id.txt_frm_barang_size);
        txt_frm_barang_beli = (EditText)findViewById(R.id.txt_frm_barang_beli);
        txt_frm_barang_jual = (EditText)findViewById(R.id.txt_frm_barang_jual);
        txt_frm_barang_stok = (EditText)findViewById(R.id.txt_frm_barang_stok);
        btn_edit_barang = (Button)findViewById(R.id.btn_edit_barang);
        btn_hapus_barang = (Button)findViewById(R.id.btn_hapus_barang);
        txt_frm_barang_btn_supplier = (Button)findViewById(R.id.txt_frm_barang_btn_supplier);
        txt_frm_barang_supplier = (EditText)findViewById(R.id.txt_frm_barang_supplier);
        txt_frm_barang_id_supplier = (EditText)findViewById(R.id.txt_frm_barang_id_supplier);

        initDB();
        get_data();
        btn_edit_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_data();
            }
        });
        btn_hapus_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BarangEditActivity.this);
                builder.setMessage("Apakah Anda Yakin?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete_data();
                            }
                        }).create().show();
            }
        });

        txt_frm_barang_btn_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cari_supplier();
            }
        });
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

    private void get_data(){
        Intent itnt = getIntent();
        String[] data = itnt.getStringArrayExtra("detail_barang");
        txt_frm_barang_kode.setText(data[0]);
        txt_frm_barang_nama.setText(data[1]);
        txt_frm_barang_jenis.setText(data[2]);
        txt_frm_barang_beli.setText(data[3]);
        txt_frm_barang_jual.setText(data[4]);
        txt_frm_barang_size.setText(data[5]);
        txt_frm_barang_warna.setText(data[6]);
        txt_frm_barang_stok.setText(data[7]);
        txt_frm_barang_id_supplier.setText(data[8]);
        txt_frm_barang_supplier.setText(data[9]);
        txt_frm_barang_kode.setEnabled(false);
    }

    private void update_data(){
        if(
                txt_frm_barang_kode.getText().toString().equals("") ||
                        txt_frm_barang_id_supplier.getText().toString().equals("") ||
                        txt_frm_barang_nama.getText().toString().equals("") ||
                        txt_frm_barang_jenis.getText().toString().equals("") ||
                        txt_frm_barang_beli.getText().toString().equals("") ||
                        txt_frm_barang_jual.getText().toString().equals("") ||
                        txt_frm_barang_size.getText().toString().equals("") ||
                        txt_frm_barang_warna.getText().toString().equals("") ||
                        txt_frm_barang_stok.getText().toString().equals("")
                ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Silahkan lengkkapi data!")
                    .setNegativeButton("Retry", null).create().show();
        }else{
            String sql_insert = "UPDATE Barang SET" +
                    " ID_SUPPLIER = '"+txt_frm_barang_id_supplier.getText().toString()+"', " +
                    " NAMA_BARANG = '"+txt_frm_barang_nama.getText().toString()+"', " +
                    " JENIS_BARANG = '"+txt_frm_barang_jenis.getText().toString()+"', " +
                    " HARGA_BELI = '"+txt_frm_barang_beli.getText().toString()+"', " +
                    " HARGA_JUAL = '"+txt_frm_barang_jual.getText().toString()+"', " +
                    " SIZE = '"+txt_frm_barang_size.getText().toString()+"', " +
                    " WARNA = '"+txt_frm_barang_warna.getText().toString()+"', " +
                    " STOK = '"+txt_frm_barang_stok.getText().toString()+"' " +
                    " WHERE ID_BARANG = '"+txt_frm_barang_kode.getText().toString()+"'";
            try {
                db.execSQL(sql_insert);
                Toast.makeText(getApplicationContext(), "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                finish();
            }catch (SQLException e){
                String er[] = e.getMessage().split(" ");
                String msg = "";
                if(er[4].equals("unique")){
                    msg = "\nID BARANG sudah ada!";
                }
                Toast.makeText(getApplicationContext(), "Data gagal diubah!"+msg, Toast.LENGTH_SHORT).show();
                Log.e("SQL ERROR", e.getMessage());
            }
        }
    }

    private void delete_data(){
        if(txt_frm_barang_kode.getText().toString().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Silahkan lengkkapi data!")
                    .setNegativeButton("Retry", null).create().show();
        }else{
            String sql_insert = "DELETE FROM Barang" +
                    " WHERE ID_BARANG = '"+txt_frm_barang_kode.getText().toString()+"'";
            try {
                db.execSQL(sql_insert);
                Toast.makeText(getApplicationContext(), "Data berhasil dihapus!", Toast.LENGTH_SHORT).show();
                finish();
            }catch (SQLException e){
                Toast.makeText(getApplicationContext(), "Data gagal dihapus!", Toast.LENGTH_SHORT).show();
                Log.e("SQL ERROR", e.getMessage());
            }
        }
    }



    private void get_data_supplier(String cari){
        Cursor c = db.rawQuery("SELECT * FROM Supplier WHERE NAMA_SUPPLIER LIKE '%"+cari+"%' ", null);
        dataSupplier.clear();
        while (c.moveToNext()){
            String[] ret = new String[4];
            ret[0] = c.getString(c.getColumnIndex("ID_SUPPLIER"));
            ret[1] = c.getString(c.getColumnIndex("NAMA_SUPPLIER"));
            ret[2] = c.getString(c.getColumnIndex("TELP_SUPPLIER"));
            ret[3] = c.getString(c.getColumnIndex("ALAMAT_SUPPLIER"));
            dataSupplier.add(ret);
        }
    }

    private void cari_supplier(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(BarangEditActivity.this);
        LayoutInflater inflter = getLayoutInflater();
        final View DialogView = inflter.inflate(R.layout.cari_supplier, null);
        builder.setView(DialogView)
                .setCancelable(true)
                .setNegativeButton("Batal", null);
        EditText cari_supplier_nama = (EditText)DialogView.findViewById(R.id.cari_supplier_nama);
        final ListView list_supplier = (ListView)DialogView.findViewById(R.id.list_supplier);
        get_data_supplier("");

        cari_supplier_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txt_cari_supplier(s.toString(), list_supplier, DialogView);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        SupplierCariAdapter sca = new SupplierCariAdapter(DialogView.getContext(), dataSupplier);
        list_supplier.setAdapter(sca);
        final AlertDialog opt = builder.create();
        opt.show();
        list_supplier.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                txt_frm_barang_supplier.setText(dataSupplier.get(i)[1]);
                txt_frm_barang_id_supplier.setText(dataSupplier.get(i)[0]);
                opt.dismiss();
            }
        });
    }

    public void txt_cari_supplier(String cari, ListView list, View v){
        get_data_supplier(cari);
        SupplierCariAdapter sca = new SupplierCariAdapter(v.getContext(), dataSupplier);
        list.setAdapter(sca);
    }
}
