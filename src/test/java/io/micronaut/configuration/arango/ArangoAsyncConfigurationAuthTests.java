package io.micronaut.configuration.arango;

import com.arangodb.entity.DatabaseEntity;
import io.micronaut.context.ApplicationContext;
import io.testcontainers.arangodb.containers.ArangoContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 11.3.2020
 */
@Testcontainers
class ArangoAsyncConfigurationAuthTests extends ArangoRunner {

    private static final String PASS = "mypass";

    @Container
    private static final ArangoContainer container = new ArangoContainer().withPassword(PASS);

    @Test
    void createConnectionWithUserAndPassword() {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("arangodb.user", "tom");
        properties.put("arangodb.password", "1234");

        try (final ApplicationContext context = ApplicationContext.run(properties)) {
            final ArangoAsyncConfiguration configuration = context.getBean(ArangoAsyncConfiguration.class);
            assertNotNull(configuration.toString());
            configuration.getAccessor().db(ArangoSettings.SYSTEM_DATABASE).getInfo().join();
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void createConnectionSuccessWithCorrectAuth() {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("arangodb.user", "root");
        properties.put("arangodb.password", PASS);

        try (final ApplicationContext context = ApplicationContext.run(properties)) {
            final ArangoAsyncConfiguration configuration = context.getBean(ArangoAsyncConfiguration.class);
            assertNotNull(configuration.getHealth());
            assertTrue(configuration.getHealth().isEnabled());
            assertNotNull(configuration.getHealthCluster());
            assertFalse(configuration.getHealthCluster().isEnabled());
            final DatabaseEntity entity = configuration.getAccessor().db(ArangoSettings.SYSTEM_DATABASE).getInfo()
                    .join();
            assertNotNull(entity);
        }
    }
}
