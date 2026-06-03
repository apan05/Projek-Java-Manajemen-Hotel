package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PegawaiPanel extends JPanel {

    private JTable            tblUser;
    private DefaultTableModel tableModel;
    private JTextField        txtUsername, txtPassword, txtKonfirmasi;
    private JComboBox<String> cmbRole;
    private JButton           btnTambah, btnEdit, btnHapus, btnRefresh;

    private final UserController controller;
    private int selectedIdUser = -1;

    public PegawaiPanel() {
        this.controller = new UserController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        JLabel lblPageTitle = new JLabel("Manajemen Pegawai");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPageTitle.setForeground(new Color(40, 50, 60));
        add(lblPageTitle, BorderLayout.NORTH);

        // ── Tabel ────────────────────────────────────────────
        String[] kolom = {"ID", "Username", "Role"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUser = new JTable(tableModel);
        tblUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUser.getColumnModel().getColumn(0).setMaxWidth(50);
        tblUser.setRowHeight(30);
        tblUser.getSelectionModel().addListSelectionListener(
                e -> { if (!e.getValueIsAdjusting()) isiFormDariTabel(); });
        add(new JScrollPane(tblUser), BorderLayout.CENTER);

        // ── Form Panel ───────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        String[] labels = {"Username:", "Password:", "Konfirmasi:"};
        JTextField[] fields = {
            txtUsername  = new JTextField(12),
            txtPassword  = new JPasswordField(12),
            txtKonfirmasi = new JPasswordField(12)
        };
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i; gbc.gridx = 0; gbc.gridwidth = 1;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; formPanel.add(fields[i], gbc);
        }

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Role:"), gbc);
        cmbRole = new JComboBox<>(new String[]{"Admin", "Resepsionis"});
        gbc.gridx = 1; formPanel.add(cmbRole, gbc);

        // Tombol Grid 2x2
        btnTambah  = new ModernButton("Tambah",  new Color(46, 204, 113));
        btnEdit    = new ModernButton("Update",  new Color(52, 152, 219));
        btnHapus   = new ModernButton("Hapus",   new Color(231, 76, 60));
        btnRefresh = new ModernButton("Refresh", new Color(149, 165, 166));

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnTambah);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnRefresh);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.EAST);

        // ── Listeners ────────────────────────────────────────
        btnTambah.addActionListener(e  -> aksiTambah());
        btnEdit.addActionListener(e    -> aksiEdit());
        btnHapus.addActionListener(e   -> aksiHapus());
        btnRefresh.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (User u : controller.getAllUsers()) { tableModel.addRow(new Object[]{u.getIdUser(), u.getUsername(), u.getRole()}); }
        selectedIdUser = -1; bersihkanForm();
    }

    private void isiFormDariTabel() {
        int row = tblUser.getSelectedRow();
        if (row < 0) return;
        selectedIdUser = (int) tableModel.getValueAt(row, 0);
        txtUsername.setText((String) tableModel.getValueAt(row, 1));
        txtPassword.setText(""); txtKonfirmasi.setText("");
        cmbRole.setSelectedItem(tableModel.getValueAt(row, 2));
    }

    private void aksiTambah() {
        String hasil = controller.tambahUser(txtUsername.getText(), txtPassword.getText(), txtKonfirmasi.getText(), (String) cmbRole.getSelectedItem());
        tampilkanHasil(hasil);
        if (hasil.startsWith("OK")) loadData();
    }

    private void aksiEdit() {
        if (selectedIdUser < 0) { showWarn("Pilih baris."); return; }
        String hasil = controller.editUser(selectedIdUser, txtUsername.getText(), txtPassword.getText(), (String) cmbRole.getSelectedItem());
        tampilkanHasil(hasil);
        if (hasil.startsWith("OK")) loadData();
    }

    private void aksiHapus() {
        if (selectedIdUser < 0) { showWarn("Pilih baris."); return; }
        int k = JOptionPane.showConfirmDialog(this, "Hapus user ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (k == JOptionPane.YES_OPTION) {
            String hasil = controller.hapusUser(selectedIdUser);
            tampilkanHasil(hasil);
            if (hasil.startsWith("OK")) loadData();
        }
    }

    private void bersihkanForm() {
        txtUsername.setText(""); txtPassword.setText(""); txtKonfirmasi.setText(""); cmbRole.setSelectedIndex(0);
    }

    private void tampilkanHasil(String hasil) {
        boolean ok = hasil.startsWith("OK");
        JOptionPane.showMessageDialog(this, hasil.substring(ok ? 3 : 7), ok ? "Berhasil" : "Error", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
    private void showWarn(String msg) { JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE); }
}