/*
 * 7-2 Project Three
 * File: CreateUserActivity.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.odriscoll.inventory.model.User;
import org.odriscoll.inventory.repository.InventoryRepository;

/**
 * A class that manages the user creation activity.
 */
public class CreateUserActivity extends AppCompatActivity {

    // The logging tag used for this class.
    private static final String TAG = "CreateUserActivity";

    // A constant name for the username parameter passed between activities.
    public static final String EXTRA_USERNAME = "org.odriscoll.inventory.username";

    // References to the EditText fields used so collect information from the user.
    private EditText mEditFirstNameText;
    private EditText mEditLastNameText;
    private EditText mEditPhoneNumberText;
    private EditText mEditUsernameText;
    private EditText mEditPasswordText;

    // A reference to the repository where user and inventory items are stored.
    private InventoryRepository mInventoryRepository;

    /**
     * Create and initialize the CreateUserActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get and save a reference to the inventory repository.
        mInventoryRepository = InventoryRepository.getInstance(getApplicationContext());

        // Initialize the layout.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.create_user_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get and save references to the UI components on this activity
        mEditFirstNameText = findViewById(R.id.edit_first_name_text);
        mEditLastNameText = findViewById(R.id.edit_last_name_text);
        mEditPhoneNumberText = findViewById(R.id.edit_phone_number_text);
        mEditUsernameText = findViewById(R.id.edit_username_text);
        mEditPasswordText = findViewById(R.id.edit_password_text);
    }

    /**
     * Callback method that is invoked when the user clicks the create button.
     */
    public void onCreateUser(View view) {
        boolean valid = true;

        // Get the first name field and validate it.
        String firstName = mEditFirstNameText.getText().toString();
        if (firstName.isBlank()) {
            // The first name field is not valid. Request focus and display an error message.
            mEditFirstNameText.requestFocus();
            mEditFirstNameText.setError(getResources().getString(R.string.name_cannot_be_empty));
            valid = false;
        }

        // Get the first name field and validate it.
        String lastName = mEditLastNameText.getText().toString();
        if (lastName.isBlank()) {
            // The last name field is not valid. Request focus and display an error message.
            mEditLastNameText.requestFocus();
            mEditLastNameText.setError(getResources().getString(R.string.name_cannot_be_empty));
            valid = false;
        }

        // Get the optional phone number.
        String phoneNumber = mEditPhoneNumberText.getText().toString();

        // Get the username field and validate it.
        String username = mEditUsernameText.getText().toString();
        if (username.isBlank()) {
            // The username field is not valid. Request focus and display an error message.
            mEditUsernameText.requestFocus();
            mEditUsernameText.setError(getResources().getString(R.string.username_cannot_be_empty));
            valid = false;
        }

        // Internally the username is case-insensitive. Store it in lower-case.
        username = username.toLowerCase();

        // Check is a user already exists in the database.
        User user = mInventoryRepository.getUser(username);
        if (user != null) {
            // The username already exists in the database. Request focus and display an error message.
            mEditUsernameText.requestFocus();
            mEditUsernameText.setError(getResources().getString(R.string.username_already_exists));
            valid = false;
        }

        // Get the password field and validate it.
        String password = mEditPasswordText.getText().toString();
        if (password.isBlank()) {
            // The password field is not valid. Request focus and display and error message.
            mEditPasswordText.requestFocus();
            mEditPasswordText.setError(getResources().getString(R.string.password_cannot_be_empty));
            valid = false;
        }

        // Note that a future enhancement would be to store a hashed password to the database
        // instead of the actual plain text password. This would be more secure.

        if (valid) {
            // The user is valid, create a user object and store it in the database.
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setUsername(username);
            user.setPassword(password);
            mInventoryRepository.addUser(user);

            // Load some predefined categories so that the user doesn't start with a blank screen.
            mInventoryRepository.addPrePackagedInventoryItems(user.getId());

            Intent intent = new Intent();

            // Pass the username field so that it can be populated on the login screen. We don't
            // pass the password back though because the user needs to type that to login.
            intent.putExtra(EXTRA_USERNAME, username);

            Log.d(TAG, "onCreateUser: username=" + username);

            // Set the result and finish the activity.
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}