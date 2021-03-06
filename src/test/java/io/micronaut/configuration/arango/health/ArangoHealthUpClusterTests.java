package io.micronaut.configuration.arango.health;

import io.micronaut.configuration.arango.ArangoRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthResult;
import io.reactivex.Single;
import io.testcontainers.arangodb.cluster.ArangoClusterDefault;
import io.testcontainers.arangodb.containers.ArangoContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Tests when health is UP for mocked ArangoDB cluster
 *
 * @author Anton Kurako (GoodforGod)
 * @since 29.2.2020
 */
@Testcontainers
class ArangoHealthUpClusterTests extends ArangoRunner {

    private static final ArangoClusterDefault CLUSTER_DEFAULT = ArangoClusterDefault.build();

    @Container
    private static final ArangoContainer agent1 = CLUSTER_DEFAULT.getAgent1();
    @Container
    private static final ArangoContainer agent2 = CLUSTER_DEFAULT.getAgent2();
    @Container
    private static final ArangoContainer agent3 = CLUSTER_DEFAULT.getAgent3();
    @Container
    private static final ArangoContainer db1 = CLUSTER_DEFAULT.getDatabase1();
    @Container
    private static final ArangoContainer db2 = CLUSTER_DEFAULT.getDatabase2();
    @Container
    private static final ArangoContainer coordinator1 = CLUSTER_DEFAULT.getCoordinator1();
    @Container
    private static final ArangoContainer coordinator2 = CLUSTER_DEFAULT.getCoordinator2();

    @Test
    void healthSingleUpForSystemDatabase() {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("arangodb.create-database-if-not-exist", true);

        try (final ApplicationContext context = ApplicationContext.run(properties)) {
            final ArangoHealthIndicator healthIndicator = context.getBean(ArangoHealthIndicator.class);

            final HealthResult result = Single.fromPublisher(healthIndicator.getResult())
                    .timeout(10, TimeUnit.SECONDS).blockingGet();
            assertNotNull(result);

            assertEquals(HealthStatus.UP, result.getStatus());
            assertEquals("arangodb", result.getName());
            assertNotNull(result.getDetails());
            assertTrue(result.getDetails() instanceof Map);
        }
    }

    @Test
    void healthSingleUpForCustomDatabase() {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("arangodb.database", "custom");
        properties.put("arangodb.create-database-if-not-exist", true);
        properties.put("arangodb.create-database-timeout-in-millis", 50000);

        try (final ApplicationContext context = ApplicationContext.run(properties)) {
            final ArangoHealthIndicator healthIndicator = context.getBean(ArangoHealthIndicator.class);

            final HealthResult result = Single.fromPublisher(healthIndicator.getResult())
                    .timeout(10, TimeUnit.SECONDS).blockingGet();
            assertNotNull(result);

            assertEquals(HealthStatus.UP, result.getStatus());
            assertEquals("arangodb", result.getName());
            assertNotNull(result.getDetails());
            assertTrue(result.getDetails() instanceof Map);
        }
    }

    @Test
    void healthClusterUpForSystemDatabase() {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("arangodb.health-cluster.enabled", true);

        try (final ApplicationContext context = ApplicationContext.run(properties)) {
            final ArangoClusterHealthIndicator clusterHealthIndicator = context.getBean(ArangoClusterHealthIndicator.class);

            final HealthResult result = Single.fromPublisher(clusterHealthIndicator.getResult())
                    .timeout(10, TimeUnit.SECONDS).blockingGet();
            assertNotNull(result);

            assertEquals(HealthStatus.UP, result.getStatus());
            assertEquals("arangodb (cluster)", result.getName());
            assertNotNull(result.getDetails());
            assertTrue(result.getDetails() instanceof Map);
            assertTrue(((Map) result.getDetails()).get("nodes") instanceof Collection);
            assertEquals(7, ((Collection) ((Map) result.getDetails()).get("nodes")).size());
        }
    }

    @Test
    void healthClusterUpForCustomDatabase() {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("arangodb.database", "custom");
        properties.put("arangodb.create-database-if-not-exist", true);
        properties.put("arangodb.create-database-timeout-in-millis", 50000);
        properties.put("arangodb.health-cluster.enabled", true);

        try (final ApplicationContext context = ApplicationContext.run(properties)) {
            final ArangoClusterHealthIndicator clusterHealthIndicator = context.getBean(ArangoClusterHealthIndicator.class);

            final HealthResult result = Single.fromPublisher(clusterHealthIndicator.getResult())
                    .timeout(10, TimeUnit.SECONDS).blockingGet();
            assertNotNull(result);

            assertEquals(HealthStatus.UP, result.getStatus());
            assertEquals("arangodb (cluster)", result.getName());
            assertNotNull(result.getDetails());
            assertTrue(result.getDetails() instanceof Map);
            assertTrue(((Map) result.getDetails()).get("nodes") instanceof Collection);
            assertEquals(7, ((Collection) ((Map) result.getDetails()).get("nodes")).size());
        }
    }
}
