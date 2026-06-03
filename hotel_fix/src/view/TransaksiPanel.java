package view;

import controller.TransaksiController;
import model.Transaksi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TransaksiPanel extends JPanel {

    private JTable            tblTransaksi;
    private DefaultTableModel tableModel;
    private JTextField        txtFrom, txtTo;
    private JLabel            lblTotal, lblBulanIni;
    private JButton           btnFilter, btnRefresh;

    private final TransaksiController controller;

    public TransaksiPanel() {
        this.controller = new TransaksiController();
        initComponents();
        loadData(null, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));

        // ── Header & Filter ──────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        
        JLabel lblPageTitle = new JLabel("Laporan Transaksi");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPageTitle.setForeground(new Color(40, 50, 60));
        headerPanel.add(lblPageTitle, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(new Color(245, 247, 250));
        filterPanel.add(new JLabel("Dari:"));
        txtFrom = new JTextField(10);
        filterPanel.add(txtFrom);
        filterPanel.add(new JLabel("Sampai:"));
        txtTo = new JTextField(LocalDate.now().toString(), 10);
        filterPanel.add(txtTo);
        
        btnFilter  = new ModernButton("Filter", new Color(52, 152, 219));
        btnFilter.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnRefresh = new ModernButton("Semua", new Color(149, 165, 166));
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        
        filterPanel.add(btnFilter);
        filterPanel.add(btnRefresh);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ── Tabel ────────────────────────────────────────────
        String[] kolom = {"ID Trx", "ID Booking", "Customer", "Kamar", "Jenis", "Total Bayar", "Tanggal Bayar"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTransaksi = new JTable(tableModel);
        tblTransaksi.setRowHeight(30);
        tblTransaksi.getColumnModel().getColumn(0).setMaxWidth(70);
        tblTransaksi.getColumnModel().getColumn(1).setMaxWidth(80);
        add(new JScrollPane(tblTransaksi), BorderLayout.CENTER);

        // ── Panel Rekap Pendapatan ───────────────────────────
        JPanel rekapPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        rekapPanel.setBackground(Color.WHITE);
        rekapPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel panelBulan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBulan.setBackground(Color.WHITE);
        panelBulan.add(new JLabel("Pendapatan Bulan Ini: "));
        lblBulanIni = new JLabel(TransaksiController.formatRupiah(controller.getPendapatanBulanIni()));
        lblBulanIni.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBulanIni.setForeground(new Color(39, 174, 96)); // Hijau uang
        panelBulan.add(lblBulanIni);
        rekapPanel.add(panelBulan);

        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.setBackground(Color.WHITE);
        panelTotal.add(new JLabel("Total Pendapatan Seluruhnya: "));
        lblTotal = new JLabel(TransaksiController.formatRupiah(controller.getTotalPendapatan()));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(52, 152, 219));
        panelTotal.add(lblTotal);
        rekapPanel.add(panelTotal);

        add(rekapPanel, BorderLayout.SOUTH);

        // ── Listeners ────────────────────────────────────────
        btnFilter.addActionListener(e -> {
            try {
                LocalDate from = LocalDate.parse(txtFrom.getText().trim());
                LocalDate to   = LocalDate.parse(txtTo.getText().trim());
                loadData(from, to);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Format tanggal tidak valid. Gunakan YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE); }
        });
        btnRefresh.addActionListener(e -> {
            txtFrom.setText(""); txtTo.setText(LocalDate.now().toString());
            loadData(null, null);
        });
    }

    private void loadData(LocalDate from, LocalDate to) {
        tableModel.setRowCount(0);
        List<Transaksi> list = (from != null) ? controller.filterByTanggal(from, to) : controller.getAllTransaksi();
        for (Transaksi t : list) {
            tableModel.addRow(new Object[]{ t.getIdTransaksi(), t.getIdBooking(), t.getNamaCustomer(), t.getNomorKamar(), t.getJenisKamar(), TransaksiController.formatRupiah(t.getTotalBayar()), t.getTanggalBayar() });
        }
        lblBulanIni.setText(TransaksiController.formatRupiah(controller.getPendapatanBulanIni()));
        lblTotal.setText(TransaksiController.formatRupiah(controller.getTotalPendapatan()));
    }
}