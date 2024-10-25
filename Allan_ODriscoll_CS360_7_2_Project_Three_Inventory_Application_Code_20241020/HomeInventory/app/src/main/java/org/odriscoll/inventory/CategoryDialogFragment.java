/*
 * 7-2 Project Three
 * File: CategoryDialogFragment.java
 * Author: Allan O’Driscoll
 * Institution: Southern New Hampshire University
 * Course: CS-360-15309-M01 Mobile Architect & Programming
 * Professor: Bill Chan
 * Date: October 20, 2024
 */
package org.odriscoll.inventory;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * A dialog box for adding new categories.
 */
public class CategoryDialogFragment extends DialogFragment {

    /**
     * An interface for receiving category information from the dialog.
     */
    public interface OnCategoryEnteredListener {
        void onCategoryEntered(String categoryText, boolean shared);
    }

    // A reference to the dialog listener.
    private OnCategoryEnteredListener mListener;

    // Create an initialize the dialog box.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Create an EditText component for the user to type the category name.
        final EditText categoryEditText = new EditText(requireActivity());
        categoryEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        categoryEditText.setMaxLines(1);

        // Create the dialog box.
        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.new_category)
                .setView(categoryEditText)
                .setPositiveButton(R.string.create, (dialog, whichButton) -> {
                    // Notify listener of a new private category.
                    String category = categoryEditText.getText().toString();
                    if (mListener != null) {
                        mListener.onCategoryEntered(category.trim(), false);
                    }
                })
                .setNeutralButton(R.string.create_and_share, (dialog, whichButton) -> {
                    // Notify listener of a shared category.
                    String category = categoryEditText.getText().toString();
                    if (mListener != null) {
                        mListener.onCategoryEntered(category.trim(), true);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    /**
     * Attach the dialog box to the current activity.
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnCategoryEnteredListener) context;
    }

    /**
     * Detach the dialog box from the current activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}