package uts.harveys.com.harveys.Transaksi;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uts.harveys.com.harveys.Adapter.SupplierCariAdapter;
import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.Master.BarangInputActivity;
import uts.harveys.com.harveys.R;

public class PembelianActivity extends AppCompatActivity {

    SQLiteDatabase db;

    ArrayList<String[]> dataSupplier = new ArrayList<String[]>();
    EditText txt_frm_pembelian_kode, txt_frm_pembelian_tanggal, txt_frm_pembelian_supplier, txt_frm_pembelian_id_supplier;
    TableLayout tbl_det_barang;
    Button btn_transaksi_tambah_barang, btn_transaksi_simpan, txt_frm_pembelian_btn_supplier;
    TextView txt_frm_pembelian_total;
    DatePickerDialog.OnDateSetListener OnDateListener;

    Double grandTotal = 0.0;
    int nm_trans = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembelian);
        txt_frm_pembelian_kode = (EditText)findViewById(R.id.txt_frm_pembelian_kode);
        txt_frm_pembelian_tanggal = (EditText)findViewById(R.id.txt_frm_pembelian_tanggal);
        txt_frm_pembelian_supplier = (EditText)findViewById(R.id.txt_frm_pembelian_supplier);
        txt_frm_pembelian_id_supplier = (EditText)findViewById(R.id.txt_frm_pembelian_id_supplier);
        tbl_det_barang = (TableLayout)findViewById(R.id.tbl_det_barang);
        btn_transaksi_tambah_barang = (Button)findViewById(R.id.btn_transaksi_tambah_barang);
        btn_transaksi_simpan = (Button)findViewById(R.id.btn_transaksi_simpan);
        txt_frm_pembelian_btn_supplier = (Button)findViewById(R.id.txt_frm_pembelian_btn_supplier);
        txt_frm_pembelian_total = (TextView)findViewById(R.id.txt_frm_pembelian_total);


        String myDate =  new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        txt_frm_pembelian_tanggal.setText(myDate);
        txt_frm_pembelian_supplier.requestFocus();
        initDB();
        get_data();
        gen_id_trans();
        txt_frm_pembelian_btn_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cari_supplier();
            }
        });
        btn_transaksi_tambah_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_frm_pembelian_id_supplier.getText().toString().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PembelianActivity.this);
                    builder.setMessage("Silahkan Pilih Supplier!")
                            .setNegativeButton("Retry", null).create().show();
                }else{
                    Intent itnt = new Intent(getApplicationContext(), PembelianBarangActivity.class);
                    itnt.putExtra("id_supplier", txt_frm_pembelian_id_supplier.getText().toString());
                    startActivity(itnt);
                }
            }
        });
        btn_transaksi_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan_transaksi();
            }
        });
        txt_frm_pembelian_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH);
                int day = ca.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        PembelianActivity.this,
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
                String date = dayOfMonth +"/"+ month +"/"+ year;
                txt_frm_pembelian_tanggal.setText(date);
            }
        };

        DbHelper.detailPembelian.clear();
    }

    private void gen_id_trans(){
        String num = "0000000"+String.valueOf(nm_trans);
        String nt = "PNB-"+num.substring(num.length()-5, num.length());
        txt_frm_pembelian_kode.setText(nt);
        Log.e("GEN - ", num);
        Log.e("GEN - ", nt);
    }

    private void initDB(){
        try {
            db = openOrCreateDatabase(DbHelper.DBNAME, MODE_PRIVATE, null);
            db.execSQL(DbHelper.sql_pembelian);
            db.execSQL(DbHelper.sql_pembelian_detail);
            db.execSQL(DbHelper.sql_trigger_pembelian);
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan init Database!", Toast.LENGTH_SHORT).show();
            Log.e("SQL ERROR", e.getMessage());
        }
    }

    private void get_data(){
        Cursor c = db.rawQuery("SELECT * FROM Pembelian", null);
        nm_trans = c.getCount()+1;
    }


    private void get_data_supplier(String cari){
        Cursor c = db.rawQuery("SELECT * FROM Supplier WHERE NAMA_SUPPLIER LIKE '%"+cari+"%' ", null);
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

    private void cari_supplier(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PembelianActivity.this);
        LayoutInflater inflter = getLayoutInflater();
        final View DialogView = inflter.inflate(R.layout.cari_supplier, null);
        builder.setView(DialogView)
                .setCancelable(true)
                .setNegativeButton("Batal", null);
        EditText cari_supplier_nama = (EditText)DialogView.findViewById(R.id.cari_supplier_nama);
        final ListView list_supplier = (ListView)DialogView.findViewById(R.id.list_supplier);
        get_data_supplier("");

        cari_supplier_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txt_cari_supplier(s.toString(), list_supplier, DialogView);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        SupplierCariAdapter sca = new SupplierCariAdapter(DialogView.getContext(), dataSupplier);
        list_supplier.setAdapter(sca);
        final AlertDialog opt = builder.create();
        opt.show();
        list_supplier.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                txt_frm_pembelian_supplier.setText(dataSupplier.get(i)[1]);
                txt_frm_pembelian_id_supplier.setText(dataSupplier.get(i)[0]);
                DbHelper.detailPembelian.clear();
                get_detail();
                opt.dismiss();
            }
        });
    }

    public void txt_cari_supplier(String cari, ListView list, View v){
        get_data_supplier(cari);
        SupplierCariAdapter sca = new SupplierCariAdapter(v.getContext(), dataSupplier);
        list.setAdapter(sca);
    }



    private void get_detail() {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        int co = tbl_det_barang.getChildCount();
        for(int i = 1; i<co; i++){
            View child = tbl_det_barang.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
        grandTotal = 0.0;
        for(int i=0;i<DbHelper.detailPembelian.size();i++){
            TableRow row = new TableRow(this);
            TextView tid = new TextView(this);
            TextView tno = new TextView(this);
            TextView tna = new TextView(this);
            TextView tjm = new TextView(this);
            TextView thg = new TextView(this);
            TextView tot = new TextView(this);

            tno.setTextColor(getResources().getColor(R.color.colorMain));
            tna.setTextColor(getResources().getColor(R.color.colorMain));
            tjm.setTextColor(getResources().getColor(R.color.colorMain));
            thg.setTextColor(getResources().getColor(R.color.colorMain));
            tot.setTextColor(getResources().getColor(R.color.colorMain));

            tna.setPadding(18,0,18,0);
            tjm.setPadding(18,0,18,0);
            thg.setPadding(18,0,18,0);
            tot.setPadding(18,0,18,0);

            Double hrg = Double.parseDouble(DbHelper.detailPembelian.get(i)[2]);
            Double st = Double.parseDouble(DbHelper.detailPembelian.get(i)[4]);
            grandTotal += st;

            tid.setText(DbHelper.detailPembelian.get(i)[0]);
            tno.setText(String.valueOf(i+1));
            tna.setText(DbHelper.detailPembelian.get(i)[1]);
            thg.setText(formatRupiah.format((double)hrg));
            tjm.setText(DbHelper.detailPembelian.get(i)[3]);
            tot.setText(formatRupiah.format((double)st));

            row.addView(tid);
            row.addView(tno);
            row.addView(tna);
            row.addView(thg);
            row.addView(tjm);
            row.addView(tot);
            tbl_det_barang.addView(row);
            final int index = i;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PembelianActivity.this);
                    LayoutInflater inflter = getLayoutInflater();
                    View DialogView = inflter.inflate(R.layout.tambah_barang_penjualan, null);
                    builder.setView(DialogView)
                            .setCancelable(true)
                            .setTitle("Ubah Barang \n"+DbHelper.detailPembelian.get(index)[1]);
                    final EditText txt_jumlah_barang = (EditText)DialogView.findViewById(R.id.txt_jumlah_barang);
                    txt_jumlah_barang.setText(DbHelper.detailPembelian.get(index)[3]);
                    builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] det = DbHelper.detailPembelian.get(index);
                            int jml = Integer.parseInt(txt_jumlah_barang.getText().toString());
                            Double hrg = Double.parseDouble(det[2]);
                            Double st = hrg*jml;
                            det[3] = String.valueOf(jml);
                            det[4] = String.valueOf(st);
                            DbHelper.detailPembelian.set(index, det);
                            builder.create().dismiss();
                            get_detail();
                        }
                    });
                    builder.setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DbHelper.detailPenjualan.remove(index);
                            builder.create().dismiss();
                            get_detail();
                        }
                    });
                    builder.create().show();
                }
            });
        }
        txt_frm_pembelian_total.setText(formatRupiah.format((double)grandTotal));
    }

    @Override
    protected void onStart() {
        super.onStart();
        get_detail();
    }

    private void simpan_transaksi(){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        if(txt_frm_pembelian_kode.getText().toString().equals("") ||
                txt_frm_pembelian_tanggal.getText().toString().equals("") ||
                txt_frm_pembelian_id_supplier.getText().toString().equals("") ||
                DbHelper.detailPembelian.isEmpty()
                ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Silahkan lengkkapi data!")
                    .setNegativeButton("Retry", null).create().show();
        }else{
            String msg = "";
            msg += "\t\t\t\t\t HAEVEYS \n";
            msg += "\t\tJl Kedung Baruk, no 12, Surabaya \n";
            msg += "=============================================\n";
            msg += "\t\t\t\tNOTA PEMBELIAN\n";
            msg += "=============================================\n";
            String sql = "INSERT INTO Pembelian VALUES(" +
                    " '"+txt_frm_pembelian_kode.getText().toString()+"',"+
                    " '"+DbHelper.id_karyawan+"',"+
                    " '"+txt_frm_pembelian_id_supplier.getText().toString()+"',"+
                    " '"+txt_frm_pembelian_tanggal.getText().toString()+"',"+
                    " "+grandTotal+""+
                    ");";
            try{
                db.execSQL(sql);
                msg += "No : "+txt_frm_pembelian_kode.getText().toString()+"\n";
                msg += "Tanggal : "+txt_frm_pembelian_tanggal.getText().toString()+"\n";
                msg += "Supllier : "+txt_frm_pembelian_supplier.getText().toString()+"\n";
                msg += "=============================================\n";
                msg += "Barang \t Jumlah \t Harga \t\t Total\n";
                msg += "---------------------------------------------\n";
                for(int i=0;i<DbHelper.detailPembelian.size();i++){
                    String sql_det = "INSERT INTO Pembelian_detail (ID_PEMBELIAN, ID_BARANG, HARGA, JUMLAH, SUBTOTAL) VALUES(" +
                            "'"+txt_frm_pembelian_kode.getText().toString()+"', "+
                            "'"+DbHelper.detailPembelian.get(i)[0]+"', "+
                            ""+DbHelper.detailPembelian.get(i)[2]+", "+
                            ""+DbHelper.detailPembelian.get(i)[3]+", "+
                            ""+DbHelper.detailPembelian.get(i)[4]+"); ";
                    try{
                        db.execSQL(sql_det);

                        Double hrg = Double.parseDouble(DbHelper.detailPembelian.get(i)[2]);
                        Double st = Double.parseDouble(DbHelper.detailPembelian.get(i)[4]);
                        msg += DbHelper.detailPembelian.get(i)[1]+" \n \t\t "+DbHelper.detailPembelian.get(i)[3];
                        msg += " \t\t "+formatRupiah.format((double)hrg)+" \t "+formatRupiah.format((double)st)+"\n";
                    }catch (Exception e){
                        Log.e("DETAIL EROOR", "GAGAL DITAMBAH!");
                        Log.e("DETAIL EROOR", sql_det);
                    }
                }
                msg += "---------------------------------------------\n";
                msg += "\t\t\t\t\t Grand Total : "+formatRupiah.format((double)grandTotal)+"\n";

                msg += "\n\n\t\t\t\t\t\t\t\tTTD\n";
                msg += "\t\t\t\t\t\t\t\t"+DbHelper.karyawan+"\n\n";
                Log.e("NOTA", msg);
                Toast.makeText(getApplicationContext(), "Transaksi Berhasil disimpan", Toast.LENGTH_SHORT).show();
                DbHelper.detailPembelian.clear();
                finish();
            }catch (SQLException e){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Terjadi Kesalahan, Data Transaksi Gagal Disimpan!")
                        .setNegativeButton("Retry", null).create().show();
            }
        }
    }
}
