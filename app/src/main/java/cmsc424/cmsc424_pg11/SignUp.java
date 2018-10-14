package cmsc424.cmsc424_pg11;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    //CAPTCHA
    private GoogleApiClient mGoogleApiClient;
    final String SiteKey = "6LfFrXQUAAAAAP_5M96S29fUFK3ddsxrOGaiTq6o";
    final String SecretKey  = "6LfFrXQUAAAAAIzzjOSE4posQ6AeZp7kk7dLiyA7";
    private boolean passCaptcha = false;

    //Authentication
    private FirebaseAuth mAuth;


    //Accept Terms and Conditions
    CheckBox acceptTerms;

    //General Sign Up
    Button btnRequest;
    TextView tvResult;
    Button signUpButton;
    EditText name;
    EditText email;
    EditText password1;
    EditText password2;

    //TAG
    public static final String TAG = "SignUp";


    //Data
    public static final String USER = "user";
    public static final String USER_NAME = "name";
    public static final String USER_EMAIL = "email";

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("server").child("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Authentication
        mAuth = FirebaseAuth.getInstance();

        //Buttons and EditTexts
        signUpButton = findViewById(R.id.signUpButton);
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        password1 = findViewById(R.id.editTextPassword1);
        password2 = findViewById(R.id.editTextPassword2);

            //actions
        signUpButton.setOnClickListener(RegisterOnClickListener);


        //CAPTCHA init
        tvResult = findViewById(R.id.result);
        btnRequest = findViewById(R.id.request);
        btnRequest.setOnClickListener(RqsOnClickListener);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(SignUp.this)
                .addOnConnectionFailedListener(SignUp.this)
                .build();

        mGoogleApiClient.connect();

        //Terms and Services
        acceptTerms = findViewById(R.id.acceptTerms);
        acceptTerms.setChecked(false);





    }//End onCreate


    /**
     * Click Listener for Sign Up / Register
     */
    View.OnClickListener RegisterOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //Get values from TextEdits
            final String newEmail = email.getText().toString();
            final String newPassword = password1.getText().toString();
            final String newPassword2 = password2.getText().toString();
            final String newName = name.getText().toString();

            //Conditions


            //Any box is empty
            if(newName.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty() || newPassword2.isEmpty()) {
                Toast.makeText(SignUp.this, "Please fill out all the boxes!", Toast.LENGTH_SHORT).show();
                return;
            }

            //CAPTCHA
            if(!passCaptcha) {
                Toast.makeText(SignUp.this, "Please verify you are not a robot!", Toast.LENGTH_SHORT).show();
                return;
            }
            //Terms and conditions
            if(!acceptTerms.isChecked()) {
                Toast.makeText(SignUp.this, "Please check the terms and conditions!", Toast.LENGTH_SHORT).show();
                return;
            }

            //Passwords are not the same
            if(!newPassword.equals(newPassword2)) {
                Toast.makeText(SignUp.this, "The two passwords you have entered are not the same!", Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.createUserWithEmailAndPassword(newEmail, newPassword)
                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userId = user.getUid();
                                saveUserToData(newEmail, newName, userId);

                                //Send email.
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Email sent.");
                                                }
                                            }
                                        });


                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUp.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });


        }
    };


    /**
     * This function will store the new user in the Firestore DB
     * @param newEmail
     * @param newName
     */
    public void saveUserToData(String newEmail, String newName, String userID) {
        Map<String, Object> userToSave = new HashMap<String, Object>();
        userToSave.put(USER_EMAIL, newEmail);
        userToSave.put(USER_NAME, newName);
        //Set city, lat, and long with default values
        userToSave.put("city", "Washington");
        userToSave.put("state", "DC");
        userToSave.put("latitude", 38.89511);
        userToSave.put("longitude", -77.03637);
        mRef.child(userID).setValue(userToSave);
    }

    /**
     * This method will update the UI correctly after clicking sign up
     * @param user
     */
    public void updateUI(FirebaseUser user) {
        if(user != null) {
            startActivity(new Intent(SignUp.this, verifyEmailActivity.class));
            //Eventually this should take you to email verification
            //TO DO
        }
        else {
            Toast.makeText(this, "Not Signed User", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Click Listener for CAPTCHA
     */
    View.OnClickListener RqsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            tvResult.setText("");

            SafetyNet.SafetyNetApi.verifyWithRecaptcha(mGoogleApiClient, SiteKey)
                    .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                        @Override
                        public void onResult(SafetyNetApi.RecaptchaTokenResult result) {
                            Status status = result.getStatus();

                            if ((status != null) && status.isSuccess()) {

                                tvResult.setTextColor(getResources().getColor(R.color.colorBlack));
                                tvResult.setText("Passed CAPTCHA\n");
                                passCaptcha = true;
                                // Indicates communication with reCAPTCHA service was
                                // successful. Use result.getTokenResult() to get the
                                // user response token if the user has completed
                                // the CAPTCHA.

                                if (!result.getTokenResult().isEmpty()) {
                                    //tvResult.append("!result.getTokenResult().isEmpty()");
                                    // User response token must be validated using the
                                    // reCAPTCHA site verify API.
                                }else{
                                    //tvResult.append("result.getTokenResult().isEmpty()");
                                }
                            } else {

                                Log.e("MY_APP_TAG", "Error occurred " +
                                        "when communicating with the reCAPTCHA service.");

                                tvResult.setTextColor(getResources().getColor(R.color.colorWarning));

                                tvResult.setText("Error occurred when communicating with the reCAPTCHA service.");
                                // Use status.getStatusCode() to determine the exact
                                // error code. Use this code in conjunction with the
                                // information in the "Handling communication errors"
                                // section of this document to take appropriate action
                                // in your app.
                            }
                        }
                    });

        }
    };


    //onStart may not be needed for Sign Up
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
    public void onConnected(@Nullable Bundle bundle) {
        //Toast.makeText(this, R.string.passCaptcha, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended: " + i, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,R.string.noPassCaptcha, Toast.LENGTH_LONG).show();
    }
}//End Activity
