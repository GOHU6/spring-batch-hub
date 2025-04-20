# Spring Boot JDBC Template

## Description
Ce projet **`spring-boot-jdbc-template`** est un exemple simple pour valider la connexion à une base de données en utilisant Spring Boot et JDBC. Il permet de tester la configuration de la base de données et d'exécuter des requêtes SQL.

## Objectifs
- Valider la connexion à une base de données.
- Tester l'exécution de requêtes SQL simples.
- Fournir une base pour intégrer JDBC dans des projets plus complexes.

---

## Prérequis
- **Java 17** ou version supérieure.
- **Maven 3.8+**.
- Une base de données (par défaut, H2 est utilisée).

---

## Configuration

### 1. **Fichier `application.properties`**
Le fichier `src/main/resources/application.properties` contient les configurations de la base de données. Voici un exemple avec H2:

```properties
spring.datasource.url=jdbc:h2:tcp://localhost/~/h2-db
spring.datasource.username=root
spring.datasource.password=****
spring.datasource.driver-class-name=org.h2.Driver