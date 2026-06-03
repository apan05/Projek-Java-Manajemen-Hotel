package controller;

import dao.UserDAO;
import model.User;

import java.util.List;

/**
 * UserController.java
 * ─────────────────────────────────────────────────────────────
 * Menangani CRUD user/pegawai (hanya bisa diakses Admin).
 * ─────────────────────────────────────────────────────────────
 */
public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    // ── Read ─────────────────────────────────────────────────
    public List<User> getAllUsers() { return userDAO.getAllUsers(); }

    // ── Create ───────────────────────────────────────────────
    public String tambahUser(String username, String password,
                              String konfirmasi, String role) {
        if (username.isBlank() || password.isBlank()) {
            return "ERROR: Username dan password wajib diisi.";
        }
        if (!password.equals(konfirmasi)) {
            return "ERROR: Password dan konfirmasi tidak cocok.";
        }
        if (password.length() < 5) {
            return "ERROR: Password minimal 5 karakter.";
        }
        if (!role.equals("Admin") && !role.equals("Resepsionis")) {
            return "ERROR: Role harus Admin atau Resepsionis.";
        }
        if (userDAO.isUsernameTaken(username.trim())) {
            return "ERROR: Username '" + username + "' sudah digunakan.";
        }
        boolean ok = userDAO.insertUser(username.trim(), password, role);
        return ok ? "OK: User " + username + " berhasil ditambahkan."
                  : "ERROR: Gagal menyimpan ke database.";
    }

    // ── Update ───────────────────────────────────────────────
    public String editUser(int idUser, String username, String password, String role) {
        if (username.isBlank()) return "ERROR: Username wajib diisi.";
        boolean ok = userDAO.updateUser(idUser, username.trim(), password, role);
        return ok ? "OK: Data user berhasil diperbarui."
                  : "ERROR: Gagal memperbarui database.";
    }

    // ── Delete ───────────────────────────────────────────────
    public String hapusUser(int idUser) {
        // Cegah Admin menghapus dirinya sendiri
        User current = AuthController.getCurrentUser();
        if (current != null && current.getIdUser() == idUser) {
            return "ERROR: Tidak bisa menghapus akun yang sedang aktif.";
        }
        boolean ok = userDAO.deleteUser(idUser);
        return ok ? "OK: User berhasil dihapus."
                  : "ERROR: Gagal menghapus user.";
    }
}
