package dao;

import database.DatabaseConnection;
import model.Transaksi;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TransaksiDAO.java  –  Data Access Object untuk tabel_transaksi
 * ─────────────────────────────────────────────────────────────
 * Menangani pencatatan pembayaran serta rekap laporan transaksi.
 * ─────────────────────────────────────────────────────────────
 */
public class TransaksiDAO {

    private final Connection conn;

    public TransaksiDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ── 1. Get All Transaksi (untuk laporan Admin) ───────────
    public List<Transaksi> getAllTransaksi() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.*, c.nama AS nama_customer, "
                   + "k.nomor_kamar, k.jenis AS jenis_kamar "
                   + "FROM tabel_transaksi t "
                   + "JOIN tabel_booking  b ON t.id_booking    = b.id_booking "
                   + "JOIN tabel_customer c ON b.id_customer   = c.id_customer "
                   + "JOIN tabel_kamar    k ON b.id_kamar      = k.id_kamar "
                   + "ORDER BY t.tanggal_bayar DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] getAllTransaksi error: " + e.getMessage());
        }
        return list;
    }

    // ── 2. Get Transaksi by Booking ID ───────────────────────
    public Transaksi getTransaksiByBookingId(int idBooking) {
        String sql = "SELECT t.*, c.nama AS nama_customer, "
                   + "k.nomor_kamar, k.jenis AS jenis_kamar "
                   + "FROM tabel_transaksi t "
                   + "JOIN tabel_booking  b ON t.id_booking  = b.id_booking "
                   + "JOIN tabel_customer c ON b.id_customer = c.id_customer "
                   + "JOIN tabel_kamar    k ON b.id_kamar    = k.id_kamar "
                   + "WHERE t.id_booking = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBooking);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] getTransaksiByBookingId error: " + e.getMessage());
        }
        return null;
    }

    // ── 3. Insert Transaksi (Pembayaran) ─────────────────────
    /**
     * Mencatat transaksi pembayaran. Status booking akan di-update
     * ke CheckOut jika checkout dilakukan bersamaan dengan bayar.
     * @return id_transaksi baru, atau -1 jika gagal
     */
    public int insertTransaksi(int idBooking, double totalBayar) {
        String sql = "INSERT INTO tabel_transaksi (id_booking, total_bayar, tanggal_bayar)"
                   + " VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idBooking);
            ps.setDouble(2, totalBayar);
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] insertTransaksi error: " + e.getMessage());
        }
        return -1;
    }

    // ── 4. Rekap Total Pendapatan ────────────────────────────
    public double getTotalPendapatan() {
        String sql = "SELECT COALESCE(SUM(total_bayar), 0) FROM tabel_transaksi";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] getTotalPendapatan error: " + e.getMessage());
        }
        return 0.0;
    }

    // ── 5. Rekap Pendapatan per Bulan ────────────────────────
    public double getPendapatanBulanIni() {
        String sql = "SELECT COALESCE(SUM(total_bayar), 0) FROM tabel_transaksi "
                   + "WHERE MONTH(tanggal_bayar) = MONTH(CURDATE()) "
                   + "  AND YEAR(tanggal_bayar)  = YEAR(CURDATE())";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] getPendapatanBulanIni error: " + e.getMessage());
        }
        return 0.0;
    }

    // ── 6. Filter Transaksi by Tanggal ───────────────────────
    public List<Transaksi> getTransaksiByDateRange(LocalDate from, LocalDate to) {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.*, c.nama AS nama_customer, "
                   + "k.nomor_kamar, k.jenis AS jenis_kamar "
                   + "FROM tabel_transaksi t "
                   + "JOIN tabel_booking  b ON t.id_booking  = b.id_booking "
                   + "JOIN tabel_customer c ON b.id_customer = c.id_customer "
                   + "JOIN tabel_kamar    k ON b.id_kamar    = k.id_kamar "
                   + "WHERE t.tanggal_bayar BETWEEN ? AND ? "
                   + "ORDER BY t.tanggal_bayar DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] getTransaksiByDateRange error: " + e.getMessage());
        }
        return list;
    }

    // ── Helper: mapping ResultSet ke Transaksi object ─────────
    private Transaksi mapRow(ResultSet rs) throws SQLException {
        Transaksi t = new Transaksi(
            rs.getInt("id_transaksi"),
            rs.getInt("id_booking"),
            rs.getDouble("total_bayar"),
            rs.getDate("tanggal_bayar").toLocalDate()
        );
        t.setNamaCustomer(rs.getString("nama_customer"));
        t.setNomorKamar(rs.getString("nomor_kamar"));
        t.setJenisKamar(rs.getString("jenis_kamar"));
        return t;
    }
}
