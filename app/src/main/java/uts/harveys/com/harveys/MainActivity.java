package uts.harveys.com.harveys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_master, btn_transaksi, btn_laporan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_master = (Button)findViewById(R.id.btn_master);
        btn_transaksi = (Button)findViewById(R.id.btn_transaksi);
        btn_laporan = (Button)findViewById(R.id.btn_laporan);

        btn_master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), MasterActivity.class);
                startActivity(itnt);
            }
        });
        btn_transaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), TransaksiActivity.class);
                startActivity(itnt);
            }
        });
        btn_laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), LaporanActivity.class);
                startActivity(itnt);
            }
        });
    }
}
