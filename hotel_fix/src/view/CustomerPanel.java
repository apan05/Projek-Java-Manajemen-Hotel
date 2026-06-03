package view;

import controller.AuthController;
import controller.CustomerController;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerPanel extends JPanel {

    private JTable            tblCustomer;
    private DefaultTableModel tableModel;
    private JTextField        txtNama, txtNoHp, txtAlamat, txtEmail, txtCari;
    private JButton           btnTambah, btnEdit, btnHapus, btnCari, btnRefresh;

    private final CustomerController controller;
    private int selectedIdCustomer = -1;

    public CustomerPanel() {
        this.controller = new CustomerController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        // ── Header Panel ──────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        
        JLabel lblPageTitle = new JLabel("Data Customer");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPageTitle.setForeground(new Color(40, 50, 60));
        headerPanel.add(lblPageTitle, BorderLayout.WEST);

        // Panel Cari
        JPanel cariPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cariPanel.setBackground(new Color(245, 247, 250));
        cariPanel.add(new JLabel("Cari Nama:"));
        txtCari = new JTextField(15);
        btnCari = new ModernButton("Cari", new Color(52, 152, 219));
        btnCari.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12)); // Padding lebih kecil
        btnRefresh = new ModernButton("Semua", new Color(149, 165, 166));
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        
        cariPanel.add(txtCari);
        cariPanel.add(btnCari);
        cariPanel.add(btnRefresh);
        headerPanel.add(cariPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ── Tabel (Kiri) ──────────────────────────────────────
        String[] kolom = {"ID", "Nama", "No. HP", "Email", "Alamat"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCustomer = new JTable(tableModel);
        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCustomer.getColumnModel().getColumn(0).setMaxWidth(50);
        tblCustomer.setRowHeight(30); // Lebih longgar
        tblCustomer.getSelectionModel().addListSelectionListener(
                e -> { if (!e.getValueIsAdjusting()) isiFormDariTabel(); });

        add(new JScrollPane(tblCustomer), BorderLayout.CENTER);

        // ── Form Panel (Kanan) ────────────────────────────────
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

        String[] labels = {"Nama:", "No. HP:", "Email:", "Alamat:"};
        JTextField[] fields = {
            txtNama   = new JTextField(12),
            txtNoHp   = new JTextField(12),
            txtEmail  = new JTextField(12),
            txtAlamat = new JTextField(12)
        };
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i; gbc.gridx = 0; gbc.gridwidth = 1;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        // ── Tombol Aksi (Grid 2x2) ────────────────────────────
        btnTambah = new ModernButton("Tambah", new Color(46, 204, 113));
        btnEdit   = new ModernButton("Update", new Color(52, 152, 219));
        btnHapus  = new ModernButton("Hapus",  new Color(231, 76, 60));

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8)); // Kompak 2x2
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnTambah);
        btnPanel.add(btnEdit);
        if (AuthController.isAdmin()) {
            btnPanel.add(btnHapus);
        }

        gbc.gridy = labels.length; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.EAST);

        // ── Listeners ────────────────────────────────────────
        btnCari.addActionListener(e    -> cariCustomer());
        txtCari.addActionListener(e    -> cariCustomer());
        btnRefresh.addActionListener(e -> loadData());
        btnTambah.addActionListener(e  -> aksiTambah());
        btnEdit.addActionListener(e    -> aksiEdit());
        btnHapus.addActionListener(e   -> aksiHapus());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Customer c : controller.getAllCustomer()) {
            tableModel.addRow(new Object[]{ c.getIdCustomer(), c.getNama(), c.getNoHp(), c.getEmail(), c.getAlamat() });
        }
        selectedIdCustomer = -1;
        bersihkanForm();
    }

    private void cariCustomer() {
        tableModel.setRowCount(0);
        for (Customer c : controller.cariCustomer(txtCari.getText().trim())) {
            tableModel.addRow(new Object[]{ c.getIdCustomer(), c.getNama(), c.getNoHp(), c.getEmail(), c.getAlamat() });
        }
    }

    private void isiFormDariTabel() {
        int row = tblCustomer.getSelectedRow();
        if (row < 0) return;
        selectedIdCustomer = (int) tableModel.getValueAt(row, 0);
        txtNama.setText((String) tableModel.getValueAt(row, 1));
        txtNoHp.setText((String) tableModel.getValueAt(row, 2));
        txtEmail.setText((String) tableModel.getValueAt(row, 3));
        txtAlamat.setText((String) tableModel.getValueAt(row, 4));
    }

    private void aksiTambah() {
        String hasil = controller.tambahCustomer(txtNama.getText(), txtNoHp.getText(), txtAlamat.getText(), txtEmail.getText());
        tampilkanHasil(hasil);
        if (hasil.startsWith("OK")) loadData();
    }

    private void aksiEdit() {
        if (selectedIdCustomer < 0) { showWarn("Pilih baris terlebih dahulu."); return; }
        String hasil = controller.editCustomer(selectedIdCustomer, txtNama.getText(), txtNoHp.getText(), txtAlamat.getText(), txtEmail.getText());
        tampilkanHasil(hasil);
        if (hasil.startsWith("OK")) loadData();
    }

    private void aksiHapus() {
        if (selectedIdCustomer < 0) { showWarn("Pilih baris terlebih dahulu."); return; }
        int k = JOptionPane.showConfirmDialog(this, "Nonaktifkan customer ini? (Soft Delete)", "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (k == JOptionPane.YES_OPTION) {
            String hasil = controller.hapusCustomer(selectedIdCustomer);
            tampilkanHasil(hasil);
            if (hasil.startsWith("OK")) loadData();
        }
    }

    private void bersihkanForm() {
        txtNama.setText(""); txtNoHp.setText(""); txtEmail.setText(""); txtAlamat.setText("");
        txtCari.setText("");
    }

    private void tampilkanHasil(String hasil) {
        boolean ok = hasil.startsWith("OK");
        JOptionPane.showMessageDialog(this, hasil.substring(ok ? 3 : 7), ok ? "Berhasil" : "Error", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}