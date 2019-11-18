package uts.harveys.com.harveys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uts.harveys.com.harveys.Master.BarangActivity;
import uts.harveys.com.harveys.Master.KaryawanActivity;
import uts.harveys.com.harveys.Master.SupplierActivity;

public class MasterActivity extends AppCompatActivity {

    Button btn_karyawan, btn_barang, btn_supplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        btn_karyawan = (Button)findViewById(R.id.btn_karyawan);
        btn_barang = (Button)findViewById(R.id.btn_barang);
        btn_supplier = (Button)findViewById(R.id.btn_supplier);

        btn_karyawan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), KaryawanActivity.class);
                startActivity(itnt);
            }
        });
        btn_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), BarangActivity.class);
                startActivity(itnt);
            }
        });
        btn_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), SupplierActivity.class);
                startActivity(itnt);
            }
        });
    }
}
