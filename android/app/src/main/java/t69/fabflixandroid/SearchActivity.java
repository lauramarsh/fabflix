package t69.fabflixandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.content.Intent;



public class SearchActivity extends AppCompatActivity {

    EditText movie;
    Button   search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        movie = (EditText) findViewById(R.id.movie);
        search = (Button)findViewById(R.id.search);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch(view, movie.getText().toString());
            }
        });

    }

    public void performSearch(View view, String movie) {
        Intent searchResults = new Intent(this, ResultsActivity.class);
        searchResults.putExtra("movie", movie);
        startActivity(searchResults);
    }
}
