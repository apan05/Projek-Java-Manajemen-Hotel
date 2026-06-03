package model;

/**
 * StandardRoom.java  –  Subclass of Kamar
 * ─────────────────────────────────────────────────────────────
 * [PBO] INHERITANCE  : Mewarisi semua field & method dari Kamar.
 * [PBO] POLYMORPHISM : Override hitungHarga() – harga polos tanpa
 *                      service charge tambahan.
 * ─────────────────────────────────────────────────────────────
 */
public class StandardRoom extends Kamar {

    // Tidak ada biaya tambahan untuk Standard
    private static final double SERVICE_CHARGE_RATE = 0.0;

    public StandardRoom() {
        super();
    }

    public StandardRoom(int idKamar, String nomorKamar, double harga,
                        String status, String fasilitas, int isActive) {
        super(idKamar, nomorKamar, "Standard", harga, status, fasilitas, isActive);
    }

    /**
     * [PBO] POLYMORPHISM – Override hitungHarga()
     * Standard: Total = harga/malam × jumlahMalam (tidak ada surcharge).
     */
    @Override
    public double hitungHarga(long jumlahMalam) {
        double total = getHarga() * jumlahMalam;
        System.out.printf("[StandardRoom] Kamar %s: Rp%.0f x %d malam = Rp%.0f%n",
                getNomorKamar(), getHarga(), jumlahMalam, total);
        return total;
    }

    @Override
    public String toString() {
        return "StandardRoom{nomor=" + getNomorKamar()
             + ", harga=" + getHarga()
             + ", status=" + getStatus() + "}";
    }
}
