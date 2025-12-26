package ui;

import javax.swing.*;
import java.awt.*;

public class BitDisplay {

    public String binaryValue; // Valeur binaire à afficher
    public int panelWidth;     // Largeur du panneau
    public int panelHeight;    // Hauteur du panneau
    public int posX;           // Position X sur le conteneur parent
    public int posY;           // Position Y sur le conteneur parent
    public JLabel valueLabel;  // Label pour afficher la valeur binaire

    // Constructeur
    public BitDisplay(String binaryValue, int width, int height, int x, int y) {
        this.binaryValue = binaryValue;
        this.panelWidth = width;
        this.panelHeight = height;
        this.posX = x;
        this.posY = y;
    }

    // Génération du panneau affichant la valeur binaire
    public JPanel generateBitPanel() {

        // Création du panneau principal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        panel.setBounds(posX, posY, panelWidth, panelHeight);
        panel.setBackground(new Color(6, 26, 83)); // Couleur de fond
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Création du label pour afficher la valeur binaire
        valueLabel = new JLabel(binaryValue, SwingConstants.CENTER);
        valueLabel.setForeground(Color.WHITE); // Couleur du texte
        valueLabel.setFont(new Font("Roboto", Font.BOLD, 12));
        valueLabel.setOpaque(false);
        valueLabel.setPreferredSize(new Dimension(panelWidth, 30));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajout du label au panneau
        panel.add(valueLabel);

        return panel; // Retourne le panneau complet
    }
}
