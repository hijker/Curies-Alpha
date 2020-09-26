package in.curies.alpha;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.InputStream;

import in.curies.alpha.model.User;


public class DashBoardActivity extends AppCompatActivity {

    private Button takeExam;
    private Button watchTutorial;
    private Button talkToTutor;
    private Button enroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        final Intent intent = getIntent();
        final User user = (User) intent.getSerializableExtra("Db User");

        // Capture the layout's TextView and set the string as its text
        final TextView textView = findViewById(R.id.textView);
        final TextView currentCourse = findViewById(R.id.current_course);
        textView.setText("Welcome : " + user.getUsername().split(" ", 2)[0]);
        currentCourse.setText("Course : " + user.getCourse());

        findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("registered")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Unsubscribe Success";
                                if (!task.isSuccessful()) {
                                    msg = "Unsubscribe Failed";
                                }
                                Log.d("TAG", msg);
                                Toast.makeText(DashBoardActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DashBoardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        takeExam = findViewById(R.id.take_exam);
        watchTutorial = findViewById(R.id.tutorials);
        talkToTutor = findViewById(R.id.talk_to_tutor);
        enroll = findViewById(R.id.enroll);

        Drawable drawable = null;
        try {
            InputStream ims = getAssets().open("bg.png");
            drawable = Drawable.createFromStream(ims, null);
            drawable.setAlpha(100);
        } catch (IOException ex) {
            Log.d("LoadingImage", "Error reading the image");
        }
        findViewById(R.id.relativeLayout).setBackground(drawable);


        if (!"None".equals(user.getCourse())) {
            enroll.setVisibility(View.INVISIBLE);
        }

        takeExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DashBoardActivity.this, ExamDashBoardActivity.class);
                intent1.putExtra("Db User", user);
                startActivity(intent1);
            }
        });

        watchTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DashBoardActivity.this, TutorialBoardActivity.class);
                intent1.putExtra("Db User", user);
                startActivity(intent1);
            }
        });

        talkToTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DashBoardActivity.this, ChatActivity.class);
                intent1.putExtra("Db User", user);
                startActivity(intent1);
            }
        });

        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DashBoardActivity.this, EnrollActivity.class);
                intent1.putExtra("Db User", user);
                startActivity(intent1);
                finish();
            }
        });


    }
}
