# 🎓 Gestion des Mémoires de Master - Backend API

[![Java](https://img.shields.io/badge/Java-17-%23ED8B00.svg?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Security-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?logo=swagger&logoColor=black)](https://swagger.io/)

---

## 📖 Présentation du projet

Ce projet est l'implémentation de l'API REST d'une **plateforme de gestion des mémoires de Master**.  
Il est réalisé dans le cadre du **Master 1 Informatique (ILSI)** à l'**Université Joseph Ki-Zerbo**, sous la direction du Dr Flavien SOMDA.

L'objectif est de centraliser et digitaliser l'ensemble du cycle de vie d'un mémoire :

- **Proposition** des sujets par les enseignants.
- **Candidature** et positionnement des étudiants.
- **Affectation** et suivi des travaux.
- **Validation** de la soutenabilité.
- **Programmation** des soutenances et constitution des jurys.
- **Évaluation** et attribution des notes finales.

---

## 🚀 Technologies utilisées

| Composant            | Technologie(s)                                                                      |
|----------------------|-------------------------------------------------------------------------------------|
| **Backend**          | Java 17, Spring Boot 3, Spring Security, Spring Data JPA, Spring Web                |
| **Base de données**  | PostgreSQL 16                                                                       |
| **Sécurité**         | Authentification JWT (JSON Web Token) avec `jjwt`                                   |
| **Documentation**    | Springdoc OpenAPI (Swagger UI)                                                      |
| **Build**            | Maven                                                                               |
| **Logs**             | Logback (SLF4J) avec rotation et archive                                           |
| **Monitoring**       | Spring Boot Actuator (Health, Metrics, Info)                                       |

---

## 📋 Prérequis

Avant de lancer l'application, assurez-vous d’avoir installé :

- **JDK 17** ou supérieur
- **Maven 3.9+**
- **PostgreSQL 16** (avec une base de données créée)
- **Git** (pour cloner le dépôt)

---

## ⚙️ Installation et configuration

### 1. Cloner le dépôt

```bash
git clone https://github.com/votre-utilisateur/gestion-memoires-backend.git
cd gestion-memoires-backend
```

### 4. Construire et lancer l'application

#### Compilation et exécution des tests

```bash
mvn clean install
```

#### Lancement du serveur

```bash
mvn spring-boot:run
```

Le serveur démarre sur le port **8082** avec le contexte **`/api`** :

- Base URL : `http://localhost:8082/api`

## 🔐 Authentification et sécurité

L'API utilise **JWT** pour sécuriser les échanges.  
Le flux d'authentification est le suivant :
1. L'utilisateur envoie ses identifiants (email / mot de passe) à `/auth/login`.

2. Le serveur valide les identifiants et génère un **token JWT**.

3. L'utilisateur inclut ce token dans l'en-tête `Authorization` de chaque requête :

   ```http
   Authorization: Bearer <votre_token_JWT>
   ```

Par défaut, le token expire après **24 heures** (configurable).

## 📚 Documentation API - Swagger / OpenAPI

La documentation interactive est accessible via **Swagger UI**.

- URL d'accès : [http://localhost:8082/api/swagger-ui/index.html](http://localhost:8082/api/swagger-ui/index.html)

- Fichier JSON OpenAPI : [http://localhost:8082/api/v3/api-docs](http://localhost:8082/api/v3/api-docs)


> 🔒 **Note** : Pour tester les endpoints sécurisés directement depuis Swagger, cliquez sur le bouton **"Authorize"** et entrez votre token JWT préfixé par `Bearer`.

---

## 📂 Structure du projet

```text

src/main/java/com/ujkz/memoire/GestionMemoiresBackend/
├── config/                # Configurations (Security, OpenAPI, CORS)
├── controller/            # Contrôleurs REST (Endpoints)
├── dto/                   # Data Transfer Objects (Requête / Réponse)
├── entity/                # Entités JPA (Modèle de données)
├── repository/            # Interfaces JPA (Accès aux données)
├── service/               # Logique métier (Interfaces + Implémentations)
├── security/              # JWT Filter, UserDetailsService
├── exception/             # Gestion centralisée des erreurs
└── utils/                 # Classes utilitaires
```
---
## 🔍 Monitoring et Logs

L'application expose des endpoints de monitoring via **Spring Boot Actuator** :

- Santé : `http://localhost:8082/api/actuator/health`

- Métriques : `http://localhost:8082/api/actuator/metrics`

- Informations : `http://localhost:8082/api/actuator/info`


Les logs sont écrits dans le fichier `logs/gestion-memoires.log` avec une rotation automatique (taille max 10MB).

---

## 🌍 CORS (Cross-Origin Resource Sharing)

L'API autorise les origines suivantes (définies dans `application.yaml`) :

- `http://localhost:3000` (React / Next.js)

- `http://localhost:4200` (Angular)

- `http://localhost:8081` (autres backends)

## 👥 Auteurs
- Étudiants : Master 1 ILSI (Promotion 2025-2026)

- Encadrant pédagogique : Dr Flavien SOMDA

- Établissement : Université Joseph Ki-Zerbo
    