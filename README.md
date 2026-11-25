# Gestion de produits — Spring MVC, Thymeleaf et Spring Security

Application web de gestion de produits réalisée en suivant la vidéo du **Pr. Mohamed Youssfi**
(Spring MVC, Thymeleaf, Spring Data JPA et Spring Security).

Elle permet de lister, rechercher, ajouter, modifier et supprimer des produits, avec une
authentification par formulaire et des droits différents selon le rôle (`USER` ou `ADMIN`).

## Technologies

| Couche | Outil |
|---|---|
| Framework | Spring Boot 3.5.16 (Java 21, Maven) |
| Web | Spring MVC, Thymeleaf, Thymeleaf Layout Dialect |
| Interface | Bootstrap 5.3.3 (WebJars) |
| Données | Spring Data JPA, Hibernate, base H2 en mémoire |
| Validation | Jakarta Bean Validation (`@NotEmpty`, `@Size`, `@Min`) |
| Sécurité | Spring Security 6, `thymeleaf-extras-springsecurity6` |
| Utilitaires | Lombok |

## Lancer l'application

```bash
./mvnw spring-boot:run
```

Sous Windows : `mvnw.cmd spring-boot:run`, ou lancer `MvcApplication` depuis IntelliJ.

Puis ouvrir **http://localhost:8094**. Trois produits (Computer, Printer, Smart Phone) sont
insérés au démarrage par un `CommandLineRunner`.

La console H2 est disponible sur http://localhost:8094/h2-console
(JDBC URL : `jdbc:h2:mem:products-db`, utilisateur `sa`, mot de passe vide).

## Comptes de test

| Utilisateur | Mot de passe | Rôles | Droits |
|---|---|---|---|
| `user1` | `1234` | USER | consulter et rechercher |
| `user2` | `1234` | USER | consulter et rechercher |
| `admin` | `1234` | USER, ADMIN | tout, plus ajouter, modifier et supprimer |

Les utilisateurs sont définis en mémoire (`InMemoryUserDetailsManager`) et leurs mots de passe
sont hachés avec BCrypt.

## Routes

| Méthode | URL | Accès | Rôle |
|---|---|---|---|
| GET | `/` | authentifié | redirige vers `/user/index` |
| GET | `/user/index?keyword=` | USER | liste des produits, filtrée par nom |
| GET | `/admin/newProduct` | ADMIN | formulaire d'ajout |
| GET | `/admin/editProduct?id=` | ADMIN | formulaire d'édition pré-rempli |
| POST | `/admin/saveProduct` | ADMIN | ajout ou mise à jour (selon le champ `id`) |
| POST | `/admin/delete?id=` | ADMIN | suppression |
| GET | `/login` | public | page de connexion personnalisée |
| POST | `/logout` | authentifié | déconnexion, retour à `/login?logout` |
| * | `/notAuthorized` | authentifié | page affichée quand l'accès est refusé |

## Structure du projet

```
src/main/java/net/madani/mvc
├── MvcApplication.java          point d'entrée + données de démarrage
├── entities/Product.java        entité JPA avec contraintes de validation
├── repository/ProductRepository.java   JpaRepository + findByNameContains
├── sec/SecurityConfig.java      utilisateurs, règles d'accès, login, logout
└── web/ProductController.java   contrôleur MVC

src/main/resources
├── application.properties       H2, JPA, port 8094
└── templates
    ├── layout1.html             page modèle : navbar Bootstrap, utilisateur connecté, logout
    ├── products.html            liste, recherche, boutons Edit et Delete (admin)
    ├── new-product.html         formulaire d'ajout et d'édition avec messages d'erreur
    ├── login.html               page de connexion
    └── notAuthorized.html       accès refusé
```

## Étapes de réalisation

1. **Initialisation du projet** : Spring Boot 3.5.16 avec Web, Data JPA, Thymeleaf, Security,
   Validation, H2, MySQL et Lombok.
2. **Entité et repository** : entité `Product` (`id`, `name`, `price`, `quantity`) et
   `ProductRepository`, avec trois produits insérés au démarrage.
3. **Sécurité désactivée temporairement** : `exclude = SecurityAutoConfiguration.class` pour
   développer les vues sans authentification.
4. **Contrôleur et vues** : liste et suppression des produits, page modèle avec
   Thymeleaf Layout Dialect et Bootstrap, formulaire d'ajout validé par `@Valid` et `BindingResult`.
5. **Spring Security** :
   - utilisateurs en mémoire avec BCrypt et règles d'accès sur `/user/**` et `/admin/**` ;
   - `@PreAuthorize` sur les méthodes du contrôleur (`@EnableMethodSecurity`) ;
   - affichage conditionnel dans les vues avec `sec:authorize` et `sec:authentication` ;
   - page de connexion personnalisée, déconnexion et page « Not Authorized ».
6. **Recherche et édition** :
   - recherche par nom avec `findByNameContains` ;
   - édition grâce à un champ `id` caché : `save()` met à jour le produit au lieu d'en créer un nouveau.

## Choix techniques et écarts par rapport à la vidéo

- **Spring Boot 3.5.x** au lieu de la version 4 proposée par Spring Initializr, pour rester
  proche de la vidéo (Spring Security 6, `thymeleaf-extras-springsecurity6`).
- **Suppression en POST** : avec la protection CSRF activée, la suppression passe par un
  formulaire POST (Thymeleaf ajoute le jeton CSRF automatiquement) plutôt que par un lien GET.
- **Déconnexion** : on utilise la déconnexion intégrée de Spring Security (`POST /logout`)
  plutôt qu'un contrôleur qui appelle `session.invalidate()`.
- **`/notAuthorized`** accepte toutes les méthodes HTTP : sans cela, un POST refusé (par exemple
  une suppression tentée par `user1`) renverrait une erreur 405 au lieu de la page d'accès refusé.
- **Console H2** : laissée accessible avec la sécurité activée (accès public, CSRF ignoré et
  frames autorisées uniquement pour `/h2-console/**`). À retirer en production.
