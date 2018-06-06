package vn.codegym.androidcustomermanagement.crud_demo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import vn.codegym.androidcustomermanagement.R;
import vn.codegym.androidcustomermanagement.bean.Customer;
import vn.codegym.androidcustomermanagement.constants.Constants;

public class EditDialogBox extends DialogFragment {

    private static final String TAG = "EditDialogBox";

    public interface EditDialogBoxListener {
        void updateData(Customer customer);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View editDialogView = inflater.inflate(R.layout.edit_dialog_layout, null);
        final EditText editedFirstName = (EditText) editDialogView.findViewById(R.id.edited_firstname);
        final EditText editedLastName = (EditText) editDialogView.findViewById(R.id.edited_lastname);

        Bundle bundle = this.getArguments();
        final int id = bundle.getInt(Constants.ID);
        builder.setTitle(String.valueOf(id));
        builder.setView(editDialogView);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String firstName = editedFirstName.getText().toString().trim();
                String lastName = editedLastName.getText().toString().trim();
                Log.d(TAG, "name : " + firstName);

                EditDialogBoxListener mainActivity = (EditDialogBoxListener) getActivity();
                mainActivity.updateData(new Customer(id, firstName, lastName));
                EditDialogBox.this.getDialog().dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditDialogBox.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}
