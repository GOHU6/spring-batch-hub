package com.example.spring_boot_jdbc_template;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootJdbcTemplateApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootJdbcTemplateApplication.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJdbcTemplateApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Charger le driver JDBC
        try {
            Class.forName("org.h2.Driver");
            logger.info("Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            logger.error("Driver loading failed", e);
            return;
        }

        // Connexion à la base de données et exécution de la requête
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Afficher les métadonnées de la base de données
            DatabaseMetaData metaData = connection.getMetaData();
            logger.info("Connected to {}", metaData.getURL());
            logger.info("Driver: {}", metaData.getDriverName());
            logger.info("Version: {}", metaData.getDriverVersion());

            // Exemple de requête SQL
            String sqlQuery = "SELECT * FROM TEST";
            logger.info("Query to be executed: {}", sqlQuery);

            // Vérifier si la requête SQL est vide
            if (sqlQuery == null || sqlQuery.isBlank()) {
                logger.warn("SQL query is empty. Exiting...");
                return;
            }

            // Exécuter la requête et traiter les résultats
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlQuery)) {

                int rowIndex = 0;
                while (resultSet.next()) {
                    String column1 = resultSet.getString(1);
                    String column2 = resultSet.getString(2);

                    logger.info("- Row {}", rowIndex);
                    logger.info("Column 1: {}", column1);
                    logger.info("Column 2: {}", column2);

                    rowIndex++;
                }
            }
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
        }
    }
}
