package cmsc424.cmsc424_pg11;

import android.content.Context;
import android.support.annotation.NonNull;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.titleAdapter.setText(mTitles.get(position));
        holder.messageAdapter.setText(mMessages.get(position));
        holder.genreAdapter.setText(mGenres.get(position));

        //TO DO listener
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleAdapter;
        TextView messageAdapter;
        TextView genreAdapter;
        //CardView parentCardView;//Notice

        public ViewHolder(View itemView) {
            super(itemView);

            titleAdapter = itemView.findViewById(R.id.recycler_view_item_title);
            messageAdapter = itemView.findViewById(R.id.recycler_view_item_message);
            genreAdapter = itemView.findViewById(R.id.recycler_view_item_genre);
            //parentCardView = itemView.findViewById(R.id.parent_card_view);//Notice

        }
    }
}
