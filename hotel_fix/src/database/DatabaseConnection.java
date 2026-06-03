package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection.java
 * ─────────────────────────────────────────────────────────────
 * [DESIGN PATTERN] Singleton Pattern:
 *   Satu instance Connection dipakai bersama seluruh kelas DAO
 *   sehingga tidak ada duplikasi koneksi yang boros resource.
 * ─────────────────────────────────────────────────────────────
 */
public class DatabaseConnection {

    // ── Konfigurasi – sesuaikan dengan environment lokal ─────
    private static final String URL      = "jdbc:mysql://localhost:3306/hotel_db"
                                         + "?useSSL=false&serverTimezone=Asia/Jakarta"
                                         + "&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = ""; // ganti sesuai password MySQL-mu

    // ── Volatile agar aman di lingkungan multi-thread ────────
    private static volatile DatabaseConnection instance;
    private Connection connection;

    // ── Private constructor (Singleton) ──────────────────────
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Koneksi MySQL berhasil.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[DB] Driver MySQL tidak ditemukan: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException("[DB] Gagal konek MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * getInstance() – Double-checked locking untuk thread-safety.
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Kembalikan objek Connection aktif.
     * Jika koneksi terputus (timeout), reconnect otomatis.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("[DB] Reconnecting...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("[DB] Gagal reconnect: " + e.getMessage(), e);
        }
        return connection;
    }

    /** Tutup koneksi saat aplikasi ditutup. */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error menutup koneksi: " + e.getMessage());
        }
    }
}
