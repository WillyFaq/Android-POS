package uts.harveys.com.harveys.Master;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import uts.harveys.com.harveys.Adapter.BarangAdapter;
import uts.harveys.com.harveys.Adapter.KaryawanAdapter;
import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.R;

public class BarangActivity extends AppCompatActivity {

    SQLiteDatabase db;

    ArrayList<String[]> dataBarang = new ArrayList<String[]>();
    ImageButton btn_add_barang;
    ListView list_barang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);
        btn_add_barang = (ImageButton)findViewById(R.id.btn_add_barang);
        list_barang = (ListView)findViewById(R.id.list_barang);

        btn_add_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), BarangInputActivity.class);
                startActivity(itnt);
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
        Cursor c = db.rawQuery("SELECT * FROM Barang a JOIN Supplier b ON a.ID_SUPPLIER = b.ID_SUPPLIER", null);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDB();
        get_data();

        BarangAdapter ca = new BarangAdapter(getApplicationContext(), dataBarang);
        list_barang.setAdapter(ca);
        list_barang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent itnt = new Intent(getApplicationContext(), BarangEditActivity.class);
                itnt.putExtra("detail_barang", dataBarang.get(i));
                startActivity(itnt);
            }
        });
    }
}
