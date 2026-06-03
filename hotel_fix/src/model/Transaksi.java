package model;

import java.time.LocalDate;

/**
 * Transaksi.java  –  Entity Model
 * ─────────────────────────────────────────────────────────────
 * [PBO] ENCAPSULATION: Semua field private, akses via getter/setter.
 * ─────────────────────────────────────────────────────────────
 */
public class Transaksi {

    // ── ENCAPSULATION: field private ─────────────────────────
    private int       idTransaksi;
    private int       idBooking;
    private double    totalBayar;
    private LocalDate tanggalBayar;

    // ── Denormalized fields (untuk display di JTable) ─────────
    private String namaCustomer;
    private String nomorKamar;
    private String jenisKamar;

    // ── Constructors ─────────────────────────────────────────
    public Transaksi() {}

    public Transaksi(int idTransaksi, int idBooking,
                     double totalBayar, LocalDate tanggalBayar) {
        this.idTransaksi  = idTransaksi;
        this.idBooking    = idBooking;
        this.totalBayar   = totalBayar;
        this.tanggalBayar = tanggalBayar;
    }

    // ── Getter & Setter ──────────────────────────────────────
    public int       getIdTransaksi()               { return idTransaksi; }
    public void      setIdTransaksi(int id)         { this.idTransaksi = id; }

    public int       getIdBooking()                 { return idBooking; }
    public void      setIdBooking(int id)           { this.idBooking = id; }

    public double    getTotalBayar()                { return totalBayar; }
    public void      setTotalBayar(double total)    { this.totalBayar = total; }

    public LocalDate getTanggalBayar()              { return tanggalBayar; }
    public void      setTanggalBayar(LocalDate d)   { this.tanggalBayar = d; }

    public String    getNamaCustomer()              { return namaCustomer; }
    public void      setNamaCustomer(String nama)   { this.namaCustomer = nama; }

    public String    getNomorKamar()                { return nomorKamar; }
    public void      setNomorKamar(String nomor)    { this.nomorKamar = nomor; }

    public String    getJenisKamar()                { return jenisKamar; }
    public void      setJenisKamar(String jenis)    { this.jenisKamar = jenis; }

    @Override
    public String toString() {
        return "Transaksi{id=" + idTransaksi
             + ", booking=" + idBooking
             + ", total=Rp" + totalBayar
             + ", tanggal=" + tanggalBayar + "}";
    }
}
