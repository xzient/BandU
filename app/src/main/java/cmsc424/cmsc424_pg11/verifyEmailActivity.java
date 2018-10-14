package cmsc424.cmsc424_pg11;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class verifyEmailActivity extends AppCompatActivity {


    Button returnSignIn;
    Button sendAgain;
    TextView messageVerify;

    public static final String TAG = "verifyEmailActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        messageVerify = findViewById(R.id.verifyEmailMessage);
        sendAgain = findViewById(R.id.buttonSendEmail);
        returnSignIn = findViewById(R.id.buttonReturnToSignIn);

        mAuth = FirebaseAuth.getInstance();

        sendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.getCurrentUser().sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    Toast.makeText(verifyEmailActivity.this, "Email sent!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        returnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(verifyEmailActivity.this, SignIn.class));
            }
        });



    }


    /*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();


        //updateUI(currentUser); //Create function to update
    }
    */
}
