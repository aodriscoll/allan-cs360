/*
 * 7-2 Project Three
 * File: AddInventoryItemActivity.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * An activity that allows the user to create a new inventory item in the database.
 */
public class AddInventoryItemActivity extends AppCompatActivity {
    // The logging tag for this class
    private static String TAG = "AddInventoryItemActivity";

    // The new items initial inventory count.
    int mItemCount = 0;

    // A reference to the inventory count TextView component.
    private TextView mCountTextView;

    // A reference to the product name EditText component.
    private EditText mEditItemTitleText;

    // The category for this item. This is passed in from the main Inventory activity.
    private String mCategory;

    // The user ID for this item. This is passed in from the main inventory activity.
    private long mUserId;

    /**
     * Create and initialize this activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get input parameters from the calling activity.
        Intent intent = getIntent();
        mCategory = intent.getStringExtra(InventoryActivity.EXTRA_PRODUCT_CATEGORY);
        mUserId = intent.getLongExtra(LoginActivity.EXTRA_USERID, 0);

        // Initialize the layout.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_inventory_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_inventory_item_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get references to the controls used by this activity.
        mCountTextView = findViewById(R.id.add_inventory_item_count);
        mEditItemTitleText = findViewById(R.id.edit_item_title_text);
        updateItemCount();

    }

    /**
     * Add the new inventory item. Validates that the name not blank and then adds
     * the necessary information to the Intent so that it can be returned to the
     * calling activity.
     */
    public void onAdd(View view) {
        Log.d(TAG, "onAdd() called");

        // Ensure the product name is not empty.
        String productName = mEditItemTitleText.getText().toString();
        if (productName.isBlank()) {
            // Set focus on the product name and display an error message.
            mEditItemTitleText.requestFocus();
            mEditItemTitleText.setError(getResources().getString(R.string.product_name_cannot_be_empty));
            return;
        }

        // Store the information in the return Intent.
        Intent intent = new Intent();
        intent.putExtra(InventoryActivity.EXTRA_PRODUCT_CATEGORY, mCategory);
        intent.putExtra(InventoryActivity.EXTRA_PRODUCT_NAME, productName);
        intent.putExtra(InventoryActivity.EXTRA_PRODUCT_COUNT, mItemCount);
        intent.putExtra(LoginActivity.EXTRA_USERID, mUserId);

        // Log the details for debugging purposes.
        Log.d(TAG, "onAdd: productCategory=" + mCategory);
        Log.d(TAG, "onAdd: productName=" + productName);
        Log.d(TAG, "onAdd: productCount=" + mItemCount);
        Log.d(TAG, "onAdd: userId=" + mUserId);

        // Set the result to OK and finish the activity.
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Cancel the activity. No inventory item will be added.
     */
    public void onCancel(View view) {
        Log.d(TAG, "onCancel() called");
        finish();
    }

    /**
     * Increment the count for the new inventory item and update the view.
     */
    public void onIncreaseCount(View view) {
        Log.d(TAG, "onIncreaseCount() called");
        mItemCount++;
        updateItemCount();
    }

    /**
     * Decrement the count for the new inventory item and update the view.
     * Note that the count cannot drop below zero.
     */
    public void onDecreaseCount(View view) {
        Log.d(TAG, "onDecreaseCount() called");
        if (mItemCount > 0) {
            // The count is currently greater than zero. Decrement the count
            // and then update the view with the reduced number.
            mItemCount--;
            updateItemCount();
        }
    }

    /**
     * Format the item count and update the view.
     */
    private void updateItemCount() {
        mCountTextView.setText(Integer.toString(mItemCount));
    }
}