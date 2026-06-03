package model;

/**
 * Kamar.java  –  Abstract Superclass
 * ─────────────────────────────────────────────────────────────
 * [PBO] ABSTRACTION  : Kamar tidak diinstansiasi langsung;
 *                      subclass (Standard/Deluxe/Suite) wajib
 *                      meng-override hitungHarga().
 * [PBO] ENCAPSULATION: Semua field private, akses via getter/setter.
 * [PBO] INHERITANCE  : StandardRoom, DeluxeRoom, SuiteRoom extends Kamar.
 * ─────────────────────────────────────────────────────────────
 */
public abstract class Kamar {

    // ── ENCAPSULATION: field private ─────────────────────────
    private int    idKamar;
    private String nomorKamar;
    private String jenis;
    private double harga;          // harga dasar dari DB
    private String status;         // Tersedia | Dipesan | Dipakai | Cleaning
    private String fasilitas;
    private int    isActive;

    // ── Constructor ──────────────────────────────────────────
    public Kamar() {}

    public Kamar(int idKamar, String nomorKamar, String jenis,
                 double harga, String status, String fasilitas, int isActive) {
        this.idKamar    = idKamar;
        this.nomorKamar = nomorKamar;
        this.jenis      = jenis;
        this.harga      = harga;
        this.status     = status;
        this.fasilitas  = fasilitas;
        this.isActive   = isActive;
    }

    // ────────────────────────────────────────────────────────
    // [PBO] POLYMORPHISM (ABSTRACTION):
    //   Setiap subclass override hitungHarga() dengan logika
    //   kalkulasi tersendiri (misal: tambah service charge, dll).
    // ────────────────────────────────────────────────────────
    /**
     * Hitung harga per malam dengan kemungkinan tambahan biaya
     * sesuai tipe kamar. Di-override oleh tiap subclass.
     *
     * @param jumlahMalam  Selisih hari checkin–checkout
     * @return Total biaya yang harus dibayar tamu
     */
    public abstract double hitungHarga(long jumlahMalam);

    // ── Getter & Setter ──────────────────────────────────────
    public int    getIdKamar()              { return idKamar; }
    public void   setIdKamar(int idKamar)   { this.idKamar = idKamar; }

    public String getNomorKamar()           { return nomorKamar; }
    public void   setNomorKamar(String n)   { this.nomorKamar = n; }

    public String getJenis()                { return jenis; }
    public void   setJenis(String jenis)    { this.jenis = jenis; }

    public double getHarga()                { return harga; }
    public void   setHarga(double harga)    { this.harga = harga; }

    public String getStatus()               { return status; }
    public void   setStatus(String status)  { this.status = status; }

    public String getFasilitas()            { return fasilitas; }
    public void   setFasilitas(String f)    { this.fasilitas = f; }

    public int    getIsActive()             { return isActive; }
    public void   setIsActive(int isActive) { this.isActive = isActive; }

    @Override
    public String toString() {
        return jenis + " [" + nomorKamar + "] – Rp" + harga + "/malam | " + status;
    }
}
