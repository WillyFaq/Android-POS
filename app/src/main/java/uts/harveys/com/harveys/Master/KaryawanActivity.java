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

import uts.harveys.com.harveys.Adapter.KaryawanAdapter;
import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.MasterActivity;
import uts.harveys.com.harveys.R;

public class KaryawanActivity extends AppCompatActivity {

    SQLiteDatabase db;

    ArrayList<String[]> dataKaryawan = new ArrayList<String[]>();
    ImageButton btn_add_karyawan;
    ListView list_karyawan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyawan);
        btn_add_karyawan = (ImageButton)findViewById(R.id.btn_add_karyawan);
        list_karyawan = (ListView)findViewById(R.id.list_karyawan);

        btn_add_karyawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), KaryawanInputActivity.class);
                startActivity(itnt);
            }
        });

    }


    private void initDB(){
        try {
            db = openOrCreateDatabase(DbHelper.DBNAME, MODE_PRIVATE, null);
            db.execSQL(DbHelper.sql_karyawan);
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan init Database!", Toast.LENGTH_SHORT).show();
            Log.e("SQL ERROR", e.getMessage());
        }
    }


    private void get_data(){
        Cursor c = db.rawQuery("SELECT * FROM Karyawan", null);
        dataKaryawan.clear();
        while (c.moveToNext()){
            String[] ret = new String[5];
            ret[0] = c.getString(c.getColumnIndex("ID_KARYAWAN"));
            ret[1] = c.getString(c.getColumnIndex("NAMA_KARYAWAN"));
            ret[2] = c.getString(c.getColumnIndex("TELP_KARYAWAN"));
            ret[3] = c.getString(c.getColumnIndex("USERNAME"));
            ret[4] = c.getString(c.getColumnIndex("PASSWORD"));
            dataKaryawan.add(ret);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDB();
        get_data();

        KaryawanAdapter ca = new KaryawanAdapter(getApplicationContext(), dataKaryawan);
        list_karyawan.setAdapter(ca);
        list_karyawan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent itnt = new Intent(getApplicationContext(), KaryawanEditActivity.class);
                itnt.putExtra("detail_karyawan", dataKaryawan.get(i));
                startActivity(itnt);
            }
        });
    }
}
