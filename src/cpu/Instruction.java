package cpu;

import java.util.Map;
import java.util.HashMap;

public class Instruction {
    public String opcode;          // Nom de l'instruction (ex: LDA, ADD)
    public String operand;         // Opérande de l'instruction (ex: #12, $2000, A,B)
    public String modeAdr;         // Mode d'adressage (non utilisé directement ici)
    public static String erreurMessag; // Message d'erreur global pour validation

    // Constructeur
    public Instruction(String opcode, String operand) {
        this.opcode = opcode;
        this.operand = operand;
    }

    // Convertit un registre en code hexadécimal pour le post-byte
    public static String getRegisterCode(String reg) {
        switch (reg) {
            case "D": return "0";
            case "X": return "1";
            case "Y": return "2";
            case "U": return "3";
            case "S": return "4";
            case "PC": return "5";
            case "A": return "8";
            case "B": return "9";
            case "CC": return "A";
            case "DP": return "B";
            default: throw new IllegalArgumentException("Registre invalide : " + reg);
        }
    }

    // Obtenir le post-byte pour les instructions TFR/EXG (ex: A,B → 89)
    public String getRegisterPostByte() {
        String[] regs = operand.replace(" ", "").split(",");
        return getRegisterCode(regs[0]) + getRegisterCode(regs[1]);
    }

    // Enum des modes d'adressage
    public enum AddressingMode {
        inherent,     // Instruction sans opérande (ex: NOP)
        immediat,     // Valeur immédiate (#xx)
        direct,       // Adresse directe (1 ou 2 octets)
        etendu,       // Adresse étendue (2 octets)
        indexe,       // Adressage indexé (ex: $10,X)
        relative,     // Saut relatif (ex: BRA, BEQ)
        registerOnly  // Instructions TFR/EXG
    }

    // Détecte le mode d'adressage d'une instruction
    public AddressingMode detectMode() {
        if (opcode.equalsIgnoreCase("TFR") || opcode.equalsIgnoreCase("EXG")) return AddressingMode.registerOnly;
        if (operand == null || operand.isBlank()) return AddressingMode.inherent;
        String op = operand.toUpperCase().replace(" ", "");
        if (op.startsWith("#")) return AddressingMode.immediat;
        if (op.contains(",")) return AddressingMode.indexe;
        if (op.startsWith("$")) return (op.length() <= 3) ? AddressingMode.direct : AddressingMode.etendu;
        return AddressingMode.relative;
    }

    // Table des opcodes avec code hex pour chaque mode
    private static final Map<String, Map<AddressingMode, String>> OPCODES = new HashMap<>();

    static {
        // Instructions de chargement
        OPCODES.put("LDA", Map.of(AddressingMode.immediat, "86", AddressingMode.direct, "96", AddressingMode.etendu, "B6", AddressingMode.indexe, "A6"));
        OPCODES.put("LDB", Map.of(AddressingMode.immediat, "C6", AddressingMode.direct, "D6", AddressingMode.etendu, "F6", AddressingMode.indexe, "E6"));
        OPCODES.put("LDX", Map.of(AddressingMode.immediat, "8E", AddressingMode.direct, "9E", AddressingMode.etendu, "BE", AddressingMode.indexe, "AE"));
        OPCODES.put("LDY", Map.of(AddressingMode.immediat, "108E", AddressingMode.direct, "109E", AddressingMode.etendu, "10BE", AddressingMode.indexe, "10AE"));
        OPCODES.put("LDU", Map.of(AddressingMode.immediat, "CE", AddressingMode.direct, "DE", AddressingMode.etendu, "FE", AddressingMode.indexe, "EE"));
        OPCODES.put("LDS", Map.of(AddressingMode.immediat, "10CE", AddressingMode.direct, "10DE", AddressingMode.etendu, "10FE", AddressingMode.indexe, "10EE"));

        // Instructions de stockage
        OPCODES.put("STA", Map.of(AddressingMode.direct, "97", AddressingMode.etendu, "B7", AddressingMode.indexe, "A7"));
        OPCODES.put("STB", Map.of(AddressingMode.direct, "D7", AddressingMode.etendu, "F7", AddressingMode.indexe, "E7"));
        OPCODES.put("STX", Map.of(AddressingMode.direct, "9F", AddressingMode.etendu, "BF", AddressingMode.indexe, "AF"));
        OPCODES.put("STY", Map.of(AddressingMode.direct, "109F", AddressingMode.etendu, "10BF", AddressingMode.indexe, "10AF"));
        OPCODES.put("STU", Map.of(AddressingMode.direct, "??", AddressingMode.etendu, "??", AddressingMode.indexe, "??"));
        OPCODES.put("STS", Map.of(AddressingMode.direct, "??", AddressingMode.etendu, "??", AddressingMode.indexe, "??"));

        // Instructions arithmétiques
        OPCODES.put("ADDA", Map.of(AddressingMode.immediat, "8B", AddressingMode.direct, "9B", AddressingMode.etendu, "BB", AddressingMode.indexe, "AB"));
        OPCODES.put("ADDB", Map.of(AddressingMode.immediat, "CB", AddressingMode.direct, "DB", AddressingMode.etendu, "FB", AddressingMode.indexe, "EB"));
        OPCODES.put("SUBA", Map.of(AddressingMode.immediat, "80", AddressingMode.direct, "90", AddressingMode.etendu, "B0", AddressingMode.indexe, "A0"));
        OPCODES.put("SUBB", Map.of(AddressingMode.immediat, "C0", AddressingMode.direct, "D0", AddressingMode.etendu, "F0", AddressingMode.indexe, "E0"));
        OPCODES.put("CMPA", Map.of(AddressingMode.immediat, "81", AddressingMode.direct, "91", AddressingMode.etendu, "B1", AddressingMode.indexe, "A1"));
        OPCODES.put("CMPB", Map.of(AddressingMode.immediat, "C1", AddressingMode.direct, "D1", AddressingMode.etendu, "F1", AddressingMode.indexe, "E1"));

        // Instructions logiques
        OPCODES.put("ANDA", Map.of(AddressingMode.immediat, "84", AddressingMode.direct, "94", AddressingMode.indexe, "A4", AddressingMode.etendu, "B4"));
        OPCODES.put("ANDB", Map.of(AddressingMode.immediat, "C4", AddressingMode.direct, "D4", AddressingMode.indexe, "E4", AddressingMode.etendu, "F4"));
        OPCODES.put("ORA", Map.of(AddressingMode.immediat, "8A", AddressingMode.direct, "9A", AddressingMode.indexe, "AA", AddressingMode.etendu, "BA"));
        OPCODES.put("ORB", Map.of(AddressingMode.immediat, "CA", AddressingMode.direct, "DA", AddressingMode.indexe, "EA", AddressingMode.etendu, "FA"));
        OPCODES.put("EORA", Map.of(AddressingMode.immediat, "88", AddressingMode.direct, "98", AddressingMode.indexe, "A8", AddressingMode.etendu, "B8"));
        OPCODES.put("EORB", Map.of(AddressingMode.immediat, "C8", AddressingMode.direct, "D8", AddressingMode.indexe, "E8", AddressingMode.etendu, "F8"));

        // Instructions de décalage/rotation
        OPCODES.put("LSLA", Map.of(AddressingMode.inherent, "48"));
        OPCODES.put("LSLB", Map.of(AddressingMode.inherent, "58"));
        OPCODES.put("LSRA", Map.of(AddressingMode.inherent, "44"));
        OPCODES.put("LSRB", Map.of(AddressingMode.inherent, "54"));
        OPCODES.put("ROLA", Map.of(AddressingMode.inherent, "49"));
        OPCODES.put("ROLB", Map.of(AddressingMode.inherent, "59"));
        OPCODES.put("RORA", Map.of(AddressingMode.inherent, "46"));
        OPCODES.put("RORB", Map.of(AddressingMode.inherent, "56"));

        // Instructions de manipulation de registre
        OPCODES.put("CLRA", Map.of(AddressingMode.inherent, "4F"));
        OPCODES.put("CLRB", Map.of(AddressingMode.inherent, "5F"));
        OPCODES.put("INCA", Map.of(AddressingMode.inherent, "4C"));
        OPCODES.put("INCB", Map.of(AddressingMode.inherent, "5C"));
        OPCODES.put("DECA", Map.of(AddressingMode.inherent, "4A"));
        OPCODES.put("DECB", Map.of(AddressingMode.inherent, "5A"));
        OPCODES.put("COMA", Map.of(AddressingMode.inherent, "43"));
        OPCODES.put("COMB", Map.of(AddressingMode.inherent, "53"));
        OPCODES.put("NEGA", Map.of(AddressingMode.inherent, "40"));
        OPCODES.put("NEGB", Map.of(AddressingMode.inherent, "50"));

        // Instructions systèmes et branches
        OPCODES.put("NOP", Map.of(AddressingMode.inherent, "12"));
        OPCODES.put("RTS", Map.of(AddressingMode.inherent, "39"));
        OPCODES.put("BRA", Map.of(AddressingMode.relative, "20"));
        OPCODES.put("BEQ", Map.of(AddressingMode.relative, "27"));
        OPCODES.put("BNE", Map.of(AddressingMode.relative, "26"));
        OPCODES.put("BCC", Map.of(AddressingMode.relative, "24"));
        OPCODES.put("BCS", Map.of(AddressingMode.relative, "25"));
        OPCODES.put("BMI", Map.of(AddressingMode.relative, "2B"));
        OPCODES.put("BPL", Map.of(AddressingMode.relative, "2A"));
        OPCODES.put("BVC", Map.of(AddressingMode.relative, "49"));
        OPCODES.put("BVS", Map.of(AddressingMode.relative, "59"));
        OPCODES.put("TFR", Map.of(AddressingMode.registerOnly, "1F"));
        OPCODES.put("EXG", Map.of(AddressingMode.registerOnly, "1E"));
        OPCODES.put("END", Map.of(AddressingMode.inherent, "3F"));
        OPCODES.put("SWI", Map.of(AddressingMode.inherent, "3F"));
        OPCODES.put("JMP", Map.of(AddressingMode.etendu, "7E", AddressingMode.indexe, "6E", AddressingMode.relative, "20"));
    }

    // Retourne le code hex pour l'instruction actuelle selon le mode détecté
    public String getOpcodeHex() {
        AddressingMode mode = detectMode();
        Map<AddressingMode, String> modes = OPCODES.get(opcode);
        if (modes == null || !modes.containsKey(mode)) throw new IllegalArgumentException("Opcode non supporté : " + opcode + " / " + mode);
        return modes.get(mode);
    }

    // Calcule l'adresse mémoire pour un offset donné
    public static String getAddress(int k) {
        return Integer.toHexString(0xFE00 + k).toUpperCase();
    }

    // Nettoie l'opérande (#, $, [, & etc.)
    public static String filterOperand(String operand) {
        return operand.replaceAll("[#$\\[&]", "");
    }

    // Vérifie la syntaxe de l'instruction
    public static boolean isSyntaxCorrect(Instruction instr, Map<String, Integer> labelsMap) {
        if (instr == null) { erreurMessag = "Erreur : instruction nulle"; return false; }
        if (instr.opcode == null || instr.opcode.isBlank()) { erreurMessag = "Erreur : opcode manquant"; return false; }
        instr.opcode = instr.opcode.toUpperCase();
        if (!OPCODES.containsKey(instr.opcode)) { erreurMessag = "Erreur : opcode non supporté : " + instr.opcode; return false; }
        AddressingMode mode;
        try { mode = instr.detectMode(); } catch (Exception e) { erreurMessag = "Erreur : mode d'adressage invalide"; return false; }
        Map<AddressingMode, String> modes = OPCODES.get(instr.opcode);
        if (!modes.containsKey(mode)) { erreurMessag = "Erreur : mode " + mode + " non valide pour " + instr.opcode; return false; }

        if (mode != AddressingMode.inherent) {
            if (instr.operand == null || instr.operand.isBlank()) { erreurMessag = "Erreur : opérande manquant pour " + instr.opcode; return false; }
            if (mode == AddressingMode.immediat && !instr.operand.startsWith("#")) { erreurMessag = "Erreur : opérande immédiat attendu (#)"; return false; }
            if (mode == AddressingMode.indexe && !instr.operand.contains(",")) { erreurMessag = "Erreur : opérande indexé invalide"; return false; }
            if (mode == AddressingMode.registerOnly && !instr.operand.matches("[A-Z],[A-Z]")) { erreurMessag = "Erreur : syntaxe EXG/TFR invalide"; return false; }
            if (mode == AddressingMode.relative && labelsMap != null && !labelsMap.containsKey(instr.operand)) { erreurMessag = "Erreur : label introuvable pour " + instr.opcode; return false; }
        }

        try {
            if (instr.operand != null &&
                mode != AddressingMode.relative &&
                mode != AddressingMode.indexe &&
                mode != AddressingMode.registerOnly &&
                mode != AddressingMode.inherent &&
                mode != AddressingMode.etendu) {
                Integer.parseInt(filterOperand(instr.operand), 16);
            }
        } catch (NumberFormatException e) { erreurMessag = "Erreur : opérande non hexadécimal : " + instr.operand; return false; }
        return true;
    }
}
