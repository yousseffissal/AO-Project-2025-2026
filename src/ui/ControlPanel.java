package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel {

    // Texte de l'étiquette et valeur affichée
    public String labelText;
    public String value;

    // Dimensions et position du panneau
    public int panelWidth;
    public int panelHeight;
    public int posX;
    public int posY;

    // JLabel pour afficher la valeur du contrôle
    public JLabel controlvalueLabel;

    // Couleur de la valeur affichée
    public Color valueColor;

    // Constructeur de la classe
    public ControlPanel(String labelText,String value,int width,int height,int x,int y,Color valueColor) {
        this.labelText = labelText;
        this.value = value;
        this.panelWidth = width;
        this.panelHeight = height;
        this.posX = x;
        this.posY = y;
        this.valueColor = valueColor;
    }

    // Génère le JPanel représentant le panneau de contrôle avec l'étiquette et la valeur
    public JPanel generateControPanel() {

        int valueFontSize = 20; // Taille de la police pour la valeur

        // Création du panneau principal
        JPanel controlpanel = new JPanel();
        controlpanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        controlpanel.setBounds(posX, posY, panelWidth, panelHeight);
        controlpanel.setBackground(new Color(0, 0, 0)); // Fond noir
        controlpanel.setBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1, true) // Bordure blanche avec coins arrondis
        );
        controlpanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlpanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        controlpanel.setLayout(new BorderLayout());

        // Création du JLabel pour le texte de l'étiquette
        JLabel controlLabel = new JLabel(labelText, SwingConstants.LEFT);
        controlLabel.setForeground(Color.WHITE); // Couleur du texte en blanc
        controlLabel.setFont(new Font("Roboto", Font.BOLD, 28)); // Police de l'étiquette
        controlLabel.setOpaque(false); // Fond transparent
        controlLabel.setBorder(
                BorderFactory.createEmptyBorder(0, 20, 0, 0) // Marge à gauche
        );

        // Création du JLabel pour afficher la valeur
        controlvalueLabel = new JLabel(value, SwingConstants.RIGHT);
        controlvalueLabel.setForeground(valueColor); // Couleur personnalisée
        controlvalueLabel.setFont(
                new Font("Bebas Neue", Font.BOLD, valueFontSize) // Police pour la valeur
        );
        controlvalueLabel.setOpaque(false); // Fond transparent
        controlvalueLabel.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 20) // Marge à droite
        );

        // Ajout des JLabel dans le panneau
        controlpanel.add(controlLabel, BorderLayout.WEST);
        controlpanel.add(controlvalueLabel, BorderLayout.EAST);

        return controlpanel; // Retourne le panneau complet
    }
}
