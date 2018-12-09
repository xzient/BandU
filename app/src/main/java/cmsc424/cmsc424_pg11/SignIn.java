package cmsc424.cmsc424_pg11;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    Button logIn;
    Button signUp;
    EditText email;
    EditText password;

    String testEmail;
    String testPassword;
    Toolbar toolbar;

    Boolean verifyEmail = false;

    public static final String TAG = "SignIn";

    //Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);






        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("BandU");
        //Initiate buttons
        logIn = findViewById(R.id.buttonLogIn);
        signUp = findViewById(R.id.buttonSignUp);
        //Initiate EditText
        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
        //Authentication
        mAuth = FirebaseAuth.getInstance();


        //Function LogIn button
        logIn.setOnClickListener(SignInListener);

        //Function SignUp button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });


    }// End onCreate


    /**
     * Sign In listener
     */
    View.OnClickListener SignInListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            testEmail = email.getText().toString();
            testPassword = password.getText().toString();

            //Check if empty
            if(testEmail.isEmpty() || testPassword.isEmpty()) {
                Toast.makeText(SignIn.this, "Add your email and password", Toast.LENGTH_SHORT).show();
                return;
            }







            mAuth.signInWithEmailAndPassword(testEmail, testPassword)
                    .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Verify if email was verified.

                            if(mAuth.getCurrentUser() == null) {
                                //Toast.makeText(SignIn.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                                return;
                            }



                            if(! mAuth.getCurrentUser().isEmailVerified()) {
                                Log.w(TAG, "emailNotVerified", task.getException());
                                Toast.makeText(SignIn.this, "Please verify your email!", Toast.LENGTH_SHORT).show();
                                verifyEmail = true;
                                return;
                            }


                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                //Toast.makeText(SignIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                    public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: User does not exist", e);
                            if (!verifyEmail) {
                                Toast.makeText(SignIn.this, "Wrong email or password!", Toast.LENGTH_SHORT).show();
                            }
                    }



            });

        }
    };

    /**
     * Updates UI accordingly
     * @param user FirebaseUser
     */
    public void updateUI(FirebaseUser user) {
        if(user != null) {

            Intent intent = new Intent(SignIn.this, BaseActivityReader.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser); //Create function to update
    }
    */


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        //am.killBackgroundProcesses(getIntent().EXTRA_PACKAGE_NAME);

        //Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //finish();
        //System.exit(0);
    }
}
