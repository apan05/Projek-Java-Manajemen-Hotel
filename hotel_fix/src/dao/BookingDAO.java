package dao;

import database.DatabaseConnection;
import model.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingDAO.java  –  Data Access Object untuk tabel_booking
 * ─────────────────────────────────────────────────────────────
 * Menangani CRUD booking termasuk proses checkin, checkout,
 * pembatalan, dan scanning booking expired (dipakai AutoCancelThread).
 * ─────────────────────────────────────────────────────────────
 */
public class BookingDAO {

    private final Connection conn;

    public BookingDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ── 1. Get All Booking (dengan nama customer & nomor kamar) ─
    public List<Booking> getAllBooking() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.nama AS nama_customer, k.nomor_kamar "
                   + "FROM tabel_booking b "
                   + "JOIN tabel_customer c ON b.id_customer = c.id_customer "
                   + "JOIN tabel_kamar    k ON b.id_kamar    = k.id_kamar "
                   + "ORDER BY b.id_booking DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] getAllBooking error: " + e.getMessage());
        }
        return list;
    }

    // ── 2. Get Booking by Status ─────────────────────────────
    public List<Booking> getBookingByStatus(String status) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.nama AS nama_customer, k.nomor_kamar "
                   + "FROM tabel_booking b "
                   + "JOIN tabel_customer c ON b.id_customer = c.id_customer "
                   + "JOIN tabel_kamar    k ON b.id_kamar    = k.id_kamar "
                   + "WHERE b.status = ? ORDER BY b.tanggal_checkin";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] getBookingByStatus error: " + e.getMessage());
        }
        return list;
    }

    // ── 3. Get Booking by ID ─────────────────────────────────
    public Booking getBookingById(int idBooking) {
        String sql = "SELECT b.*, c.nama AS nama_customer, k.nomor_kamar "
                   + "FROM tabel_booking b "
                   + "JOIN tabel_customer c ON b.id_customer = c.id_customer "
                   + "JOIN tabel_kamar    k ON b.id_kamar    = k.id_kamar "
                   + "WHERE b.id_booking = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBooking);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[BookingDAO] getBookingById error: " + e.getMessage());
        }
        return null;
    }

    // ── 4. Insert Booking ────────────────────────────────────
    /**
     * Membuat booking baru dan langsung mengubah status kamar
     * menjadi 'Dipesan' dalam satu operasi (idealnya transaksi DB).
     * @return id_booking yang baru dibuat, atau -1 jika gagal
     */
    public int insertBooking(int idCustomer, int idKamar,
                              LocalDate checkin, LocalDate checkout) {
        String sqlBooking = "INSERT INTO tabel_booking "
                          + "(id_customer, id_kamar, tanggal_checkin, tanggal_checkout, status)"
                          + " VALUES (?, ?, ?, ?, 'Menunggu')";
        String sqlKamar   = "UPDATE tabel_kamar SET status='Dipesan' WHERE id_kamar=?";

        try {
            conn.setAutoCommit(false);  // mulai transaksi

            int newId = -1;
            try (PreparedStatement ps = conn.prepareStatement(
                    sqlBooking, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idCustomer);
                ps.setInt(2, idKamar);
                ps.setDate(3, Date.valueOf(checkin));
                ps.setDate(4, Date.valueOf(checkout));
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) newId = keys.getInt(1);
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlKamar)) {
                ps2.setInt(1, idKamar);
                ps2.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return newId;

        } catch (SQLException e) {
            System.err.println("[BookingDAO] insertBooking error: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { /**/ }
            return -1;
        }
    }

    // ── 5. Proses Check-In ───────────────────────────────────
    /** Ubah status booking → CheckIn dan status kamar → Dipakai */
    public boolean prosesCheckIn(int idBooking, int idKamar) {
        String sqlB = "UPDATE tabel_booking SET status='CheckIn' WHERE id_booking=?";
        String sqlK = "UPDATE tabel_kamar   SET status='Dipakai' WHERE id_kamar=?";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlB);
                 PreparedStatement ps2 = conn.prepareStatement(sqlK)) {
                ps1.setInt(1, idBooking); ps1.executeUpdate();
                ps2.setInt(1, idKamar);   ps2.executeUpdate();
            }
            conn.commit(); conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("[BookingDAO] prosesCheckIn error: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { /**/ }
            return false;
        }
    }

    // ── 6. Proses Check-Out ──────────────────────────────────
    /** Ubah status booking → CheckOut dan status kamar → Cleaning
     *  (CleaningThread akan mengembalikannya ke Tersedia setelah delay) */
    public boolean prosesCheckOut(int idBooking, int idKamar) {
        String sqlB = "UPDATE tabel_booking SET status='CheckOut' WHERE id_booking=?";
        String sqlK = "UPDATE tabel_kamar   SET status='Cleaning' WHERE id_kamar=?";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlB);
                 PreparedStatement ps2 = conn.prepareStatement(sqlK)) {
                ps1.setInt(1, idBooking); ps1.executeUpdate();
                ps2.setInt(1, idKamar);   ps2.executeUpdate();
            }
            conn.commit(); conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("[BookingDAO] prosesCheckOut error: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { /**/ }
            return false;
        }
    }

    // ── 7. Batalkan Booking ──────────────────────────────────
    /** Ubah status booking → Batal dan kembalikan kamar → Tersedia */
    public boolean batalkanBooking(int idBooking, int idKamar) {
        String sqlB = "UPDATE tabel_booking SET status='Batal' WHERE id_booking=?";
        String sqlK = "UPDATE tabel_kamar   SET status='Tersedia' WHERE id_kamar=?";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlB);
                 PreparedStatement ps2 = conn.prepareStatement(sqlK)) {
                ps1.setInt(1, idBooking); ps1.executeUpdate();
                ps2.setInt(1, idKamar);   ps2.executeUpdate();
            }
            conn.commit(); conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("[BookingDAO] batalkanBooking error: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { /**/ }
            return false;
        }
    }

    // ── 8. Scan Booking Expired (dipakai AutoCancelThread) ───
    /**
     * Mengembalikan semua booking berstatus 'Menunggu' yang
     * tanggal_checkin-nya sudah lewat dari hari ini.
     * AutoCancelThread akan memanggil batalkanBooking() untuk masing-masing.
     */
    public List<Booking> getBookingExpired() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.nama AS nama_customer, k.nomor_kamar "
                   + "FROM tabel_booking b "
                   + "JOIN tabel_customer c ON b.id_customer = c.id_customer "
                   + "JOIN tabel_kamar    k ON b.id_kamar    = k.id_kamar "
                   + "WHERE b.status='Menunggu' AND b.tanggal_checkin < CURDATE()";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] getBookingExpired error: " + e.getMessage());
        }
        return list;
    }

    // ── Helper: mapping ResultSet ke Booking object ───────────
    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking(
            rs.getInt("id_booking"),
            rs.getInt("id_customer"),
            rs.getInt("id_kamar"),
            rs.getDate("tanggal_checkin").toLocalDate(),
            rs.getDate("tanggal_checkout").toLocalDate(),
            rs.getString("status")
        );
        b.setNamaCustomer(rs.getString("nama_customer"));
        b.setNomorKamar(rs.getString("nomor_kamar"));
        return b;
    }
}
