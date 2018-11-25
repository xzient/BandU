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
import android.widget.TextView;

public class SubscriptionsFragment extends Fragment {

    private static final String TAG = "SubscriptionsFragment";

    Button add_sub, current_sub;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        Log.d(TAG, "onCreateView: View created.");


        current_sub = view.findViewById(R.id.button_subscriptions_current_subscriptions);
        add_sub = view.findViewById(R.id.button_subscriptions_add_subscriptions);


        current_sub.setOnClickListener(onClickListener);
        add_sub.setOnClickListener(onClickListener);





        return view;
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            switch (view.getId()) {
                case R.id.button_subscriptions_current_subscriptions:
                    fragmentManager.beginTransaction().replace(R.id.reader_fragment_container, new CurrentSubscriptionsFragment()).addToBackStack(TAG).commit();
                    break;

                case R.id.button_subscriptions_add_subscriptions:
                    fragmentManager.beginTransaction().replace(R.id.reader_fragment_container, new AddSubscriptionsFragment()).addToBackStack(TAG).commit();
                    break;

            }


        }
    };



}
