package model;

/**
 * Customer.java  –  Entity Model
 * ─────────────────────────────────────────────────────────────
 * [PBO] ENCAPSULATION: Semua field private, akses via getter/setter.
 * ─────────────────────────────────────────────────────────────
 */
public class Customer {

    // ── ENCAPSULATION: field private ─────────────────────────
    private int    idCustomer;
    private String nama;
    private String noHp;
    private String alamat;
    private String email;
    private int    isActive;

    // ── Constructors ─────────────────────────────────────────
    public Customer() {}

    public Customer(int idCustomer, String nama, String noHp,
                    String alamat, String email, int isActive) {
        this.idCustomer = idCustomer;
        this.nama       = nama;
        this.noHp       = noHp;
        this.alamat     = alamat;
        this.email      = email;
        this.isActive   = isActive;
    }

    // ── Getter & Setter ──────────────────────────────────────
    public int    getIdCustomer()               { return idCustomer; }
    public void   setIdCustomer(int id)         { this.idCustomer = id; }

    public String getNama()                     { return nama; }
    public void   setNama(String nama)          { this.nama = nama; }

    public String getNoHp()                     { return noHp; }
    public void   setNoHp(String noHp)          { this.noHp = noHp; }

    public String getAlamat()                   { return alamat; }
    public void   setAlamat(String alamat)      { this.alamat = alamat; }

    public String getEmail()                    { return email; }
    public void   setEmail(String email)        { this.email = email; }

    public int    getIsActive()                 { return isActive; }
    public void   setIsActive(int isActive)     { this.isActive = isActive; }

    @Override
    public String toString() {
        return "Customer{id=" + idCustomer
             + ", nama='" + nama + "'"
             + ", noHp='" + noHp + "'}";
    }
}
