package cmsc424.cmsc424_pg11;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment{

    private static final String TAG = "SettingsFragment";

    //Variables

    private Button mBecomeAPublisher, mDeRegister, mSignOut;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Log.d(TAG, "onCreateView: Started.");

        mBecomeAPublisher = view.findViewById(R.id.become_a_publisher);
        mDeRegister = view.findViewById(R.id.deregister_user);
        mSignOut = view.findViewById(R.id.btn_sign_out);

        mBecomeAPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.reader_fragment_container, new BecomePublisherFragment()).addToBackStack(TAG).commit();

            }
        });

        mDeRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endSession();
            }
        });

        return view;
    }//End view


    public void openDialog() {
        DialogFragment alertDialogDeregister = new AlertDialogDeregister();
        alertDialogDeregister.show(getActivity().getSupportFragmentManager(), "AlertDialogDeregister");

    }

    public void endSession () {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



}
