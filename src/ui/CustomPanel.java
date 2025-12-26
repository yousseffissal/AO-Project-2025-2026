package ui;

import javax.swing.*;
import java.awt.*;

public class CustomPanel extends JPanel {

    // Image de fond du panneau
    private Image backgroundImage;

    // Constructeur
    public CustomPanel() {
        setOpaque(false); // Permet la transparence du panneau
        setLayout(null);  // Utilisation d'un layout nul pour positionnement absolu

        // Chargement de l'image de fond depuis les ressources
        ImageIcon icon = new ImageIcon(
                getClass().getResource("/icons/Background.png")
        );
        backgroundImage = icon.getImage();
    }

    // Méthode pour peindre le panneau avec l'image de fond
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            // Dessine l'image de fond pour couvrir tout le panneau
            g.drawImage(
                    backgroundImage,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
            );
        } else {
            // Si l'image est introuvable, remplir le panneau avec une couleur grise
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
