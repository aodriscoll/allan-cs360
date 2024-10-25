/*
 * 7-2 Project Three
 * File: InventoryItem.java
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
 * Entity object for and inventory item.
 */
@Entity
public class InventoryItem {
    // Placeholder string should always sort to the end.
    public static String PLACEHOLDER = "\uFFFFPLACEHOLDER";

    // The record ID for the inventory item
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    // The user ID of the owner or zero if the item is shared.
    @ColumnInfo(name = "owner_id")
    private long mOwnerId;

    // The name of the inventory item.
    @NonNull
    @ColumnInfo(name = "product_name")
    private String mProductName;

    // The category that this item belongs to.
    @NonNull
    @ColumnInfo(name = "category")
    private String mCategory;

    // The current count for this inventory item.
    @ColumnInfo(name = "count")
    private long mCount;

    // The card mode for this inventory item. This is used to create placeholder
    // cards for inventory categories.
    @NonNull
    @ColumnInfo(name = "mode")
    private InventoryCardMode mMode;

    /**
     * Constructor for an inventory item.
     */
    public InventoryItem(long ownerId, @NonNull String productName,
                         @NonNull String category, long count) {
        this.mOwnerId = ownerId;
        this.mProductName = productName;
        this.mCategory = category;
        this.mCount = count;
        this.mMode = InventoryCardMode.NORMAL;
    }

    /**
     * Returns the database ID for this inventory item.
     */
    public long getId() {
        return mId;
    }

    /**
     * Set the database ID for this inventory item.
     */
    public void setId(long id) {
        this.mId = id;
    }

    /**
     * Return the user ID for the owner of this item.
     */
    public long getOwnerId() {
        return mOwnerId;
    }

    /**
     * Set the user ID for the owner of this item.
     */
    public void setOwnerId(long mOwnerId) {
        this.mOwnerId = mOwnerId;
    }

    /**
     * Return the product name for this item.
     */
    @NonNull
    public String getProductName() {
        return mProductName;
    }

    /**
     * Set the product name for this item.
     */
    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    /**
     * Return the current count for this item.
     */
    public long getCount() {
        return mCount;
    }

    /**
     * Set the current count for this item.
     */
    public void setCount(long count) {
        if (count >= 0) {
            this.mCount = count;
        } else {
            // The count cannot be less than zero.
            this.mCount = 0;
        }
    }

    /**
     * Increment the count for this item.
     * @return the updated count.
     */
    public long increaseCount() {
        mCount++;
        return mCount;
    }

    /**
     * Decrement the count for this item.
     * Note that the count cannot drop below zero.
     * @return the updated count.
     */
    public long decreaseCount() {
        if (mCount > 0) {
            mCount--;
        }
        return mCount;
    }

    /**
     * Get the category for this item.
     */
    @NonNull
    public String getCategory() {
        return mCategory;
    }

    /**
     * Set the category for this item.
     */
    public void setCategory(String category) {
        this.mCategory = category;
    }

    /**
     * Get the card mode for this inventory item.
     */
    @NonNull
    public InventoryCardMode getMode() {
        return mMode;
    }

    /**
     * Set the card mode for this inventory item.
     */
    public void setMode(InventoryCardMode mode) {
        this.mMode = mode;
    }

    /**
     * Create a string representation for this item.
     */
    @Override
    @NonNull
    public String toString() {
        return "InventoryItem{" +
                "mId=" + mId +
                ", mOwnerId=" + mOwnerId +
                ", mProductName='" + mProductName + '\'' +
                ", mCategory='" + mCategory + '\'' +
                ", mCount=" + mCount +
                ", mMode=" + mMode +
                '}';
    }
}
