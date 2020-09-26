package in.curies.alpha;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import in.curies.alpha.model.User;

import static in.curies.alpha.utils.Utilities.isOnline;

public class CreateProfileActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private ProgressBar progressBar;
    private TextView textView;
    private Button register;
    private EditText name;
    private EditText number;
    private EditText email;

    private DatabaseReference mDatabase;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.frame_layout);
        frameLayout.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.user_db_progress_bar);
        textView = findViewById(R.id.user_db_progress_text);

        register = findViewById(R.id.user_register);
        name = findViewById(R.id.cpName);
        number = findViewById(R.id.cpNumber);
        email = findViewById(R.id.cpEmail);

        Drawable drawable = null;
        try {
            InputStream ims = getAssets().open("bg.png");
            drawable = Drawable.createFromStream(ims, null);
            drawable.setAlpha(100);
        } catch (IOException ex) {
            Log.d("LoadingImage", "Error reading the image");
        }
        frameLayout.setBackground(drawable);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        number.setText(currentUser.getPhoneNumber());
        getUserData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isOnline()) {
            Intent intent1 = new Intent(CreateProfileActivity.this, NoInternetActivity.class);
            intent1.putExtra("intent", getIntent());
            startActivity(intent1);
            finish();
        }

        mDatabase.child("users").child(currentUser.getPhoneNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            goToDashBoard(user);
                        } else {
                            frameLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void goToDashBoard(User user) {
        Intent intent = new Intent(CreateProfileActivity.this, DashBoardActivity.class);
        intent.putExtra("Db User", user);
        startActivity(intent);
        finish();
    }

    private void getUserData() {

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOnline()){
                    Toast.makeText(CreateProfileActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    register.setClickable(false);
                    if (dataValid()) {
                        progressBar.setVisibility(View.VISIBLE);
                        textView.setText("Registration in progress");
                        textView.setVisibility(View.VISIBLE);
                        registerUser();
                    } else {
                        register.setClickable(true);
                    }
                }
            }
        });

    }

    private void registerUser() {
        String userEmail = email.getText().toString();
        String userName = name.getText().toString();
        String userNumber = number.getText().toString();
        final User user = new User(userName, userEmail, userNumber, "None", false, "", new Date(), null);

        mDatabase.child("users").child(userNumber).setValue(user)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                goToDashBoard(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateProfileActivity.this, "Failed to update on db, Please try again", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                register.setClickable(true);
            }
        });
    }

    private boolean dataValid() {
        String message = "";
        if(name.getText().toString().isEmpty()){
            message = "Name can not be empty";
        } else if(email.getText().toString().isEmpty()){
            message = "Email can not be empty";
        } else if(!(email.getText().toString().contains("@")
                && (email.getText().toString().contains(".")))) {
            message = "Enter valid email";
        }
        if(message.isEmpty()){
            return true;
        }
        Toast.makeText(CreateProfileActivity.this, message, Toast.LENGTH_SHORT).show();
        return false;
    }
}