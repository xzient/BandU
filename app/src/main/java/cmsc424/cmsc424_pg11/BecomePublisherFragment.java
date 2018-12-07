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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BecomePublisherFragment extends Fragment{

    private static final String TAG = "BecomePublisherFragment";

    //Variables
    Button mButton;

    private DatabaseReference mDatabase;

    private FirebaseUser mCurrentFirebaseUser;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_become_publisher, container, false);

        Log.d(TAG, "onCreateView: Started.");
        mCurrentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mButton = view.findViewById(R.id.button_become_a_publisher);

        mDatabase = FirebaseDatabase.getInstance().getReference("server").child("users").child(mCurrentFirebaseUser.getUid()).child("publisher");

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.setValue(true);
                Toast.makeText(getContext(), "Registering successful!", Toast.LENGTH_SHORT).show();

                FragmentManager fr = getFragmentManager();
                fr.popBackStack();
                fr.popBackStack();
            }
        });

        return view;
    }//End view



    

}
