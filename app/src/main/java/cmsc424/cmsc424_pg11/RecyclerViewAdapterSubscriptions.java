package cmsc424.cmsc424_pg11;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapterSubscriptions extends RecyclerView.Adapter<RecyclerViewAdapterSubscriptions.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapterSubs";

    private ArrayList<String> mGenres = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapterSubscriptions(Context mContext, ArrayList<String> mGenres) {


        this.mGenres = mGenres;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_subscription, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.genreAdapter.setText(mGenres.get(position));

        //Listener

        holder.parentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on:"+ mGenres.get(position));

                /*

                Bundle bundle = new Bundle();
                //Change fragment
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragment = new EventFragment();

                bundle.putString("Genre", mGenres.get(position));

                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, fragment).addToBackStack(null).commit();
                */
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView genreAdapter;
        CardView parentCardView;

        public ViewHolder(View itemView) {
            super(itemView);


            genreAdapter = itemView.findViewById(R.id.recycler_view_item_subscription_genre);
            parentCardView = itemView.findViewById(R.id.parent_card_view_subscription);

        }
    }
}
