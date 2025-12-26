package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

import ui.CustomScroller;

// Classe GUI
// Fenêtre principale de l'application de simulation Moto 6809
// Gère l'interface graphique globale (éditeur, boutons, panneaux, intro)
public class GUI extends JFrame {

    // ======================= Composants principaux =======================
    public JTextArea codeEditor;               // Zone d’édition du code assembleur
    public JTextArea notesArea;                // Zone de notes (lecture seule)
    public JButton btnRun, btnStep, btnSave;   // Boutons de contrôle
    public JButton btnNew, btnOpen, btnExit;   // Boutons de gestion de fichiers
    public int introDuration = 11;             // Durée de l’introduction (secondes)

    // Constructeur principal
    // param cpuPanel : panneau contenant les composants du processeur
    // param sharedListener : écouteur d’actions partagé
    public GUI(JPanel cpuPanel, ActionListener sharedListener) {

        // ======================= Configuration de la fenêtre =======================
        setSize(1250, 650);
        setTitle("Moto6809 Emulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true); // Supprime la barre de titre par défaut
        setLocationRelativeTo(null); // Centrer la fenêtre à l'écran

        // Icone de l'application
        ImageIcon appIcon = new ImageIcon(getClass().getResource("/icons/logo.png"));
        setIconImage(appIcon.getImage());

        // ======================= Palette de couleurs =======================
        Color background = new Color(0, 0, 0);
        Color foreground = Color.WHITE;
        Color runColor   = new Color(0, 153, 76);   
        Color stepColor  = new Color(255, 153, 51); 
        Color saveColor  = new Color(0, 102, 204);  
        Color newColor   = new Color(153, 51, 255); 
        Color exitColor  = new Color(255, 51, 51);
        Color openColor  = new Color(102, 102, 102);
        Color buttonHover = new Color(40,40,40);
        Color accent = new Color(72,255,21);

        // ======================= Panneau racine =======================
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(background);

        // ======================= Panneau droit (éditeur de code) =======================
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBackground(background);
        editorPanel.setPreferredSize(new Dimension((int)(getWidth() * 0.3), getHeight()));
        editorPanel.setBorder(new EmptyBorder(20, 20, 20, 0));

        // Zone d'édition du code assembleur
        codeEditor = new JTextArea();
        codeEditor.setBackground(new Color(20, 20, 20));
        codeEditor.setForeground(foreground);
        codeEditor.setFont(new Font("Source Code Pro", Font.PLAIN, 20));
        codeEditor.setCaretColor(Color.RED); // Curseur rouge
        codeEditor.setBorder(BorderFactory.createLineBorder(accent, 2, true));
        codeEditor.setLineWrap(true);

        // Effet de focus : change la bordure lorsqu'on clique dans le JTextArea
        codeEditor.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                codeEditor.setBorder(BorderFactory.createLineBorder(accent, 2, true));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                codeEditor.setBorder(BorderFactory.createLineBorder(accent, 2, true));
            }
        });

        // Scrollpane pour l'éditeur avec scroll personnalisé
        JScrollPane editorScroll = new JScrollPane(codeEditor);
        editorScroll.setBorder(BorderFactory.createEmptyBorder());
        editorScroll.getVerticalScrollBar().setUI(new CustomScroller());

        editorPanel.add(editorScroll, BorderLayout.CENTER);

        // ======================= Zone de notes utilisateur =======================
        notesArea = new JTextArea();
        notesArea.setEditable(false);
        notesArea.setBackground(new Color(10, 10, 10));
        notesArea.setForeground(new Color(180, 180, 180));
        notesArea.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setMargin(new Insets(8, 10, 8, 10));
        notesArea.setText(
            "Ce simulateur a été développé par Youssef Fissal et Youness Irda dans le cadre du projet Module d’Architecture des Ordinateurs pour simuler le Motorola 6809 microprocesseur, Faculté des Sciences et Techniques de Settat (FSTS), Université Hassan Ier\n" +
            "© Tous droits réservés 2025."
        );
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(0, 120));
        notesScroll.setBorder(BorderFactory.createLineBorder(accent, 1, true));
        notesScroll.getVerticalScrollBar().setUI(new CustomScroller());

        editorPanel.add(notesScroll, BorderLayout.SOUTH);

        // ======================= Panneau gauche (CPU + boutons) =======================
        JPanel cpuContainer = new JPanel(new BorderLayout());
        cpuContainer.setBackground(background);
        cpuContainer.setPreferredSize(new Dimension((int)(getWidth() * 0.7), getHeight()));
        cpuContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Barre de boutons de contrôle
        JPanel controlBar = new JPanel(new GridLayout(1, 6, 18, 5));
        controlBar.setBackground(background);
        controlBar.setBorder(new EmptyBorder(0, 0, 10, 0)); 

        // Création des boutons de contrôle avec couleurs, texte et écouteur partagé
        btnRun   = createControlButton("Exécuter ▶️", runColor, foreground, buttonHover, accent, sharedListener);
        btnStep  = createControlButton("Pas à Pas 👣", stepColor, foreground, buttonHover, accent, sharedListener);
        btnSave  = createControlButton("Enregistrer ✔", saveColor, foreground, buttonHover, accent, sharedListener);
        btnNew   = createControlButton("Exporter 📤", newColor, foreground, buttonHover, accent, sharedListener);
        btnOpen  = createControlButton("Importer 📥", openColor, foreground, buttonHover, accent, sharedListener);
        btnExit  = createControlButton("Quitter ❌", exitColor, foreground, buttonHover, accent, sharedListener);

        // Attribution des commandes d'action pour chaque bouton
        btnRun.setActionCommand("RUN");
        btnOpen.setActionCommand("OPEN");
        btnExit.setActionCommand("EXIT");
        btnStep.setActionCommand("STEP");
        btnSave.setActionCommand("SAVE");
        btnNew.setActionCommand("NEW");

        // Par défaut, les boutons d'exécution sont désactivés
        btnRun.setEnabled(false);
        btnStep.setEnabled(false);

        // Ajout des boutons dans la barre de contrôle
        controlBar.add(btnRun);
        controlBar.add(btnStep);
        controlBar.add(btnSave);
        controlBar.add(btnNew);
        controlBar.add(btnOpen);
        controlBar.add(btnExit);

        // Ajout du panneau CPU et de la barre de contrôle dans le conteneur
        cpuContainer.add(cpuPanel, BorderLayout.CENTER);
        cpuContainer.add(controlBar, BorderLayout.NORTH);

       // ======================= Assemblage principal =======================
        // On ajoute le panneau CPU à droite et l'éditeur à gauche
        rootPanel.add(cpuContainer, BorderLayout.EAST);
        rootPanel.add(editorPanel, BorderLayout.CENTER);

        // ======================= Écran d’introduction =======================
        // Premier écran avec image d’introduction
        JPanel introImage = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon img = new ImageIcon(getClass().getResource("/icons/Intro.png"));
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Deuxième écran d’introduction avec aide/texte
        JPanel introHelp = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon img = new ImageIcon(getClass().getResource("/icons/Welcome.jpeg"));
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // ======================= Bouton "Suivant" =======================
        // Bouton pour passer à l'application après l’introduction
        JButton btnNext = new JButton("Suivant") {
            Color base = new Color(0, 0, 0);
            Color hover = new Color(40,40,40);
            Color borderColor = new Color(72, 255, 21);
            Color accent = new Color(72, 255, 21);
            Color text = Color.WHITE;
            int borderThickness = 2;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel model = getModel();

                // Couleur selon l'état du bouton (normal, survol, pressé, désactivé)
                if (!model.isEnabled()) {
                    g2.setColor(base.darker());
                } else if (model.isPressed()) {
                    g2.setColor(accent.darker());
                } else if (model.isRollover()) {
                    g2.setColor(hover);
                } else {
                    g2.setColor(base);
                }

                // Fond du bouton arrondi
                g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                                getHeight(), getHeight());

                // Bordure verte si pas survolé ni pressé
                if (!model.isRollover() && !model.isPressed()) {
                    g2.setColor(borderColor);
                    g2.setStroke(new BasicStroke(borderThickness));
                    g2.drawRoundRect(
                        borderThickness / 2,
                        borderThickness / 2,
                        getWidth() - borderThickness,
                        getHeight() - borderThickness,
                        getHeight(),
                        getHeight()
                    );
                }

                // Couleur du texte selon état
                g2.setColor(
                    model.isPressed() ? Color.BLACK :
                    model.isRollover() ? accent : text
                );

                // Calcul position centrée du texte
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(getText());
                int stringHeight = fm.getAscent();

                g2.drawString(
                    getText(),
                    (getWidth() - stringWidth) / 2,
                    (getHeight() + stringHeight) / 2 - 3
                );

                g2.dispose();
            }
        };

        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnNext.setOpaque(false);
        btnNext.setFocusPainted(false);
        btnNext.setContentAreaFilled(false);
        btnNext.setBorderPainted(false);
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.setRolloverEnabled(true);
        btnNext.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btnNext.repaint(); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e)  { btnNext.repaint(); }
        });

        // ======================= CardLayout =======================
        // Gestion de plusieurs écrans (intro image, aide, application)
        CardLayout layoutSwitcher = new CardLayout();
        JPanel cardContainer = new JPanel(layoutSwitcher);

        // Action du bouton "Suivant" : passer à l'application
        btnNext.addActionListener(e ->
            layoutSwitcher.show(cardContainer, "APP")
        );

        // ======================= Panneau bas =======================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 30, 45, 55));
        bottomPanel.add(btnNext, BorderLayout.EAST);
        introHelp.add(bottomPanel, BorderLayout.SOUTH);

        // ======================= Ajout des écrans =======================
        cardContainer.add(introImage, "INTRO_IMAGE");
        cardContainer.add(introHelp, "INTRO_HELP");
        cardContainer.add(rootPanel, "APP");

        add(cardContainer);
        layoutSwitcher.show(cardContainer, "INTRO_IMAGE");

        // ======================= Timer transition =======================
        // Affiche l'écran d’aide après 6 secondes
        Timer introTimer = new Timer(6000, e ->
                layoutSwitcher.show(cardContainer, "INTRO_HELP")
        );
        introTimer.setRepeats(false);
        introTimer.start();

        // ======================= Raccourcis clavier =======================
        InputMap im = rootPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPanel.getActionMap();

        // Ctrl+S -> Sauvegarder
        im.put(KeyStroke.getKeyStroke("control S"), "SAVE");
        am.put("SAVE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { btnSave.doClick(); }
        });

        // Ctrl+X -> Exécuter
        im.put(KeyStroke.getKeyStroke("control X"), "RUN");
        am.put("RUN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { btnRun.doClick(); }
        });

        // ENTER -> Pas à pas
        im.put(KeyStroke.getKeyStroke("ENTER"), "STEP");
        am.put("STEP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { btnStep.doClick(); }
        });

        // Ctrl+N -> Nouveau
        im.put(KeyStroke.getKeyStroke("control N"), "NEW");
        am.put("NEW", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { btnNew.doClick(); }
        });

        // Ctrl+O -> Ouvrir
        im.put(KeyStroke.getKeyStroke("control O"), "OPEN");
        am.put("OPEN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { btnOpen.doClick(); }
        });

        // ESCAPE -> Quitter
        im.put(KeyStroke.getKeyStroke("ESCAPE"), "EXIT");
        am.put("EXIT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { btnExit.doClick(); }
        });

        // Rendre la fenêtre visible
        setVisible(true);
    }
    // Crée un bouton stylisé avec animation au survol
    private JButton createControlButton(String label, Color base, Color text, Color hover, Color accent, ActionListener listener) {

        // Création d’un bouton avec rendu personnalisé
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Active l’antialiasing pour des contours lisses
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Récupération du modèle du bouton pour connaître son état
                ButtonModel model = getModel();

                // Détermination de la couleur de fond selon l’état
                if (!model.isEnabled()) g2.setColor(base.darker());        // Bouton désactivé
                else if (model.isPressed()) g2.setColor(accent.darker());  // Bouton pressé
                else if (model.isRollover()) g2.setColor(hover);           // Bouton survolé
                else g2.setColor(base);                                     // État normal

                // Dessine un rectangle arrondi avec la couleur choisie
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

                // Définition de la couleur du texte selon l’état
                g2.setColor(
                    model.isPressed() ? Color.BLACK :          // Texte noir si pressé
                    model.isRollover() ? accent : text        // Texte accent si survolé, sinon couleur normale
                );

                // Calcul pour centrer le texte
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(getText());
                int stringHeight = fm.getAscent();

                // Dessine le texte centré horizontalement et verticalement
                g2.drawString(getText(), (getWidth() - stringWidth) / 2,
                            (getHeight() + stringHeight) / 2 - 3);

                g2.dispose(); // Libère les ressources graphiques
            }
        };

        // ================= Paramètres généraux du bouton =================
        btn.setRolloverEnabled(true);          // Active l’effet de survol
        btn.setFocusPainted(false);            // Supprime le contour bleu au focus
        btn.setContentAreaFilled(false);       // Désactive le remplissage par défaut
        btn.setBorderPainted(false);           // Supprime la bordure par défaut
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Curseur en forme de main
        btn.addActionListener(listener);       // Ajoute l’action au clic

        // ================= Repeindre le bouton lors du survol =================
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.repaint(); } // Survol : repeint
            @Override
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.repaint(); } // Sortie du survol : repeint
        });

        return btn; // Retourne le bouton stylisé
    }

}