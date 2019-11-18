package uts.harveys.com.harveys;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase db;

    EditText txt_login_username, txt_login_password;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txt_login_username = (EditText)findViewById(R.id.txt_login_username);
        txt_login_password = (EditText)findViewById(R.id.txt_login_password);
        btn_login = (Button)findViewById(R.id.btn_login);
        initDB();
        initKaryawan();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = txt_login_username.getText().toString();
                String pass = txt_login_password.getText().toString();
                if(uname.equals("") || pass.equals("")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Isikan username/password!")
                            .setNegativeButton("Retry", null).create().show();
                }else{
                    Cursor c = db.rawQuery("SELECT * FROM Karyawan WHERE USERNAME = '"+uname+"' AND PASSWORD = '"+pass+"' ", null);
                    if(c.getCount()>0){
                        c.moveToFirst();
                        String nama = c.getString(c.getColumnIndex("NAMA_KARYAWAN"));
                        DbHelper.id_karyawan = c.getString(c.getColumnIndex("ID_KARYAWAN"));
                        DbHelper.karyawan = c.getString(c.getColumnIndex("NAMA_KARYAWAN"));
                        Toast.makeText(getApplication(), "Login Berhasil ! \nSelamat Datang "+nama, Toast.LENGTH_SHORT).show();
                        Intent intt = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intt);
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("username/password salah!")
                                .setNegativeButton("Retry", null).create().show();
                    }
                }

            }
        });
    }


    private void initDB(){
        try {
            db = openOrCreateDatabase(DbHelper.DBNAME, MODE_PRIVATE, null);
            db.execSQL(DbHelper.sql_karyawan);
            db.execSQL(DbHelper.sql_barang);
            db.execSQL(DbHelper.sql_supplier);
            db.execSQL(DbHelper.sql_penjualan);
            db.execSQL(DbHelper.sql_penjualan_detail);
            db.execSQL(DbHelper.sql_trigger_penjualan);
            db.execSQL(DbHelper.sql_pembelian);
            db.execSQL(DbHelper.sql_pembelian_detail);
            db.execSQL(DbHelper.sql_trigger_pembelian);
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan init Database!", Toast.LENGTH_SHORT).show();
            Log.e("SQL ERROR", e.getMessage());
        }
    }

    private void initKaryawan(){
        Cursor c = db.rawQuery("SELECT * FROM Karyawan", null);
        if(c.getCount()<1){
            db.execSQL("INSERT INTO karyawan VALUES('K0001', 'Administrator', '088888888888', 'a', 'a');");
        }
    }
}
