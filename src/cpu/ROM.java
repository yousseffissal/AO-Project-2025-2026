package cpu;

import javax.swing.*;
import ui.ComponentShadow;
import ui.CustomScroller;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;

public class ROM {

    public int panelWidth;   // Largeur du panneau ROM
    public int panelHeight;  // Hauteur du panneau ROM
    public int posX;         // Position X du panneau
    public int posY;         // Position Y du panneau
    public LinkedHashMap<String, String> romMemoryData; // Données de la ROM
    public ArrayList<JPanel> cellPanels; // Liste des panneaux représentant chaque cellule
    public JPanel containerPanel; // Panneau conteneur pour toutes les cellules
    public Color highlightColor;  // Couleur de surlignage pour la cellule courante
    public String currentAddress; // Adresse actuellement sélectionnée

    // Constructeur
    public ROM(int width, int height, int x, int y,
               LinkedHashMap<String, String> data, Color color) {
        this.panelWidth = width;
        this.panelHeight = height;
        this.posX = x;
        this.posY = y;
        this.romMemoryData = data;
        this.cellPanels = new ArrayList<>();
        this.highlightColor = color;
    }

    // Génère le panneau graphique de la ROM
    public JPanel generateROMPanel() {
        JPanel romPanel = new JPanel(new BorderLayout());
        romPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        romPanel.setBounds(posX, posY, panelWidth, panelHeight);
        romPanel.setBackground(new Color(6, 26, 83));
        romPanel.setBorder(new ComponentShadow());

        // Label du header "ROM"
        JLabel headerLabel = new JLabel("ROM", SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Roboto Black", Font.BOLD, 22));
        romPanel.add(headerLabel, BorderLayout.NORTH);

        // Conteneur pour toutes les cellules
        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBackground(new Color(6, 26, 83));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Création de chaque cellule ROM
        for (Map.Entry<String, String> entry : romMemoryData.entrySet()) {
            JPanel romCellPanel = new JPanel(new BorderLayout());
            romCellPanel.setBackground(new Color(6, 26, 83));
            romCellPanel.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));

            // Label de l'adresse
            JLabel addressLabel = new JLabel(entry.getKey(), SwingConstants.LEFT);
            addressLabel.setForeground(Color.WHITE);
            addressLabel.setFont(new Font("Bebas Neue", Font.PLAIN, 14));
            romCellPanel.add(addressLabel, BorderLayout.WEST);

            // Label de la valeur
            JLabel valueLabel = new JLabel(entry.getValue(), SwingConstants.RIGHT);
            valueLabel.setForeground(Color.WHITE);
            valueLabel.setFont(new Font("Bebas Neue", Font.PLAIN, 14));
            romCellPanel.add(valueLabel, BorderLayout.EAST);

            // Effet au survol de la souris
            HoverEffect(addressLabel, valueLabel);

            containerPanel.add(romCellPanel);

            // Séparateur entre les cellules
            JPanel separator = new JPanel();
            separator.setPreferredSize(new Dimension(0, 1));
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            separator.setBackground(Color.WHITE);
            containerPanel.add(separator);

            // Ajout du panneau cellule à la liste
            cellPanels.add(romCellPanel);
        }

        // Scroll pane pour le conteneur
        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(40, 40, 40));
        scrollPane.getVerticalScrollBar().setUI(new CustomScroller());
        scrollPane.getVerticalScrollBar().setUnitIncrement(6);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        romPanel.add(scrollPane, BorderLayout.CENTER);

        return romPanel;
    }

    // Met à jour les données de la ROM et le panneau
    public void updateROM(LinkedHashMap<String, String> romMemoryData) {
        this.romMemoryData = romMemoryData;
        this.cellPanels.clear();
        containerPanel.removeAll();

        for (Map.Entry<String, String> entry : romMemoryData.entrySet()) {
            JPanel romCellPanel = new JPanel(new BorderLayout());
            romCellPanel.setBackground(new Color(6,26,83));
            romCellPanel.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));
    
            JLabel addressLabel = new JLabel(entry.getKey(), SwingConstants.LEFT);
            addressLabel.setForeground(Color.WHITE);
            addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            romCellPanel.add(addressLabel, BorderLayout.WEST);

            JLabel valueLabel = new JLabel(entry.getValue(), SwingConstants.RIGHT);
            valueLabel.setForeground(Color.WHITE);
            valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            romCellPanel.add(valueLabel, BorderLayout.EAST);

            HoverEffect(addressLabel, valueLabel);

            containerPanel.add(romCellPanel);

            JPanel separator = new JPanel();
            separator.setPreferredSize(new Dimension(0, 1));
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            separator.setBackground(Color.WHITE);
            containerPanel.add(separator);

            cellPanels.add(romCellPanel);
        }

        containerPanel.revalidate();
        containerPanel.repaint();
    }

    // Met en surbrillance l'adresse actuelle dans la ROM
    public void setCurrent(String addressKey) {
        // Réinitialise toutes les cellules à la couleur blanche
        for (JPanel cellPanel : cellPanels) {
            for (Component component : cellPanel.getComponents()) {
                if (component instanceof JLabel label) {
                    label.setForeground(Color.WHITE);
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
            }
        }

        // Surbrillance de la cellule correspondante à l'adresse
        for (JPanel cellPanel : cellPanels) {
            JLabel addressLabel = (JLabel) cellPanel.getComponent(0);
            if (addressLabel.getText().equals(addressKey)) {
                this.currentAddress = addressKey;
                addressLabel.setForeground(highlightColor);
                addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

                JLabel valueLabel = (JLabel) cellPanel.getComponent(1);
                valueLabel.setForeground(highlightColor);
                valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                break;
            }
        }

        containerPanel.revalidate();
        containerPanel.repaint();
    }

    // Effet de survol pour les labels adresse et valeur
    public void HoverEffect(JLabel addressLabel, JLabel valueLabel) {
        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                addressLabel.setForeground(Color.YELLOW);
                valueLabel.setForeground(Color.YELLOW);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                addressLabel.setForeground(Color.WHITE);
                valueLabel.setForeground(Color.WHITE);
            }
        };
        addressLabel.addMouseListener(hoverAdapter);
        valueLabel.addMouseListener(hoverAdapter);
    }
}
