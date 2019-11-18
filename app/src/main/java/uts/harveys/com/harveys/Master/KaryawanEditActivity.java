package uts.harveys.com.harveys.Master;

import android.content.DialogInterface;
import android.content.Intent;
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

public class KaryawanEditActivity extends AppCompatActivity {

    SQLiteDatabase db;

    EditText txt_frm_karyawan_kode, txt_frm_karyawan_nama, txt_frm_karyawan_telp, txt_frm_karyawan_username, txt_frm_karyawan_password;
    Button btn_edit_karyawan, btn_hapus_karyawan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyawan_edit);
        txt_frm_karyawan_kode = (EditText)findViewById(R.id.txt_frm_karyawan_kode);
        txt_frm_karyawan_nama = (EditText)findViewById(R.id.txt_frm_karyawan_nama);
        txt_frm_karyawan_telp = (EditText)findViewById(R.id.txt_frm_karyawan_telp);
        txt_frm_karyawan_username = (EditText)findViewById(R.id.txt_frm_karyawan_username);
        txt_frm_karyawan_password = (EditText)findViewById(R.id.txt_frm_karyawan_password);
        btn_edit_karyawan = (Button)findViewById(R.id.btn_edit_karyawan);
        btn_hapus_karyawan = (Button)findViewById(R.id.btn_hapus_karyawan);

        initDB();
        get_data();
        btn_edit_karyawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_data();
            }
        });
        btn_hapus_karyawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(KaryawanEditActivity.this);
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
        Intent itnt = getIntent();
        String[] data = itnt.getStringArrayExtra("detail_karyawan");
        txt_frm_karyawan_kode.setText(data[0]);
        txt_frm_karyawan_nama.setText(data[1]);
        txt_frm_karyawan_telp.setText(data[2]);
        txt_frm_karyawan_username.setText(data[3]);
        txt_frm_karyawan_password.setText(data[4]);
        txt_frm_karyawan_kode.setEnabled(false);
    }

    private void update_data(){
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
            String sql_insert = "UPDATE Karyawan SET" +
                    " NAMA_KARYAWAN = '"+txt_frm_karyawan_nama.getText().toString()+"', " +
                    " TELP_KARYAWAN = '"+txt_frm_karyawan_telp.getText().toString()+"', " +
                    " USERNAME = '"+txt_frm_karyawan_username.getText().toString()+"', " +
                    " PASSWORD = '"+txt_frm_karyawan_password.getText().toString()+"' " +
                    " WHERE ID_KARYAWAN = '"+txt_frm_karyawan_kode.getText().toString()+"'";
            try {
                db.execSQL(sql_insert);
                Toast.makeText(getApplicationContext(), "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                finish();
            }catch (SQLException e){
                String er[] = e.getMessage().split(" ");
                String msg = "";
                if(er[4].equals("unique")){
                    msg = "\nID KARYAWAN/USERNAME sudah ada!";
                }
                Toast.makeText(getApplicationContext(), "Data gagal diubah!"+msg, Toast.LENGTH_SHORT).show();
                Log.e("SQL ERROR", e.getMessage());
            }
        }
    }

    private void delete_data(){
        if(txt_frm_karyawan_kode.getText().toString().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Silahkan lengkkapi data!")
                    .setNegativeButton("Retry", null).create().show();
        }else{
            String sql_insert = "DELETE FROM Karyawan" +
                    " WHERE ID_KARYAWAN = '"+txt_frm_karyawan_kode.getText().toString()+"'";
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
}
