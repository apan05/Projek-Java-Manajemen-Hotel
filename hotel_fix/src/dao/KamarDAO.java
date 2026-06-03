package dao;

import database.DatabaseConnection;
import model.Kamar;
import model.StandardRoom;
import model.DeluxeRoom;
import model.SuiteRoom;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * KamarDAO.java  –  Data Access Object untuk tabel_kamar
 * ─────────────────────────────────────────────────────────────
 * [DESIGN PATTERN] Factory Method (createKamarFromResultSet):
 *   Membaca kolom 'jenis' dari DB dan menginstansiasi subclass
 *   Kamar yang tepat (Standard/Deluxe/Suite) secara dinamis.
 *   Caller cukup menerima List<Kamar> tanpa tahu subclass detail.
 *
 * [PBO] POLYMORPHISM: List<Kamar> berisi campuran tiga subclass;
 *   saat controller memanggil kamar.hitungHarga(), JVM otomatis
 *   dispatch ke implementasi override yang sesuai.
 * ─────────────────────────────────────────────────────────────
 */
public class KamarDAO {

    private final Connection conn;

    public KamarDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ─────────────────────────────────────────────────────────
    // [FACTORY METHOD] – Inti polymorphism di layer DAO
    // ─────────────────────────────────────────────────────────
    /**
     * Membaca satu baris ResultSet dan menginstansiasi subclass
     * Kamar yang sesuai dengan kolom 'jenis'.
     */
    private Kamar createKamarFromResultSet(ResultSet rs) throws SQLException {
        int    id       = rs.getInt("id_kamar");
        String nomor    = rs.getString("nomor_kamar");
        String jenis    = rs.getString("jenis");
        double harga    = rs.getDouble("harga");
        String status   = rs.getString("status");
        String fasilitas= rs.getString("fasilitas");
        int    isActive = rs.getInt("is_active");

        // Factory logic: instantiate subclass berdasarkan nilai 'jenis'
        switch (jenis) {
            case "Standard":
                return new StandardRoom(id, nomor, harga, status, fasilitas, isActive);
            case "Deluxe":
                return new DeluxeRoom(id, nomor, harga, status, fasilitas, isActive);
            case "Suite":
                return new SuiteRoom(id, nomor, harga, status, fasilitas, isActive);
            default:
                throw new IllegalArgumentException("[KamarDAO] Jenis kamar tidak dikenal: " + jenis);
        }
    }

    // ── 1. Get All Active Kamar ──────────────────────────────
    public List<Kamar> getAllKamar() {
        List<Kamar> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_kamar WHERE is_active = 1 ORDER BY nomor_kamar";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(createKamarFromResultSet(rs));  // Factory call
            }
        } catch (SQLException e) {
            System.err.println("[KamarDAO] getAllKamar error: " + e.getMessage());
        }
        return list;
    }

    // ── 2. Get Kamar Tersedia ────────────────────────────────
    public List<Kamar> getKamarTersedia() {
        List<Kamar> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_kamar WHERE status='Tersedia' AND is_active=1 ORDER BY nomor_kamar";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(createKamarFromResultSet(rs));  // Factory call
            }
        } catch (SQLException e) {
            System.err.println("[KamarDAO] getKamarTersedia error: " + e.getMessage());
        }
        return list;
    }

    // ── 3. Get Kamar by ID ───────────────────────────────────
    public Kamar getKamarById(int idKamar) {
        String sql = "SELECT * FROM tabel_kamar WHERE id_kamar = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKamar);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return createKamarFromResultSet(rs);  // Factory call
            }
        } catch (SQLException e) {
            System.err.println("[KamarDAO] getKamarById error: " + e.getMessage());
        }
        return null;
    }

    // ── 4. Insert Kamar ──────────────────────────────────────
    public boolean insertKamar(String nomor, String jenis, double harga,
                                String fasilitas) {
        String sql = "INSERT INTO tabel_kamar (nomor_kamar, jenis, harga, status, fasilitas, is_active)"
                   + " VALUES (?, ?, ?, 'Tersedia', ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomor);
            ps.setString(2, jenis);
            ps.setDouble(3, harga);
            ps.setString(4, fasilitas);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[KamarDAO] insertKamar error: " + e.getMessage());
            return false;
        }
    }

    // ── 5. Update Kamar ──────────────────────────────────────
    public boolean updateKamar(int idKamar, String nomor, String jenis,
                                double harga, String fasilitas, String status) {
        String sql = "UPDATE tabel_kamar SET nomor_kamar=?, jenis=?, harga=?,"
                   + " fasilitas=?, status=? WHERE id_kamar=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomor);
            ps.setString(2, jenis);
            ps.setDouble(3, harga);
            ps.setString(4, fasilitas);
            ps.setString(5, status);
            ps.setInt(6, idKamar);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[KamarDAO] updateKamar error: " + e.getMessage());
            return false;
        }
    }

    // ── 6. Update Status Kamar (dipakai Thread) ──────────────
    public boolean updateStatusKamar(int idKamar, String status) {
        String sql = "UPDATE tabel_kamar SET status=? WHERE id_kamar=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idKamar);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[KamarDAO] updateStatusKamar error: " + e.getMessage());
            return false;
        }
    }

    // ── 7. Soft Delete Kamar ─────────────────────────────────
    /**
     * [SOFT DELETE] Tidak menghapus baris dari DB, hanya
     * mengubah is_active = 0 agar FK constraint aman.
     */
    public boolean softDeleteKamar(int idKamar) {
        String sql = "UPDATE tabel_kamar SET is_active = 0 WHERE id_kamar = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKamar);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[KamarDAO] softDeleteKamar error: " + e.getMessage());
            return false;
        }
    }

    // ── 8. Cek Nomor Kamar Duplikat ──────────────────────────
    public boolean isNomorKamarTaken(String nomor) {
        String sql = "SELECT COUNT(*) FROM tabel_kamar WHERE nomor_kamar = ? AND is_active = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[KamarDAO] isNomorKamarTaken error: " + e.getMessage());
        }
        return false;
    }
}
