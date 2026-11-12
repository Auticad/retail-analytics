package io.github.auticad.retail;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;

/**
 * Avvia un MySQL usa-e-getta in Docker per i test di integrazione.
 * @ServiceConnection fa puntare il DataSource al container: url, utente e password
 * di application.properties vengono ignorati durante questi test.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    MySQLContainer mysqlContainer() {
        return new MySQLContainer("mysql:9");
    }
}
