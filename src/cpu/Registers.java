package cpu;

import javax.swing.*;
import java.awt.*;

public class Registers {

    public String registerName;    // Nom du registre (ex: A, B, X, etc.)
    public String registerValue;   // Valeur actuelle du registre
    public int panelWidth, panelHeight, panelX, panelY; // Dimensions et position du panneau
    public JLabel nameLabel, valueLabel;  // Labels pour le nom et la valeur du registre
    public Color valueColor;       // Couleur de la valeur du registre

    // Constructeur du registre
    public Registers(String name, String value, int width, int height, int x, int y, Color color) {
        this.registerName = name;
        this.registerValue = value;
        this.panelWidth = width;
        this.panelHeight = height;
        this.panelX = x;
        this.panelY = y;
        this.valueColor = color;
    }

    // Génère le panneau graphique du registre
    public JPanel generateRegisterPanel() {
        int valueFontSize = 20; // Taille de police pour la valeur du registre

        // Création du panneau avec fond noir
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color RegisterColor = new Color(0, 0, 0); // Couleur de fond noire
                g2d.setPaint(RegisterColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        panel.setBounds(panelX, panelY, panelWidth, panelHeight);
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));

        // Label pour le nom du registre
        nameLabel = new JLabel(registerName, SwingConstants.LEFT);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Roboto", Font.BOLD, 28));
        nameLabel.setOpaque(false);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        // Label pour la valeur du registre
        valueLabel = new JLabel(registerValue, SwingConstants.RIGHT);
        valueLabel.setForeground(valueColor);
        valueLabel.setFont(new Font("Bebas Neue", Font.BOLD, valueFontSize));
        valueLabel.setOpaque(false);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Ajout des labels au panneau
        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);

        return panel;
    }
}
