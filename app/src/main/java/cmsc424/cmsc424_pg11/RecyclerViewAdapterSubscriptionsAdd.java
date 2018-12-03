package cmsc424.cmsc424_pg11;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecyclerViewAdapterSubscriptionsAdd extends RecyclerSwipeAdapter<RecyclerViewAdapterSubscriptionsAdd.ViewHolder>{

    private static final String TAG = "RVAdapterSubsAdd";

    private ArrayList<String> mGenres;
    private Context mContext;
    private ArrayList<String> mGenreIdList;
    FirebaseUser mCurrentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDataBase;

    public RecyclerViewAdapterSubscriptionsAdd(Context mContext, ArrayList<String> mGenres, ArrayList<String> mGenreIdList) {


        this.mGenres = mGenres;
        this.mContext = mContext;
        this.mGenreIdList = mGenreIdList;
        this.mDataBase = FirebaseDatabase.getInstance().getReference("server").child("genreuser");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_subscription_add, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.genreAdapter.setText(mGenres.get(position).toString());

        holder.parentCardView.setShowMode(SwipeLayout.ShowMode.PullOut);

        //Right swipe
        holder.parentCardView.addDrag(SwipeLayout.DragEdge.Right, holder.parentCardView.findViewById(R.id.right_swipe_add));


        holder.parentCardView.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //add to data
                mDataBase.child(mGenreIdList.get(position)).child(mCurrentFirebaseUser.getUid()).setValue(true);




                //Remove from display
                mItemManger.removeShownLayouts(holder.parentCardView);
                mGenres.remove(position);
                mGenreIdList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mGenres.size());
                mItemManger.closeAllItems();

                Toast.makeText(view.getContext(), "Subscribed to genre!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView genreAdapter;
        ImageView add;
        SwipeLayout parentCardView;

        public ViewHolder(View itemView) {
            super(itemView);


            add = itemView.findViewById(R.id.add_swipe);
            genreAdapter = itemView.findViewById(R.id.recycler_view_item_subscription_add_genre);
            parentCardView = itemView.findViewById(R.id.parent_card_view_subscription_add);

        }
    }
}
