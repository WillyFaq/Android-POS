package uts.harveys.com.harveys;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class DbHelper {

    public static final String DBNAME = "dbkasir";

    public static final String sql_karyawan = "CREATE TABLE IF NOT EXISTS Karyawan (" +
                                                "ID_KARYAWAN VARCHAR PRIMARY KEY, " +
                                                "NAMA_KARYAWAN VARCHAR, " +
                                                "TELP_KARYAWAN VARCHAR, " +
                                                "USERNAME VARCHAR UNIQUE, " +
                                                "PASSWORD VARCHAR);";

    public static final String sql_barang = "CREATE TABLE IF NOT EXISTS Barang (" +
                                                "ID_BARANG VARCHAR PRIMARY KEY, " +
                                                "ID_SUPPLIER VARCHAR, " +
                                                "NAMA_BARANG VARCHAR, " +
                                                "JENIS_BARANG VARCHAR, " +
                                                "HARGA_BELI DOUBLE, " +
                                                "HARGA_JUAL DOUBLE, " +
                                                "SIZE VARCHAR, " +
                                                "WARNA VARCHAR, " +
                                                "STOK INT, " +
                                                "FOREIGN KEY(ID_SUPPLIER) REFERENCES Supplier(ID_SUPPLIER) " +
                                                ");";

    public static final String sql_supplier = "CREATE TABLE IF NOT EXISTS Supplier (" +
                                                "ID_SUPPLIER VARCHAR PRIMARY KEY, " +
                                                "NAMA_SUPPLIER VARCHAR, " +
                                                "TELP_SUPPLIER VARCHAR, " +
                                                "ALAMAT_SUPPLIER VARCHAR);";

    public static final String sql_penjualan = "CREATE TABLE IF NOT EXISTS Penjualan (" +
                                                "ID_PENJUALAN VARCHAR PRIMARY KEY, " +
                                                "ID_KARYAWAN VARCHAR, " +
                                                "NAMA_CUSTOMER VARCHAR, " +
                                                "TANGGAL_PENJUALAN DATE, " +
                                                "TOTAL DOUBLE," +
                                                "FOREIGN KEY(ID_KARYAWAN) REFERENCES Karyawan(ID_KARYAWAN) " +
                                                ");";

    public static final String sql_penjualan_detail = "CREATE TABLE IF NOT EXISTS Penjualan_detail (" +
                                                        "ID_PENJUALAN_DETAIL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                        "ID_PENJUALAN VARCHAR, " +
                                                        "ID_BARANG VARCHAR, " +
                                                        "HARGA DOUBLE, " +
                                                        "JUMLAH INTEGER, " +
                                                        "SUBTOTAL DOUBLE," +
                                                        "FOREIGN KEY(ID_PENJUALAN) REFERENCES Penjualan(ID_PENJUALAN), " +
                                                        "FOREIGN KEY(ID_BARANG) REFERENCES Barang(ID_BARANG) " +
                                                        ");";

    public static final String sql_trigger_penjualan = "CREATE TRIGGER IF NOT EXISTS t_transaksi AFTER INSERT ON Penjualan_detail " +
                                                        "BEGIN " +
                                                        "UPDATE Barang SET STOK = STOK - new.JUMLAH WHERE ID_BARANG = new.ID_BARANG; " +
                                                        "END;";


    public static String id_karyawan = "";
    public static String karyawan = "";
    public static ArrayList<String[]> detailPenjualan = new ArrayList<String[]>();
    public static ArrayList<String[]> detailPembelian = new ArrayList<String[]>();

    public static final String sql_pembelian = "CREATE TABLE IF NOT EXISTS Pembelian (" +
                                                "ID_PEMBELIAN VARCHAR PRIMARY KEY, " +
                                                "ID_KARYAWAN VARCHAR, " +
                                                "ID_SUPPLIER VARCHAR, " +
                                                "TANGGAL_PEMBELIAN DATE, " +
                                                "TOTAL DOUBLE," +
                                                "FOREIGN KEY(ID_KARYAWAN) REFERENCES Karyawan(ID_KARYAWAN), " +
                                                "FOREIGN KEY(ID_SUPPLIER) REFERENCES Supplier(ID_SUPPLIER) " +
                                                ");";

    public static final String sql_pembelian_detail = "CREATE TABLE IF NOT EXISTS Pembelian_detail (" +
                                                        "ID_PEMBELIAN_DETAIL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                        "ID_PEMBELIAN VARCHAR, " +
                                                        "ID_BARANG VARCHAR, " +
                                                        "HARGA DOUBLE, " +
                                                        "JUMLAH INTEGER, " +
                                                        "SUBTOTAL DOUBLE," +
                                                        "FOREIGN KEY(ID_PEMBELIAN) REFERENCES Pembelian(ID_PEMBELIAN), " +
                                                        "FOREIGN KEY(ID_BARANG) REFERENCES Barang(ID_BARANG) " +
                                                        ");";

    public static final String sql_trigger_pembelian = "CREATE TRIGGER IF NOT EXISTS t_transaksi_pembelian AFTER INSERT ON Pembelian_detail " +
                                                        "BEGIN " +
                                                        "UPDATE Barang SET STOK = STOK + new.JUMLAH WHERE ID_BARANG = new.ID_BARANG; " +
                                                        "END;";
}
