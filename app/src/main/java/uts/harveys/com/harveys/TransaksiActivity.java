package uts.harveys.com.harveys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uts.harveys.com.harveys.Master.KaryawanActivity;
import uts.harveys.com.harveys.Transaksi.PembelianActivity;
import uts.harveys.com.harveys.Transaksi.PenjualanActivity;

public class TransaksiActivity extends AppCompatActivity {

    Button btn_penjualan, btn_pembelian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);
        btn_penjualan = (Button)findViewById(R.id.btn_penjualan);
        btn_pembelian = (Button)findViewById(R.id.btn_pembelian);

        btn_penjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), PenjualanActivity.class);
                startActivity(itnt);
            }
        });
        btn_pembelian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), PembelianActivity.class);
                startActivity(itnt);
            }
        });
    }
}
