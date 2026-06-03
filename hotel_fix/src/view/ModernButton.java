package view;

import javax.swing.*;
import java.awt.*;

public class ModernButton extends JButton {
    public ModernButton(String text, Color bgColor) {
        super(text);
        setBackground(bgColor);
        
        // Teks warna putih agar kontras dengan warna tombol
        setForeground(Color.WHITE); 
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Hapus efek kaku bawaan Swing
        setFocusPainted(false);
        setBorderPainted(false); 
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Beri ruang (padding) agar tombol lebih berisi dan elegan
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
}