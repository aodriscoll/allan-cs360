/*
 * 7-2 Project Three
 * File: InventoryListViewModel.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;
import org.odriscoll.inventory.model.InventoryItem;
import org.odriscoll.inventory.repository.InventoryRepository;
import java.util.List;

/**
 * A list view model used to interface with the InventoryItem database methods.
 */
public class InventoryListViewModel {
    private InventoryRepository mInventoryRepository;

    /**
     * Construct the list view model.
     */
    public InventoryListViewModel(Application application) {
        mInventoryRepository = InventoryRepository.getInstance(application.getApplicationContext());
    }

    /**
     * Get a list of live inventory items.
     */
    public LiveData<List<InventoryItem>> getInventoryItems(long ownerId) {
        return mInventoryRepository.getInventoryItems(ownerId);
    }

    /**
     * Add the given inventory item to the database.
     */
    public void addInventoryItem(InventoryItem item) {
        mInventoryRepository.addInventoryItem(item);
    }

    /**
     * Update the given inventory item in the database.
     */
    public void updateInventoryItem(InventoryItem item) {
        mInventoryRepository.updateInventoryItem(item);
    }

    /**
     * Delete the given inventory item from the database.
     */
    public void deleteInventoryItem(InventoryItem item) {
        mInventoryRepository.deleteInventoryItem(item);
    }
}

