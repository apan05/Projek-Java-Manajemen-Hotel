package model;

import java.time.LocalDate;

/**
 * Booking.java  –  Entity Model
 * ─────────────────────────────────────────────────────────────
 * [PBO] ENCAPSULATION: Semua field private, akses via getter/setter.
 * ─────────────────────────────────────────────────────────────
 */
public class Booking {

    // ── ENCAPSULATION: field private ─────────────────────────
    private int       idBooking;
    private int       idCustomer;
    private int       idKamar;
    private LocalDate tanggalCheckin;
    private LocalDate tanggalCheckout;
    private String    status;        // Menunggu | CheckIn | CheckOut | Batal

    // ── Denormalized fields (untuk display di JTable) ─────────
    private String namaCustomer;
    private String nomorKamar;

    // ── Constructors ─────────────────────────────────────────
    public Booking() {}

    public Booking(int idBooking, int idCustomer, int idKamar,
                   LocalDate tanggalCheckin, LocalDate tanggalCheckout,
                   String status) {
        this.idBooking       = idBooking;
        this.idCustomer      = idCustomer;
        this.idKamar         = idKamar;
        this.tanggalCheckin  = tanggalCheckin;
        this.tanggalCheckout = tanggalCheckout;
        this.status          = status;
    }

    // ── Getter & Setter ──────────────────────────────────────
    public int       getIdBooking()                      { return idBooking; }
    public void      setIdBooking(int id)                { this.idBooking = id; }

    public int       getIdCustomer()                     { return idCustomer; }
    public void      setIdCustomer(int id)               { this.idCustomer = id; }

    public int       getIdKamar()                        { return idKamar; }
    public void      setIdKamar(int id)                  { this.idKamar = id; }

    public LocalDate getTanggalCheckin()                 { return tanggalCheckin; }
    public void      setTanggalCheckin(LocalDate d)      { this.tanggalCheckin = d; }

    public LocalDate getTanggalCheckout()                { return tanggalCheckout; }
    public void      setTanggalCheckout(LocalDate d)     { this.tanggalCheckout = d; }

    public String    getStatus()                         { return status; }
    public void      setStatus(String status)            { this.status = status; }

    public String    getNamaCustomer()                   { return namaCustomer; }
    public void      setNamaCustomer(String nama)        { this.namaCustomer = nama; }

    public String    getNomorKamar()                     { return nomorKamar; }
    public void      setNomorKamar(String nomor)         { this.nomorKamar = nomor; }

    @Override
    public String toString() {
        return "Booking{id=" + idBooking
             + ", customer=" + namaCustomer
             + ", kamar=" + nomorKamar
             + ", checkin=" + tanggalCheckin
             + ", checkout=" + tanggalCheckout
             + ", status=" + status + "}";
    }
}
