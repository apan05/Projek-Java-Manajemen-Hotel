-- ============================================================
--  hotel_db.sql  |  Hotel Management System Database Script
--  Run: mysql -u root -p < hotel_db.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS hotel_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hotel_db;

-- ──────────────────────────────────────────────
--  1. tabel_users
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tabel_users (
    id_user    INT          NOT NULL AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,          -- simpan sebagai plain/hash sesuai kebutuhan
    role       ENUM('Admin','Resepsionis') NOT NULL,
    PRIMARY KEY (id_user)
) ENGINE=InnoDB;

-- ──────────────────────────────────────────────
--  2. tabel_kamar
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tabel_kamar (
    id_kamar     INT          NOT NULL AUTO_INCREMENT,
    nomor_kamar  VARCHAR(10)  NOT NULL UNIQUE,
    jenis        ENUM('Standard','Deluxe','Suite') NOT NULL,
    harga        DOUBLE       NOT NULL,
    status       ENUM('Tersedia','Dipesan','Dipakai','Cleaning') NOT NULL DEFAULT 'Tersedia',
    fasilitas    TEXT,
    is_active    TINYINT(1)   NOT NULL DEFAULT 1,   -- Soft Delete
    PRIMARY KEY (id_kamar)
) ENGINE=InnoDB;

-- ──────────────────────────────────────────────
--  3. tabel_customer
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tabel_customer (
    id_customer  INT          NOT NULL AUTO_INCREMENT,
    nama         VARCHAR(100) NOT NULL,
    no_hp        VARCHAR(20),
    alamat       TEXT,
    email        VARCHAR(100),
    is_active    TINYINT(1)   NOT NULL DEFAULT 1,   -- Soft Delete
    PRIMARY KEY (id_customer)
) ENGINE=InnoDB;

-- ──────────────────────────────────────────────
--  4. tabel_booking
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tabel_booking (
    id_booking       INT  NOT NULL AUTO_INCREMENT,
    id_customer      INT  NOT NULL,
    id_kamar         INT  NOT NULL,
    tanggal_checkin  DATE NOT NULL,
    tanggal_checkout DATE NOT NULL,
    status           ENUM('Menunggu','CheckIn','CheckOut','Batal') NOT NULL DEFAULT 'Menunggu',
    PRIMARY KEY (id_booking),
    CONSTRAINT fk_booking_customer FOREIGN KEY (id_customer) REFERENCES tabel_customer(id_customer),
    CONSTRAINT fk_booking_kamar    FOREIGN KEY (id_kamar)    REFERENCES tabel_kamar(id_kamar)
) ENGINE=InnoDB;

-- ──────────────────────────────────────────────
--  5. tabel_transaksi
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tabel_transaksi (
    id_transaksi  INT    NOT NULL AUTO_INCREMENT,
    id_booking    INT    NOT NULL,
    total_bayar   DOUBLE NOT NULL,
    tanggal_bayar DATE   NOT NULL,
    PRIMARY KEY (id_transaksi),
    CONSTRAINT fk_transaksi_booking FOREIGN KEY (id_booking) REFERENCES tabel_booking(id_booking)
) ENGINE=InnoDB;

-- ============================================================
--  DATA DUMMY
-- ============================================================

-- Users (password = "admin123" / "resep123" — ganti hash di produksi)
INSERT INTO tabel_users (username, password, role) VALUES
    ('admin',    'admin123',  'Admin'),
    ('resep01',  'resep123',  'Resepsionis'),
    ('resep02',  'resep123',  'Resepsionis');

-- Kamar
INSERT INTO tabel_kamar (nomor_kamar, jenis, harga, status, fasilitas, is_active) VALUES
    ('101', 'Standard', 300000,  'Tersedia', 'AC, TV, WiFi',                      1),
    ('102', 'Standard', 300000,  'Tersedia', 'AC, TV, WiFi',                      1),
    ('201', 'Deluxe',   550000,  'Tersedia', 'AC, TV, WiFi, Bathtub, Minibar',    1),
    ('202', 'Deluxe',   550000,  'Dipesan',  'AC, TV, WiFi, Bathtub, Minibar',    1),
    ('301', 'Suite',    1200000, 'Tersedia', 'AC, TV, WiFi, Jacuzzi, Ruang Tamu, Dapur Kecil', 1),
    ('302', 'Suite',    1200000, 'Tersedia', 'AC, TV, WiFi, Jacuzzi, Ruang Tamu, Dapur Kecil', 1);

-- Customer
INSERT INTO tabel_customer (nama, no_hp, alamat, email, is_active) VALUES
    ('Budi Santoso',   '081234567890', 'Jl. Mawar No.1, Jakarta',    'budi@email.com',    1),
    ('Siti Rahayu',    '082345678901', 'Jl. Melati No.5, Bandung',   'siti@email.com',    1),
    ('Andi Wijaya',    '083456789012', 'Jl. Kenanga No.10, Surabaya','andi@email.com',    1),
    ('Dewi Lestari',   '084567890123', 'Jl. Anggrek No.3, Bali',     'dewi@email.com',    1);

-- Booking (id_customer=1 pesan kamar 202, sudah Dipesan)
INSERT INTO tabel_booking (id_customer, id_kamar, tanggal_checkin, tanggal_checkout, status) VALUES
    (1, 4, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Menunggu'),
    (2, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Menunggu');

-- Transaksi dummy (booking id=1 sudah dibayar)
INSERT INTO tabel_transaksi (id_booking, total_bayar, tanggal_bayar) VALUES
    (1, 1650000, CURDATE());
