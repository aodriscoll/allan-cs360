/*
 * 7-2 Project Three
 * File: LoginActivity.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.odriscoll.inventory.repository.InventoryRepository;
import org.odriscoll.inventory.model.User;

/**
 * An activity that manages the user login.
 */
public class LoginActivity extends AppCompatActivity {

    // The logging tag used for this class.
    private static final String TAG = "LoginActivity";

    // A constant name for the userid parameter passed between activities.
    public static final String EXTRA_USERID = "org.odriscoll.inventory.userid";

    // References to the EditText fields used so collect information from the user.
    private EditText mEditUsernameText;
    private EditText mEditPasswordText;

    // A reference to the repository where user and inventory items are stored.
    private InventoryRepository mInventoryRepository;

    /**
     * Create and initialize the LoginActivity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get and save a reference to the inventory repository.
        mInventoryRepository = InventoryRepository.getInstance(getApplicationContext());

        // Initialize the layout.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get and save references to the UI components on this activity
        mEditUsernameText = findViewById(R.id.edit_username_text);
        mEditPasswordText = findViewById(R.id.edit_password_text);
    }

    /**
     * A callback method that is invoked when the user clicks the sign in button.
     */
    public void onSignIn(View view) {
        Log.d(TAG, "onSignIn() called");

        String username = mEditUsernameText.getText().toString();
        String password = mEditPasswordText.getText().toString();

        // Validate the username and password.
        if (username.isBlank() || password.isBlank()) {
            // The username or password fields are not valid. Request focus and display an error
            // message. Note that we always set the focus on the username field and refer to both.
            // For security reasons, we don't want to clue the user into knowing which one
            // is invalid.
            mEditUsernameText.requestFocus();
            mEditUsernameText.setError(getResources().getString(R.string.username_or_password_incorrect));
            Log.d(TAG, "onSignIn() Username or Password blank");
            return;
        }

        // Internally the username is case-insensitive. Store it in lower-case.
        username = username.toLowerCase();

        // Load the given user from the database.
        User user = mInventoryRepository.getUser(username);
        if (user == null) {
            // The user doesn't exist in the database. Request focus and display an error message.
            // Note that we always set the focus on the username field and refer to both. For
            // security reasons, we don't want to clue the user into knowing which one is invalid.
            mEditUsernameText.requestFocus();
            mEditUsernameText.setError(getResources().getString(R.string.username_or_password_incorrect));
            Log.d(TAG, "onSignIn() Username " + username + " does not exist");
            return;
        }

        // Verify that the username and password match.
        if (!username.equalsIgnoreCase(user.getUsername()) || !password.equals(user.getPassword())) {
            mEditUsernameText.requestFocus();
            mEditUsernameText.setError(getResources().getString(R.string.username_or_password_incorrect));
            Log.d(TAG, "onSignIn() Username or password is incorrect");
            return;
        }

        // Blank out the username and password so they are not present when the login screen loads again.
        mEditUsernameText.setText("");
        mEditPasswordText.setText("");

        // Create an intent and launch the inventory activity.
        Intent intent = new Intent(this, InventoryActivity.class);
        intent.putExtra(EXTRA_USERID, user.getId());
        startActivity(intent);
    }

    /**
     * A callback method that is invoked when the user clicks the create user button.
     */
    public void onCreateUser(View view) {
        Log.d(TAG, "onCreateUser() called");

        // Create an intent and launch the create user activity.
        Intent intent = new Intent(this, CreateUserActivity.class);
        mCreateUserResultLauncher.launch(intent);
    }

    // Register for results the create user activity.
    ActivityResultLauncher<Intent> mCreateUserResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                /**
                 * A callback method that will receive the results from the create user activity.
                 */
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult called");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult RESULT_OK");
                        Intent data = result.getData();
                        if (data != null) {
                            Log.d(TAG, "onActivityResult data not null");
                            // Receive the username and use it to populate the login form.
                            String username = data.getStringExtra(CreateUserActivity.EXTRA_USERNAME);
                            mEditUsernameText.setText(username);
                            Log.d(TAG, "onActivityResult username=" + username);
                       }
                    }
                }
            });
}