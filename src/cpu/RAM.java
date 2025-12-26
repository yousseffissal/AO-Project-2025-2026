package cpu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import ui.ComponentShadow;
import ui.CustomScroller;

public class RAM {

    public int panelWidth;                     // Largeur du panneau RAM
    public int panelHeight;                    // Hauteur du panneau RAM
    public int posX;                           // Position X du panneau
    public int posY;                           // Position Y du panneau
    public LinkedHashMap<String, String> RamMemoryData; // Contenu de la RAM (adresse -> valeur)
    public ArrayList<JPanel> cellPanels;       // Liste des panneaux représentant chaque cellule
    public JPanel containerPanel;              // Conteneur des cellules
    public Color highlightColor;               // Couleur de surbrillance lors du hover

    // Constructeur
    public RAM(int width, int height, int x, int y, LinkedHashMap<String, String> data, Color color) {
        this.panelWidth = width;
        this.panelHeight = height;
        this.posX = x;
        this.posY = y;
        this.RamMemoryData = data;
        this.cellPanels = new ArrayList<>();
        this.highlightColor = color;
    }

    // Génère le panneau RAM complet avec toutes les cellules
    public JPanel generateRAMPanel() {
        JPanel memoryPanel = new JPanel();
        memoryPanel.setLayout(new BorderLayout());
        memoryPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        memoryPanel.setBounds(posX, posY, panelWidth, panelHeight);
        memoryPanel.setBackground(new Color(6, 26, 83));
        memoryPanel.setBorder(new ComponentShadow());

        // Header "RAM"
        JLabel headerLabel = new JLabel("RAM", SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Roboto Black", Font.BOLD, 22));
        memoryPanel.add(headerLabel, BorderLayout.NORTH);

        // Conteneur pour les cellules mémoire
        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBackground(new Color(6, 26, 83));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Création de chaque cellule mémoire
        for (Map.Entry<String, String> entry : RamMemoryData.entrySet()) {
            JPanel memoryCell = new JPanel();
            memoryCell.setLayout(new BorderLayout());
            memoryCell.setBackground(new Color(6, 26, 83));
            memoryCell.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

            JLabel addressLabel = new JLabel(entry.getKey(), SwingConstants.LEFT); // Adresse
            addressLabel.setForeground(Color.WHITE);
            addressLabel.setFont(new Font("Bebas Neue", Font.PLAIN, 14));
            memoryCell.add(addressLabel, BorderLayout.WEST);

            JLabel valueLabel = new JLabel(entry.getValue(), SwingConstants.RIGHT); // Valeur
            valueLabel.setForeground(Color.WHITE);
            valueLabel.setFont(new Font("Bebas Neue", Font.PLAIN, 14));
            memoryCell.add(valueLabel, BorderLayout.EAST);

            HoverEffect(addressLabel, valueLabel); // Effet hover

            containerPanel.add(memoryCell);

            // Séparateur entre les cellules
            JPanel separator = new JPanel();
            separator.setPreferredSize(new Dimension(0, 1));
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            separator.setBackground(Color.WHITE);
            containerPanel.add(separator);

            cellPanels.add(memoryCell);
        }

        // Scroll pane pour RAM
        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(40, 40, 40));
        scrollPane.getVerticalScrollBar().setUI(new CustomScroller());
        scrollPane.getVerticalScrollBar().setUnitIncrement(6);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        memoryPanel.add(scrollPane, BorderLayout.CENTER);

        return memoryPanel;
    }

    // Met à jour le contenu de la RAM avec de nouvelles données
    public void updateRAM(LinkedHashMap<String, String> newMemoryData) {
        this.RamMemoryData = newMemoryData;
        cellPanels.clear();
        containerPanel.removeAll();

        for (Map.Entry<String, String> entry : newMemoryData.entrySet()) {
            JPanel memoryCell = new JPanel(new BorderLayout());
            memoryCell.setBackground(new Color(6,26,83));
            memoryCell.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));

            JLabel addressLabel = new JLabel(entry.getKey(), SwingConstants.LEFT);
            addressLabel.setForeground(Color.WHITE);
            addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            memoryCell.add(addressLabel, BorderLayout.WEST);

            JLabel valueLabel = new JLabel(entry.getValue(), SwingConstants.RIGHT);
            valueLabel.setForeground(Color.WHITE);
            valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            memoryCell.add(valueLabel, BorderLayout.EAST);

            HoverEffect(addressLabel, valueLabel);

            containerPanel.add(memoryCell);

            JPanel separator = new JPanel();
            separator.setPreferredSize(new Dimension(0, 1));
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            separator.setBackground(Color.WHITE);
            containerPanel.add(separator);

            cellPanels.add(memoryCell);
        }

        containerPanel.revalidate();
        containerPanel.repaint();
    }

    // Réinitialise la RAM (vide toutes les cellules)
    public void resetRAM() {
        LinkedHashMap<String, String> ramData = new LinkedHashMap<>();
        this.RamMemoryData = ramData;
        cellPanels.clear();
        containerPanel.removeAll();

        for (Map.Entry<String, String> entry : RamMemoryData.entrySet()) {
            JPanel cellPanel = new JPanel();
            cellPanel.setLayout(new BorderLayout());
            cellPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel keyLabel = new JLabel(entry.getKey(), SwingConstants.LEFT);
            keyLabel.setForeground(new Color(180, 180, 180));
            keyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cellPanel.add(keyLabel, BorderLayout.WEST);

            JLabel valueLabel = new JLabel(entry.getValue(), SwingConstants.RIGHT);
            valueLabel.setForeground(new Color(180, 180, 180));
            valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cellPanel.add(valueLabel, BorderLayout.EAST);

            HoverEffect(keyLabel, valueLabel);

            containerPanel.add(cellPanel);
            containerPanel.add(Box.createVerticalStrut(10));
            containerPanel.add(cellPanel);
        }

        containerPanel.revalidate();
        containerPanel.repaint();
    }

    // Effet hover sur les cellules mémoire
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
