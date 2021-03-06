package io.micronaut.configuration.arango;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;

/**
 * ArangoDB Sync Accessor {@link ArangoDB} and database name as configured for
 * application.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 15.3.2020
 */
public class ArangoClient implements AutoCloseable {

    /**
     * Configured database name for application
     * {@link ArangoAsyncConfiguration#getDatabase()}.
     */
    private final String database;

    /**
     * ArangoDB accessor {@link ArangoDB}.
     */
    private final ArangoDB arangodb;

    public ArangoClient(ArangoConfiguration configuration) {
        this.database = configuration.getDatabase();
        this.arangodb = configuration.getAccessor();
    }

    public String database() {
        return database;
    }

    /**
     * @return Accessor to specified ArangoDB database.
     */
    public ArangoDatabase db() {
        return arangodb.db(database);
    }

    /**
     * @return Configured ArangoDB accessor {@link ArangoDB}.
     */
    public ArangoDB accessor() {
        return arangodb;
    }

    @Override
    public void close() {
        if (arangodb != null)
            arangodb.shutdown();
    }

    @Override
    public String toString() {
        return "ArangoClient {"
                + ", database='" + database()
                + '}';
    }
}
