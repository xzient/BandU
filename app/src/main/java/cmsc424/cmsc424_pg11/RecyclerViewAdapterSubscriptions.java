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
import java.util.List;


public class RecyclerViewAdapterSubscriptions extends RecyclerSwipeAdapter<RecyclerViewAdapterSubscriptions.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapterSubs";

    private ArrayList<String> mValues = new ArrayList<>();
    private Context mContext;
    private ArrayList<String> mValueIdList;
    private boolean mIsLocation;
    FirebaseUser mCurrentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDataBase;

    public RecyclerViewAdapterSubscriptions(Context mContext, ArrayList<String> mmValues, ArrayList<String> mValueIdList, boolean mIsLocation) {


        this.mValues = mmValues;
        this.mContext = mContext;
        this.mValueIdList = mValueIdList;
        this.mIsLocation = mIsLocation;
        if(mIsLocation) {
            this.mDataBase = FirebaseDatabase.getInstance().getReference("server").child("cityuser");
        }
        else {
            this.mDataBase = FirebaseDatabase.getInstance().getReference("server").child("genreuser");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_subscription, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.genreAdapter.setText(mValues.get(position));

        holder.parentCardView.setShowMode(SwipeLayout.ShowMode.PullOut);

        //Right swipe
        holder.parentCardView.addDrag(SwipeLayout.DragEdge.Right, holder.parentCardView.findViewById(R.id.right_swipe));


        holder.parentCardView.setOnClickListener(new View.OnClickListener() {

            boolean wasClosed = true;
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on:"+ mValues.get(position));

                if (SwipeLayout.Status.Close == holder.parentCardView.getOpenStatus())
                {
                    if (wasClosed)
                    {

                        holder.parentCardView.open();
                    }
                    else
                    {
                        wasClosed = true;
                        //holder.parentCardView.close();
                    }
                }
                else
                {
                    wasClosed = false;
                    //holder.parentCardView.close();
                }
            }
        });



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

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mDataBase.child(mValueIdList.get(position)).child(mCurrentFirebaseUser.getUid()).removeValue();




                //Remove from display
                mItemManger.removeShownLayouts(holder.parentCardView);
                mValues.remove(position);
                mValueIdList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mValues.size());
                mItemManger.closeAllItems();

                //Toast.makeText(view.getContext(), "Unsubscribed to genre!", Toast.LENGTH_SHORT).show();

                if (mIsLocation) {
                    Toast.makeText(view.getContext(), "Unsubscribed to location!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(view.getContext(), "Unsubscribed to genre!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView genreAdapter;
        ImageView delete;
        SwipeLayout parentCardView;

        public ViewHolder(View itemView) {
            super(itemView);


            delete = itemView.findViewById(R.id.delete_swipe);
            genreAdapter = itemView.findViewById(R.id.recycler_view_item_subscription_genre);
            parentCardView = itemView.findViewById(R.id.parent_card_view_subscription);

        }
    }

    public void updateList(List<String> newListGenres, List<String> newListIDs) {
        mValueIdList = new ArrayList<>();
        mValues = new ArrayList<>();

        mValues.addAll(newListGenres);
        mValueIdList.addAll(newListIDs);
        notifyDataSetChanged();
    }
}
