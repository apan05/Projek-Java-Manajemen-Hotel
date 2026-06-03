package thread;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * RealtimeClockThread.java
 * ─────────────────────────────────────────────────────────────
 * [MULTITHREADING] Menampilkan jam digital real-time di Dashboard.
 *
 * Berjalan sebagai Daemon Thread: otomatis mati saat JVM shutdown,
 * tidak menghalangi aplikasi untuk ditutup.
 *
 * [THREAD SAFETY] Semua update ke komponen Swing (lblClock.setText)
 * dibungkus dalam SwingUtilities.invokeLater() agar aman dari
 * ConcurrentModificationException / Swing threading rule violation.
 * ─────────────────────────────────────────────────────────────
 */
public class RealtimeClockThread extends Thread {

    private final JLabel lblClock;
    private volatile boolean running = true;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy  |  HH:mm:ss",
                                        new java.util.Locale("id", "ID"));

    /**
     * @param lblClock Label di DashboardView yang akan diperbarui tiap detik
     */
    public RealtimeClockThread(JLabel lblClock) {
        this.lblClock = lblClock;
        setDaemon(true);              // mati otomatis saat app ditutup
        setName("Thread-RealtimeClock");
    }

    @Override
    public void run() {
        System.out.println("[RealtimeClockThread] Dimulai.");
        while (running) {
            final String waktu = LocalDateTime.now().format(FORMATTER);

            // ── [THREAD SAFETY] Update UI harus via invokeLater ──
            SwingUtilities.invokeLater(() -> {
                if (lblClock != null) {
                    lblClock.setText(waktu);
                }
            });

            try {
                Thread.sleep(1000); // Tidur 1 detik
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
        System.out.println("[RealtimeClockThread] Berhenti.");
    }

    /** Hentikan loop thread dengan aman. */
    public void stopClock() {
        running = false;
        interrupt();
    }
}
