package uts.harveys.com.harveys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uts.harveys.com.harveys.Laporan.LaporanPembelianActivity;
import uts.harveys.com.harveys.Laporan.LaporanPembelianDetailActivity;
import uts.harveys.com.harveys.Laporan.LaporanPenjualanActivity;
import uts.harveys.com.harveys.Laporan.LaporanPenjualanDetailActivity;
import uts.harveys.com.harveys.Laporan.LaporanStokActivity;
import uts.harveys.com.harveys.Transaksi.PenjualanActivity;

public class LaporanActivity extends AppCompatActivity {

    Button btn_laporan_penjualan, btn_laporan_penjualan_detail, btn_laporan_pembelian, btn_laporan_pembelian_detail, btn_laporan_stok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);
        btn_laporan_penjualan = (Button)findViewById(R.id.btn_laporan_penjualan);
        btn_laporan_penjualan_detail = (Button)findViewById(R.id.btn_laporan_penjualan_detail);
        btn_laporan_pembelian = (Button)findViewById(R.id.btn_laporan_pembelian);
        btn_laporan_pembelian_detail = (Button)findViewById(R.id.btn_laporan_pembelian_detail);
        btn_laporan_stok = (Button)findViewById(R.id.btn_laporan_stok);

        btn_laporan_penjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), LaporanPenjualanActivity.class);
                startActivity(itnt);
            }
        });
        btn_laporan_penjualan_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), LaporanPenjualanDetailActivity.class);
                startActivity(itnt);
            }
        });
        btn_laporan_pembelian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), LaporanPembelianActivity.class);
                startActivity(itnt);
            }
        });
        btn_laporan_pembelian_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), LaporanPembelianDetailActivity.class);
                startActivity(itnt);
            }
        });
        btn_laporan_stok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), LaporanStokActivity.class);
                startActivity(itnt);
            }
        });
    }
}
