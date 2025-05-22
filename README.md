# Tetris

Un jeu de Tetris développé en Java avec interface graphique Swing.

---
## Description

Ce projet est une implémentation du jeu classique Tetris avec des fonctionnalités étendues, notamment un mode deux
joueurs et une intelligence artificielle.
---
## Prérequis

- Java Development Kit (JDK) 21
- Gradle 8.8 ou supérieur
----
## Installation

1. Clonez le dépôt :

   ```bash
    git clone https://github.com/Eclairsombre/TetrisJava.git
    ```
2. Naviguez vers le répertoire du projet:

    ```bash
    cd TetrisJava
    ```
----
## Exécution du jeu

Pour exécuter le jeu, utilisez la commande suivante :

```bash
./gradlew run
```

Pour lancer en débug entre 1 et 4:

```bash
./gradlew run -debug numberOfDebug
```
----
# Pour Jouer

| Action          | Joueur 1 | Joueur 2          |
|-----------------|----------|-------------------|
| Descendre       | S        | L                 |
| Aller à gauche  | Q        | K                 |
| Aller à droite  | D        | M                 |
| Chute rapide    | Z / B    | O / Flèche Bas    |
| Rotation gauche | V / A    | I / Flèche Gauche |
| Rotation droite | N / E    | P / Flèche Droite |
| Maintenir pièce | Espace   | J / Flèche Haut   |
| IA              | T        | Y                 |
| Pause           | Echap    | Echap             |

---

# Contributeur
Ce projet a été developpé par Alexandre Thouny et Thibaut Laracine. 