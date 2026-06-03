package model;

/**
 * User.java  –  Abstract Superclass
 * ─────────────────────────────────────────────────────────────
 * [PBO] ABSTRACTION  : Kelas ini tidak bisa diinstansiasi langsung.
 *                      Method login() dan logout() wajib di-implement
 *                      oleh setiap subclass (Admin, Resepsionis).
 * [PBO] ENCAPSULATION: Semua field dideklarasikan private;
 *                      diakses hanya via getter/setter.
 * ─────────────────────────────────────────────────────────────
 */
public abstract class User {

    // ── ENCAPSULATION: field private ─────────────────────────
    private int    idUser;
    private String username;
    private String password;
    private String role;

    // ── Constructor ──────────────────────────────────────────
    public User(int idUser, String username, String password, String role) {
        this.idUser   = idUser;
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    public User() {}

    // ── ABSTRACTION: abstract method yang wajib diimplementasi ─
    public abstract boolean login(String username, String password);
    public abstract void    logout();

    // ── Getter & Setter ──────────────────────────────────────
    public int    getIdUser()   { return idUser; }
    public void   setIdUser(int idUser) { this.idUser = idUser; }

    public String getUsername() { return username; }
    public void   setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void   setPassword(String password) { this.password = password; }

    public String getRole()     { return role; }
    public void   setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + idUser + ", username='" + username + "', role='" + role + "'}";
    }
}
