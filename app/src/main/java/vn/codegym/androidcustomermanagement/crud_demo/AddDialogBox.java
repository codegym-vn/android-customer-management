package vn.codegym.androidcustomermanagement.crud_demo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import vn.codegym.androidcustomermanagement.R;
import vn.codegym.androidcustomermanagement.bean.Customer;


public class AddDialogBox extends DialogFragment {

    public interface AddDialogBoxListener {
        void createData(Customer customer);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View addDialogView = inflater.inflate(R.layout.add_dialog_layout, null);
        final EditText addedFirstName = (EditText) addDialogView.findViewById(R.id.added_firstName);
        final EditText addedLastName = (EditText) addDialogView.findViewById(R.id.added_lastName);

        builder.setTitle("Add Customer");
        builder.setView(addDialogView);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String firstName = addedFirstName.getText().toString().trim();
                String lastName = addedLastName.getText().toString().trim();


                AddDialogBoxListener mainActivity = (AddDialogBoxListener) getActivity();
                mainActivity.createData(new Customer(firstName, lastName));
                AddDialogBox.this.getDialog().dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddDialogBox.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}
