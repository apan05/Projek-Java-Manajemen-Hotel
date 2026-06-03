package model;

/**
 * DeluxeRoom.java  –  Subclass of Kamar
 * ─────────────────────────────────────────────────────────────
 * [PBO] INHERITANCE  : Mewarisi semua field & method dari Kamar.
 * [PBO] POLYMORPHISM : Override hitungHarga() – tambah service
 *                      charge 10% dari total harga.
 * ─────────────────────────────────────────────────────────────
 */
public class DeluxeRoom extends Kamar {

    // Service charge 10% untuk kamar Deluxe
    private static final double SERVICE_CHARGE_RATE = 0.10;

    public DeluxeRoom() {
        super();
    }

    public DeluxeRoom(int idKamar, String nomorKamar, double harga,
                      String status, String fasilitas, int isActive) {
        super(idKamar, nomorKamar, "Deluxe", harga, status, fasilitas, isActive);
    }

    /**
     * [PBO] POLYMORPHISM – Override hitungHarga()
     * Deluxe: Total = (harga × malam) + 10% service charge.
     */
    @Override
    public double hitungHarga(long jumlahMalam) {
        double subtotal = getHarga() * jumlahMalam;
        double charge   = subtotal * SERVICE_CHARGE_RATE;
        double total    = subtotal + charge;
        System.out.printf("[DeluxeRoom] Kamar %s: Rp%.0f x %d malam + 10%% charge = Rp%.0f%n",
                getNomorKamar(), getHarga(), jumlahMalam, total);
        return total;
    }

    public double getServiceChargeRate() {
        return SERVICE_CHARGE_RATE;
    }

    @Override
    public String toString() {
        return "DeluxeRoom{nomor=" + getNomorKamar()
             + ", harga=" + getHarga()
             + ", serviceCharge=10%"
             + ", status=" + getStatus() + "}";
    }
}
