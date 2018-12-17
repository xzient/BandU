package cmsc424.cmsc424_pg11;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class EventFragment extends Fragment {

    private static final String TAG = "EventFragment";

    Bundle bundle;
    TextView mTitle, mMessage, mGenre;
    VideoView mVideo;

    private ProgressBar mProgressCircle;
    private ImageView mPlayButton, mImage;
    private RelativeLayout mRelativeLayoutVideo;
    private MediaPlayer mAudio;
    String audioPath;
    private Button button;

    Drawable drawableStart;
    Drawable drawablePause;
    DatabaseReference mRef;
    String mUserID;

    SeekBar seekBar;
    Handler handler;
    Runnable runnable;

    ImageView addToFavorites;
    String eventId;
    Boolean alreadyFav = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        Log.d(TAG, "onCreateView: View created.");

        bundle = this.getArguments();
        handler = new Handler();

        mUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();




        mTitle = view.findViewById(R.id.event_fragment_title);
        mGenre = view.findViewById(R.id.event_fragment_genre);
        mMessage = view.findViewById(R.id.event_fragment_message);
        mVideo = view.findViewById(R.id.event_fragment_video);
        mImage = view.findViewById(R.id.event_fragment_image);
        mPlayButton = view.findViewById(R.id.event_fragment_play);
        mProgressCircle = view.findViewById(R.id.event_fragment_progress_circular);
        mRelativeLayoutVideo = view.findViewById(R.id.event_fragment_relatlay_video);
        mAudio = new MediaPlayer();
        button = view.findViewById(R.id.event_fragment_button_audio);
        seekBar = view.findViewById(R.id.event_fragment_seekbar);
        addToFavorites = view.findViewById(R.id.event_fragment_add_to_favorites);

        mRef = FirebaseDatabase.getInstance().getReference("server").child("favorites");



        String videoPath = bundle.getString("Video");
        String imagePath = bundle.getString("Image");
        audioPath = bundle.getString("Audio");
        mPlayButton.setVisibility(View.GONE);

        drawableStart = getResources().getDrawable(R.drawable.ic_play_arrow);



        drawablePause = getResources().getDrawable(R.drawable.ic_pause);



        if(bundle != null) {
            eventId = bundle.getString("ID");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if( dataSnapshot.hasChild(eventId) && dataSnapshot.child(eventId).hasChild(mUserID)) {
                        addToFavorites.setBackgroundResource(R.drawable.ic_star);
                        alreadyFav = true;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }







        //Set Values
        if(bundle != null) {

            addToFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (alreadyFav) {
                        mRef.child(eventId).child(mUserID).removeValue();
                        Toast.makeText(getContext(), "Event removed from favorites", Toast.LENGTH_SHORT).show();
                        addToFavorites.setBackgroundResource(R.drawable.ic_star_border);
                        alreadyFav = false;
                    }
                    else {
                        mRef.child(eventId).child(mUserID).setValue(true);
                        Toast.makeText(getContext(), "Event added to favorites", Toast.LENGTH_SHORT).show();
                        addToFavorites.setBackgroundResource(R.drawable.ic_star);
                        alreadyFav = true;
                    }


                }
            });




            mTitle.setText(bundle.getString("Title"));
            mGenre.setText(bundle.getString("Genre"));
            mMessage.setText(bundle.getString("Message"));


            if (bundle.getString("Genre").contains("Shoegaze")) {
                view.setBackgroundColor(getResources().getColor(R.color.colorShoegaze));
            }





            //Video
            //There is not video
            if (mVideo.isPlaying()) {
                mPlayButton.setVisibility(View.GONE);
            }

            if (videoPath != null && !videoPath.trim().equals("")) {

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

                mPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mVideo.start();
                        mPlayButton.setVisibility(View.GONE);
                    }
                });



            }
            else {
                mVideo.setVisibility(View.GONE);
                mProgressCircle.setVisibility(View.GONE);
                mRelativeLayoutVideo.setVisibility(View.GONE);
            }

            //Image
            if (imagePath != null && !imagePath.trim().equals("")) {
                Glide.with(getContext()).load(imagePath).into(mImage);
            }
            else {
                mImage.setVisibility(View.GONE);
            }

            //Audio

            if (audioPath != null && !audioPath.trim().equals("")) {
                try {
                    mAudio.setDataSource(audioPath);
                    mAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    mAudio.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            //mAudio.start();
                            seekBar.setMax(mAudio.getDuration());
                            playCycle();

                        }


                    });
                    mAudio.prepare();





                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                            if (input) {
                                mAudio.seekTo(progress);
                            }

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                }catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
            else {
                button.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mAudio.isPlaying()){

                        mAudio.pause();
                        playCycle();
                        button.setBackgroundResource(R.drawable.ic_play_arrow);



                    }
                    else{
                        mAudio.start();
                        playCycle();
                        button.setBackgroundResource(R.drawable.ic_pause);

                    }





                }
            });

            if (audioPath != null && !audioPath.trim().equals("")) {



            }
            else {
                //TO DO
            }


        }




        return view;
    }

    public void playCycle() {
        seekBar.setProgress(mAudio.getCurrentPosition());

        if(mAudio.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mAudio.stop();
    }
}
