/*
 * 7-2 Project Three
 * File: InventoryRepository.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import org.odriscoll.inventory.model.InventoryCardMode;
import org.odriscoll.inventory.model.InventoryItem;
import org.odriscoll.inventory.model.User;
import java.util.List;

/**
 * A repository singleton object used to interface with the
 * backend database.
 */
public class InventoryRepository {

    // A static reference to the one and only instance of this object.
    private static InventoryRepository mInstance;

    // a reference to the user data access object.
    private final UserDao mUserDao;

    // a reference to the inventory item data access object.
    private final InventoryItemDao mInventoryItemDao;

    /**
     * Get a reference to the instance of singleton object.
     */
    public static InventoryRepository getInstance(Context context) {
        if (mInstance == null) {
            // Create the singleton if this is the first call.
            mInstance = new InventoryRepository(context);
        }

        // return the instance.
        return mInstance;
    }

    /**
     * Constructor used to create the inventory repository given the application context.
     */
    private InventoryRepository(Context context) {
        // initialize the database
        InventoryDatabase database =
                Room.databaseBuilder(context, InventoryDatabase.class, "inventory.db")
                .allowMainThreadQueries()
                .build();

        // Get references to the data access objects.
        mUserDao = database.userDao();
        mInventoryItemDao = database.inventoryItemDao();
    }

    /**
     * Add a set of pre-defined categories to help the user get started.
     * These can be deleted if the user doesn't want them. However we
     * want to give them something to start with so that they are not
     * presented with a blank screen.
     */
    public void addPrePackagedInventoryItems(long ownerId) {
        // A list of predefined categories.
        String[] categories = {
            "Pantry Items",
            "Household Supplies",
            "Office Supplies",
            "Hobby Items"
        };

        // Add a placeholder for each of the items in the list.
        for (String category : categories) {
            InventoryItem item;
            item = new InventoryItem(ownerId, InventoryItem.PLACEHOLDER, category, 0);
            item.setMode(InventoryCardMode.ADD);
            addInventoryItem(item);
        }
    }

    /**
     * Add a user to the database. Sets the user's database ID so that
     * it is available to the caller.
     */
    public void addUser(User user) {
        long userId = mUserDao.addUser(user);
        user.setId(userId);
    }

    /**
     * Get a user from the database given the user ID.
     */
    public User getUser(long userId) {
        return mUserDao.getUser(userId);
    }

    /**
     * Get a user from the database given the username.
     */
    public User getUser(String username) {
        return mUserDao.getUser(username.toLowerCase());
    }

    /**
     * Get a list of all users in the database.
     */
    public List<User> getUsers() {
        return mUserDao.getUsers();
    }

    /**
     * Delete the given user from the database.
     */
    public void deleteUser(User user) {
        mUserDao.deleteUser(user);
    }

    /**
     * Add an inventory item to the database. Sets the items database ID
     * so that it is available to the caller.
     */
    public void addInventoryItem(InventoryItem item) {
        long itemId = mInventoryItemDao.addInventoryItem(item);
        item.setId(itemId);
    }

    /**
     * Update the given inventory item.
     */
    public void updateInventoryItem(InventoryItem item) {
        mInventoryItemDao.updateInventoryItem(item);
    }

    /**
     * Delete the given inventory item.
     */
    public void deleteInventoryItem(InventoryItem item) {
        mInventoryItemDao.deleteInventoryItem(item);
    }

    /**
     * Get a list of all inventory items owned by or shared with the given user.
     */
    public LiveData<List<InventoryItem>> getInventoryItems(long ownerId) {
        return mInventoryItemDao.getInventoryItems(ownerId);
    }

    /**
     * Get a list of categories defined by the user.
     *
     */
    public List<String> getCategoriesForUser(long ownerId) {
        return mInventoryItemDao.getCategoriesForUser(ownerId);
    }

    /**
     * Get the item count for the given user and category. This can be
     * used to see how many inventory items are associate with the category.
     */
    public int getCountForCategory(long ownerId, String category) {
        return mInventoryItemDao.getCountForCategory(ownerId, category);
    }
}
