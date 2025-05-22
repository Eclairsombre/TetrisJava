# Rapport  –  Tetris

Laracine Thibaut, Alexandre Thouny
## 1. Introduction

Ce projet Tetris vise à implémenter un jeu de Tetris fidèle aux règles officielles tout en intégrant des mécaniques avancées.
Nous vous présenterons ici les principales fonctionnalités et mécaniques de jeu.

---

## 2. Interface Utilisateur

Le projet comporte 5 écrans principaux :
- Écran d’accueil
- Écran de jeu
- Écran de pause
- Écran de sélection de musique
- Écran de fin de jeu
Ceux-ci sont séparé dans des classes distinctes, et sont gérés par un système de gestion d’écran (GeneralScreen)

---

## 3. Modes de jeu

### Mode Solo
- Joueur unique contre la difficulté croissante.
- Gestion dynamique de la vitesse et des niveaux.

### Mode Deux Joueurs
- Affrontement local entre deux joueurs sur un seul écran.
- Gestion indépendante des grilles et des scores.

### Mode IA
- Intégration d’une intelligence artificielle basée sur l’algorithme présenté ici :
  [Tetris AI – The Near Perfect Player](https://codemyroad.wordpress.com/2013/04/14/tetris-ai-the-near-perfect-player/)
- chaque Grille de jeu est autonome et possède ainsi sa propre IA.

---

## 4. Contrôle et Jouabilité

### Système de Pause
- Mise en pause du jeu à tout moment.
- Reprise immédiate sans perte de contexte.

### Sélecteur et Prévisualisation de Musique
- Choix personnalisé de pistes audio.
- Lecture en arrière-plan avec aperçu avant validation.

### Système de Meilleurs Scores
- Sauvegarde automatique dans un fichier local.
- Affichage des records par mode de jeu.

---

## 5. Mécaniques de Jeu Avancées

### Debug Mode
- Visualisation des états internes du jeu.
- Affichage des hitboxes, grilles logiques, heuristiques IA (si activé).
- Mode utile pour le développement ou l’analyse algorithmique.

### T-Spin
- Reconnaissance et validation des T-Spins simples, doubles et triples.
- Intégration dans le système de score officiel.
- Voir [T-Spin](https://four.lol/srs/t-spin)

### Back-to-Back & Système de Score Avancé
- Calcul des scores selon les règles de la version officielle :  
  [Scoring in Tetris](https://playstudios.helpshift.com/hc/fr/16-tetris-mobile/faq/2437-scoring-in-tetris/?contact=1)
- Prise en compte des combos, back-to-back, T-Spins, lignes multiples.

### Gestion de la Difficulté
- Augmentation progressive de la vitesse des pièces selon le niveau.
  La vitesse correspond au nombre de pièces tombé par seconde, et se bloque à 40ms au dela du niveau 25

---

## 6. Informations Affichées en Temps Réel

### Visualisation des 3 Pièces Suivantes
- Aide stratégique à la planification.
- Amélioration du gameplay compétitif.

### Statistiques Affichées
- Score total.
- Nombre de lignes complétées.
- Niveau actuel.

### Système de Hold
- Mémorisation d'une pièce pour utilisation ultérieure.
- Une seule utilisation de hold par tour de pièce.
