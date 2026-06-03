package controller;

import dao.KamarDAO;
import model.Kamar;

import java.util.List;

/**
 * KamarController.java
 * ─────────────────────────────────────────────────────────────
 * Jembatan antara View Kamar dan KamarDAO.
 * Berisi validasi bisnis sebelum data dikirim ke DAO.
 * ─────────────────────────────────────────────────────────────
 */
public class KamarController {

    private final KamarDAO kamarDAO;

    public KamarController() {
        this.kamarDAO = new KamarDAO();
    }

    // ── Read ─────────────────────────────────────────────────
    public List<Kamar> getAllKamar()        { return kamarDAO.getAllKamar(); }
    public List<Kamar> getKamarTersedia()  { return kamarDAO.getKamarTersedia(); }
    public Kamar       getKamarById(int id){ return kamarDAO.getKamarById(id); }

    // ── Create ───────────────────────────────────────────────
    /**
     * Validasi input sebelum memanggil DAO insert.
     * @return pesan sukses/error untuk ditampilkan di View
     */
    public String tambahKamar(String nomor, String jenis, String hargaStr, String fasilitas) {
        if (nomor.isBlank() || jenis.isBlank() || hargaStr.isBlank()) {
            return "ERROR: Nomor, jenis, dan harga wajib diisi.";
        }
        double harga;
        try {
            harga = Double.parseDouble(hargaStr.replace(",", "").replace(".", ""));
        } catch (NumberFormatException e) {
            return "ERROR: Format harga tidak valid.";
        }
        if (harga <= 0) return "ERROR: Harga harus lebih dari 0.";
        if (!jenis.equals("Standard") && !jenis.equals("Deluxe") && !jenis.equals("Suite")) {
            return "ERROR: Jenis harus Standard, Deluxe, atau Suite.";
        }
        if (kamarDAO.isNomorKamarTaken(nomor.trim())) {
            return "ERROR: Nomor kamar '" + nomor + "' sudah terdaftar.";
        }
        boolean ok = kamarDAO.insertKamar(nomor.trim(), jenis, harga, fasilitas.trim());
        return ok ? "OK: Kamar " + nomor + " berhasil ditambahkan."
                  : "ERROR: Gagal menyimpan ke database.";
    }

    // ── Update ───────────────────────────────────────────────
    public String editKamar(int idKamar, String nomor, String jenis,
                             String hargaStr, String fasilitas, String status) {
        if (nomor.isBlank() || jenis.isBlank() || hargaStr.isBlank()) {
            return "ERROR: Field tidak boleh kosong.";
        }
        double harga;
        try {
            harga = Double.parseDouble(hargaStr.replace(",", "").replace(".", ""));
        } catch (NumberFormatException e) {
            return "ERROR: Format harga tidak valid.";
        }
        boolean ok = kamarDAO.updateKamar(idKamar, nomor.trim(), jenis, harga,
                                          fasilitas.trim(), status);
        return ok ? "OK: Data kamar berhasil diperbarui."
                  : "ERROR: Gagal memperbarui database.";
    }

    // ── Soft Delete ──────────────────────────────────────────
    public String hapusKamar(int idKamar) {
        boolean ok = kamarDAO.softDeleteKamar(idKamar);
        return ok ? "OK: Kamar berhasil dinonaktifkan (soft delete)."
                  : "ERROR: Gagal menonaktifkan kamar.";
    }

    // ── Update Status (dipakai Thread & Checkout) ────────────
    public boolean updateStatus(int idKamar, String status) {
        return kamarDAO.updateStatusKamar(idKamar, status);
    }
}
