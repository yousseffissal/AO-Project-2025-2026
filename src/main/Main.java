package main;

import javax.swing.*;
import ui.*;
import cpu.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

public class Main {

    public static void main(String[] args) {

        // Création du panneau principal personnalisé
        CustomPanel customPanel = new CustomPanel();
        customPanel.setLayout(null);
        Color color = new Color(255, 229, 0); // Couleur principale pour les composants

        // Initialisation des données de la ROM
        LinkedHashMap<String, String> romData = new LinkedHashMap<>();
        for (int i = 0; i <= 30; i++) {
            romData.put(String.format("FE%02X", i), ""); // Adresse FE00 à FE1E
        }
        ROM rom = new ROM(170, 250, 235, 315, romData, color); // Création du panneau ROM

        // Initialisation des données de la RAM
        LinkedHashMap<String, String> ramData = new LinkedHashMap<>();
        for (int i = 0; i <= 30; i++) {
            ramData.put(String.format("%04X", i), "00"); // Adresse 0000 à 001E
        }
        RAM ram = new RAM(170, 250, 415, 315, ramData, color); // Création du panneau RAM

        // Création des registres principaux
        Registers registreA  = new Registers("A",  "00", 106, 40, 595, 315, color);
        Registers registreB  = new Registers("B",  "00", 106, 40, 719, 315, color);
        Registers registreX  = new Registers("X", "0000", 230, 40, 595, 365, color);
        Registers registreY  = new Registers("Y", "0000", 230, 40, 595, 417, color);
        Registers registreU  = new Registers("U", "0000", 230, 40, 595, 469, color);
        Registers registreS  = new Registers("S", "0000", 230, 40, 595, 524, color);
        Registers registrePC = new Registers("PC","FE00", 250, 40, 10, 60, color);
        Registers registreDP = new Registers("DP","00", 125, 40, 10, 110, color);

        // Création des flags
        Registers flagN = new Registers("N", "0", 214, 40, 10, 315, color);
        Registers flagZ = new Registers("Z", "1", 214, 40, 10, 365, color);
        Registers flagV = new Registers("V", "0", 214, 40, 10, 417, color);
        Registers flagC = new Registers("C", "0", 214, 40, 10, 469, color);
        Registers flagH = new Registers("H", "0", 214, 40, 10, 524, color);

        // Registre I (interruption/control)
        ControlPanel registreI = new ControlPanel("RI", "00", 250, 40, 10, 10, color);

        // Affichage binaire des registres A et B
        BitDisplay binA = new BitDisplay("00000000", 80, 20, registreA.panelX + 12, registreA.panelY - 20);
        BitDisplay binB = new BitDisplay("00000000", 80, 20, registreB.panelX + 12, registreB.panelY - 20);

        // Création de l'ALU
        ALU alu = new ALU(160, 209, 634, 80, "00", "00", "00", color, color);
        JPanel aluPanel = alu.generateALUPanel();
        customPanel.add(aluPanel);

        // Ajout des panneaux RAM et ROM au panneau principal
        customPanel.add(ram.generateRAMPanel());
        customPanel.add(rom.generateROMPanel());

        // Ajout des panneaux des registres principaux
        customPanel.add(registreA.generateRegisterPanel());
        customPanel.add(registreB.generateRegisterPanel());
        customPanel.add(registreX.generateRegisterPanel());
        customPanel.add(registreY.generateRegisterPanel());
        customPanel.add(registreU.generateRegisterPanel());
        customPanel.add(registreS.generateRegisterPanel());
        customPanel.add(registrePC.generateRegisterPanel());
        customPanel.add(registreDP.generateRegisterPanel());

        // Ajout des flags
        customPanel.add(flagN.generateRegisterPanel());
        customPanel.add(flagZ.generateRegisterPanel());
        customPanel.add(flagV.generateRegisterPanel());
        customPanel.add(flagC.generateRegisterPanel());
        customPanel.add(flagH.generateRegisterPanel());

        // Ajout du registre de contrôle et des affichages binaires
        customPanel.add(registreI.generateControPanel());
        customPanel.add(binA.generateBitPanel());
        customPanel.add(binB.generateBitPanel());

        // Initialisation du CPU avec tous les composants
        CPU cpu = new CPU(
            ram, rom,
            registreA, registreB,
            registreX, registreY,
            registreU, registreS,
            registrePC, registreDP,
            registreI,
            flagN, flagZ, flagV, flagC, flagH,
            alu,
            binA, binB
        );

        // Création de la GUI et liaison des boutons avec les actions CPU
        SwingUtilities.invokeLater(() -> new GUI(customPanel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                GUI gui = (GUI) ((JButton) e.getSource()).getTopLevelAncestor();
                String actionCommand = e.getActionCommand();

                cpu.initializeLines(gui); // Initialisation de la ligne active

                // Déclenchement de l'action en fonction du bouton
                switch (actionCommand) {
                    case "RUN":  cpu.executeAll(gui);     break;
                    case "STEP": cpu.executeStep(gui);    break;
                    case "SAVE": cpu.saveProgram(gui);    break;
                    case "CLEAR":cpu.clearProgram(gui);   break;
                    case "NEW":  cpu.createNewFile(gui);  break;
                    case "OPEN": cpu.openFile(gui);       break;
                    case "EXIT": System.exit(0);   break;
                }
            }
        }));
    }
}
