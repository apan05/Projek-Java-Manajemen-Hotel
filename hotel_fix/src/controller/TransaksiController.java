package controller;

import dao.BookingDAO;
import dao.TransaksiDAO;
import model.Booking;
import model.Transaksi;

import java.time.LocalDate;
import java.util.List;

/**
 * TransaksiController.java
 * ─────────────────────────────────────────────────────────────
 * Menangani pencatatan pembayaran dan laporan keuangan.
 * ─────────────────────────────────────────────────────────────
 */
public class TransaksiController {

    private final TransaksiDAO transaksiDAO;
    private final BookingDAO   bookingDAO;

    public TransaksiController() {
        this.transaksiDAO = new TransaksiDAO();
        this.bookingDAO   = new BookingDAO();
    }

    // ── Read ─────────────────────────────────────────────────
    public List<Transaksi> getAllTransaksi()                    { return transaksiDAO.getAllTransaksi(); }
    public Transaksi       getByBookingId(int idBooking)        { return transaksiDAO.getTransaksiByBookingId(idBooking); }
    public double          getTotalPendapatan()                 { return transaksiDAO.getTotalPendapatan(); }
    public double          getPendapatanBulanIni()              { return transaksiDAO.getPendapatanBulanIni(); }

    public List<Transaksi> filterByTanggal(LocalDate from, LocalDate to) {
        if (from == null || to == null) return getAllTransaksi();
        return transaksiDAO.getTransaksiByDateRange(from, to);
    }

    // ── Create Transaksi (Bayar) ─────────────────────────────
    /**
     * Mencatat pembayaran untuk booking yang sudah CheckIn.
     * @return "OK:<id_transaksi>" atau "ERROR:<pesan>"
     */
    public String bayar(int idBooking, double totalBayar) {
        Booking b = bookingDAO.getBookingById(idBooking);
        if (b == null) return "ERROR: Booking tidak ditemukan.";
        if (!"CheckIn".equals(b.getStatus()) && !"CheckOut".equals(b.getStatus())) {
            return "ERROR: Pembayaran hanya untuk booking berstatus CheckIn/CheckOut.";
        }
        if (totalBayar <= 0) return "ERROR: Total bayar harus lebih dari 0.";

        // Cek apakah sudah dibayar sebelumnya
        if (transaksiDAO.getTransaksiByBookingId(idBooking) != null) {
            return "ERROR: Booking ini sudah memiliki transaksi pembayaran.";
        }

        int newId = transaksiDAO.insertTransaksi(idBooking, totalBayar);
        return newId > 0 ? "OK:" + newId
                         : "ERROR: Gagal menyimpan transaksi ke database.";
    }

    // ── Format rupiah untuk display di View ──────────────────
    public static String formatRupiah(double amount) {
        return String.format("Rp %,.0f", amount).replace(",", ".");
    }
}
