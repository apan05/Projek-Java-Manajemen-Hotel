package controller;

import dao.CustomerDAO;
import model.Customer;

import java.util.List;

/**
 * CustomerController.java
 * ─────────────────────────────────────────────────────────────
 * Jembatan antara View Customer dan CustomerDAO.
 * ─────────────────────────────────────────────────────────────
 */
public class CustomerController {

    private final CustomerDAO customerDAO;

    public CustomerController() {
        this.customerDAO = new CustomerDAO();
    }

    // ── Read ─────────────────────────────────────────────────
    public List<Customer> getAllCustomer()             { return customerDAO.getAllCustomer(); }
    public Customer       getCustomerById(int id)     { return customerDAO.getCustomerById(id); }
    public List<Customer> cariCustomer(String keyword){ return customerDAO.searchByName(keyword); }

    // ── Create ───────────────────────────────────────────────
    public String tambahCustomer(String nama, String noHp, String alamat, String email) {
        if (nama.isBlank()) return "ERROR: Nama customer wajib diisi.";
        if (noHp.isBlank()) return "ERROR: Nomor HP wajib diisi.";
        boolean ok = customerDAO.insertCustomer(nama.trim(), noHp.trim(),
                                                alamat.trim(), email.trim());
        return ok ? "OK: Customer " + nama + " berhasil ditambahkan."
                  : "ERROR: Gagal menyimpan ke database.";
    }

    // ── Update ───────────────────────────────────────────────
    public String editCustomer(int idCustomer, String nama, String noHp,
                                String alamat, String email) {
        if (nama.isBlank() || noHp.isBlank()) {
            return "ERROR: Nama dan nomor HP wajib diisi.";
        }
        boolean ok = customerDAO.updateCustomer(idCustomer, nama.trim(),
                                                noHp.trim(), alamat.trim(), email.trim());
        return ok ? "OK: Data customer berhasil diperbarui."
                  : "ERROR: Gagal memperbarui database.";
    }

    // ── Soft Delete ──────────────────────────────────────────
    public String hapusCustomer(int idCustomer) {
        boolean ok = customerDAO.softDeleteCustomer(idCustomer);
        return ok ? "OK: Customer berhasil dinonaktifkan (soft delete)."
                  : "ERROR: Gagal menonaktifkan customer.";
    }
}
