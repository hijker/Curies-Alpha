package in.curies.alpha;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.io.InputStream;

import static in.curies.alpha.utils.Utilities.isOnline;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        Intent intent = getIntent();
        final Intent intent_to_send = intent.getParcelableExtra("intent");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    startActivity(intent_to_send);
                    finish();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(NoInternetActivity.this, "No Internet Connection. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView imageView = findViewById(R.id.no_internet);
        Drawable drawable = null;
        try {
            InputStream ims = getAssets().open("nonet.png");
            drawable = Drawable.createFromStream(ims, null);
        } catch (IOException ex) {
            Log.d("LoadingImage", "Error reading the image");
        }
        imageView.setImageDrawable(drawable);
    }
}