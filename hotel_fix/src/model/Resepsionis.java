package model;

/**
 * Resepsionis.java  –  Subclass of User
 * ─────────────────────────────────────────────────────────────
 * [PBO] INHERITANCE : Mewarisi semua field dan method dari User.
 * [PBO] ABSTRACTION : Mengimplementasikan abstract method login()
 *                     dan logout() sesuai hak akses Resepsionis.
 * ─────────────────────────────────────────────────────────────
 */
public class Resepsionis extends User {

    public Resepsionis() {
        super();
    }

    public Resepsionis(int idUser, String username, String password) {
        super(idUser, username, password, "Resepsionis");
    }

    /**
     * Implementasi login untuk Resepsionis.
     */
    @Override
    public boolean login(String username, String password) {
        return this.getUsername().equals(username)
            && this.getPassword().equals(password);
    }

    @Override
    public void logout() {
        System.out.println("[Resepsionis] " + getUsername() + " telah logout.");
    }

    /**
     * Resepsionis hanya punya akses operasional, bukan master data.
     */
    public boolean hasFullAccess() {
        return false;
    }

    @Override
    public String toString() {
        return "Resepsionis{id=" + getIdUser() + ", username='" + getUsername() + "'}";
    }
}
