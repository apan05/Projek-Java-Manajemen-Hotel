package view;

import controller.AuthController;
import controller.KamarController;
import model.Kamar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KamarPanel extends JPanel {

    private JTable            tblKamar;
    private DefaultTableModel tableModel;
    private JTextField        txtNomor, txtHarga, txtFasilitas;
    private JComboBox<String> cmbJenis, cmbStatus;
    private JButton           btnTambah, btnEdit, btnHapus, btnRefresh;

    private final KamarController controller;
    private int selectedIdKamar = -1;

    public KamarPanel() {
        this.controller = new KamarController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        // ── Header Panel ──────────────────────────────────────
        JLabel lblPageTitle = new JLabel("Manajemen Data Kamar");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPageTitle.setForeground(new Color(40, 50, 60));
        add(lblPageTitle, BorderLayout.NORTH);

        // ── Tabel (Sebelah Kiri / Tengah) ────────────────────
        String[] kolom = {"ID", "Nomor", "Jenis", "Harga", "Status", "Fasilitas"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKamar = new JTable(tableModel);
        tblKamar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKamar.getColumnModel().getColumn(0).setMaxWidth(50);
        tblKamar.setRowHeight(30); // Jarak baris lebih longgar dan elegan
        tblKamar.getTableHeader().setReorderingAllowed(false);

        // Isi form saat baris dipilih
        tblKamar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) isiFormDariTabel();
        });

        add(new JScrollPane(tblKamar), BorderLayout.CENTER);

        // ── Panel Form Detail (Sebelah Kanan) ────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE); // Kotak form berwarna putih bersih
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setPreferredSize(new Dimension(320, 0)); // Agak dilebarkan sedikit

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 6, 8, 6); // Jarak antar input
        gbc.gridx = 0; gbc.gridwidth = 2;

        addFormRow(formPanel, gbc, 0, "Nomor Kamar:", txtNomor = new JTextField(12));
        addComboRow(formPanel, gbc, 1, "Jenis:",
                cmbJenis = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"}));
        addFormRow(formPanel, gbc, 2, "Harga/Malam:", txtHarga = new JTextField(12));
        addFormRow(formPanel, gbc, 3, "Fasilitas:",   txtFasilitas = new JTextField(12));
        addComboRow(formPanel, gbc, 4, "Status:",
                cmbStatus = new JComboBox<>(new String[]{"Tersedia","Dipesan","Dipakai","Cleaning"}));

        // ── Tombol Aksi (Gaya Grid Kotak Kompak 2x2) ─────────
        btnTambah  = new ModernButton("Tambah",  new Color(52, 152, 219)); // Biru Utama
        btnEdit    = new ModernButton("Update",  new Color(149, 165, 166)); // Abu Sekunder
        btnHapus   = new ModernButton("Hapus",   new Color(231, 76, 60));  // Merah Bahaya
        btnRefresh = new ModernButton("Refresh", new Color(149, 165, 166));

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8)); // Tatanan Kotak Kompak 2x2
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnTambah);
        btnPanel.add(btnEdit);
        if (AuthController.isAdmin()) btnPanel.add(btnHapus);
        btnPanel.add(btnRefresh);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6); // Jarak dari form ke tombol lebih jauh
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
        List<Kamar> list = controller.getAllKamar();
        for (Kamar k : list) {
            tableModel.addRow(new Object[]{
                k.getIdKamar(), k.getNomorKamar(), k.getJenis(),
                String.format("Rp %,.0f", k.getHarga()).replace(",", "."),
                k.getStatus(), k.getFasilitas()
            });
        }
        selectedIdKamar = -1;
        bersihkanForm();
    }

    private void isiFormDariTabel() {
        int row = tblKamar.getSelectedRow();
        if (row < 0) return;
        selectedIdKamar = (int) tableModel.getValueAt(row, 0);
        txtNomor.setText((String) tableModel.getValueAt(row, 1));
        cmbJenis.setSelectedItem(tableModel.getValueAt(row, 2));
        String harga = tableModel.getValueAt(row, 3).toString()
                .replace("Rp ", "").replace(".", "");
        txtHarga.setText(harga);
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 4));
        txtFasilitas.setText((String) tableModel.getValueAt(row, 5));
    }

    private void aksiTambah() {
        String hasil = controller.tambahKamar(
                txtNomor.getText(), (String) cmbJenis.getSelectedItem(),
                txtHarga.getText(), txtFasilitas.getText());
        tampilkanHasil(hasil);
        if (hasil.startsWith("OK")) loadData();
    }

    private void aksiEdit() {
        if (selectedIdKamar < 0) { showWarn("Pilih baris terlebih dahulu."); return; }
        String hasil = controller.editKamar(
                selectedIdKamar, txtNomor.getText(),
                (String) cmbJenis.getSelectedItem(), txtHarga.getText(),
                txtFasilitas.getText(), (String) cmbStatus.getSelectedItem());
        tampilkanHasil(hasil);
        if (hasil.startsWith("OK")) loadData();
    }

    private void aksiHapus() {
        if (selectedIdKamar < 0) { showWarn("Pilih baris terlebih dahulu."); return; }
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Nonaktifkan kamar ini? (Soft Delete)", "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            String hasil = controller.hapusKamar(selectedIdKamar);
            tampilkanHasil(hasil);
            if (hasil.startsWith("OK")) loadData();
        }
    }

    // ── Helpers ──────────────────────────────────────────────
    private void bersihkanForm() {
        txtNomor.setText(""); txtHarga.setText(""); txtFasilitas.setText("");
        cmbJenis.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
    }

    private void tampilkanHasil(String hasil) {
        if (hasil.startsWith("OK")) {
            JOptionPane.showMessageDialog(this, hasil.substring(3), "Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, hasil.substring(7), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridwidth = 1; gbc.gridy = row * 2;
        gbc.gridx = 0; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; p.add(field, gbc);
    }

    private void addComboRow(JPanel p, GridBagConstraints gbc, int row, String label, JComboBox<String> cmb) {
        gbc.gridwidth = 1; gbc.gridy = row * 2;
        gbc.gridx = 0; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; p.add(cmb, gbc);
    }
}