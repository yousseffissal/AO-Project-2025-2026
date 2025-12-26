package cpu;

import javax.swing.*;
import java.awt.*;

public class ALU {

    // Propriétés du panneau et position
    public int panelWidth;   // largeur du panneau ALU
    public int panelHeight;  // hauteur du panneau ALU
    public int positionX;    // position X du panneau
    public int positionY;    // position Y du panneau

    // Données des opérandes et résultat
    public String operandLeft;   // opérande gauche
    public String operandRight;  // opérande droit
    public String aluResult;     // résultat de l'ALU

    // Composants graphiques
    public JLabel leftOperandLabel;   // affichage de l'opérande gauche
    public JLabel rightOperandLabel;  // affichage de l'opérande droit
    public JPanel resultLabel;        // panneau affichant le résultat
    private JLabel resultValue;       // valeur du résultat affichée

    // Couleurs des éléments
    public Color resultColor;   // couleur du résultat
    public Color operandColor;  // couleur des opérandes

    // Constructeur : initialisation de l'ALU
    public ALU(int width, int height, int x, int y, String left, String right, String result, Color resultColor, Color operandColor) {
        this.panelWidth = width;
        this.panelHeight = height;
        this.positionX = x;
        this.positionY = y;
        this.operandLeft = left;
        this.operandRight = right;
        this.aluResult = result;
        this.resultColor = resultColor;
        this.operandColor = operandColor;
    }

    // Création et configuration du panneau ALU
    public JPanel generateALUPanel() {

        int fontSize = 18; // taille de police de base

        // Création du panneau ALU avec possibilité de dessin personnalisé
        JPanel aluPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // ici on peut ajouter un dessin personnalisé si nécessaire
            }
        };

        // Paramètres du panneau
        aluPanel.setLayout(null); // positionnement manuel
        aluPanel.setOpaque(false); // fond transparent
        aluPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        aluPanel.setBounds(positionX, positionY, panelWidth, panelHeight);

        // Configuration de l'étiquette de l'opérande gauche
        leftOperandLabel = new JLabel(operandLeft);
        leftOperandLabel.setBounds(10, 10, panelWidth / 2 - 20, 30);
        leftOperandLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leftOperandLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        leftOperandLabel.setForeground(operandColor);

        // Configuration de l'étiquette de l'opérande droit
        rightOperandLabel = new JLabel(operandRight);
        rightOperandLabel.setBounds(panelWidth / 2 + 10, 10, panelWidth / 2 - 20, 30);
        rightOperandLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rightOperandLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        rightOperandLabel.setForeground(operandColor);

        int resultSize = 40; // taille du panneau résultat

        // Configuration du panneau de résultat
        resultLabel = new JPanel();
        resultLabel.setBounds((panelWidth - resultSize) / 2, panelHeight - resultSize - 10, resultSize, resultSize);
        resultLabel.setBackground(new Color(6, 26, 83));
        resultLabel.setLayout(new BorderLayout());
        resultLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));

        // Configuration de l'étiquette du résultat
        resultValue = new JLabel(aluResult);
        resultValue.setHorizontalAlignment(SwingConstants.CENTER);
        resultValue.setFont(new Font("Arial", Font.BOLD, fontSize + 6));
        resultValue.setForeground(resultColor);

        resultLabel.add(resultValue, BorderLayout.CENTER);

        // Ajout des composants au panneau ALU
        aluPanel.add(leftOperandLabel);
        aluPanel.add(rightOperandLabel);
        aluPanel.add(resultLabel);

        return aluPanel;
    }

    // Mise à jour des valeurs de l'ALU
    public void updateALU(String left, String right, String result) {
        this.operandLeft = left;
        this.operandRight = right;
        this.aluResult = result;

        if (leftOperandLabel != null)
            leftOperandLabel.setText(left);

        if (rightOperandLabel != null)
            rightOperandLabel.setText(right);

        if (resultValue != null)
            resultValue.setText(result);
    }
}
