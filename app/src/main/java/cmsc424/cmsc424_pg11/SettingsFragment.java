package cmsc424.cmsc424_pg11;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SettingsFragment extends Fragment{

    private static final String TAG = "SettingsFragment";

    //Variables

    private Button mBecomeAPublisher, mDeRegister;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Log.d(TAG, "onCreateView: Started.");

        mBecomeAPublisher = view.findViewById(R.id.become_a_publisher);
        mDeRegister = view.findViewById(R.id.deregister_user);

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

        return view;
    }//End view


    public void openDialog() {

    }



}
