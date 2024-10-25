/*
 * 7-2 Project Three
 * File: InventoryItemDao.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import org.odriscoll.inventory.model.InventoryItem;
import java.util.List;

/**
 * A data access object for inventory items.
 */
@Dao
public interface InventoryItemDao {
    /**
     * Get the inventory item for the specified ID value.
     */
    @Query("SELECT * FROM InventoryItem WHERE id = :id")
    LiveData<InventoryItem> getInventoryItem(long id);

    /**
     * Get the inventory items for the user, including those that are shared.
     */
    @Query("SELECT * FROM InventoryItem WHERE owner_id = :ownerId or owner_id = 0 ORDER BY category COLLATE NOCASE, product_name COLLATE NOCASE")
    LiveData<List<InventoryItem>> getInventoryItems(long ownerId);

    /**
     * Get a list of categories that the user has configured.
     */
    @Query("SELECT DISTINCT(category) FROM InventoryItem WHERE owner_id = :ownerId or owner_id = 0 ORDER BY category COLLATE NOCASE")
    List<String> getCategoriesForUser(long ownerId);

    /**
     * Get the count of items for a given user and category.
     */
    @Query("SELECT COUNT(*) FROM InventoryItem where (owner_id = :ownerId or owner_id = 0) and category = :category")
    int getCountForCategory(long ownerId, String category);

    /**
     * Add an inventory item to the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addInventoryItem(InventoryItem item);

    /**
     * Update an inventory item in the database.
     */
    @Update
    void updateInventoryItem(InventoryItem item);

    /**
     * Delete an inventory item from the database.
     */
    @Delete
    void deleteInventoryItem(InventoryItem item);
}
