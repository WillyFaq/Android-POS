package uts.harveys.com.harveys.Master;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.R;

public class KaryawanInputActivity extends AppCompatActivity {

    SQLiteDatabase db;

    EditText txt_frm_karyawan_kode, txt_frm_karyawan_nama, txt_frm_karyawan_telp, txt_frm_karyawan_username, txt_frm_karyawan_password;
    Button btn_simpan_karyawan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyawan_input);
        txt_frm_karyawan_kode = (EditText)findViewById(R.id.txt_frm_karyawan_kode);
        txt_frm_karyawan_nama = (EditText)findViewById(R.id.txt_frm_karyawan_nama);
        txt_frm_karyawan_telp = (EditText)findViewById(R.id.txt_frm_karyawan_telp);
        txt_frm_karyawan_username = (EditText)findViewById(R.id.txt_frm_karyawan_username);
        txt_frm_karyawan_password = (EditText)findViewById(R.id.txt_frm_karyawan_password);
        btn_simpan_karyawan = (Button)findViewById(R.id.btn_simpan_karyawan);

        initDB();
        btn_simpan_karyawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_data();
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

    private void add_data(){
        if(
                txt_frm_karyawan_kode.getText().toString().equals("") ||
                txt_frm_karyawan_nama.getText().toString().equals("") ||
                txt_frm_karyawan_telp.getText().toString().equals("") ||
                txt_frm_karyawan_username.getText().toString().equals("") ||
                txt_frm_karyawan_password.getText().toString().equals("")
                ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Silahkan lengkkapi data!")
                    .setNegativeButton("Retry", null).create().show();
        }else{
            String sql_insert = "INSERT INTO Karyawan VALUES(" +
                    " '"+txt_frm_karyawan_kode.getText().toString()+"', " +
                    " '"+txt_frm_karyawan_nama.getText().toString()+"', " +
                    " '"+txt_frm_karyawan_telp.getText().toString()+"', " +
                    " '"+txt_frm_karyawan_username.getText().toString()+"', " +
                    " '"+txt_frm_karyawan_password.getText().toString()+"' " +
                    ")";
            try {
                db.execSQL(sql_insert);
                Toast.makeText(getApplicationContext(), "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                finish();
            }catch (SQLException e){
                String er[] = e.getMessage().split(" ");
                String msg = "";
                if(er[4].equals("unique")){
                    msg = "\nID KARYAWAN/USERNAME sudah ada!";
                }
                Toast.makeText(getApplicationContext(), "Data gagal ditambahkan!"+msg, Toast.LENGTH_SHORT).show();
                Log.e("SQL ERROR", e.getMessage());
            }
        }
    }
}
