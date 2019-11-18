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
import uts.harveys.com.harveys.Adapter.SupplierAdapter;
import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.R;

public class SupplierActivity extends AppCompatActivity {

    SQLiteDatabase db;

    ArrayList<String[]> dataSupplier = new ArrayList<String[]>();
    ImageButton btn_add_supplier;
    ListView list_supplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        btn_add_supplier = (ImageButton)findViewById(R.id.btn_add_supplier);
        list_supplier = (ListView)findViewById(R.id.list_supplier);

        btn_add_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), SupplierInputActivity.class);
                startActivity(itnt);
            }
        });
    }

    private void initDB(){
        try {
            db = openOrCreateDatabase(DbHelper.DBNAME, MODE_PRIVATE, null);
            db.execSQL(DbHelper.sql_supplier);
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan init Database!", Toast.LENGTH_SHORT).show();
            Log.e("SQL ERROR", e.getMessage());
        }
    }


    private void get_data(){
        Cursor c = db.rawQuery("SELECT * FROM Supplier", null);
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

    @Override
    protected void onStart() {
        super.onStart();
        initDB();
        get_data();

        SupplierAdapter ca = new SupplierAdapter(getApplicationContext(), dataSupplier);
        list_supplier.setAdapter(ca);
        list_supplier.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent itnt = new Intent(getApplicationContext(), SupplierEditActivity.class);
                itnt.putExtra("detail_supplier", dataSupplier.get(i));
                startActivity(itnt);
            }
        });
    }
}
