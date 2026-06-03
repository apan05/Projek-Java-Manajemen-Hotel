package dao;

import database.DatabaseConnection;
import model.Admin;
import model.Resepsionis;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO.java  –  Data Access Object untuk tabel_users
 * ─────────────────────────────────────────────────────────────
 * Bertanggung jawab atas semua operasi DB terkait User:
 *   authenticate, getAll, insert, update, delete.
 * ─────────────────────────────────────────────────────────────
 */
public class UserDAO {

    private final Connection conn;

    public UserDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ── 1. Autentikasi Login ─────────────────────────────────
    /**
     * Cek username + password ke DB dan kembalikan objek User
     * yang sesuai rolenya (Admin atau Resepsionis).
     * Mengembalikan null jika kredensial salah.
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM tabel_users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int    id   = rs.getInt("id_user");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String role = rs.getString("role");

                // Kembalikan subclass yang tepat berdasarkan role
                if ("Admin".equals(role)) {
                    return new Admin(id, user, pass);
                } else {
                    return new Resepsionis(id, user, pass);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] authenticate error: " + e.getMessage());
        }
        return null;
    }

    // ── 2. Get All Users ─────────────────────────────────────
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_users ORDER BY id_user";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int    id   = rs.getInt("id_user");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String role = rs.getString("role");

                User u = "Admin".equals(role)
                        ? new Admin(id, user, pass)
                        : new Resepsionis(id, user, pass);
                list.add(u);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllUsers error: " + e.getMessage());
        }
        return list;
    }

    // ── 3. Insert User ───────────────────────────────────────
    public boolean insertUser(String username, String password, String role) {
        String sql = "INSERT INTO tabel_users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] insertUser error: " + e.getMessage());
            return false;
        }
    }

    // ── 4. Update User ───────────────────────────────────────
    public boolean updateUser(int idUser, String username, String password, String role) {
        String sql = "UPDATE tabel_users SET username=?, password=?, role=? WHERE id_user=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setInt(4, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] updateUser error: " + e.getMessage());
            return false;
        }
    }

    // ── 5. Delete User (Hard Delete – user tidak punya FK) ───
    public boolean deleteUser(int idUser) {
        String sql = "DELETE FROM tabel_users WHERE id_user=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] deleteUser error: " + e.getMessage());
            return false;
        }
    }

    // ── 6. Cek Username Duplikat ─────────────────────────────
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM tabel_users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] isUsernameTaken error: " + e.getMessage());
        }
        return false;
    }
}
