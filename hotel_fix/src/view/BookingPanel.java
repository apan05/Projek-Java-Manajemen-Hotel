package view;

import controller.BookingController;
import controller.CustomerController;
import controller.KamarController;
import controller.TransaksiController;
import model.Booking;
import model.Customer;
import model.Kamar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class BookingPanel extends JPanel {

    private JTable            tblBooking;
    private DefaultTableModel tableModel;

    private JComboBox<String> cmbCustomer, cmbKamar;
    private JTextField        txtCheckin, txtCheckout;
    private JLabel            lblHitungHarga;
    private JButton           btnBooking, btnCheckIn, btnCheckOut, btnBatal, btnRefresh;

    private final BookingController    bookingController;
    private final KamarController      kamarController;
    private final CustomerController   customerController;
    private final TransaksiController  transaksiController;

    private int    selectedIdBooking = -1;
    private int    selectedIdKamar   = -1;
    private String selectedStatus    = "";

    public BookingPanel() {
        this.bookingController   = new BookingController();
        this.kamarController     = new KamarController();
        this.customerController  = new CustomerController();
        this.transaksiController = new TransaksiController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        JLabel lblPageTitle = new JLabel("Booking & Operasional");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPageTitle.setForeground(new Color(40, 50, 60));
        add(lblPageTitle, BorderLayout.NORTH);

        // ── Tabel Booking ────────────────────────────────────
        String[] kolom = {"ID", "Customer", "Kamar", "Check-In", "Check-Out", "Status"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBooking = new JTable(tableModel);
        tblBooking.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBooking.getColumnModel().getColumn(0).setMaxWidth(50);
        tblBooking.setRowHeight(30);
        tblBooking.getSelectionModel().addListSelectionListener(
                e -> { if (!e.getValueIsAdjusting()) isiDariTabel(); });

        add(new JScrollPane(tblBooking), BorderLayout.CENTER);

        // ── Panel Kanan (Form & Aksi) ────────────────────────
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setBackground(new Color(245, 247, 250));
        rightPanel.setPreferredSize(new Dimension(320, 0));

        // 1. Form Booking Baru
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
        JLabel lblTitleForm = new JLabel("Buat Booking Baru");
        lblTitleForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblTitleForm, gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 1; formPanel.add(new JLabel("Customer:"), gbc);
        cmbCustomer = new JComboBox<>(); gbc.gridx = 1; formPanel.add(cmbCustomer, gbc);

        gbc.gridy = 2; gbc.gridx = 0; formPanel.add(new JLabel("Kamar:"), gbc);
        cmbKamar = new JComboBox<>(); gbc.gridx = 1; formPanel.add(cmbKamar, gbc);

        gbc.gridy = 3; gbc.gridx = 0; formPanel.add(new JLabel("Check-In:"), gbc);
        txtCheckin = new JTextField(LocalDate.now().toString()); gbc.gridx = 1; formPanel.add(txtCheckin, gbc);

        gbc.gridy = 4; gbc.gridx = 0; formPanel.add(new JLabel("Check-Out:"), gbc);
        txtCheckout = new JTextField(LocalDate.now().plusDays(1).toString()); gbc.gridx = 1; formPanel.add(txtCheckout, gbc);

        gbc.gridy = 5; gbc.gridx = 0; formPanel.add(new JLabel("Est. Harga:"), gbc);
        lblHitungHarga = new JLabel("Rp 0");
        lblHitungHarga.setForeground(new Color(39, 174, 96));
        lblHitungHarga.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 1; formPanel.add(lblHitungHarga, gbc);

        // Listener hitung harga
        cmbKamar.addActionListener(e -> hitungEstimasi());
        txtCheckin.addFocusListener(new java.awt.event.FocusAdapter() { public void focusLost(java.awt.event.FocusEvent e) { hitungEstimasi(); } });
        txtCheckout.addFocusListener(new java.awt.event.FocusAdapter() { public void focusLost(java.awt.event.FocusEvent e) { hitungEstimasi(); } });

        btnBooking = new ModernButton("Buat Booking", new Color(52, 152, 219));
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2; gbc.insets = new Insets(15, 6, 0, 6);
        formPanel.add(btnBooking, gbc);
        rightPanel.add(formPanel, BorderLayout.NORTH);

        // 2. Panel Aksi (Grid 2x2)
        JPanel aksiPanel = new JPanel(new GridBagLayout());
        aksiPanel.setBackground(Color.WHITE);
        aksiPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbcAksi = new GridBagConstraints();
        gbcAksi.fill = GridBagConstraints.HORIZONTAL;
        gbcAksi.insets = new Insets(0, 0, 15, 0);
        gbcAksi.gridx = 0; gbcAksi.gridy = 0;

        JLabel lblTitleAksi = new JLabel("Aksi Booking Terpilih");
        lblTitleAksi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        aksiPanel.add(lblTitleAksi, gbcAksi);

        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        btnGrid.setBackground(Color.WHITE);
        btnCheckIn  = new ModernButton("Check-In",  new Color(46, 204, 113));
        btnCheckOut = new ModernButton("Check-Out", new Color(230, 126, 34));
        btnBatal    = new ModernButton("Batalkan",  new Color(231, 76, 60));
        btnRefresh  = new ModernButton("Refresh",   new Color(149, 165, 166));
        
        btnGrid.add(btnCheckIn);
        btnGrid.add(btnCheckOut);
        btnGrid.add(btnBatal);
        btnGrid.add(btnRefresh);

        gbcAksi.gridy = 1; gbcAksi.insets = new Insets(0,0,0,0);
        aksiPanel.add(btnGrid, gbcAksi);

        rightPanel.add(aksiPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // ── Listeners ────────────────────────────────────────
        btnBooking.addActionListener(e  -> aksiBooking());
        btnCheckIn.addActionListener(e  -> aksiCheckIn());
        btnCheckOut.addActionListener(e -> aksiCheckOut());
        btnBatal.addActionListener(e    -> aksiBatal());
        btnRefresh.addActionListener(e  -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Booking b : bookingController.getAllBooking()) {
            tableModel.addRow(new Object[]{ b.getIdBooking(), b.getNamaCustomer(), b.getNomorKamar(), b.getTanggalCheckin(), b.getTanggalCheckout(), b.getStatus() });
        }
        refreshDropdowns();
        selectedIdBooking = -1; selectedIdKamar = -1; selectedStatus = "";
    }

    private void refreshDropdowns() {
        cmbCustomer.removeAllItems();
        for (Customer c : customerController.getAllCustomer()) { cmbCustomer.addItem(c.getIdCustomer() + " – " + c.getNama()); }
        cmbKamar.removeAllItems();
        for (Kamar k : kamarController.getKamarTersedia()) { cmbKamar.addItem(k.getIdKamar() + " – " + k.getNomorKamar() + " (" + k.getJenis() + ")"); }
        hitungEstimasi();
    }

    private void hitungEstimasi() {
        try {
            String itemKamar = (String) cmbKamar.getSelectedItem();
            if (itemKamar == null) { lblHitungHarga.setText("Rp 0"); return; }
            int idKamar = Integer.parseInt(itemKamar.split(" – ")[0]);
            LocalDate ci = LocalDate.parse(txtCheckin.getText().trim());
            LocalDate co = LocalDate.parse(txtCheckout.getText().trim());
            double total = bookingController.kalkulasiHarga(idKamar, ci, co);
            lblHitungHarga.setText(TransaksiController.formatRupiah(total));
        } catch (Exception ex) {
            lblHitungHarga.setText("Format tanggal salah");
        }
    }

    private void isiDariTabel() {
        int row = tblBooking.getSelectedRow();
        if (row < 0) return;
        selectedIdBooking = (int) tableModel.getValueAt(row, 0);
        selectedStatus    = (String) tableModel.getValueAt(row, 5);
        Booking b = bookingController.getBookingById(selectedIdBooking);
        if (b != null) selectedIdKamar = b.getIdKamar();
    }

    private void aksiBooking() {
        try {
            String itemCustomer = (String) cmbCustomer.getSelectedItem();
            String itemKamar    = (String) cmbKamar.getSelectedItem();
            if (itemCustomer == null || itemKamar == null) { showWarn("Pilih customer dan kamar."); return; }
            int idCustomer = Integer.parseInt(itemCustomer.split(" – ")[0]);
            int idKamar    = Integer.parseInt(itemKamar.split(" – ")[0]);
            LocalDate ci   = LocalDate.parse(txtCheckin.getText().trim());
            LocalDate co   = LocalDate.parse(txtCheckout.getText().trim());

            String hasil = bookingController.buatBooking(idCustomer, idKamar, ci, co);
            tampilkanHasil(hasil);
            if (hasil.startsWith("OK")) loadData();
        } catch (Exception ex) { showWarn("Format input tidak valid: " + ex.getMessage()); }
    }

    private void aksiCheckIn() {
        if (selectedIdBooking < 0) { showWarn("Pilih booking terlebih dahulu."); return; }
        String hasil = bookingController.prosesCheckIn(selectedIdBooking);
        tampilkanHasil(hasil);
        if (hasil.startsWith("OK")) loadData();
    }

    private void aksiCheckOut() {
        if (selectedIdBooking < 0) { showWarn("Pilih booking terlebih dahulu."); return; }
        Booking b = bookingController.getBookingById(selectedIdBooking);
        if (b == null) return;
        double tagihan = bookingController.kalkulasiHarga(b.getIdKamar(), b.getTanggalCheckin(), b.getTanggalCheckout());

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Total tagihan: " + TransaksiController.formatRupiah(tagihan) + "\nProses Check-Out dan catat pembayaran?", "Konfirmasi Check-Out", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            String hasilCO = bookingController.prosesCheckOut(selectedIdBooking);
            if (hasilCO.startsWith("OK")) {
                String hasilBayar = transaksiController.bayar(selectedIdBooking, tagihan);
                tampilkanHasil(hasilCO + "\n" + hasilBayar);
            } else { tampilkanHasil(hasilCO); }
            loadData();
        }
    }

    private void aksiBatal() {
        if (selectedIdBooking < 0) { showWarn("Pilih booking terlebih dahulu."); return; }
        int k = JOptionPane.showConfirmDialog(this, "Batalkan booking ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (k == JOptionPane.YES_OPTION) {
            String hasil = bookingController.batalkanBooking(selectedIdBooking);
            tampilkanHasil(hasil);
            if (hasil.startsWith("OK")) loadData();
        }
    }

    private void tampilkanHasil(String hasil) {
        boolean ok = hasil.startsWith("OK");
        JOptionPane.showMessageDialog(this, hasil.substring(ok ? 3 : 7), ok ? "Berhasil" : "Error", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
    private void showWarn(String msg) { JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE); }
}