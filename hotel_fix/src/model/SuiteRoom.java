package model;

/**
 * SuiteRoom.java  –  Subclass of Kamar
 * ─────────────────────────────────────────────────────────────
 * [PBO] INHERITANCE  : Mewarisi semua field & method dari Kamar.
 * [PBO] POLYMORPHISM : Override hitungHarga() – service charge 20%
 *                      plus biaya sarapan tetap per malam.
 * ─────────────────────────────────────────────────────────────
 */
public class SuiteRoom extends Kamar {

    // Suite: service charge 20% + biaya sarapan Rp150.000/malam
    private static final double SERVICE_CHARGE_RATE  = 0.20;
    private static final double BREAKFAST_COST_NIGHT = 150_000.0;

    public SuiteRoom() {
        super();
    }

    public SuiteRoom(int idKamar, String nomorKamar, double harga,
                     String status, String fasilitas, int isActive) {
        super(idKamar, nomorKamar, "Suite", harga, status, fasilitas, isActive);
    }

    /**
     * [PBO] POLYMORPHISM – Override hitungHarga()
     * Suite: Total = (harga × malam) + 20% service charge
     *              + (biaya sarapan × malam).
     */
    @Override
    public double hitungHarga(long jumlahMalam) {
        double subtotal   = getHarga() * jumlahMalam;
        double charge     = subtotal * SERVICE_CHARGE_RATE;
        double breakfast  = BREAKFAST_COST_NIGHT * jumlahMalam;
        double total      = subtotal + charge + breakfast;
        System.out.printf("[SuiteRoom] Kamar %s: Rp%.0f x %d malam + 20%% + sarapan = Rp%.0f%n",
                getNomorKamar(), getHarga(), jumlahMalam, total);
        return total;
    }

    public double getServiceChargeRate() {
        return SERVICE_CHARGE_RATE;
    }

    public double getBreakfastCostPerNight() {
        return BREAKFAST_COST_NIGHT;
    }

    @Override
    public String toString() {
        return "SuiteRoom{nomor=" + getNomorKamar()
             + ", harga=" + getHarga()
             + ", serviceCharge=20%, sarapan=Rp150.000/malam"
             + ", status=" + getStatus() + "}";
    }
}
