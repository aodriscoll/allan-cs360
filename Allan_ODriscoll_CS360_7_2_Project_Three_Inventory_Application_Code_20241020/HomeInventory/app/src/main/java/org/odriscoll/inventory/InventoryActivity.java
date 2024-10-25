/*
 * 7-2 Project Three
 * File: InventoryActivity.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import org.odriscoll.inventory.model.InventoryCardMode;
import org.odriscoll.inventory.model.InventoryItem;
import org.odriscoll.inventory.model.User;
import org.odriscoll.inventory.repository.InventoryRepository;
import org.odriscoll.inventory.viewmodel.InventoryListViewModel;
import java.util.List;

/**
 * This is the main activity in the application that lets users create, read, update, and delete
 * inventory items. Inventory items are stored on a per-user basis with the option to create
 * categories that are shared with all users. Inventory items are displayed in cards in a scrollable
 * recycle view / grid layout. A placeholder card should always be present for each category, allowing
 * user to add new items for the category. Plus and Minus buttons are displayed on each card allowing
 * the user to increase or decrease the inventory count. When the count on an inventory item changes
 * from one to zero, the application will attempt to send an SSM alert message using the phone
 * number that was given on user creation. The user can switch to the messaging app (even in the
 * emulator) to view these alerts. The application also makes use of TOAST messages for these and
 * other events. A long press on a given card will invoke an action bar that enables the user to
 * delete the item. Categories can be deleted in the same way as long as they are empty. Toolbar
 * items are presented to allow the user to 1) Test the SSM permissions; 2) Add a new category;
 * and 3) Exit back to the login screen.
 */
public class InventoryActivity extends AppCompatActivity
    implements PermissionRationaleDialog.PermissionRationaleDialogListener,
    CategoryDialogFragment.OnCategoryEnteredListener {

    // The logging tag used for this class.
    private static final String TAG = "InventoryActivity";

    // A constant name for the category parameter passed between activities.
    public static final String EXTRA_PRODUCT_CATEGORY = "org.odriscoll.inventory.product.category";

    // A constant name for the product name parameter passed between activities.
    public static final String EXTRA_PRODUCT_NAME = "org.odriscoll.inventory.product.name";

    // A constant name for the product count parameter passed between activities.
    public static final String EXTRA_PRODUCT_COUNT = "org.odriscoll.inventory.product.count";

    // A reference to the inventory adapter used by the recycler view.
    private InventoryAdapter mInventoryAdapter;

    // A reference to the recycler view used to display the cards.
    private RecyclerView mRecyclerView;

    // A reference the the inventory list.
    private InventoryListViewModel mInventoryListViewModel;

    // A reference to the action mode menu when in delete mode.
    private ActionMode mActionMode = null;

    // The position of the selected item.
    private int mSelectedInventoryItemPosition = RecyclerView.NO_POSITION;

    // The user ID of the logged on user.
    private long mActiveUserId;

    // The users phone number to send alerts.
    private String mUserPhoneNumber;

    // Cached colors used for displaying inventory cards.
    private int[] mCategoryColors;

    // Color for the selected card when in delete mode.
    private int mDeleteColor;

    /**
     * Create and initialize the LoginActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Get the userID from the login screen.
        Intent intent = getIntent();
        mActiveUserId = intent.getLongExtra(LoginActivity.EXTRA_USERID, 0);
        Log.d(TAG, "onCreate() with userid=" + mActiveUserId);

        // Create the inventory list view model
        mInventoryListViewModel = new InventoryListViewModel(getApplication());

        // Get a reference to the recycler view.
        mRecyclerView = findViewById(R.id.inventory_recycler_view);

        // Create the grid layout. Calculate the number of columns that we can fit on the screen.
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int cardWidth = (int)getResources().getDimension(R.dimen.card_width);
        float displayWidth = displayMetrics.widthPixels;
        int spanLength = (int)(displayWidth / cardWidth);
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), spanLength);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Get the card colors and cache them locally.
        mCategoryColors = getResources().getIntArray(R.array.category_colors);
        mDeleteColor = getResources().getColor(R.color.red, this.getTheme());

        // Load the user object from the database and store the phone number for SMS messages.
        InventoryRepository inventoryRepository = InventoryRepository.getInstance(this.getApplicationContext());
        User user = inventoryRepository.getUser(mActiveUserId);
        if (user != null) {
            mUserPhoneNumber = user.getPhoneNumber();
        } else {
            mUserPhoneNumber = "";
        }

        setSupportActionBar(findViewById(R.id.inventory_toolbar));

        // Create an observer that will update the UI when the inventory item list changes.
        mInventoryListViewModel.getInventoryItems(mActiveUserId).observe(this, inventoryItems -> {
            updateUI(inventoryItems);
        });
    }

    /**
     * A helper method used to update the inventory cards on the display.
     */
    private void updateUI(List<InventoryItem> inventoryList) {
        mInventoryAdapter = new InventoryAdapter(inventoryList);
        mRecyclerView.setAdapter(mInventoryAdapter);
    }

    /****************************************************************
     * Methods that initialize a toolbar menu and handle the selected
     * options.
     ****************************************************************/

    /**
     * Inflate the menu for the toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_menu, menu);
        return true;
    }

    /**
     * A callback for menu button selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, String.format("%d", item.getItemId()));

        // Get the selected menu item.
        int selectedItem = item.getItemId();

        if (selectedItem == R.id.logout_button) {
            // Handle the logout option by finishing the activity.
            finish();
            return true;
        } else if (selectedItem == R.id.add_inventory_category_button) {
            // Handle the add inventory category option by displaying the category dialog box.
            CategoryDialogFragment dialog = new CategoryDialogFragment();
            dialog.show(getSupportFragmentManager(), "categoryDialog");
        } else if (selectedItem == R.id.test_sms_button) {
            // Handle the test SMS option by requesting SMS permissions. This will potentially
            // invoke several dialog boxes to complete the permission request.
            if (hasSmsPermissions()) {
                Toast.makeText(getApplicationContext(),
                        "PERMISSION ALREADY GRANTED: SMS messages can be sent.",
                        Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A callback method that is invoked to receive the results from the category dialog box.
     */
    @Override
    public void onCategoryEntered(String categoryText, boolean shared) {
        Log.d(TAG, "onCategoryEntered() called with " + categoryText);

        // Get a list of existing categories from the database.
        InventoryRepository inventoryRepository = InventoryRepository.getInstance(
                this.getApplicationContext());
        List<String> categories = inventoryRepository.getCategoriesForUser(mActiveUserId);

        // Check if the category already exists.
        for (String category : categories) {
            if (category.equalsIgnoreCase(categoryText)) {
                // The category already exists. Display a TOAST letting the user know.
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.category_already_exists),
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Determine the user id for the category. Zero means shared,
        // other the active user id will be used.
        long userId = shared ? 0 : mActiveUserId;

        // Add the new category item to the database
        InventoryItem item = new InventoryItem(userId, InventoryItem.PLACEHOLDER,
                categoryText, 0);
        item.setMode(InventoryCardMode.ADD);
        Log.d(TAG, "onCategoryEntered() Adding " + item.toString());
        mInventoryListViewModel.addInventoryItem(item);

        // Update the view
        mInventoryAdapter.notifyDataSetChanged();
    }

    /****************************************************************
     * Code to handle sending SSM message alerts, including obtaining
     * permission from the user.
     ****************************************************************/
    private final int REQUEST_SEND_SMS = 0;

    /**
     * Format and send an SSM base alert for an inventory item.
     */
    private void sendInventoryAlertMessage(InventoryItem item) {

        // Check for SSM permissions.
        if (hasSmsPermissions()) {

            // If the phone number is blank we won't be able to send the message. Display a TOAST instead.
            String phoneNumber = mUserPhoneNumber;
            if (phoneNumber.isBlank()) {
                Toast.makeText(getApplicationContext(),
                        "Unable to send alert message because phone number was not provided.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Format and attempt to send the text message.
                SmsManager messenger = SmsManager.getDefault();
                String message = "ALERT: Inventory item with a category of \"" + item.getCategory()
                        +"\" and a product name of \"" + item.getProductName() +
                        "\" has a count of " + item.getCount() + ".";
                messenger.sendTextMessage(phoneNumber,null,
                        message,null,null);
                Toast.makeText(getApplicationContext(),
                        "Inventory alert message was sent successfully to " + phoneNumber,
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // If the message failed to send then notify the user.
                Toast.makeText(getApplicationContext(), "Inventory alert message failed to send",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Check if the user has granted permissions to send SSM based messages.
     */
    private boolean hasSmsPermissions() {
        String smsPermission = android.Manifest.permission.SEND_SMS;
        // Check if the permission has been granted.
        if (ContextCompat.checkSelfPermission(this, smsPermission)
                != PackageManager.PERMISSION_GRANTED) {
            // The permission has not bee granted. Check if the rationale dialog should be displayed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, smsPermission )) {
                // Display the rationale dialog. This informs the user of how the permission
                // will be used withing the application to send alert messages.
                PermissionRationaleDialog dialog = new PermissionRationaleDialog(this);
                dialog.show(getSupportFragmentManager(), "SMSPermission");
            } else {
                // Request permissions for SMS based messaging.
                ActivityCompat.requestPermissions(this,
                        new String[]{smsPermission}, REQUEST_SEND_SMS);
            }

            return false;
        }

        return true;
    }

    /**
     * A callback that receives the results from the permission dialog.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);

        // Check the result code.
        switch (requestCode) {
            case REQUEST_SEND_SMS: {
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted. Display a TOAST message to the user.
                    Toast.makeText(getApplicationContext(),
                            "SMS message can be sent.", Toast.LENGTH_LONG).show();
                }
                else {
                    // The permission was not granted. Display a TOAST message to the user.
                    Toast.makeText(getApplicationContext(),
                            "PERMISSION NOT GRANTED, SMS message will not be sent.",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * Invoke the permission request once the user accepts the rationale.
     */
    @Override
    public void onPermissionRationalAcceptance() {
        String smsPermission = android.Manifest.permission.SEND_SMS;
        ActivityCompat.requestPermissions(this,
                new String[]{smsPermission}, REQUEST_SEND_SMS);
    }

    /****************************************************************
     * Code to handle the recycler view.
     ****************************************************************/

    /**
     * A class that represents and inventory card within the recycler view.
     */
    private class InventoryItemHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {

        // A reference to the inventory item.
        private InventoryItem mInventoryItem;

        // References to the controls within the inventory card.
        private final TextView mInventoryTitleTextView;
        private final TextView mInventoryCountTextView;
        private final TextView mInventoryCategoryTextView;
        private final ImageButton mIncreaseCountButton;
        private final ImageButton mDecreaseCountButton;

        /**
         * Constructor for creating an inventory item holder.
         */
        public InventoryItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.inventory_card_view, parent, false));

            // Set up the listener which is used to respond to long click events.
            itemView.setOnLongClickListener(this);

            // Set up the listener which is used to respond to normal click events.
            itemView.setOnClickListener(this);

            // Get and store references to the controls withing the inventory card.
            mInventoryTitleTextView = itemView.findViewById(R.id.inventory_item_title);
            mInventoryCountTextView = itemView.findViewById(R.id.inventory_item_count);
            mInventoryCategoryTextView = itemView.findViewById(R.id.inventory_item_category);

            // Set up the listener which is used to increase the count on an inventory item.
            mIncreaseCountButton = itemView.findViewById(R.id.increase_count_button);
            mIncreaseCountButton.setOnClickListener(this);

            // Set up the listener which is used to decrease the count on an inventory item.
            mDecreaseCountButton = itemView.findViewById(R.id.decrease_count_button);
            mDecreaseCountButton.setOnClickListener(this);
        }

        /**
         * Bind the inventory card to the data and its place in the recycler view.
         */
        public void bind(InventoryItem inventoryItem, int position) {
            // Save a reference to the inventory item.
            mInventoryItem = inventoryItem;

            // Compute the color for the inventory card.
            String productCategory = inventoryItem.getCategory();
            int colorIndex = (productCategory.hashCode() & 0x7FFFFFFF) % mCategoryColors.length;

            // Check the card mode.
            if (mInventoryItem.getMode() == InventoryCardMode.ADD) {
                // This is a category placeholder card. Placeholder cards have no title.
                mInventoryTitleTextView.setText("");
                mInventoryCategoryTextView.setText(inventoryItem.getCategory());
                mInventoryCountTextView.setText("+");
                // Make the buttons invisible.
                mIncreaseCountButton.setVisibility(View.INVISIBLE);
                mDecreaseCountButton.setVisibility(View.INVISIBLE);

                // Set the color for the card.
                if (mSelectedInventoryItemPosition == position) {
                    mInventoryTitleTextView.setBackgroundColor(mDeleteColor);
                } else {
                    mInventoryTitleTextView.setBackgroundColor(mCategoryColors[colorIndex]);
                }
            } else {
                // This is a normal inventory card. Set the product name (title) and category.
                mInventoryTitleTextView.setText(inventoryItem.getProductName());
                mInventoryCategoryTextView.setText(inventoryItem.getCategory());

                // Format and set the inventory count.
                mInventoryCountTextView.setText(String.format("%d", inventoryItem.getCount()));

                // Set the card color and check if the increase/decrease buttons should be visible.
                if (mSelectedInventoryItemPosition == position) {
                    // The card is selected for deletion.
                    // Make the increase/decrease buttons invisible.
                    mIncreaseCountButton.setVisibility(View.INVISIBLE);
                    mDecreaseCountButton.setVisibility(View.INVISIBLE);
                    // Set the card color.
                    mInventoryTitleTextView.setBackgroundColor(mDeleteColor);
                } else {
                    // The card is in normal display code.
                    mIncreaseCountButton.setVisibility(View.VISIBLE);
                    mDecreaseCountButton.setVisibility(View.VISIBLE);
                    // Set the card color.
                    mInventoryTitleTextView.setBackgroundColor(mCategoryColors[colorIndex]);
                }
            }
        }

        /**
         * Handler for normal click events.
         */
        @Override
        public void onClick(View view) {
            int viewId = view.getId();

            Log.d(TAG, "InventoryItemHolder::onClick() Called for " + viewId + " Item: " +
                    mInventoryItem.getProductName());

            // Check where the user clicked.
            if (viewId == R.id.increase_count_button) {
                // The user clicked the increase button.
                Log.d(TAG, "click was for increase_count_button");
                // increase the inventory count and update the view.
                mInventoryItem.increaseCount();
                mInventoryListViewModel.updateInventoryItem(mInventoryItem);
                mInventoryAdapter.notifyItemChanged(getAbsoluteAdapterPosition());
            } else if (viewId == R.id.decrease_count_button) {
                // The user clicked the decrease button.
                Log.d(TAG, "click was for decrease_count_button");
                // First get the current inventory count.
                long previousCount = mInventoryItem.getCount();
                // Decrease the inventory count.
                mInventoryItem.decreaseCount();
                // Check if the count is changing to zero.
                if (mInventoryItem.getCount() == 0 && previousCount > 0) {
                    // Send an SSM alert message for the inventory item.
                    sendInventoryAlertMessage(mInventoryItem);
                }
                // Update the display.
                mInventoryListViewModel.updateInventoryItem(mInventoryItem);
                mInventoryAdapter.notifyItemChanged(getAbsoluteAdapterPosition());
            } else if (mActionMode != null || mInventoryItem.getMode() == InventoryCardMode.ADD) {
                // The user clicked on a category placeholder card.
                // Start an activity to allow the user to enter a new inventory item.
                Intent intent = new Intent(InventoryActivity.this,
                        AddInventoryItemActivity.class);
                // Pass the category and userID to the new activity.
                intent.putExtra(EXTRA_PRODUCT_CATEGORY, mInventoryItem.getCategory());
                intent.putExtra(LoginActivity.EXTRA_USERID, mInventoryItem.getOwnerId());
                // Launch the AddInventoryItemActivity.
                mAddInventoryItemResultLauncher.launch(intent);
            }
        }

        /**
         * Handler for long click events. This is primarily used to delete inventory items
         * and empty categories.
         */
        @Override
        public boolean onLongClick(View view) {
            // If the action mode has already been invoked then just return.
            if (mActionMode != null) {
                return true;
            }

            // Check if this is a placeholder card.
            if (mInventoryItem.getMode() != InventoryCardMode.NORMAL) {
                // Get the inventory item count for the selected category.
                InventoryRepository inventoryRepository = InventoryRepository.getInstance(
                        getApplicationContext());
                int count = inventoryRepository.getCountForCategory(
                        mActiveUserId, mInventoryItem.getCategory());
                // Check if the category is empty.
                if (count > 1) {
                    // There is at least one other inventory item in this category. The category
                    // itself cannot be deleted. Display a TOAST and ignore the event.
                    Toast.makeText(getApplicationContext(),
                            "All items in category must be deleted first.",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            }

            // Save the current position.
            mSelectedInventoryItemPosition = getAbsoluteAdapterPosition();

            // Re-bind the selected item. This will update the display, showing
            // the card in delete mode.
            mInventoryAdapter.notifyItemChanged(mSelectedInventoryItemPosition);

            // Show the context action bar.
            mActionMode = InventoryActivity.this.startActionMode(mActionModeCallback);

            return true;
        }

        // A callback for the context action bar.
        private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

            /**
             * Initialize the context action bar.
             */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Provide context menu for action bar
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.inventory_item_context_menu, menu);
                return true;
            }

            /**
             * Prepare the action mode bar.
             */
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            /**
             * Handle click events for the action bar.
             */
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Process the action item selection.
                if (item.getItemId() == R.id.delete_inventory_item) {
                    Log.d(TAG, "onActionItemClicked() Deleting inventory item: " +
                            mInventoryItem.getProductName());

                    // Delete the selected inventory item or category.
                    mInventoryListViewModel.deleteInventoryItem(mInventoryItem);

                    // Show a snack bar indicating that an item was deleted and
                    // provide an undo button.
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                            R.string.inventory_item_deleted, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.undo, view -> {
                        // Undo the action by adding the inventory item back.
                        mInventoryListViewModel.addInventoryItem(mInventoryItem);
                    });

                    // Display the snack bar.
                    snackbar.show();

                    // Close the action bar.
                    mode.finish();

                    // Click was consumed.
                    return true;
                }

                // Click was not consumed.
                return false;
            }

            /**
             * Destroy the context action bar.
             */
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;

                // Update the selected item so that it will return to its normal viewing mode.
                mInventoryAdapter.notifyItemChanged(mSelectedInventoryItemPosition);

                // Clear the current selection.
                mSelectedInventoryItemPosition = RecyclerView.NO_POSITION;
            }
        };
    }

    // Register for results from the add inventory item activity.
    ActivityResultLauncher<Intent> mAddInventoryItemResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                /**
                 * Callback that receives the results from the add inventory item activity.
                 */
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult called");
                    // Check if the result was successful and the item should be added.
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult RESULT_OK");
                        Intent data = result.getData();
                        if (data != null) {
                            // Extract the data from the result.
                            String productCategory = data.getStringExtra(EXTRA_PRODUCT_CATEGORY);
                            String productName = data.getStringExtra(EXTRA_PRODUCT_NAME);
                            int productCount = data.getIntExtra(EXTRA_PRODUCT_COUNT, 0);
                            long userId = data.getLongExtra(LoginActivity.EXTRA_USERID, 0);

                            // Create and add the inventory item to the database.
                            InventoryItem item = new InventoryItem(userId, productName,
                                    productCategory, productCount);
                            Log.d(TAG, "onActivityResult() Adding " + item.toString());
                            mInventoryListViewModel.addInventoryItem(item);

                            // Update the display.
                            mInventoryAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

    /**
     * An adapter class for the inventory item recycler view.
     */
    private class InventoryAdapter extends RecyclerView.Adapter<InventoryItemHolder> {

        // The current list of inventory items.
        private final List<InventoryItem> mInventoryItemList;

        // The InventoryAdapter constructor accepting a list of inventory items.
        public InventoryAdapter(List<InventoryItem> inventoryItems) {
            mInventoryItemList = inventoryItems;
        }

        /**
         * Create the view group that will hold the data and state of our inventory cards
         * that are on the display.
         */
        @NonNull
        @Override
        public InventoryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new InventoryItemHolder(layoutInflater, parent);
        }

        /**
         * Bind the data from an inventory item to a view holder.
         */
        @Override
        public void onBindViewHolder(InventoryItemHolder holder, int position){
            holder.bind(mInventoryItemList.get(position), position);
        }

        /**
         * Get the count of inventory items in the list.
         */
        @Override
        public int getItemCount() {
            int size = mInventoryItemList.size();
            return size;
        }
    }
}