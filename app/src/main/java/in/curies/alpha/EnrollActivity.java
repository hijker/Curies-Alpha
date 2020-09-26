package in.curies.alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.curies.alpha.model.User;

import static in.curies.alpha.utils.Utilities.isOnline;

public class EnrollActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView textView;
    private Spinner spinner;
    private EditText number;
    private EditText coupon;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        spinner = findViewById(R.id.spinner);
        number = findViewById(R.id.number);
        coupon = findViewById(R.id.coupon);

        Drawable drawable = null;
        try {
            InputStream ims = getAssets().open("bg.png");
            drawable = Drawable.createFromStream(ims, null);
            drawable.setAlpha(100);
        } catch (IOException ex) {
            Log.d("LoadingImage", "Error reading the image");
        }
        findViewById(R.id.relativeLayout).setBackground(drawable);


        String[] items = new String[]{"Trail", "Class X", "Class XI", "Class XII"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        Intent intent = getIntent();
        final User user = (User) intent.getSerializableExtra("Db User");

        number.setText(user.getPhoneNumber());

        progressBar = findViewById(R.id.enrollment_progress_bar);
        textView = findViewById(R.id.enrollment_progress_text);

        register = findViewById(R.id.enroll);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline()) {
                    Toast.makeText(EnrollActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    register.setClickable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setText("Registration in progress");
                    textView.setVisibility(View.VISIBLE);
                    enrollUser();
                }
            }
    });

}

    private void enrollUser() {
        String course = spinner.getSelectedItem().toString();
        String phoneNumber = number.getText().toString();
        String couponCode = coupon.getText().toString();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("course", course);
        childUpdates.put("couponCode", couponCode);
        childUpdates.put("enrollmentDate", new Date());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child(phoneNumber)
                .updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EnrollActivity.this, "Registration Success full", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EnrollActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EnrollActivity.this, "Failed to enroll please try again", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                register.setClickable(true);
            }
        });
    }
}