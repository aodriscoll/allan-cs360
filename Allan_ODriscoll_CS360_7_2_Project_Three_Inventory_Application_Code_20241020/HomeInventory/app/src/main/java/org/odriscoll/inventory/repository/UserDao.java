/*
 * 7-2 Project Three
 * File UserDao.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory.repository;

import androidx.room.*;
import org.odriscoll.inventory.model.User;
import java.util.List;

/**
 * A data access object for the user table.
 */
@Dao
public interface UserDao {
    /**
     * Get the user with the given ID value.
     */
    @Query("SELECT * FROM User WHERE id = :id")
    User getUser(long id);

    /**
     * Get the user with the given username.
     */
    @Query("SELECT * FROM User WHERE username = :username")
    User getUser(String username);

    /**
     * Get a list of all users.
     */
    @Query("SELECT * FROM User ORDER BY username COLLATE NOCASE")
    List<User> getUsers();

    /**
     * Add the given user to the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addUser(User user);

    /**
     * Update the given user in the database.
     */
    @Update
    void updateUser(User user);

    /**
     * Delete the given user from the database.
     */
    @Delete
    void deleteUser(User user);
}
