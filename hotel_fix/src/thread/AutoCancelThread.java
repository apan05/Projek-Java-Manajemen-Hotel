package thread;

import dao.BookingDAO;
import model.Booking;

import javax.swing.SwingUtilities;
import java.util.List;

/**
 * AutoCancelThread.java
 * ─────────────────────────────────────────────────────────────
 * [MULTITHREADING] Berjalan periodik di background untuk
 * mendeteksi dan membatalkan booking yang sudah lewat batas
 * waktu tanpa proses Check-In oleh tamu.
 *
 * Alur kerja (setiap interval):
 *   1. Query tabel_booking: status='Menunggu' & checkin < hari ini
 *   2. Untuk setiap booking expired → batalkan + kembalikan kamar
 *   3. Callback onRefresh dipanggil via invokeLater() agar View
 *      (JTable) bisa refresh tanpa race condition.
 *
 * [THREAD SAFETY] Perubahan visual wajib via SwingUtilities.invokeLater().
 * ─────────────────────────────────────────────────────────────
 */
public class AutoCancelThread extends Thread {

    // Interval scan: 60 detik (ubah ke 10 untuk demo cepat)
    private static final long INTERVAL_MS = 60_000L;

    private volatile boolean running = true;

    // Callback ke View untuk refresh tabel setelah ada pembatalan
    private Runnable onRefresh;

    public AutoCancelThread() {
        setDaemon(true);
        setName("Thread-AutoCancel");
    }

    /** Pasang callback untuk refresh tabel di DashboardView/BookingView. */
    public void setOnRefresh(Runnable onRefresh) {
        this.onRefresh = onRefresh;
    }

    @Override
    public void run() {
        System.out.println("[AutoCancelThread] Dimulai. Interval scan: "
                         + (INTERVAL_MS / 1000) + " detik.");

        while (running) {
            scanAndCancel();

            try {
                Thread.sleep(INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
        System.out.println("[AutoCancelThread] Berhenti.");
    }

    /** Scan booking expired dan batalkan satu per satu. */
    private void scanAndCancel() {
        BookingDAO bookingDAO = new BookingDAO();
        List<Booking> expired = bookingDAO.getBookingExpired();

        if (expired.isEmpty()) {
            System.out.println("[AutoCancelThread] Tidak ada booking expired.");
            return;
        }

        System.out.println("[AutoCancelThread] Ditemukan " + expired.size()
                         + " booking expired. Memproses...");

        boolean adaPerubahan = false;

        for (Booking b : expired) {
            boolean ok = bookingDAO.batalkanBooking(b.getIdBooking(), b.getIdKamar());
            if (ok) {
                adaPerubahan = true;
                System.out.printf("[AutoCancelThread] Booking #%d (Customer: %s, Kamar: %s)"
                        + " dibatalkan otomatis.%n",
                        b.getIdBooking(), b.getNamaCustomer(), b.getNomorKamar());
            }
        }

        // ── [THREAD SAFETY] Refresh View hanya jika ada perubahan ──
        if (adaPerubahan && onRefresh != null) {
            SwingUtilities.invokeLater(onRefresh);
        }
    }

    /** Hentikan thread dengan aman. */
    public void stopThread() {
        running = false;
        interrupt();
    }
}
