package config;

import org.flywaydb.core.Flyway;

public class FlyWayConfig {

    public static void migrate() {

        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/sorveteria",
                        "nick",
                        "nicki12072007"
                )
                .locations("filesystem:src/main/resources/db/migration", "classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
        System.out.println("✅ Flyway executado com sucesso!");
    }
}