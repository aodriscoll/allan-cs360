/*
 * 7-2 Project Three
 * File: PermissionRationaleDialog.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * A dialog box for displaying the SSM permission rationale to the user.
 */
public class PermissionRationaleDialog extends DialogFragment
        implements DialogInterface.OnClickListener {

    // The logging tag used by this class.
    private final String TAG = "PermissionRationaleDialog";

    // A reference to the dialog box listener.
    private final PermissionRationaleDialogListener listener;

    /**
     * An interface for accepting the results from the dialog box.
     */
    public interface PermissionRationaleDialogListener {
        public void onPermissionRationalAcceptance();
    }

    /**
     * Construct the permission rationale dialog box.
     */
    public PermissionRationaleDialog(PermissionRationaleDialogListener listener) {
        this.listener = listener;
    }

    /**
     * Create and initialize the dialog box.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.sms_permission_rationale_title)
                .setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) this)
                .setMessage(R.string.sms_permission_rationale_message);
        return builder.create();
    }

    /**
     * A callback for button clicks.
     */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d(TAG, "Permission rationale message accepted.");
        listener.onPermissionRationalAcceptance();
    }
}