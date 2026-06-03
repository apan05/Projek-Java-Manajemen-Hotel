package view;

import controller.AuthController;
import com.formdev.flatlaf.FlatLightLaf; // Import FlatLaf di sini jika diperlukan

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * LoginView.java  –  Form Login Modern & Minimalis
 */
public class LoginView extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JLabel         lblStatus;

    private final AuthController authController;

    public LoginView() {
        this.authController = new AuthController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Hotel Management System – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 340); // Sedikit disesuaikan ukurannya agar proporsional
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 50, 80)); // Warna header/background utama tetap dipertahankan
        panel.setBorder(new EmptyBorder(35, 40, 35, 40));
        setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        // ── Judul Aplikasi
        JLabel lblTitle = new JLabel("Hotel Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Mengganti ke Segoe UI
        lblTitle.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Silakan masuk untuk melanjutkan", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(180, 200, 220));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 20, 5); // Beri jarak bawah lebih banyak setelah subtitle
        panel.add(lblSub, gbc);

        // Reset inset untuk field input
        gbc.insets = new Insets(6, 5, 6, 5);

        // ── Username
        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        panel.add(makeLabel("Username"), gbc); // Menghilangkan titik dua (:) agar clean ala web modern
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        styleField(txtUsername);
        panel.add(txtUsername, gbc);

        // ── Password
        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(makeLabel("Password"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        styleField(txtPassword);
        panel.add(txtPassword, gbc);

        // ── Tombol Login (Menggunakan ModernButton kustom)
        btnLogin = new ModernButton("LOGIN", new Color(52, 152, 219));
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5); // Padding atas diperlebar supaya tombol terpisah rapi
        panel.add(btnLogin, gbc);

        // ── Label Status Error
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(new Color(231, 76, 60));
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 5, 0, 5);
        panel.add(lblStatus, gbc);

        // ── Action Listeners
        btnLogin.addActionListener(e -> prosesLogin());
        txtPassword.addActionListener(e -> prosesLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocusInWindow());
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        btnLogin.setEnabled(false);
        lblStatus.setText("Memverifikasi...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                return authController.login(username, password);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        dispose();
                        new MainFrame().setVisible(true);
                    } else {
                        lblStatus.setText("Username atau password salah!");
                        txtPassword.setText("");
                        txtPassword.requestFocusInWindow();
                    }
                } catch (Exception ex) {
                    lblStatus.setText("Koneksi database gagal: " + ex.getMessage());
                } finally {
                    btnLogin.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(200, 220, 240));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Dibuat sedikit bold agar tegas
        return lbl;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // FlatLaf secara default sudah membuat border bagus, 
        // kita hanya perlu menambahkan padding dalam (empty border) agar teks input tidak menempel ke pinggir
        field.setBorder(BorderFactory.createCompoundBorder(
            field.getBorder(), 
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }
}