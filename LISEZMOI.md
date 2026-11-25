# CovoitGo : projet complet (Android MVC + Spring Boot)

L'interface Android suit les **maquettes IHM** (`CovoitGo_maquettes_IHM_fin.pdf`) : thème rouge, cartes arrondies, barre de navigation Rechercher / Publier / Réservations / Profil.

```
CovoitGo/            → projet Android Studio complet (à ouvrir avec File → Open)
CovoitGo-Backend/    → API REST Spring Boot + MySQL (à ouvrir dans IntelliJ IDEA)
LISEZMOI.md          → ce guide
```

## 1. Lancer le backend

**Prérequis :** JDK 17 ou plus, MySQL (XAMPP ou WAMP), IntelliJ IDEA (Community suffit).

1. Démarrez **MySQL** dans XAMPP. La base `covoitgo` est créée automatiquement.
2. Dans IntelliJ : **File → Open**, puis choisissez le dossier `CovoitGo-Backend`. Attendez que Maven télécharge les dépendances.
3. Si votre MySQL a un mot de passe, modifiez `spring.datasource.password` dans `src/main/resources/application.properties`.
4. Lancez `CovoitGoApplication` (bouton ▶).
5. La console affiche : `Compte admin cree : admin@esprit.tn / admin123`.

Pour tester dans un navigateur : http://localhost:8080/api/trajets/estimer?depart=Ezzahra&arrivee=Esprit renvoie le prix conseillé (4,5 DT) et le prix maximum (6 DT).

**Si vous aviez déjà lancé une ancienne version du backend :** vous n'avez rien à supprimer. Changez simplement le nom de la base dans `application.properties` (par exemple `covoitgo2` dans l'URL), et une nouvelle base propre sera créée.

## 2. Ouvrir l'application Android

Le dossier `CovoitGo` est un **projet Android Studio complet**, prêt à ouvrir. Il ne modifie pas votre ancien projet, et vous n'avez rien à supprimer ni à copier.

1. Décompressez le zip, par exemple dans `C:\Users\21650\AndroidStudioProjects\CovoitGo-complet\`.
2. Dans Android Studio : **File → Open**, puis sélectionnez le dossier **`CovoitGo`** (celui qui contient `settings.gradle.kts`).
3. Si l'IDE le demande, cliquez sur **Trust Project**.
4. Attendez la fin de la synchronisation Gradle. Le premier lancement télécharge Gradle et les bibliothèques, ce qui peut prendre quelques minutes.
5. Si le message « Project JDK is not defined » ou une erreur de JDK apparaît : **File → Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK = jbr-21 (JetBrains Runtime)**.
6. Si Android Studio propose de mettre à jour le plugin Android Gradle, vous pouvez accepter.
7. Choisissez un émulateur et lancez ▶.

**Sur un vrai téléphone :** dans `app/src/main/java/com/example/covoitgo/model/api/RetrofitClient.java`, remplacez `10.0.2.2` par l'adresse IP de votre PC (commande `ipconfig`). Le téléphone et le PC doivent être sur le même Wi-Fi.

## 3. Correspondance avec les maquettes

| Maquette | Écran Android | Ce qu'il fait |
|---|---|---|
| 1. Où allez-vous aujourd'hui ? | `RechercheActivity` | Départ, arrivée (Esprit par défaut), bouton d'inversion, date, nombre de passagers, trajets fréquents, bandeau « Vous avez une voiture ? » |
| 2. Vers Esprit | `ResultatsActivity` | Cartes de trajets (heures, villes, prix, conducteur, note, Vérifié Esprit, places), tris « Départ le plus tôt », « Prix le plus bas », « Mieux notés » |
| 3. Détail du trajet | `DetailTrajetActivity` | Horaires, prix « À régler en espèces », conducteur avec ses avis, véhicule, places restantes, « Signaler ce trajet », « Demander une place » |
| 4. Mes réservations | `MesReservationsActivity` | Onglets « À venir » et « Passées », badges Acceptée / En attente / Refusée, téléphone affiché une fois la demande acceptée, « Annuler la réservation », « Chercher un autre trajet » |
| 5. Publier un trajet | `PublierTrajetActivity` | Départ et arrivée inversables, date, heure, places −/+, prix avec « Prix conseillé … · maximum … », trajet récurrent L M M J V S |
| 6. Connexion | `LoginActivity` | E-mail de l'établissement, mot de passe, « Mot de passe oublié ? », « Créer un compte » |

Ces écrans complémentaires reprennent la même charte : `InscriptionActivity`, `ProfilActivity`, `VehiculeActivity`, `MesTrajetsActivity`, `DemandesActivity` et `AdminActivity`.

## 4. Architecture MVC (Android)

| Couche | Package | Rôle |
|---|---|---|
| Modèle | `model`, `model.api` | Entités (Utilisateur, Vehicule, Trajet, Reservation, Signalement), SessionManager, appels Retrofit |
| Vue | `view` + `res/layout` | Activities et layouts XML des maquettes : affichage uniquement |
| Contrôleur | `controller` | Validation des saisies, appel du Modèle, retour à la Vue par `ResultCallback` |

Le backend suit les couches du dossier de conception : `Controller → Service → Repository → MySQL`.

## 5. Cas d'utilisation couverts

| Cas d'utilisation | Écran Android | API |
|---|---|---|
| S'inscrire (e-mail @esprit.tn) | InscriptionActivity | POST /api/auth/inscription |
| S'authentifier | LoginActivity | POST /api/auth/login |
| Gérer profil | ProfilActivity | PUT /api/utilisateurs/{id} |
| Enregistrer son véhicule | VehiculeActivity | POST /api/vehicules/conducteur/{id} |
| Valider un conducteur | AdminActivity | PUT /api/admin/conducteurs/{id}/valider |
| Estimer le prix de la place | PublierTrajetActivity | GET /api/trajets/estimer?depart=&arrivee= |
| Gérer les trajets | PublierTrajetActivity, MesTrajetsActivity | POST /api/trajets, PUT /api/trajets/{id}/annuler |
| Réserver une place | RechercheActivity, ResultatsActivity, DetailTrajetActivity | GET /api/trajets/recherche, POST /api/reservations |
| Gérer les réservations | MesReservationsActivity | PUT /api/reservations/{id}/annuler |
| Traiter une réservation | DemandesActivity | PUT /api/reservations/{id}/accepter, /refuser |
| Évaluer | MesReservationsActivity, DemandesActivity (onglet Passées) | POST /api/evaluations |
| Signaler un comportement | DetailTrajetActivity, MesReservationsActivity, DemandesActivity | POST /api/signalements |
| Gérer les réclamations | AdminActivity | PUT /api/admin/signalements/{id}/traiter |

## 6. Scénario de test complet

1. **Étudiant 1 (futur conducteur)** : « Créer un compte » avec `sami.benali@esprit.tn`. Allez dans **Profil → Mon véhicule** et enregistrez le véhicule (Peugeot 208, grise, 3 places).
2. **Déconnexion**, puis connexion en **admin** (`admin@esprit.tn` / `admin123`) : onglet **Conducteurs → Valider**.
3. **Étudiant 1** : onglet **Publier**. Départ : Ezzahra. Le prix conseillé de 4,500 DT (maximum 6 DT) s'affiche. Choisissez demain à 07:30, puis **Publier le trajet**.
4. **Étudiant 2** : créez un compte `ines.mansour@esprit.tn`. Sur l'accueil, mettez la date à demain, touchez la puce « Ezzahra → Esprit », ouvrez le trajet, puis **Demander une place**.
5. **Étudiant 1** : **Profil → Demandes de réservation reçues → Accepter**.
6. **Étudiant 2** : **Réservations** affiche le badge « Acceptée » et le téléphone du conducteur. Après la date du trajet, l'onglet **Passées** propose **Évaluer** et **Signaler**.
7. **Admin** : onglet **Réclamations → Traiter la réclamation**.

## 7. Règles métier

- **Inscription :** réservée aux adresses `@esprit.tn`. Tout étudiant peut réserver.
- **Devenir conducteur :** l'étudiant enregistre son véhicule (statut « en attente »), puis l'administration valide son compte (« Vérifié Esprit »). C'est seulement après cette validation qu'il peut publier.
- **Prix conseillé :** 0,150 DT par km, avec un minimum de 2 DT, arrondi à 0,500 DT. Le **prix maximum** vaut le prix conseillé × 4/3 (par exemple, 4,500 DT conseillé donne 6 DT maximum). Un prix plus élevé est refusé.
- **Distances :** elles viennent d'une table approximative des lieux fréquents vers Esprit El Ghazala, dans `LieuService` (Ezzahra 30 km, Manouba 18 km, Mégrine 22 km…). Un lieu inconnu compte 20 km. Complétez ou corrigez cette table selon vos besoins.
- **Heure d'arrivée :** elle est estimée à partir de la distance (1,3 min par km, arrondie à 5 min).
- **Trajet récurrent :** un trajet est créé pour chaque jour coché, pendant les 4 semaines suivantes.
- **Places :** elles sont décomptées quand le conducteur **accepte** une demande. Si un passager annule une réservation acceptée, les places sont rendues.
- **Téléphone :** celui du conducteur n'est visible qu'une fois la demande acceptée, comme sur la maquette 3.
- **Mots de passe :** ils sont hachés avec BCrypt.

## 8. Limites (projet académique)

- Il n'y a pas de jeton d'authentification (JWT) : l'application envoie l'identifiant de l'utilisateur connecté.
- « Mot de passe oublié ? » affiche seulement un message invitant à contacter l'administration : aucun e-mail n'est envoyé.
- Les distances sont approximatives : il n'y a pas d'API de cartographie.
