package uts.harveys.com.harveys.Transaksi;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import uts.harveys.com.harveys.DbHelper;
import uts.harveys.com.harveys.MasterActivity;
import uts.harveys.com.harveys.R;

public class PenjualanActivity extends AppCompatActivity {

    SQLiteDatabase db;

    EditText txt_frm_penjualan_kode, txt_frm_penjualan_tanggal, txt_frm_penjualan_customer;
    TableLayout tbl_det_barang;
    Button btn_transaksi_tambah_barang, btn_transaksi_simpan, btn_cetak;
    TextView txt_frm_penjualan_total;
    DatePickerDialog.OnDateSetListener OnDateListener;

    Double grandTotal = 0.0;
    int nm_trans = 0;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    UUID udd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan);
        txt_frm_penjualan_kode = (EditText)findViewById(R.id.txt_frm_penjualan_kode);
        txt_frm_penjualan_tanggal = (EditText)findViewById(R.id.txt_frm_penjualan_tanggal);
        txt_frm_penjualan_customer = (EditText)findViewById(R.id.txt_frm_penjualan_customer);
        tbl_det_barang = (TableLayout)findViewById(R.id.tbl_det_barang);
        btn_transaksi_tambah_barang = (Button)findViewById(R.id.btn_transaksi_tambah_barang);
        btn_transaksi_simpan = (Button)findViewById(R.id.btn_transaksi_simpan);
        txt_frm_penjualan_total = (TextView)findViewById(R.id.txt_frm_penjualan_total);

        String myDate =  new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        txt_frm_penjualan_tanggal.setText(myDate);
        txt_frm_penjualan_customer.requestFocus();
        initDB();
        get_data();
        gen_id_trans();
        DbHelper.detailPenjualan.clear();
        get_detail();
        btn_transaksi_tambah_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(getApplicationContext(), PenjualanBarangActivity.class);
                startActivity(itnt);
            }
        });
        btn_transaksi_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan_transaksi();
            }
        });
        txt_frm_penjualan_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH);
                int day = ca.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                                            PenjualanActivity.this,
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
                txt_frm_penjualan_tanggal.setText(date);
            }
        };
        DbHelper.detailPenjualan.clear();

        try {
            findBT();
            openBT();
        }catch (Exception e){
            e.printStackTrace();
        }
        btn_cetak = (Button)findViewById(R.id.btn_cetak);
        btn_cetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cek_print();
            }
        });
    }


    private void gen_id_trans(){
        String num = "0000000"+String.valueOf(nm_trans);
        String nt = "PNJ-"+num.substring(num.length()-5, num.length());
        txt_frm_penjualan_kode.setText(nt);
        Log.e("GEN - ", num);
        Log.e("GEN - ", nt);
    }

    private void initDB(){
        try {
            db = openOrCreateDatabase(DbHelper.DBNAME, MODE_PRIVATE, null);
            db.execSQL(DbHelper.sql_penjualan);
            db.execSQL(DbHelper.sql_penjualan_detail);
            db.execSQL(DbHelper.sql_trigger_penjualan);
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan init Database!", Toast.LENGTH_SHORT).show();
            Log.e("SQL ERROR", e.getMessage());
        }
    }

    private void get_data(){
        Cursor c = db.rawQuery("SELECT * FROM Penjualan", null);
        nm_trans = c.getCount()+1;
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
        for(int i=0;i<DbHelper.detailPenjualan.size();i++){
            TableRow row = new TableRow(this);
            TextView tid = new TextView(this);
            TextView tno = new TextView(this);
            TextView tna = new TextView(this);
            TextView tjm = new TextView(this);
            TextView thg = new TextView(this);
            TextView tot = new TextView(this);

            row.setPadding(0,10,0,10);

            tno.setTextColor(getResources().getColor(R.color.colorMain));
            tna.setTextColor(getResources().getColor(R.color.colorMain));
            tjm.setTextColor(getResources().getColor(R.color.colorMain));
            thg.setTextColor(getResources().getColor(R.color.colorMain));
            tot.setTextColor(getResources().getColor(R.color.colorMain));

            tna.setPadding(18,0,18,0);
            tjm.setPadding(18,0,18,0);
            thg.setPadding(18,0,18,0);
            tot.setPadding(18,0,18,0);

            Double hrg = Double.parseDouble(DbHelper.detailPenjualan.get(i)[2]);
            Double st = Double.parseDouble(DbHelper.detailPenjualan.get(i)[4]);
            grandTotal += st;
            Log.e("DATA ", String.valueOf(i)+" "+DbHelper.detailPenjualan.get(i)[1]);
            tid.setText(DbHelper.detailPenjualan.get(i)[0]);
            tno.setText(String.valueOf(i+1));
            tna.setText(DbHelper.detailPenjualan.get(i)[1]);
            thg.setText(formatRupiah.format((double)hrg));
            tjm.setText(DbHelper.detailPenjualan.get(i)[3]);
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PenjualanActivity.this);
                    LayoutInflater inflter = getLayoutInflater();
                    View DialogView = inflter.inflate(R.layout.tambah_barang_penjualan, null);
                    builder.setView(DialogView)
                            .setCancelable(true)
                            .setTitle("Ubah Barang \n"+DbHelper.detailPenjualan.get(index)[1]);
                    final EditText txt_jumlah_barang = (EditText)DialogView.findViewById(R.id.txt_jumlah_barang);
                    txt_jumlah_barang.setText(DbHelper.detailPenjualan.get(index)[3]);
                    builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int min_stok = get_min_stok(DbHelper.detailPenjualan.get(index)[0]);
                            String[] det = DbHelper.detailPenjualan.get(index);
                            int jml = Integer.parseInt(txt_jumlah_barang.getText().toString());
                            Double hrg = Double.parseDouble(det[2]);
                            Double st = hrg*jml;
                            det[3] = String.valueOf(jml);
                            det[4] = String.valueOf(st);
                            if(jml>=min_stok){
                                Toast.makeText(getApplicationContext(), "Stok Tidak Mencukupi!", Toast.LENGTH_LONG).show();
                            }else{
                                DbHelper.detailPenjualan.set(index, det);
                                builder.create().dismiss();
                                get_detail();
                            }
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
        txt_frm_penjualan_total.setText(formatRupiah.format((double)grandTotal));
    }

    private int get_min_stok(String id){
        Log.e("ID MIN STOK", id);
        String sql = "SELECT * FROM Barang " +
                " WHERE ID_BARANG = '"+id+"'";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int ret = Integer.parseInt(c.getString(c.getColumnIndex("STOK")));
        Log.e("MIN STOK", String.valueOf(ret));
        return ret;
    }

    @Override
    protected void onStart() {
        super.onStart();
        get_detail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            closeBT();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void simpan_transaksi(){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        if(txt_frm_penjualan_kode.getText().toString().equals("") ||
                txt_frm_penjualan_tanggal.getText().toString().equals("") ||
                txt_frm_penjualan_customer.getText().toString().equals("") ||
                DbHelper.detailPenjualan.isEmpty()
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
            String sql = "INSERT INTO Penjualan VALUES(" +
                    " '"+txt_frm_penjualan_kode.getText().toString()+"',"+
                    " '"+DbHelper.id_karyawan+"',"+
                    " '"+txt_frm_penjualan_customer.getText().toString()+"',"+
                    " '"+txt_frm_penjualan_tanggal.getText().toString()+"',"+
                    " "+grandTotal+""+
                    ");";
            try{
                db.execSQL(sql);
                msg += "No : "+txt_frm_penjualan_kode.getText().toString()+"\n";
                msg += "Tanggal : "+txt_frm_penjualan_tanggal.getText().toString()+"\n";
                msg += "Customer : "+txt_frm_penjualan_customer.getText().toString()+"\n";
                msg += "=============================================\n";
                msg += "Barang \t Jumlah \t Harga \t\t Total\n";
                msg += "---------------------------------------------\n";
                for(int i=0;i<DbHelper.detailPenjualan.size();i++){
                    String sql_det = "INSERT INTO Penjualan_detail (ID_PENJUALAN, ID_BARANG, HARGA, JUMLAH, SUBTOTAL) VALUES(" +
                            "'"+txt_frm_penjualan_kode.getText().toString()+"', "+
                            "'"+DbHelper.detailPenjualan.get(i)[0]+"', "+
                            ""+DbHelper.detailPenjualan.get(i)[2]+", "+
                            ""+DbHelper.detailPenjualan.get(i)[3]+", "+
                            ""+DbHelper.detailPenjualan.get(i)[4]+"); ";
                    try{
                        db.execSQL(sql_det);

                        Double hrg = Double.parseDouble(DbHelper.detailPenjualan.get(i)[2]);
                        Double st = Double.parseDouble(DbHelper.detailPenjualan.get(i)[4]);
                        msg += DbHelper.detailPenjualan.get(i)[1]+" \n \t\t "+DbHelper.detailPenjualan.get(i)[2];
                        msg += " \t\t "+formatRupiah.format((double)hrg)+" \t "+formatRupiah.format((double)st)+"\n";
                    }catch (Exception e){
                        Log.e("DETAIL EROOR", "GAGAL DITAMBAH!");
                        Log.e("DETAIL EROOR", sql_det);
                    }
                }
                msg += "---------------------------------------------\n";
                msg += "\t\t\t\t\t Grand Total : "+formatRupiah.format((double)grandTotal)+"\n";

                msg += "\n\n\t***** Terimakasih Atas Kunjungan Anda *****\n";
                msg += "\t\t\t\t\t"+DbHelper.karyawan+"\n\n";
                Log.e("NOTA", msg);
                Toast.makeText(getApplicationContext(), "Transaksi Berhasil disimpan", Toast.LENGTH_SHORT).show();

                try {
                    mmOutputStream.write(msg.getBytes());
                    //Toast.makeText(getApplicationContext(), "Data Sent", Toast.LENGTH_LONG).show();
                    //closeBT();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                DbHelper.detailPenjualan.clear();
                finish();
            }catch (SQLException e){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Terjadi Kesalahan, Data Transaksi Gagal Disimpan!")
                        .setNegativeButton("Retry", null).create().show();
            }
        }
    }


    /*=========================== PRINT AREA =================================================*/

    public void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "No bluetooth adapter available", Toast.LENGTH_SHORT).show();
                Log.e("Bluetooth", "No bluetooth adapter available");
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices

                    /*Log.e("DEVICE NAME", device.getName());
                    Log.e("UUIDDD", String.valueOf(device.getUuids()));
                    ParcelUuid[] pa = device.getUuids();
                    for(int i=0; i<pa.length; i++){
                        Log.e("UUIDDD____"+String.valueOf(i), String.valueOf(pa[i].getUuid()));
                    }*/
                    if (device.getName().equals("printer001")) {
                        mmDevice = device;
                        break;
                    }

                    if (device.getName().equals("Galaxy J7+")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            Toast.makeText(getApplicationContext(), "Bluetooth device found.", Toast.LENGTH_SHORT).show();
            Log.e("Bluetooth", "Bluetooth device found. "+mmDevice.getName());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            //UUID uuid = UUID.fromString("00001115-0000-1000-8000-00805f9b34fb");
            //UUID uuid = UUID.fromString("00001112-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();


            Toast.makeText(getApplicationContext(), "Bluetooth Opened.", Toast.LENGTH_SHORT).show();
            Log.e("Bluetooth", "Bluetooth Opened.");

        } catch (Exception e) {
            Log.e("Bluetooth", "Bluetooth SOCKET.");
            e.printStackTrace();
        }
    }

    public void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Toast.makeText(getApplicationContext(), "Bluetooth Closed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cek_print(){
        try{
            String msg = "";
            msg += "\t\t\t\t\tHAEVEYS \n";
            msg += "\t\tJl Kedung Baruk, no 12, Surabaya \n";
            msg += "=============================================\n";
            msg += "\t\t\t\tNOTA PEMBELIAN\n";
            msg += "=============================================\n";
            msg += "No : "+txt_frm_penjualan_kode.getText().toString()+"\n";
            msg += "Tanggal : "+txt_frm_penjualan_tanggal.getText().toString()+"\n";
            msg += "Customer : "+txt_frm_penjualan_customer.getText().toString()+"\n";
            msg += "=============================================\n";
            msg += "Barang \t Jumlah \t Harga \t\t Total\n";
            msg += "---------------------------------------------\n";
            msg += "Flanel Oke Sip (L) \n" +
                    "\t\t 350000.0 \t\t Rp350.000 \t Rp700.000\n" +
                    "Flanel Keren (S, M, L, XL) \n" +
                    "\t\t 300000.0 \t\t Rp300.000 \t Rp600.000\n" +
                    "Ini dia (M) \n" +
                    "\t\t 350000.0 \t\t Rp350.000 \t Rp700.000\n";
            msg += "---------------------------------------------\n";
            msg += "\t\t\t\t\t Grand Total : Rp1,123.345\n";

            msg += "\n\n\t***** Terimakasih Atas Kunjungan Anda *****\n";
            msg += "\t\t\t\t\t"+DbHelper.karyawan+"\n\n";
            //if(mmDevice.is){}
            Log.e("PESAN", "DIPRINT");
            mmOutputStream.write(msg.getBytes());

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
