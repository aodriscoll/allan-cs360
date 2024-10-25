/*
 * 7-2 Project Three
 * File: User.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity object for the user.
 */
@Entity
public class User {
    // The database ID for this user record.
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    // The user's first name.
    @NonNull
    @ColumnInfo(name = "first_name")
    private String mFirstName;

    // The user's last name.
    @NonNull
    @ColumnInfo(name = "last_name")
    private String mLastName;

    // The user's phone number.
    @NonNull
    @ColumnInfo(name = "phone_number")
    private String mPhoneNumber;

    // The user's username.
    @NonNull
    @ColumnInfo(name = "username")
    private String mUsername;

    // The user's password.
    @NonNull
    @ColumnInfo(name = "password")
    private String mPassword;

    /**
     * Return the database ID for this record.
     */
    public long getId() {
        return mId;
    }

    /**
     * Set the database ID for this record.
     */
    public void setId(long id) {
        this.mId = id;
    }

    /**
     * Get the user's first name.
     */
    @NonNull
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Set the user's first name.
     */
    public void setFirstName(@NonNull String firstName) {
        this.mFirstName = firstName;
    }

    /**
     * Get the user's last name.
     */
    @NonNull
    public String getLastName() {
        return mLastName;
    }

    /**
     * Set the user's last name.
     */
    public void setLastName(@NonNull String lastName) {
        this.mLastName = lastName;
    }

    /**
     * Get the user's phone number.
     */
    @NonNull
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    /**
     * Set the user's phone number.
     */
    public void setPhoneNumber(@NonNull String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    /**
     * Get the user's username.
     */
    @NonNull
    public String getUsername() {
        return mUsername;
    }

    /**
     * Set the user's username.
     */
    public void setUsername(@NonNull String username) {
        this.mUsername = username.toLowerCase();
    }

    /**
     * Get the user's password.
     */
    @NonNull
    public String getPassword() {
        return mPassword;
    }

    /**
     * Set the user's password.
     */
    public void setPassword(@NonNull String password) {
        this.mPassword = password;
    }

    /**
     * Create a string representation of this user record.
     */
    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "mId=" + mId +
                ", mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                ", mUsername='" + mUsername + '\'' +
                '}';
    }
}
