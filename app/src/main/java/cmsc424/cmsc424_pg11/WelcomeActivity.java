package cmsc424.cmsc424_pg11;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;


public class WelcomeActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    private ImageView mImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mImageView = findViewById(R.id.imageView);


        mImageView.setImageResource(getResources().getIdentifier("@drawable/microphone", null, this.getPackageName()));







        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent = new Intent(WelcomeActivity.this, SignIn.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(WelcomeActivity.this, BaseActivityReader.class);
                    startActivity(intent);
                }
                finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
