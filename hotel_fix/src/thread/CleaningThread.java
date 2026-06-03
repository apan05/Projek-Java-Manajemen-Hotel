package thread;

import dao.KamarDAO;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.util.function.Consumer;

/**
 * CleaningThread.java
 * ─────────────────────────────────────────────────────────────
 * [MULTITHREADING] Simulasi proses pembersihan kamar setelah
 * tamu Check-Out.
 *
 * Alur kerja:
 *   1. Kamar di-set status "Cleaning" oleh BookingController.
 *   2. Thread ini dimulai, hitung mundur 10 detik (simulasi).
 *   3. Setelah selesai → update DB: status kamar = "Tersedia".
 *   4. Callback (onSelesai) dipanggil via invokeLater() untuk
 *      refresh tabel di View.
 *
 * [THREAD SAFETY] Semua manipulasi komponen Swing dibungkus
 * dalam SwingUtilities.invokeLater() sesuai spesifikasi.
 * ─────────────────────────────────────────────────────────────
 */
public class CleaningThread extends Thread {

    private final int    idKamar;
    private final String nomorKamar;
    private final int    durasiDetik;   // durasi simulasi cleaning (detik)

    // Optional callback ke View untuk refresh tabel setelah selesai
    private Consumer<String> onSelesai;

    /**
     * Constructor default: durasi cleaning 10 detik.
     */
    public CleaningThread(int idKamar, String nomorKamar) {
        this(idKamar, nomorKamar, 10);
    }

    /**
     * Constructor dengan durasi kustom.
     */
    public CleaningThread(int idKamar, String nomorKamar, int durasiDetik) {
        this.idKamar    = idKamar;
        this.nomorKamar = nomorKamar;
        this.durasiDetik = durasiDetik;
        setDaemon(true);
        setName("Thread-Cleaning-Kamar-" + nomorKamar);
    }

    /** Pasang callback yang dipanggil saat cleaning selesai. */
    public void setOnSelesai(Consumer<String> onSelesai) {
        this.onSelesai = onSelesai;
    }

    @Override
    public void run() {
        System.out.printf("[CleaningThread] Kamar %s mulai dibersihkan (%d detik simulasi)...%n",
                nomorKamar, durasiDetik);

        try {
            // Hitung mundur simulasi cleaning
            for (int i = durasiDetik; i > 0; i--) {
                final int sisa = i;
                // ── [THREAD SAFETY] Log progress bisa dipakai label progress ─
                SwingUtilities.invokeLater(() ->
                    System.out.printf("[CleaningThread] Kamar %s: %d detik lagi...%n",
                            nomorKamar, sisa)
                );
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[CleaningThread] Kamar " + nomorKamar + " dibatalkan.");
            return;
        }

        // ── Update status kamar ke "Tersedia" di database ────
        KamarDAO kamarDAO = new KamarDAO();
        boolean ok = kamarDAO.updateStatusKamar(idKamar, "Tersedia");

        if (ok) {
            System.out.println("[CleaningThread] Kamar " + nomorKamar
                             + " selesai dibersihkan → Tersedia.");
        } else {
            System.err.println("[CleaningThread] Gagal update status kamar " + nomorKamar);
        }

        // ── [THREAD SAFETY] Panggil callback ke View via invokeLater ──
        if (onSelesai != null) {
            final String pesan = ok
                ? "Kamar " + nomorKamar + " selesai dibersihkan dan kini Tersedia."
                : "Gagal mengubah status kamar " + nomorKamar + ".";

            SwingUtilities.invokeLater(() -> onSelesai.accept(pesan));
        }
    }
}
