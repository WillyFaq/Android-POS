package uts.harveys.com.harveys.Laporan;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uts.harveys.com.harveys.Adapter.LaporanAdapter;
import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.R;

public class LaporanPembelianActivity extends AppCompatActivity {

    SQLiteDatabase db;

    ArrayList<String[]> dataLaporan = new ArrayList<String[]>();
    EditText tgl_awal, tgl_akhir;
    TextView txt_total_lap;
    ListView list_laporan;
    Double total = 0.0;
    DatePickerDialog.OnDateSetListener OnDateListener, OnDateListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_pembelian);
        tgl_awal = (EditText)findViewById(R.id.tgl_awal);
        tgl_akhir = (EditText)findViewById(R.id.tgl_akhir);
        txt_total_lap = (TextView)findViewById(R.id.txt_total_lap);
        list_laporan = (ListView)findViewById(R.id.list_laporan);

        String myDate =  new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tgl_awal.setText(myDate);
        tgl_akhir.setText(myDate);

        tgl_awal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                get_data();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tgl_akhir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                get_data();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tgl_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH);
                int day = ca.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        LaporanPembelianActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        OnDateListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        OnDateListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String day = (dayOfMonth<10)?"0"+dayOfMonth:String.valueOf(dayOfMonth);
                String date = day +"/"+ month +"/"+ year;
                tgl_awal.setText(date);
            }
        };
        tgl_akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH);
                int day = ca.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        LaporanPembelianActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        OnDateListener2,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        OnDateListener2 = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String day = (dayOfMonth<10)?"0"+dayOfMonth:String.valueOf(dayOfMonth);
                String date = day +"/"+ month +"/"+ year;
                tgl_akhir.setText(date);
            }
        };
    }

    private void initDB(){
        try {
            db = openOrCreateDatabase(DbHelper.DBNAME, MODE_PRIVATE, null);
            db.execSQL(DbHelper.sql_pembelian);
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan init Database!", Toast.LENGTH_SHORT).show();
            Log.e("SQL ERROR", e.getMessage());
        }
    }

    private void get_data(){
        String sql = "SELECT * FROM Pembelian a JOIN Karyawan b ON a.ID_KARYAWAN = b.ID_KARYAWAN JOIN Supplier c ON a.ID_SUPPLIER = c.ID_SUPPLIER";
        if(!tgl_awal.getText().toString().equals("") || !tgl_akhir.getText().toString().equals("")){
            sql += " WHERE TANGGAL_PEMBELIAN BETWEEN '"+tgl_awal.getText().toString()+"' AND '"+tgl_akhir.getText().toString()+"' ";
        }
        Log.e("SQL", sql);
        Cursor c = db.rawQuery(sql, null);
        dataLaporan.clear();
        total = 0.0;
        while (c.moveToNext()){
            String[] ret = new String[6];
            ret[0] = c.getString(c.getColumnIndex("ID_PEMBELIAN"));
            ret[1] = c.getString(c.getColumnIndex("ID_KARYAWAN"));
            ret[2] = c.getString(c.getColumnIndex("NAMA_SUPPLIER"));
            ret[3] = c.getString(c.getColumnIndex("TANGGAL_PEMBELIAN"));
            ret[4] = c.getString(c.getColumnIndex("TOTAL"));
            ret[5] = c.getString(c.getColumnIndex("NAMA_KARYAWAN"));
            dataLaporan.add(ret);
            total += Double.parseDouble(ret[4]);
        }

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        txt_total_lap.setText(formatRupiah.format((double)total));
        LaporanAdapter ca = new LaporanAdapter(getApplicationContext(), dataLaporan);
        list_laporan.setAdapter(ca);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDB();
        get_data();
    }
}
