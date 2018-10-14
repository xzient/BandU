package cmsc424.cmsc424_pg11;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    Button logIn;
    Button signUp;
    EditText email;
    EditText password;

    String testEmail;
    String testPassword;

    public static final String TAG = "SignIn";

    //Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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

            if(testEmail.isEmpty() || testPassword.isEmpty()) {
                Toast.makeText(SignIn.this, "Add your email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(testEmail, testPassword)
                    .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
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
                    });

        }
    };

    /**
     * Updates UI accordingly
     * @param user FirebaseUser
     */
    public void updateUI(FirebaseUser user) {
        if(user != null) {
            startActivity(new Intent(SignIn.this, MapsActivity.class));
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


}
