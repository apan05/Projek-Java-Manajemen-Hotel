package dao;

import database.DatabaseConnection;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAO.java  –  Data Access Object untuk tabel_customer
 * ─────────────────────────────────────────────────────────────
 * [SOFT DELETE] Hapus customer hanya mengubah is_active=0,
 *   riwayat booking/transaksi tetap utuh (FK aman).
 * ─────────────────────────────────────────────────────────────
 */
public class CustomerDAO {

    private final Connection conn;

    public CustomerDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ── 1. Get All Active Customer ───────────────────────────
    public List<Customer> getAllCustomer() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_customer WHERE is_active = 1 ORDER BY nama";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getAllCustomer error: " + e.getMessage());
        }
        return list;
    }

    // ── 2. Get Customer by ID ────────────────────────────────
    public Customer getCustomerById(int idCustomer) {
        String sql = "SELECT * FROM tabel_customer WHERE id_customer = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCustomer);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getCustomerById error: " + e.getMessage());
        }
        return null;
    }

    // ── 3. Search Customer by Name ───────────────────────────
    public List<Customer> searchByName(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_customer WHERE nama LIKE ? AND is_active = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] searchByName error: " + e.getMessage());
        }
        return list;
    }

    // ── 4. Insert Customer ───────────────────────────────────
    public boolean insertCustomer(String nama, String noHp, String alamat, String email) {
        String sql = "INSERT INTO tabel_customer (nama, no_hp, alamat, email, is_active)"
                   + " VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, noHp);
            ps.setString(3, alamat);
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] insertCustomer error: " + e.getMessage());
            return false;
        }
    }

    // ── 5. Update Customer ───────────────────────────────────
    public boolean updateCustomer(int idCustomer, String nama, String noHp,
                                   String alamat, String email) {
        String sql = "UPDATE tabel_customer SET nama=?, no_hp=?, alamat=?, email=?"
                   + " WHERE id_customer=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, noHp);
            ps.setString(3, alamat);
            ps.setString(4, email);
            ps.setInt(5, idCustomer);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] updateCustomer error: " + e.getMessage());
            return false;
        }
    }

    // ── 6. Soft Delete Customer ──────────────────────────────
    /**
     * [SOFT DELETE] Tidak benar-benar menghapus baris dari DB.
     * Kolom is_active di-set 0 agar histori booking tetap valid.
     */
    public boolean softDeleteCustomer(int idCustomer) {
        String sql = "UPDATE tabel_customer SET is_active = 0 WHERE id_customer = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCustomer);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] softDeleteCustomer error: " + e.getMessage());
            return false;
        }
    }

    // ── Helper: mapping ResultSet ke Customer object ──────────
    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("id_customer"),
            rs.getString("nama"),
            rs.getString("no_hp"),
            rs.getString("alamat"),
            rs.getString("email"),
            rs.getInt("is_active")
        );
    }
}
