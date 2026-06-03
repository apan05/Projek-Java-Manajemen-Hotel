package model;

/**
 * Admin.java  –  Subclass of User
 * ─────────────────────────────────────────────────────────────
 * [PBO] INHERITANCE : Mewarisi semua field dan method dari User.
 * [PBO] ABSTRACTION : Mengimplementasikan abstract method login()
 *                     dan logout() sesuai hak akses Admin.
 * ─────────────────────────────────────────────────────────────
 */
public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(int idUser, String username, String password) {
        super(idUser, username, password, "Admin");
    }

    /**
     * Implementasi login untuk Admin.
     * Validasi aktual dilakukan di UserDAO; method ini sebagai
     * representasi behaviour Admin di layer Model.
     */
    @Override
    public boolean login(String username, String password) {
        return this.getUsername().equals(username)
            && this.getPassword().equals(password);
    }

    @Override
    public void logout() {
        System.out.println("[Admin] " + getUsername() + " telah logout.");
    }

    /**
     * Hak akses eksklusif Admin: mengelola data master.
     */
    public boolean hasFullAccess() {
        return true;
    }

    @Override
    public String toString() {
        return "Admin{id=" + getIdUser() + ", username='" + getUsername() + "'}";
    }
}
