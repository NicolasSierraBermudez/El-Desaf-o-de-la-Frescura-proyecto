package com.frescuratotal.erp.common;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO (Data Access Object) contract.
 *
 * <p><b>Design pattern: DAO.</b> This interface abstracts persistence of an
 * entity type {@code T} identified by a key {@code K}. Concrete JDBC
 * implementations hide all SQL details from controllers and views
 * (DIP: higher layers depend on this abstraction, not on JDBC).</p>
 *
 * @param <T> entity type
 * @param <K> primary-key type
 */
public interface GenericDao<T, K> {

    /**
     * Persists a new entity.
     *
     * @param entity entity to insert
     * @return the generated key, or the entity's key
     */
    K create(T entity);

    /**
     * Finds an entity by its primary key.
     *
     * @param id primary key
     * @return an {@link Optional} containing the entity if found
     */
    Optional<T> findById(K id);

    /**
     * Returns all entities.
     *
     * @return list of all entities
     */
    List<T> findAll();

    /**
     * Updates an existing entity.
     *
     * @param entity entity with updated values
     * @return {@code true} if a row was updated
     */
    boolean update(T entity);

    /**
     * Deletes an entity by its primary key.
     *
     * @param id primary key
     * @return {@code true} if a row was deleted
     */
    boolean delete(K id);
}
