package cmsc424.cmsc424_pg11;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;


public class AlertDialogDeregister extends AppCompatDialogFragment {


    private static final String TAG="AlertDialogDeregister";

    public interface AlertDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    AlertDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AlertDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        builder.setTitle("Alert")
                .setMessage("Are you sure you want to delete your account?")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(AlertDialogDeregister.this);
                        //getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogPositiveClick(AlertDialogDeregister.this);
                    }
                });


        return builder.create();
    }
}
