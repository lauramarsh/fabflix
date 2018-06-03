package t69.fabflixandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.HashMap;
import android.widget.TextView;


public class MovieDetailsActivity extends AppCompatActivity {


    TextView textView;
    HashMap<String, String> details = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle bundle = getIntent().getExtras();
        details = (HashMap<String, String>) bundle.get("content");
        populateDetailsMap(details);
    }

    private void populateDetailsMap(HashMap<String, String> hm) {
        String detailString = "Title: " + hm.get("title")
                + "\n\nYear: " + hm.get("year")
                + "\n\nDirector: " + hm.get("director")
                + "\n\nGenres: " + hm.get("genres")
                + "\n\nStars: " + hm.get("stars");
    }
}

