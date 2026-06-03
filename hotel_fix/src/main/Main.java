package main;

import view.LoginView;
import database.DatabaseConnection;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.Font;

import javax.swing.*;

/**
 * Main.java  –  Entry Point Aplikasi
 * ─────────────────────────────────────────────────────────────
 * Seluruh inisialisasi Swing WAJIB dijalankan di Event Dispatch
 * Thread (EDT) via SwingUtilities.invokeLater() sesuai
 * thread-safety policy Java Swing.
 * ─────────────────────────────────────────────────────────────
 */
public class Main {

    public static void main(String[] args) {
        // Terapkan tema FlatLaf Light yang bersih dan modern
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Opsional: Bikin font default lebih bagus (mirip web)
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
        } catch (Exception e) {
            System.err.println("Gagal memuat FlatLaf. Fallback ke default.");
        }

        // Inisialisasi koneksi DB satu kali di startup
        try {
            DatabaseConnection.getInstance();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal terhubung ke database MySQL!\n\n" + e.getMessage()
                + "\n\nPastikan MySQL berjalan dan konfigurasi di DatabaseConnection.java benar.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Jalankan GUI di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginView login = new LoginView();
            login.setVisible(true);
        });
    }
}
