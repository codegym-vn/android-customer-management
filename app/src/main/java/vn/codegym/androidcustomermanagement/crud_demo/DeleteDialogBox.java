package vn.codegym.androidcustomermanagement.crud_demo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import vn.codegym.androidcustomermanagement.R;
import vn.codegym.androidcustomermanagement.constants.Constants;

public class DeleteDialogBox extends DialogFragment {

    public interface DeleteDialogBoxListener {
        void deleteData(int id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle bundle = this.getArguments();
        final int id = bundle.getInt(Constants.ID);

        builder.setTitle(String.valueOf(id));
        builder.setMessage("Confirm Deletion ?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteDialogBoxListener mainActivity = (DeleteDialogBoxListener) getActivity();
                mainActivity.deleteData(id);
                DeleteDialogBox.this.getDialog().dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteDialogBox.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}
