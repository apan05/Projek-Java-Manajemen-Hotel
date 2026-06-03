package controller;

import dao.BookingDAO;
import dao.KamarDAO;
import model.Booking;
import model.Kamar;
import thread.CleaningThread;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * BookingController.java
 * ─────────────────────────────────────────────────────────────
 * Menangani alur utama operasional hotel:
 *   Booking → Check-In → Check-Out → pembayaran (+ CleaningThread)
 *
 * [java.time] Kalkulasi selisih hari menggunakan LocalDate dan
 *   ChronoUnit.DAYS sesuai spesifikasi.
 * ─────────────────────────────────────────────────────────────
 */
public class BookingController {

    private final BookingDAO bookingDAO;
    private final KamarDAO   kamarDAO;

    public BookingController() {
        this.bookingDAO = new BookingDAO();
        this.kamarDAO   = new KamarDAO();
    }

    // ── Read ─────────────────────────────────────────────────
    public List<Booking> getAllBooking()                       { return bookingDAO.getAllBooking(); }
    public List<Booking> getBookingByStatus(String status)    { return bookingDAO.getBookingByStatus(status); }
    public Booking       getBookingById(int id)               { return bookingDAO.getBookingById(id); }

    // ── Create Booking ───────────────────────────────────────
    /**
     * Validasi tanggal lalu buat booking baru.
     * @return "OK:<id_booking>" atau "ERROR:<pesan>"
     */
    public String buatBooking(int idCustomer, int idKamar,
                               LocalDate checkin, LocalDate checkout) {
        if (checkin == null || checkout == null) {
            return "ERROR: Tanggal checkin dan checkout wajib diisi.";
        }
        if (!checkin.isBefore(checkout)) {
            return "ERROR: Tanggal checkout harus setelah tanggal checkin.";
        }
        if (checkin.isBefore(LocalDate.now())) {
            return "ERROR: Tanggal checkin tidak boleh di masa lalu.";
        }

        // Cek status kamar masih tersedia
        Kamar kamar = kamarDAO.getKamarById(idKamar);
        if (kamar == null) return "ERROR: Kamar tidak ditemukan.";
        if (!"Tersedia".equals(kamar.getStatus())) {
            return "ERROR: Kamar " + kamar.getNomorKamar() + " tidak tersedia.";
        }

        int newId = bookingDAO.insertBooking(idCustomer, idKamar, checkin, checkout);
        return newId > 0 ? "OK:" + newId
                         : "ERROR: Gagal menyimpan booking ke database.";
    }

    // ── Proses Check-In ──────────────────────────────────────
    public String prosesCheckIn(int idBooking) {
        Booking b = bookingDAO.getBookingById(idBooking);
        if (b == null)                          return "ERROR: Booking tidak ditemukan.";
        if (!"Menunggu".equals(b.getStatus()))  return "ERROR: Status booking bukan 'Menunggu'.";

        boolean ok = bookingDAO.prosesCheckIn(idBooking, b.getIdKamar());
        return ok ? "OK: Check-In berhasil untuk kamar " + b.getNomorKamar() + "."
                  : "ERROR: Gagal proses Check-In.";
    }

    // ── Proses Check-Out ─────────────────────────────────────
    /**
     * Mengubah status booking → CheckOut dan kamar → Cleaning,
     * lalu langsung menjalankan CleaningThread di background.
     *
     * [MULTITHREADING] CleaningThread berjalan terpisah dari EDT.
     */
    public String prosesCheckOut(int idBooking) {
        Booking b = bookingDAO.getBookingById(idBooking);
        if (b == null)                         return "ERROR: Booking tidak ditemukan.";
        if (!"CheckIn".equals(b.getStatus()))  return "ERROR: Tamu belum melakukan Check-In.";

        boolean ok = bookingDAO.prosesCheckOut(idBooking, b.getIdKamar());
        if (ok) {
            // Jalankan CleaningThread: kamar akan kembali Tersedia setelah delay
            CleaningThread cleaningThread = new CleaningThread(b.getIdKamar(), b.getNomorKamar());
            cleaningThread.start();
            return "OK: Check-Out berhasil. Kamar " + b.getNomorKamar()
                 + " sedang dibersihkan (otomatis Tersedia dalam 10 detik).";
        }
        return "ERROR: Gagal proses Check-Out.";
    }

    // ── Batalkan Booking ─────────────────────────────────────
    public String batalkanBooking(int idBooking) {
        Booking b = bookingDAO.getBookingById(idBooking);
        if (b == null) return "ERROR: Booking tidak ditemukan.";
        if ("CheckIn".equals(b.getStatus()) || "CheckOut".equals(b.getStatus())) {
            return "ERROR: Tidak bisa membatalkan booking yang sudah CheckIn/CheckOut.";
        }
        boolean ok = bookingDAO.batalkanBooking(idBooking, b.getIdKamar());
        return ok ? "OK: Booking berhasil dibatalkan."
                  : "ERROR: Gagal membatalkan booking.";
    }

    // ── Kalkulasi Harga ──────────────────────────────────────
    /**
     * [java.time] Hitung selisih hari lalu panggil
     * hitungHarga() secara polimorfis sesuai tipe kamar.
     *
     * [PBO] POLYMORPHISM: kamar.hitungHarga() dispatch ke
     *   Standard/Deluxe/Suite sesuai tipe objek sebenarnya.
     */
    public double kalkulasiHarga(int idKamar, LocalDate checkin, LocalDate checkout) {
        if (checkin == null || checkout == null) return 0.0;
        long jumlahMalam = ChronoUnit.DAYS.between(checkin, checkout);
        if (jumlahMalam <= 0) return 0.0;

        Kamar kamar = kamarDAO.getKamarById(idKamar);
        if (kamar == null) return 0.0;

        // [POLYMORPHISM] metode override dipanggil di sini
        return kamar.hitungHarga(jumlahMalam);
    }

    /** Hitung jumlah malam dari tanggal checkin ke checkout. */
    public long hitungJumlahMalam(LocalDate checkin, LocalDate checkout) {
        if (checkin == null || checkout == null) return 0;
        return ChronoUnit.DAYS.between(checkin, checkout);
    }
}
