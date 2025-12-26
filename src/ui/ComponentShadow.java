package ui;

import javax.swing.border.Border;
import java.awt.*;

public class ComponentShadow implements Border {

    // Retourne les marges (insets) du bord
    @Override
    public Insets getBorderInsets(Component composant) {
        return new Insets(10, 10, 10, 10); // Marge de 10 pixels sur chaque côté
    }

    // Indique si le bord est opaque ou non
    @Override
    public boolean isBorderOpaque() {
        return false; // Bord non opaque (translucide)
    }

    // Dessine le bord avec un effet d'ombre
    @Override
    public void paintBorder(
            Component composant,
            Graphics g,
            int x,
            int y,
            int largeur,
            int hauteur
    ) {

        Graphics2D g2d = (Graphics2D) g;

        // Activation de l'anticrénelage pour des coins arrondis plus lisses
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Couleur noire semi-transparente pour l'ombre
        g2d.setColor(new Color(0, 0, 0, 80));

        // Dessine un rectangle arrondi avec un léger décalage pour créer l'effet d'ombre
        g2d.fillRoundRect(
                x + 5,       // Décalage X
                y + 5,       // Décalage Y
                largeur - 10, // Largeur réduite pour l'ombre
                hauteur - 10, // Hauteur réduite pour l'ombre
                10,          // Arc horizontal pour arrondir les coins
                10           // Arc vertical pour arrondir les coins
        );
    }
}
