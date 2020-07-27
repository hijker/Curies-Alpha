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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.InputStream;

public class FireBaseLogin extends AppCompatActivity {

    EditText email;
    EditText password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Curie");

        Drawable drawable = null;
        try {
            InputStream ims = getAssets().open("icon.jpg");
            drawable = Drawable.createFromStream(ims, null);
            drawable.setAlpha(100);
        }
        catch(IOException ex) {
            Log.d("LoadingImage", "Error reading the image");
        }
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setBackground(drawable);

        findViewById(R.id.offer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FireBaseLogin.this, WebViewActivity.class);
                intent.putExtra("url", "http://curies.in/courses/");
                startActivity(intent);
            }
        });

        findViewById(R.id.about_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FireBaseLogin.this, WebViewActivity.class);
                intent.putExtra("url", "http://curies.in/about/");
                startActivity(intent);
            }
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        final Button signIn = findViewById(R.id.sign_in_button);
        final Button signUp = findViewById(R.id.sign_up_button);

        mAuth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    void signIn(){
        String message = "";
        String emailString = email.getText().toString();
        String passString = password.getText().toString();

        if(emailString.isEmpty()) {
            message = "Email can not be empty";
        } else if(!emailString.contains("@")){
            message = "Invalid email";
        } else if(passString.isEmpty()){
            message = "Password can not be empty";
        } else if(passString.length() < 8) {
            message = "Password has to be at least 8 characters";
        }
        if(!message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(emailString, passString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                goToMainActivity(user);
                            } else {
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(FireBaseLogin.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void goToMainActivity(FirebaseUser user) {
        FirebaseMessaging.getInstance().subscribeToTopic("tests")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Success";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }
                        Log.d("TAG", msg);
                        Toast.makeText(FireBaseLogin.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        Intent intent = new Intent(FireBaseLogin.this, MainActivity.class);
        intent.putExtra("Logged In User", user);
        startActivity(intent);
        finish();
    }

    void signUp(){
        String message = "";
        String emailString = email.getText().toString();
        String passString = password.getText().toString();

        if(emailString.isEmpty()) {
            message = "Email can not be empty";
        } else if(!emailString.contains("@")){
            message = "Invalid email";
        } else if(passString.isEmpty()){
            message = "Password can not be empty";
        } else if(passString.length() < 8) {
            message = "Password has to be at least 8 characters";
        }
        if(!message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(emailString, passString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                goToMainActivity(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(FireBaseLogin.this, "Sign Up failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            goToMainActivity(currentUser);
        }
    }
}