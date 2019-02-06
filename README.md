# Mini SGBD

>> Projet universitaire (reçu un 20/20)

Ceci est un mini système de gestion de base de données relationnelles codé à 4 personnes et supportant les commandes suivantes:

Création d'une table:
  - create <nom_rel> <nb_colonnes> <type_nième_col>
Insertion de tuples:
  - insert <nom_rel> <val_de_la_nième_col>
  - fill <nom_rel> <nom_fichier> 
Affichage de tuples:  
  - selectall <nom_rel>
  - select <nom_rel> <num_col_filtrée> <valeur_filtre>
  - join <nom_rel_1> <nom_rel_2> <col_rel_1> <col_rel_2>
Remise à 0 de la BDD: 
  - clean
Quitter la BDD:
  - exit

Il est codé en Java, sans API tierce et est compatible MacOS / Windows / Linux
