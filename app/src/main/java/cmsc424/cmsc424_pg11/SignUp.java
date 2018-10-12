package cmsc424.cmsc424_pg11;

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

import org.w3c.dom.Text;

public class SignUp extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    //CAPTCHA
    private GoogleApiClient mGoogleApiClient;
    final String SiteKey = "6LfFrXQUAAAAAP_5M96S29fUFK3ddsxrOGaiTq6o";
    final String SecretKey  = "6LfFrXQUAAAAAIzzjOSE4posQ6AeZp7kk7dLiyA7";
    private boolean passCaptcha = false;

    Button btnRequest;
    TextView tvResult;
    CheckBox acceptTerms;
    Button signUpButton;
    EditText name;
    EditText email;
    EditText password1;
    EditText passwword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Buttons and EditTexts
        signUpButton = findViewById(R.id.signUpButton);
        name = findViewById(R.id.editTextEmail);
        email = findViewById(R.id.editTextName);
        password1 = findViewById(R.id.editTextPassword1);
        passwword2 = findViewById(R.id.editTextPassword2);


        //CAPTCHA init
        tvResult = findViewById(R.id.result);
        btnRequest = findViewById(R.id.request);
        btnRequest.setOnClickListener(RqsOnClickListener);

        //Terms and Services
        acceptTerms = findViewById(R.id.acceptTerms);
        acceptTerms.setChecked(false);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(SignUp.this)
                .addOnConnectionFailedListener(SignUp.this)
                .build();

        mGoogleApiClient.connect();



    }//End onCreate


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

                                tvResult.setText("Error occurred " +
                                        "when communicating with the reCAPTCHA service.");
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
