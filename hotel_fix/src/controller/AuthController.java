package controller;

import dao.UserDAO;
import model.User;

/**
 * AuthController.java
 * ─────────────────────────────────────────────────────────────
 * Menangani logika autentikasi (login/logout).
 * Menyimpan sesi user yang sedang aktif secara statik agar
 * bisa diakses dari semua View tanpa passing parameter.
 * ─────────────────────────────────────────────────────────────
 */
public class AuthController {

    private final UserDAO userDAO;

    // ── Sesi aktif (statik – satu user login di satu waktu) ──
    private static User currentUser = null;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    /**
     * Validasi kredensial ke DB, simpan sesi jika berhasil.
     * @return true jika login sukses
     */
    public boolean login(String username, String password) {
        if (username == null || username.isBlank()
         || password == null || password.isBlank()) {
            return false;
        }
        User user = userDAO.authenticate(username.trim(), password.trim());
        if (user != null) {
            currentUser = user;
            System.out.println("[Auth] Login berhasil: " + user);
            return true;
        }
        System.out.println("[Auth] Login gagal untuk username: " + username);
        return false;
    }

    /** Hapus sesi user yang sedang aktif. */
    public void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
    }

    // ── Getter Sesi ──────────────────────────────────────────
    public static User    getCurrentUser()   { return currentUser; }
    public static boolean isLoggedIn()       { return currentUser != null; }
    public static boolean isAdmin()          { return currentUser != null && "Admin".equals(currentUser.getRole()); }
    public static boolean isResepsionis()    { return currentUser != null && "Resepsionis".equals(currentUser.getRole()); }
}
