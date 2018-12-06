package cmsc424.cmsc424_pg11;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    Bundle bundle;

    TextView mTitle, mMessage, mGenre;
    VideoView mVideo;

    private ProgressBar mProgressCircle;
    private ImageView mPlayButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        Log.d(TAG, "onCreateView: View created.");

        bundle = this.getArguments();

        mTitle = view.findViewById(R.id.event_fragment_title);
        mGenre = view.findViewById(R.id.event_fragment_genre);
        mMessage = view.findViewById(R.id.event_fragment_message);
        mVideo = view.findViewById(R.id.event_fragment_video);
        mPlayButton = view.findViewById(R.id.event_fragment_play);
        mProgressCircle = view.findViewById(R.id.event_fragment_progress_circular);


        String videoPath = bundle.getString("Video");
        String imagePath = bundle.getString("Image");
        mPlayButton.setVisibility(View.GONE);

        //Set Values
        if(bundle != null) {

            mTitle.setText(bundle.getString("Title"));
            mGenre.setText(bundle.getString("Genre"));
            mMessage.setText(bundle.getString("Message"));


            //Video
            //There is not video
            if (videoPath.trim().equals("")) {
                mVideo.setVisibility(View.GONE);
                mProgressCircle.setVisibility(View.GONE);
            }
            else {
                Uri uri = Uri.parse(videoPath);
                mVideo.setVideoURI(uri);

                mVideo.requestFocus();

                MediaController mediaController = new MediaController(getContext());
                mVideo.setMediaController(mediaController);
                mediaController.setAnchorView(mVideo);

                mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mProgressCircle.setVisibility(View.GONE);
                        mPlayButton.setVisibility(View.VISIBLE);
                    }
                });
                if (mVideo.isPlaying()) {
                    mPlayButton.setVisibility(View.GONE);
                }
                mPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mVideo.start();
                        mPlayButton.setVisibility(View.GONE);
                    }
                });
            }

            //Image


        }
        return view;
    }



}
