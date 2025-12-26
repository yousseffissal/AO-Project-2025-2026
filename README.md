## À propos du projet

Ce simulateur a été développé par **Youssef Fissal** et **Youness Irda** dans le cadre du projet du module **Architecture des Ordinateurs**.  
Il permet la simulation pédagogique du microprocesseur **Motorola 6809**.

Projet réalisé à la **Faculté des Sciences et Techniques de Settat (FST Settat)**,  
**Université Hassan Ier**.

---

## Captures d'écran / Illustrations

Voici quelques images illustrant le simulateur en fonctionnement :

### 1. Intro Panel
![Intro Panel](/src/icons/3.png)

### 2. Welcome Panel
![Welcome Panel](/src/icons/1.png)

### 3. Interface Principale
![Interface Principale](/src/icons/2.png)

---

## Code pour Tester le programme

    ; Chargement des constantes
    LDA #$50       ; A = 0x50
    LDB #$60       ; B = 0x60

    ; Stockage en mémoire
    STA $0001      ; mem[1] = A
    STB $0002      ; mem[2] = B

    ; Préparer les index
    LDX #$0002     ; X = 2
    LDY #$0001     ; Y = 1

    ; Charger depuis la mémoire
    LDA ,X         ; A = mem[X] (0x60)
    LDB ,Y         ; B = mem[Y] (0x50)

    ; Calculs
    ADDA #$50      ; A += 0x50
    ADDB #$FF      ; B += 0xFF (-1)
    SUBA #$D0      ; A -= 0x60 + 0x70 combiné

    ; Échanges et transferts
    EXG A,B        ; swap A et B
    TFR X,S        ; X → S

    ; Rotations et décalages
    ROLA           ; A << 1
    RORA           ; A >> 1
    ROLB           ; B << 1
    RORB           ; B >> 1
    LSLA           ; A << 1 logique
    LSLB           ; B << 1 logique
    LSRA           ; A >> 1 logique
    LSRB           ; B >> 1 logique

    ; Comparaison et incrément/décrément
    CMPA #$50      ; comparer A avec 0x0
    INCA           ; A++
    DECB         ; B--
    END

---

## Exécution du programme sur un autre ordinateur

Pour exécuter l’application Moto6809 Emulator sur un autre ordinateur, il est nécessaire que Java soit installé sur la machine cible.

Assurez-vous que Java 21 ou une version plus récente est installée.
Vous pouvez vérifier la version de Java en ouvrant l’invite de commandes (CMD) et en tapant :

    java -version


Si Java n’est pas installé ou si la version est inférieure à 21, téléchargez et installez Java JDK 21 depuis le site officiel d’Oracle ou une distribution compatible (OpenJDK).

Copiez le fichier Moto6809Emulator.exe sur l’ordinateur cible.

Double-cliquez sur le fichier EXE pour lancer l’application.
Aucune configuration supplémentaire n’est nécessaire si Java 21 est correctement installé.

⚠️ Remarque :
L’application ne démarrera pas si Java est absent ou si la version installée est inférieure à la version minimale requise (Java 21)
