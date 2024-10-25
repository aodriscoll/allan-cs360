/*
 * 7-2 Project Three
 * File: InventoryDatabase.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */

package org.odriscoll.inventory.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import org.odriscoll.inventory.model.InventoryItem;
import org.odriscoll.inventory.model.User;

/**
 * An abstract class that represents the Room Database for this application.
 * It includes data access objects for the two tables used by the application.
 */
@Database(entities = {User.class, InventoryItem.class}, version = 1, exportSchema = false)
public abstract class InventoryDatabase extends RoomDatabase {

    /**
     * Get the data access object for users.
     */
    public abstract UserDao userDao();

    /**
     * Get the data access object for inventory items.
     */
    public abstract InventoryItemDao inventoryItemDao();
}