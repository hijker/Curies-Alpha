package in.curies.alpha;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static in.curies.alpha.utils.Utilities.isOnline;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText number;
    Button getOTP;
    EditText otp;
    Button verify;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth.getCurrentUser() != null) {
            goToCreateProfileActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable drawable = null;
        try {
            InputStream ims = getAssets().open("bg.png");
            drawable = Drawable.createFromStream(ims, null);
            drawable.setAlpha(100);
        } catch (IOException ex) {
            Log.d("LoadingImage", "Error reading the image");
        }
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setBackground(drawable);

        findViewById(R.id.offer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                intent.putExtra("url", "http://curies.in/courses/");
                startActivity(intent);
            }
        });

        findViewById(R.id.about_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                intent.putExtra("url", "http://curies.in/about/");
                startActivity(intent);
            }
        });

        number = findViewById(R.id.number);
        getOTP = findViewById(R.id.get_otp);
        otp = findViewById(R.id.otp);
        verify = findViewById(R.id.verify);

        mAuth = FirebaseAuth.getInstance();

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void goToCreateProfileActivity() {
        FirebaseMessaging.getInstance().subscribeToTopic("registered")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Success registered for : tests";
                        if (!task.isSuccessful()) {
                            msg = "failed registered for : tests";
                        }
                        Log.d("TAG", msg);
                    }
                });
        Intent intent = new Intent(LoginActivity.this, CreateProfileActivity.class);
        startActivity(intent);
        finish();
    }

    void validate() {
        String message = "";
        String phone = number.getText().toString();

        if (phone.isEmpty()) {
            message = "Number can not be empty";
        } else if (phone.length() != 10) {
            message = "Please Enter 10 digit valid number";
        }
        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("TAG", "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(LoginActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();
                    getOTP.setClickable(true);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(LoginActivity.this, "Please try again Later", Toast.LENGTH_SHORT).show();
                } else {
                    getOTP.setClickable(true);
                    Toast.makeText(LoginActivity.this, "Some error occurred, Make sure the number is valid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull final String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);

                getOTP.setText("Resend Otp");

                otp.setVisibility(View.VISIBLE);
                verify.setVisibility(View.VISIBLE);

                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (otp.getText().toString().isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Invalid otp", Toast.LENGTH_SHORT).show();
                        } else {
                            verify.setClickable(false);
                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.getText().toString());
                            signInWithPhoneAuthCredential(credential);
                        }
                    }
                });
            }
        };

        if (!message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            if (!isOnline()) {
                Intent intent1 = new Intent(LoginActivity.this, NoInternetActivity.class);
                intent1.putExtra("intent", getIntent());
                startActivity(intent1);
                finish();
            }
            getOTP.setClickable(false);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91" + phone,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            goToCreateProfileActivity();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "Invalid otp", Toast.LENGTH_SHORT).show();
                                verify.setClickable(true);
                            }
                        }
                    }
                });
    }
}