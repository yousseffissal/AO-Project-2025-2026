package cpu;

import java.util.LinkedHashMap;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import main.*;
import ui.*;

public class CPU {

    // Déclaration des composants principaux du CPU
    private RAM ram;               // Mémoire RAM
    private ROM rom;               // Mémoire ROM
    private Registers A, B, X, Y, U, S, PC, DP; // Registres principaux
    private Registers N, Z, V, C, H; // Flags
    private ControlPanel RI;       // Panneau de contrôle
    private BitDisplay binA, binB; // Affichage binaire des registres A et B
    private java.util.Stack<Integer> callStack = new java.util.Stack<>(); // Pile pour les appels
    private ALU alu;               // Unité arithmétique et logique

    // Variables pour le programme en cours
    String[] codeLines;            // Tableau des lignes de code
    boolean isSaved = false;       // Indique si le programme est enregistré
    int currentLineIndex = 0;      // Index de la ligne actuelle
    int currentAddress = 0;        // Adresse actuelle dans la ROM
    LinkedHashMap<String, Integer> labelsMap = new LinkedHashMap<>(); // Labels et leurs positions

    // Constructeur du CPU
    public CPU(RAM ram, ROM rom,
               Registers A, Registers B,
               Registers X, Registers Y,
               Registers U, Registers S,
               Registers PC, Registers DP, ControlPanel RI,
               Registers N, Registers Z,
               Registers V, Registers C, Registers H,
               ALU alu, BitDisplay binA, BitDisplay binB) {

        // Initialisation des composants
        this.ram = ram;
        this.rom = rom;

        this.A = A; this.B = B;
        this.X = X; this.Y = Y;
        this.U = U; this.S = S;
        this.PC = PC; this.DP = DP;
        this.RI = RI;

        this.N = N; this.Z = Z; this.V = V; this.C = C; this.H = H;

        this.alu = alu;
        this.binA = binA;
        this.binB = binB;
    }

    // Initialiser les lignes de code depuis l'éditeur
    public void initializeLines(GUI gui) {
        // Supprimer les commentaires et convertir en majuscules
        codeLines = gui.codeEditor.getText().replaceAll(";.*?\\n", "\n").toUpperCase().split("\\n+");

        // Identifier les labels et les stocker
        for (int i = 0; i < codeLines.length; i++) {
            if (codeLines[i].contains(":")) {
                labelsMap.put(codeLines[i].split(":")[0], i);
            }
        }

        // Supprimer les lignes contenant uniquement les labels
        for (int i = 0; i < codeLines.length; i++) {
            codeLines[i] = codeLines[i].contains(":") ? "" : codeLines[i];
        }
    }

    // Exécuter tout le programme
    public void executeAll(GUI gui) {
        while (currentLineIndex < codeLines.length) {
            executeStep(gui);
        }
    }

    // Exécuter une seule instruction (step by step)
    public void executeStep(GUI gui) {
        if (!isSaved) { // Vérifier si le programme est enregistré
            saveProgram(gui);
            return;
        }

        if (currentLineIndex >= codeLines.length) {
            JOptionPane.showMessageDialog(null, "Exécution terminée !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String line = codeLines[currentLineIndex].trim();
        if (line.isEmpty()) { // Ignorer les lignes vides
            currentLineIndex++;
            return;
        }

        // Séparer l'opcode et l'opérande
        String[] parts = line.split("\\s+");
        String opcode = parts[0];
        String operand = parts.length > 1 ? parts[1] : null;

        // Créer une instruction
        Instruction instr = new Instruction(opcode, operand);

        // Exécuter l'instruction
        executeInstruction(instr);

        // Calculer la taille de l'instruction pour avancer le PC
        int opcodeLength = instr.getOpcodeHex().length() / 2; 
        int operandLength = 0;
        if (operand != null) {
            String rawOperand = Instruction.filterOperand(operand);
            operandLength = rawOperand.length() / 2;
        }
        int step = opcodeLength + operandLength;

        // Mettre à jour les registres et le panneau de contrôle
        PC.valueLabel.setText(Instruction.getAddress(currentAddress+step));
        RI.controlvalueLabel.setText(rom.romMemoryData.getOrDefault(Instruction.getAddress(currentAddress), "00"));
        rom.setCurrent(Instruction.getAddress(currentAddress));

        currentLineIndex++;
        currentAddress += step;
    }

    // Enregistrer le programme dans la ROM
    public void saveProgram(GUI gui) {
        // Vérifier la syntaxe de chaque ligne
        for (int i = 0; i < codeLines.length; i++) {
            String line = codeLines[i].trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            String opcode = parts[0];
            String operand = parts.length > 1 ? parts[1] : null;
            Instruction instr = new Instruction(opcode, operand);
            if (operand != null && !Instruction.isSyntaxCorrect(instr, labelsMap)) {
                JOptionPane.showMessageDialog(null,
                        "Erreur de syntaxe à la ligne " + (i + 1) + "\n" + Instruction.erreurMessag,
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Vérifier que la dernière instruction est END
        int lastInstrIndex = -1;
        for (int i = codeLines.length - 1; i >= 0; i--) {
            if (!codeLines[i].trim().isEmpty()) {
                lastInstrIndex = i;
                break;
            }
        }

        if (lastInstrIndex == -1 || !codeLines[lastInstrIndex].trim().equalsIgnoreCase("END")) {
            JOptionPane.showMessageDialog(
                null,
                "Erreur : le programme doit se terminer par l'instruction END.",
                "Erreur d'exécution",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Mettre à jour l'état du programme
        isSaved = true;
        gui.btnSave.setText("Supprimer");
        gui.btnSave.setActionCommand("CLEAR");
        gui.btnStep.setEnabled(true);
        gui.btnStep.setForeground(Color.WHITE);
        gui.btnRun.setEnabled(true);
        gui.btnRun.setForeground(Color.WHITE);

        // Conversion des instructions en hex pour la ROM
        LinkedHashMap<String, String> romMap = new LinkedHashMap<>();
        int k = 0;

        for (int i = 0; i < codeLines.length; i++) {
            if (codeLines[i].equals("")) continue;
            String line = codeLines[i].trim();
            String[] parts = line.split("\\s+");
            String opcode = parts[0];
            String operand = parts.length > 1 ? parts[1] : null;
            Instruction ins = new Instruction(opcode, operand);
            String opcodeHex = ins.getOpcodeHex().toUpperCase();
            for (int j = 0; j < opcodeHex.length(); j += 2) {
                romMap.put(Instruction.getAddress(k++), opcodeHex.substring(j, j + 2));
            }

            // Gestion des instructions TFR / EXG
            if (ins.detectMode() == Instruction.AddressingMode.registerOnly) {
                String postByte = ins.getRegisterPostByte();
                romMap.put(Instruction.getAddress(k++), postByte);
                continue;
            }

            // Conversion des opérandes
            if (operand != null) {
                String rawOperand = Instruction.filterOperand(ins.operand);
                if (!rawOperand.matches("\\$?[0-9A-Fa-f]+") && !rawOperand.startsWith("#")) continue;

                rawOperand = Instruction.filterOperand(rawOperand);
                for (int p = 0; p < rawOperand.length(); p += 2) {
                    String byteStr = (p + 2 <= rawOperand.length()) ? rawOperand.substring(p, p + 2) : rawOperand.substring(p);
                    romMap.put(Instruction.getAddress(k++), byteStr);
                }

                if (rawOperand.contains(",")) { 
                    romMap.put(Instruction.getAddress(k++), getIndexPostByte(rawOperand));
                    String[] part = rawOperand.split(",");
                    if (!part[0].isEmpty()) {
                        int val = part[0].startsWith("$") ? Integer.parseInt(part[0].substring(1), 16)
                                : Integer.parseInt(part[0]);
                        romMap.put(Instruction.getAddress(k++), String.format("%02X", val & 0xFF));
                    }
                } else if (rawOperand.startsWith("#")) { 
                    String imm = rawOperand.replace("#", "").replace("$", "");
                    if (imm.length() > 2) {
                        romMap.put(Instruction.getAddress(k++), imm.substring(0, 2));
                        romMap.put(Instruction.getAddress(k++), imm.substring(2, 4));
                    } else {
                        romMap.put(Instruction.getAddress(k++), imm);
                    }
                } else if (rawOperand.startsWith("$")) { 
                    String addr = rawOperand.replace("$", "");
                    if (addr.length() > 2) {
                        romMap.put(Instruction.getAddress(k++), addr.substring(0, 2));
                        romMap.put(Instruction.getAddress(k++), addr.substring(2, 4));
                    } else {
                        romMap.put(Instruction.getAddress(k++), addr);
                    }
                }
            }
        }

        rom.updateROM(romMap);
        PC.valueLabel.setText(Instruction.getAddress(currentLineIndex));
    }

    // Fonction pour effacer le programme en cours et réinitialiser le CPU
    public void clearProgram(GUI gui) {
            isSaved = false;
            currentLineIndex = currentAddress = 0;
            gui.btnSave.setText("Enregistrer ✔");
            gui.btnSave.setActionCommand("SAVE");
            gui.btnStep.setEnabled(false);
            gui.btnRun.setEnabled(false);

            // Vider la ROM
            LinkedHashMap<String, String> emptyROM = new LinkedHashMap<>();
            for (int i = 0; i <= 30; i++) {
                emptyROM.put(String.format("FE%02X", i), "");
            }
            rom.updateROM(emptyROM);

            // Vider la RAM
            LinkedHashMap<String, String> emptyRAM = new LinkedHashMap<>();
            for (int i = 0; i <= 30; i++) {
                emptyRAM.put(String.format("%04X", i), "00");
            }
            ram.updateRAM(emptyRAM);
            alu.updateALU("00", "00", "00");

            // Réinitialiser les registres et les flags
            A.valueLabel.setText("00"); B.valueLabel.setText("00");
            U.valueLabel.setText("0000"); S.valueLabel.setText("0000");
            X.valueLabel.setText("0000"); Y.valueLabel.setText("0000");
            DP.valueLabel.setText("00"); RI.controlvalueLabel.setText("00");
            PC.valueLabel.setText("FE00");
            C.valueLabel.setText("0"); N.valueLabel.setText("0");
            Z.valueLabel.setText("1"); H.valueLabel.setText("0");
            V.valueLabel.setText("0"); binA.valueLabel.setText("00000000");
            binB.valueLabel.setText("00000000");
        }

    public static void saveTextToFile(String textContent) {
        // Ouvre une boîte de dialogue pour sélectionner un dossier
        JFileChooser folderPicker = new JFileChooser();
        folderPicker.setDialogTitle("Sélectionnez un dossier"); // Titre de la fenêtre
        folderPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // On ne peut choisir que des dossiers

        int userChoice = folderPicker.showOpenDialog(null); // Affiche la boîte de dialogue
        if (userChoice == JFileChooser.APPROVE_OPTION) { // Si l'utilisateur a choisi un dossier
            File chosenFolder = folderPicker.getSelectedFile();
            String newFileName = JOptionPane.showInputDialog("Entrez le nom du fichier (sans extension):"); // Demande le nom du fichier

            if (newFileName != null && !newFileName.trim().isEmpty()) { // Vérifie que le nom n'est pas vide
                Path newFilePath = Paths.get(chosenFolder.getAbsolutePath(), newFileName + ".asmb"); // Crée le chemin complet du fichier
                try {
                    Files.createDirectories(newFilePath.getParent()); // Crée le dossier si nécessaire
                    Files.createFile(newFilePath); // Crée le fichier
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFilePath.toFile()))) {
                        writer.write(textContent); // Écrit le contenu dans le fichier
                    }
                    JOptionPane.showMessageDialog(null, "Fichier créé avec succès : " + newFilePath); // Confirmation
                } catch (IOException ex) {
                    // Gestion des erreurs d'écriture
                    JOptionPane.showMessageDialog(null,
                            "Erreur lors de la création du fichier : " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Message si le nom du fichier est vide
                JOptionPane.showMessageDialog(null, "Le nom du fichier ne peut pas être vide.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Message si aucun dossier n'a été sélectionné
            JOptionPane.showMessageDialog(null, "Aucun dossier sélectionné.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String loadTextFile() {
        // Ouvre une boîte de dialogue pour sélectionner un fichier à lire
        JFileChooser filePicker = new JFileChooser();
        filePicker.setDialogTitle("Sélectionnez un fichier à lire"); // Titre de la fenêtre

        int userChoice = filePicker.showOpenDialog(null);
        if (userChoice == JFileChooser.APPROVE_OPTION) { // Si l'utilisateur a sélectionné un fichier
            File chosenFile = filePicker.getSelectedFile();
            try (BufferedReader fileReader = new BufferedReader(new FileReader(chosenFile))) {
                // Lit le contenu du fichier ligne par ligne
                StringBuilder fileContent = new StringBuilder();
                String currentLine;
                while ((currentLine = fileReader.readLine()) != null) {
                    fileContent.append(currentLine).append("\n"); // Ajoute chaque ligne au contenu
                }
                return fileContent.toString(); // Retourne le contenu complet
            } catch (IOException ex) {
                // Gestion des erreurs de lecture
                JOptionPane.showMessageDialog(null,
                        "Erreur lors de la lecture du fichier : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Message si aucun fichier n'a été sélectionné
            JOptionPane.showMessageDialog(null,
                    "Aucun fichier sélectionné.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return null; // Retourne null si aucun fichier n'a été lu
    }

    public void createNewFile(GUI gui) { 
        // Sauvegarde le contenu de l'éditeur dans un nouveau fichier
        saveTextToFile(gui.codeEditor.getText()); 
    }

    public void openFile(GUI gui) {
        // Charge le contenu d'un fichier et l'affiche dans l'éditeur
        String content = loadTextFile();
        if (content != null) gui.codeEditor.setText(content); // Si le fichier est lu correctement
    }

    private String getRegValue(String reg) {
        // Retourne la valeur actuelle d'un registre donné
        if (reg == null) return "";
        switch (reg) {
            case "A": return A.valueLabel.getText();
            case "B": return B.valueLabel.getText();
            case "X": return X.valueLabel.getText();
            case "Y": return Y.valueLabel.getText();
            case "U": return U.valueLabel.getText();
            case "S": return S.valueLabel.getText();
            case "DP": return DP.valueLabel.getText();
            default: return "";
        }
    }

    private void setRegValue(String reg, String val) {
        // Met à jour la valeur d'un registre et sa représentation binaire si nécessaire
        int intVal = Integer.parseInt(val, 16); // Convertit la valeur hexadécimale en entier
        String binVal = String.format("%8s", Integer.toBinaryString(intVal & 0xFF)).replace(' ', '0'); // Conversion en binaire sur 8 bits

        switch (reg) {
            case "A":
                A.valueLabel.setText(String.format("%02X", intVal & 0xFF));
                binA.valueLabel.setText(binVal); // Mise à jour affichage binaire
                break;
            case "B":
                B.valueLabel.setText(String.format("%02X", intVal & 0xFF));
                binB.valueLabel.setText(binVal);
                break;
            case "U": U.valueLabel.setText(String.format("%04X", intVal & 0xFFFF)); break;
            case "S": S.valueLabel.setText(String.format("%04X", intVal & 0xFFFF)); break;
            case "X": X.valueLabel.setText(String.format("%04X", intVal & 0xFFFF)); break;
            case "Y": Y.valueLabel.setText(String.format("%04X", intVal & 0xFFFF)); break;
            case "DP": DP.valueLabel.setText(String.format("%02X", intVal & 0xFF)); break;
            default: throw new IllegalArgumentException("Registre inconnu: " + reg); // Gestion erreur registre
        }
    }

    public String offsetshifter(String operand) {
        // Calcule l'adresse effective pour les modes indexés
        operand = operand.trim();
        String[] split = operand.split(",");
        if (split.length != 2) throw new IllegalArgumentException("L'operand doit étre sous forme valeur,register ou ,register");

        int offset = 0;
        String offsetPart = split[0].trim();
        if (offsetPart.isEmpty()) offset = 0;
        else if (offsetPart.startsWith("$")) offset = Integer.parseInt(offsetPart.substring(1), 16);
        else offset = Integer.parseInt(offsetPart);

        String reg = split[1].trim().toUpperCase();
        int regInt = Integer.parseInt(getRegValue(reg), 16);
        int result = (regInt + offset) & 0xFFFF; // Masque pour garder 16 bits

        return String.format("%04X", result); // Retourne l'adresse finale
    }

    private String getIndexPostByte(String operand) {
        // Retourne le code post-byte pour les modes indexés
        operand = operand.toUpperCase().replace(" ", "");
        if (operand.equals(",X")) return "84";
        if (operand.equals(",Y")) return "A4";
        if (operand.equals(",U")) return "C4";
        if (operand.equals(",S")) return "E4";

        throw new IllegalArgumentException("Mode indexé non supporté : " + operand);
    }

    public void executeInstruction(Instruction instr) {
        // Exécute l'instruction passée en paramètre
        String opcode = instr.opcode.toUpperCase();
        switch (opcode) {
            case "LDA": case "LDB": case "LDX": case "LDY": case "LDU": case "LDS": executeLD(instr); break; // Load
            case "STA": case "STB": case "STX": case "STY": case "STU": case "STS": executeST(instr); break; // Store
            case "ADDA": case "ADDB": executeADD(instr); break; // Addition
            case "SUBA": case "SUBB": executeSUB(instr); break; // Soustraction
            case "INCA": case "INCB": executeINC(instr); break; // Incrément
            case "DECA": case "DECB": executeDEC(instr); break; // Décrément
            case "LSLA": case "LSLB": executeLSL(instr); break; // Décalage logique à gauche
            case "LSRA": case "LSRB": executeLSR(instr); break; // Décalage logique à droite
            case "ROLA": case "ROLB": executeROL(instr); break; // Rotation à gauche
            case "RORA": case "RORB": executeROR(instr); break; // Rotation à droite
            case "NOP": executeNOP(instr); break; // No operation
            case "CLRA": case "CLRB": executeCLR(instr); break; // Clear
            case "COMA": case "COMB": executeCOM(instr); break; // Complement
            case "NEGA": case "NEGB": executeNEG(instr); break; // Negation
            case "CMPA": case "CMPB": executeCMP(instr); break; // Compare
            case "EXG": executeEXG(instr); break; // Exchange
            case "TFR": executeTFR(instr); break; // Transfer
            case "SWI": executeSWI(instr); break; // Software interrupt
            case "END": executeEND(instr); break; // End of program
            case "ANDA": case "ANDB": executeAND(instr); break; // AND logique
            case "ORA":  case "ORB":  executeOR(instr);  break; // OR logique
            case "EORA": case "EORB": executeEOR(instr); break; // XOR logique
            case "JMP": executeJMP(instr); break; // Jump
            case "BEQ": executeBEQ(instr); break; // Branch if equal
            case "BNE": executeBNE(instr); break; // Branch if not equal
            case "BMI": executeBMI(instr); break; // Branch if minus
            case "BPL": executeBPL(instr); break; // Branch if plus
            case "BCC": executeBCC(instr); break; // Branch if carry clear
            case "BCS": executeBCS(instr); break; // Branch if carry set
            case "BVC": executeBVC(instr); break; // Branch if overflow clear
            case "BVS": executeBVS(instr); break; // Branch if overflow set
            case "BRA": executeBRA(instr); break; // Branch always
            default: throw new IllegalArgumentException("Instruction non supportée : " + opcode); // Erreur si instruction inconnue
        }
    }

    private void jumpToLabel(String label) {
        // Vérifie que le label existe
        if (!labelsMap.containsKey(label)) 
            throw new IllegalArgumentException("Label non trouvé: " + label);

        // Met à jour l'index de la ligne courante
        currentLineIndex = labelsMap.get(label);
        currentAddress = 0;

        // Calcul de l'adresse actuelle en fonction des instructions précédentes
        for (int i = 0; i < currentLineIndex; i++) {
            String line = codeLines[i].trim();
            if (line.isEmpty()) continue; // Ignore les lignes vides

            String[] parts = line.split("\\s+"); // Sépare opcode et operand
            Instruction instrTmp = new Instruction(parts[0], parts.length > 1 ? parts[1] : null);

            currentAddress += instrTmp.getOpcodeHex().length() / 2; // Taille de l'opcode
            if (parts.length > 1) 
                currentAddress += Instruction.filterOperand(parts[1]).length() / 2; // Taille de l'opérande
        }

        // Met à jour les labels RI et PC
        RI.controlvalueLabel.setText(rom.romMemoryData.get(Instruction.getAddress(currentAddress)));
        PC.valueLabel.setText(Instruction.getAddress(currentAddress));
        rom.setCurrent(Instruction.getAddress(currentAddress));
    }

    public void executeLD(Instruction instr) {
        // Extraire le registre à partir de l'opcode (ex: LDA -> A)
        String reg = instr.opcode.substring(2);
        String mode = instr.detectMode().name(); // Détecte le mode d'adressage
        String value = "00"; // Valeur par défaut

        switch (mode) {
            case "immediat":
                value = instr.operand.replace("#", "").replace("$", ""); // Valeur immédiate
                break;

            case "direct":
                String addrDirect = instr.operand.replace("$", "");
                if (addrDirect.length() < 4) {
                    addrDirect = String.format("%04X", Integer.parseInt(addrDirect, 16));
                }
                value = ram.RamMemoryData.getOrDefault(addrDirect, "00"); // Lecture mémoire directe
                break;

            case "etendu":
                String addrEtendu = instr.operand.replace("$", "");
                if (addrEtendu.length() < 4) {
                    addrEtendu = String.format("%04X", Integer.parseInt(addrEtendu, 16));
                }
                value = ram.RamMemoryData.getOrDefault(addrEtendu, "00"); // Lecture mémoire étendue
                break;

            case "indexe":
                String addrIndexe = offsetshifter(instr.operand);
                value = ram.RamMemoryData.getOrDefault(addrIndexe, "00"); // Lecture mémoire indexée
                break;
        }

        setRegValue(reg, value); // Met à jour le registre

        // Met à jour les flags Z et N
        Z.valueLabel.setText(value.equals("00") ? "1" : "0");
        N.valueLabel.setText((Integer.parseInt(value,16) & 0x80) != 0 ? "1" : "0");
    }

    public void executeST(Instruction instr) {
        String reg = instr.opcode.substring(2);
        String operand = instr.operand;
        String mode = instr.detectMode().name();
        String value = getRegValue(reg);

        String highByte = value.length() > 2 ? value.substring(0, value.length() - 2) : "00"; 
        String lowByte  = value.length() > 2 ? value.substring(value.length() - 2) : value;

        switch (mode) {
            case "direct":
                String directAddr = DP.valueLabel.getText() + operand.substring(1);
                if (value.length() > 2) {
                    ram.RamMemoryData.put(directAddr, highByte);
                    int nextAddr = Integer.parseInt(directAddr, 16) + 1;
                    ram.RamMemoryData.put(String.format("%04X", nextAddr), lowByte);
                } else {
                    ram.RamMemoryData.put(directAddr, lowByte);
                }
                break;

            case "etendu":
                String extAddr = operand.substring(1);
                if (value.length() > 2) {
                    ram.RamMemoryData.put(extAddr, highByte);
                    int nextAddr = Integer.parseInt(extAddr, 16) + 1;
                    ram.RamMemoryData.put(String.format("%04X", nextAddr), lowByte);
                } else {
                    ram.RamMemoryData.put(extAddr, lowByte);
                }
                break;

            case "indexe":
                String indexAddr = offsetshifter(operand.replace("$", ""));
                if (value.length() > 2) {
                    ram.RamMemoryData.put(indexAddr, highByte);
                    int nextAddr = Integer.parseInt(indexAddr, 16) + 1;
                    ram.RamMemoryData.put(String.format("%04X", nextAddr), lowByte);
                } else {
                    ram.RamMemoryData.put(indexAddr, lowByte);
                }
                break;
        }

        ram.updateRAM(ram.RamMemoryData); // Actualise la RAM
    }

    public void executeADD(Instruction instr) {
        String reg = instr.opcode.substring(3);
        String operand = instr.operand;
        String mode = instr.detectMode().name();
        int operandValue = 0;

        switch (mode) {
            case "immediat":
                operandValue = Integer.parseInt(operand.replace("#","").replace("$",""),16);
                break;
            case "direct":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(DP.valueLabel.getText() + operand.replace("$",""), "00"), 16);
                break;
            case "etendu":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(operand.replace("$",""), "00"), 16);
                break;
        }

        int regValue = Integer.parseInt(getRegValue(reg), 16);
        int result = regValue + operandValue;

        alu.updateALU(String.format("%02X", operandValue), String.format("%02X", regValue), String.format("%02X", result & 0xFF));

        // Mise à jour des flags
        C.valueLabel.setText(result > 0xFF ? "1" : "0");
        Z.valueLabel.setText((result & 0xFF) == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
        V.valueLabel.setText(((regValue ^ result) & (operandValue ^ result) & 0x80) != 0 ? "1" : "0");

        setRegValue(reg, String.format("%02X", result & 0xFF));
    }

    public void executeSUB(Instruction instr) {
        String reg = instr.opcode.substring(3);
        String operand = instr.operand;
        String mode = instr.detectMode().name();
        int operandValue = 0;

        switch (mode) {
            case "immediat":
                operandValue = Integer.parseInt(operand.replace("#","").replace("$",""),16);
                break;
            case "direct":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(DP.valueLabel.getText() + operand.replace("$",""), "00"), 16);
                break;
            case "etendu":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(operand.replace("$",""), "00"), 16);
                break;
        }

        int regValue = Integer.parseInt(getRegValue(reg), 16);
        int result = regValue - operandValue;

        alu.updateALU(String.format("%02X", operandValue), String.format("%02X", regValue), String.format("%02X", result & 0xFF));

        // Mise à jour des flags
        C.valueLabel.setText(result < 0 ? "1" : "0"); // Carry / Borrow
        Z.valueLabel.setText((result & 0xFF) == 0 ? "1" : "0"); // Zero
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0"); // Negative
        V.valueLabel.setText(((regValue ^ operandValue) & (regValue ^ result) & 0x80) != 0 ? "1" : "0"); // Overflow

        setRegValue(reg, String.format("%02X", result & 0xFF));
    }

    public void executeCMP(Instruction instr) {
        String reg = instr.opcode.substring(3); // Registre à comparer
        int regVal = Integer.parseInt(getRegValue(reg), 16);
        String operand = instr.operand;
        String mode = instr.detectMode().name();
        int memVal = 0;

        // Déterminer la valeur de l'opérande selon le mode
        switch (mode) {
            case "immediat":
                memVal = Integer.parseInt(operand.replace("#", "").replace("$", ""), 16);
                break;
            case "direct":
                memVal = Integer.parseInt(rom.romMemoryData.getOrDefault(DP.valueLabel.getText() + operand.replace("$", ""), "00"), 16);
                break;
            case "etendu":
                memVal = Integer.parseInt(rom.romMemoryData.getOrDefault(operand.replace("$", ""), "00"), 16);
                break;
        }

        int result = regVal - memVal;

        // Mise à jour des flags
        Z.valueLabel.setText((result & 0xFF) == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
        C.valueLabel.setText(result < 0 ? "1" : "0");
    }

    public void executeINC(Instruction instr) {
        String reg = instr.opcode.substring(3);
        int result = Integer.parseInt(getRegValue(reg), 16) + 1;

        // Flags
        Z.valueLabel.setText((result & 0xFF) == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
        V.valueLabel.setText((Integer.parseInt(getRegValue(reg), 16) == 0x7F) ? "1" : "0");

        setRegValue(reg, String.format("%02X", result & 0xFF));
    }

    public void executeDEC(Instruction instr) {
        String reg = instr.opcode.substring(3);
        int currentValue = Integer.parseInt(getRegValue(reg), 16);
        int result = currentValue - 1;

        // Flags
        Z.valueLabel.setText((result & 0xFF) == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
        V.valueLabel.setText((currentValue == 0x80) ? "1" : "0");

        setRegValue(reg, String.format("%02X", result & 0xFF));
    }

    public void executeLSL(Instruction instr) {
        String reg = instr.opcode.substring(3,4);
        int val = Integer.parseInt(getRegValue(reg), 16);
        int carry = (val & 0x80) >> 7;
        int result = (val << 1) & 0xFF;

        setRegValue(reg, String.format("%02X", result));
        C.valueLabel.setText(String.valueOf(carry));
        Z.valueLabel.setText(result == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
    }

    public void executeLSR(Instruction instr) {
        String reg = instr.opcode.substring(3,4);
        int val = Integer.parseInt(getRegValue(reg), 16);
        int carry = val & 0x01;
        int result = (val >> 1) & 0xFF;

        setRegValue(reg, String.format("%02X", result));
        C.valueLabel.setText(String.valueOf(carry));
        Z.valueLabel.setText(result == 0 ? "1" : "0");
        N.valueLabel.setText("0");
    }

    public void executeROL(Instruction instr) {
        String reg = instr.opcode.substring(3,4);
        int val = Integer.parseInt(getRegValue(reg), 16);
        int carryIn = Integer.parseInt(C.valueLabel.getText());
        int carryOut = (val & 0x80) >> 7;
        int result = ((val << 1) & 0xFF) | carryIn;

        setRegValue(reg, String.format("%02X", result));
        C.valueLabel.setText(String.valueOf(carryOut));
        Z.valueLabel.setText(result == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
    }

    public void executeROR(Instruction instr) {
        String reg = instr.opcode.substring(3,4);
        int val = Integer.parseInt(getRegValue(reg), 16);
        int carryIn = Integer.parseInt(C.valueLabel.getText());
        int carryOut = val & 0x01;
        int result = ((carryIn << 7) | (val >> 1)) & 0xFF;

        setRegValue(reg, String.format("%02X", result));
        C.valueLabel.setText(String.valueOf(carryOut));
        Z.valueLabel.setText(result == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
    }

    public void executeNOP(Instruction instr) { 
        // Instruction NOP : ne fait rien
    }

    public void executeCLR(Instruction instr) { 
        setRegValue(instr.opcode.substring(3), "00"); 
        Z.valueLabel.setText("1");
        N.valueLabel.setText("0");
        V.valueLabel.setText("0");
        C.valueLabel.setText("0");
    }

    public void executeSWI(Instruction instr) { 
        currentLineIndex = codeLines.length; 
        JOptionPane.showMessageDialog(null, "Execution step by step done !");
    }

    public void executeEND(Instruction instr) { 
        currentLineIndex = codeLines.length; 
        JOptionPane.showMessageDialog(null, "Execution step by step done !");
    }

    public void executeCOM(Instruction instr) {
        String reg = instr.opcode.substring(3);
        int val = Integer.parseInt(getRegValue(reg), 16);
        int complementValue = (~val) & 0xFF;
        setRegValue(reg, String.format("%02X", complementValue));
        Z.valueLabel.setText(complementValue == 0 ? "1" : "0");
        N.valueLabel.setText((complementValue & 0x80) != 0 ? "1" : "0");
        C.valueLabel.setText(complementValue > 0xFF ? "1" : "0");
    }

    public void executeNEG(Instruction instr) {
        String reg = instr.opcode.substring(3);
        int val = Integer.parseInt(getRegValue(reg), 16);
        int negVal = (~val + 1) & 0xFF;
        setRegValue(reg, String.format("%02X", negVal));
        C.valueLabel.setText(negVal == 0 ? "0" : "1");
        Z.valueLabel.setText(negVal == 0 ? "1" : "0");
    }

    public void executeTFR(Instruction instr) {
        String[] regs = instr.operand.split(",");
        setRegValue(regs[1].trim(), getRegValue(regs[0].trim()));
    }

    public void executeEXG(Instruction instr) {
        String[] regs = instr.operand.split(",");
        String temp = getRegValue(regs[1].trim());
        setRegValue(regs[1].trim(), getRegValue(regs[0].trim()));
        setRegValue(regs[0].trim(), temp);
    }

    public void executeRTS(Instruction instr) { 
        // Retour de sous-programme
        if (!callStack.isEmpty()) {
            currentAddress = callStack.pop();
            int index = 0;
            int addr = 0xFE00;

            // Calcul de la ligne courante correspondant à l'adresse récupérée
            for (; index < codeLines.length; index++) {
                if (codeLines[index].isEmpty()) continue;

                int step = 1;
                String line = codeLines[index].trim();
                String[] parts = line.split("\\s+");

                if (parts.length > 1) {
                    String op = Instruction.filterOperand(parts[1]);
                    step += op.length() / 2;
                }

                if ((addr + step) > currentAddress) break;
                addr += step;
            }

            currentLineIndex = index;

            // Mise à jour des registres de contrôle
            RI.controlvalueLabel.setText(rom.romMemoryData.get(Instruction.getAddress(currentAddress)));
            PC.valueLabel.setText(Instruction.getAddress(currentAddress));
            rom.setCurrent(Instruction.getAddress(currentAddress));
        } else {
            currentLineIndex = codeLines.length;
        }
    }

    public void executeAND(Instruction instr) {
        String reg = instr.opcode.endsWith("A") ? "A" : "B";
        String mode = instr.detectMode().name();
        int operandValue = 0;

        switch (mode) {
            case "immediat":
                operandValue = Integer.parseInt(instr.operand.replace("#", "").replace("$", ""), 16);
                break;
            case "direct":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(
                        DP.valueLabel.getText() + instr.operand.replace("$", ""), "00"), 16);
                break;
            case "etendu":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(
                        instr.operand.replace("$", ""), "00"), 16);
                break;
            case "indexe":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(
                        offsetshifter(instr.operand), "00"), 16);
                break;
        }

        int regValue = Integer.parseInt(getRegValue(reg), 16);
        int result = regValue & operandValue;

        setRegValue(reg, String.format("%02X", result));

        // Mise à jour des flags
        Z.valueLabel.setText(result == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
        V.valueLabel.setText("0");
    }

    public void executeOR(Instruction instr) {
        String reg = instr.opcode.endsWith("A") ? "A" : "B";
        String mode = instr.detectMode().name();
        int operandValue = 0;

        switch (mode) {
            case "immediat":
                operandValue = Integer.parseInt(instr.operand.replace("#", "").replace("$", ""), 16);
                break;
            case "direct":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(
                        DP.valueLabel.getText() + instr.operand.replace("$", ""), "00"), 16);
                break;
            case "etendu":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(
                        instr.operand.replace("$", ""), "00"), 16);
                break;
            case "indexe":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(
                        offsetshifter(instr.operand), "00"), 16);
                break;
        }

        int regValue = Integer.parseInt(getRegValue(reg), 16);
        int result = regValue | operandValue;

        setRegValue(reg, String.format("%02X", result));

        // Flags
        Z.valueLabel.setText(result == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
        V.valueLabel.setText("0");
    }

    private void executeEOR(Instruction instr) {
        String reg = instr.opcode.endsWith("A") ? "A" : "B";
        String mode = instr.detectMode().name();
        int operandValue = 0;

        switch (mode) {
            case "immediat":
                operandValue = Integer.parseInt(instr.operand.replace("#", "").replace("$", ""), 16);
                break;
            case "direct":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(
                        DP.valueLabel.getText() + instr.operand.replace("$", ""), "00"), 16);
                break;
            case "etendu":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(
                        instr.operand.replace("$", ""), "00"), 16);
                break;
            case "indexe":
                operandValue = Integer.parseInt(ram.RamMemoryData.getOrDefault(
                        offsetshifter(instr.operand), "00"), 16);
                break;
        }

        int regValue = Integer.parseInt(getRegValue(reg), 16);
        int result = regValue ^ operandValue;

        setRegValue(reg, String.format("%02X", result));

        // Flags
        Z.valueLabel.setText(result == 0 ? "1" : "0");
        N.valueLabel.setText((result & 0x80) != 0 ? "1" : "0");
        V.valueLabel.setText("0");
    }

    // Instructions de saut
    public void executeJMP(Instruction instr) {
        jumpToLabel(instr.operand); // Jump inconditionnel
    }

    public void executeBEQ(Instruction instr) {
        if (Z.valueLabel.getText().equals("1")) jumpToLabel(instr.operand); // Branch si zéro
    }

    public void executeBNE(Instruction instr) {
        if (Z.valueLabel.getText().equals("0")) jumpToLabel(instr.operand); // Branch si non zéro
    }

    public void executeBMI(Instruction instr) {
        if (N.valueLabel.getText().equals("1")) jumpToLabel(instr.operand); // Branch si négatif
    }

    public void executeBPL(Instruction instr) {
        if (N.valueLabel.getText().equals("0")) jumpToLabel(instr.operand); // Branch si positif
    }

    public void executeBCC(Instruction instr) {
        if (C.valueLabel.getText().equals("0")) jumpToLabel(instr.operand); // Branch si pas de retenue
    }

    public void executeBCS(Instruction instr) {
        if (C.valueLabel.getText().equals("1")) jumpToLabel(instr.operand); // Branch si retenue
    }

    public void executeBVC(Instruction instr) {
        if (V.valueLabel.getText().equals("0")) jumpToLabel(instr.operand); // Branch si pas de overflow
    }

    public void executeBVS(Instruction instr) {
        if (V.valueLabel.getText().equals("1")) jumpToLabel(instr.operand); // Branch si overflow
    }

    public void executeBRA(Instruction instr) {
        jumpToLabel(instr.operand); // Branch inconditionnel
    }
}
