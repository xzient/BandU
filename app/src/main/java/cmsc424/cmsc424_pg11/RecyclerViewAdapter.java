package cmsc424.cmsc424_pg11;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mMessages = new ArrayList<>();
    private ArrayList<String> mGenres = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mTitles, ArrayList<String> mMessages, ArrayList<String> mGenres) {
        this.mTitles = mTitles;
        this.mMessages = mMessages;
        this.mGenres = mGenres;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.titleAdapter.setText(mTitles.get(position));
        //holder.messageAdapter.setText(mMessages.get(position));
        holder.genreAdapter.setText(mGenres.get(position));

        //Listener

        holder.parentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on:"+ mTitles.get(position));

                Bundle bundle = new Bundle();
                //Change fragment
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragment = new EventFragment();
                bundle.putString("Title", mTitles.get(position));
                bundle.putString("Genre", mGenres.get(position));
                bundle.putString("Message", mMessages.get(position));
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container, fragment).addToBackStack(null).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleAdapter;
        //TextView messageAdapter;
        TextView genreAdapter;
        CardView parentCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleAdapter = itemView.findViewById(R.id.recycler_view_item_title);
            //messageAdapter = itemView.findViewById(R.id.recycler_view_item_message);
            genreAdapter = itemView.findViewById(R.id.recycler_view_item_genre);
            parentCardView = itemView.findViewById(R.id.parent_card_view);

        }
    }
}
