package view;

import controller.AuthController;
import thread.AutoCancelThread;
import thread.RealtimeClockThread;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    private JLabel lblClock;
    private RealtimeClockThread clockThread;
    private AutoCancelThread autoCancelThread;

    public MainFrame() {
        initComponents();
        startBackgroundThreads();
    }

    private void initComponents() {
        setTitle("Hotel Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 750); // Ukuran ideal untuk layout sidebar
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // ─────────────────────────────────────────────────────
        // 1. SIDEBAR KIRI (WEST)
        // ─────────────────────────────────────────────────────
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(33, 37, 41)); // Warna gelap elegan ala SaaS
        sidebarPanel.setPreferredSize(new Dimension(240, 0));
        sidebarPanel.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Logo / Nama Aplikasi di atas Sidebar
        JLabel lblLogo = new JLabel("HOTELIER");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(lblLogo);
        
        JLabel lblRole = new JLabel(AuthController.getCurrentUser().getRole());
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(150, 160, 175));
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(lblRole);
        
        sidebarPanel.add(Box.createVerticalStrut(40)); // Spacer vertikal

        // Tombol-Tombol Navigasi Sidebar
        addNavButton("Manajemen Kamar", "KAMAR");
        addNavButton("Data Customer", "CUSTOMER");
        addNavButton("Booking & Check-In", "BOOKING");
        addNavButton("Transaksi & Laporan", "TRANSAKSI");

        if (AuthController.isAdmin()) {
            addNavButton("Manajemen Pegawai", "PEGAWAI");
        }

        // Dorong tombol logout ke paling bawah sidebar
        sidebarPanel.add(Box.createVerticalGlue());

        // Jam Digital Real-time di Sidebar bawah
        lblClock = new JLabel("Memuat waktu...");
        lblClock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblClock.setForeground(new Color(180, 190, 200));
        lblClock.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(lblClock);
        sidebarPanel.add(Box.createVerticalStrut(15));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnLogout.setBackground(new Color(217, 83, 79));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> prosesLogout());
        sidebarPanel.add(btnLogout);

        root.add(sidebarPanel, BorderLayout.WEST);

        // ─────────────────────────────────────────────────────
        // 2. MEJA UTAMA / CONTENT AREA (CENTER)
        // ─────────────────────────────────────────────────────
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 250));
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Daftarkan semua halaman (Ubah file View lama menjadi JPanel terlebih dahulu)
        contentPanel.add(new KamarPanel(), "KAMAR");
        contentPanel.add(new CustomerPanel(), "CUSTOMER");
        contentPanel.add(new BookingPanel(), "BOOKING");
        contentPanel.add(new TransaksiPanel(), "TRANSAKSI");
        
        if (AuthController.isAdmin()) {
            contentPanel.add(new PegawaiPanel(), "PEGAWAI");
        }

        root.add(contentPanel, BorderLayout.CENTER);

        // Window Listener untuk mematikan thread background saat close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent we) {
                stopBackgroundThreads();
                System.exit(0);
            }
        });
    }

    private void addNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBackground(new Color(33, 37, 41)); // Menyamai background sidebar
        btn.setForeground(new Color(200, 205, 210));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Listener ganti halaman saat tombol diklik
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        
        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createVerticalStrut(8)); // Jarak antar tombol
    }

    private void startBackgroundThreads() {
        clockThread = new RealtimeClockThread(lblClock);
        clockThread.start();
        autoCancelThread = new AutoCancelThread();
        autoCancelThread.start();
    }

    private void stopBackgroundThreads() {
        if (clockThread != null) clockThread.stopClock();
        if (autoCancelThread != null) autoCancelThread.stopThread();
    }

    private void prosesLogout() {
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            stopBackgroundThreads();
            dispose();
            new LoginView().setVisible(true);
        }
    }
}