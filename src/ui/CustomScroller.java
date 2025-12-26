package ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomScroller extends BasicScrollBarUI {

    // Méthode pour configurer les couleurs de la barre de défilement
    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(255, 229, 0); // Couleur du curseur (thumb)
        trackColor = new Color(0, 0, 0);     // Couleur du fond (track)
    }

    // Création du bouton pour réduire la barre de défilement (invisible ici)
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createInvisibleButton();
    }

    // Création du bouton pour augmenter la barre de défilement (invisible ici)
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createInvisibleButton();
    }

    // Méthode utilitaire pour créer un bouton invisible
    private JButton createInvisibleButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    // Taille minimale du curseur de la scrollbar
    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(8, 30);
    }

    // Taille maximale du curseur de la scrollbar
    @Override
    protected Dimension getMaximumThumbSize() {
        return new Dimension(8, 30);
    }

    // Taille préférée de la scrollbar (ajustée selon l'orientation)
    @Override
    public Dimension getPreferredSize(JComponent c) {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            return new Dimension(8, super.getPreferredSize(c).height); // Vertical
        } else {
            return new Dimension(super.getPreferredSize(c).width, 8); // Horizontal
        }
    }
}
