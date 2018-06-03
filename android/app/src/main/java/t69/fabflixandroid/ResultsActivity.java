package t69.fabflixandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultsActivity extends AppCompatActivity {
    // variables
    String tParam = "";
    int pageNum = 0;
    JSONArray results = null;
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<HashMap<String, String>> movieInfo = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle bundle = getIntent().getExtras();
        tParam = bundle.getString("movieTitle");
        if (tParam != null && !"".equals(tParam)) {
            titleResults(tParam, "1");
        }

        // On tap, go to the individual movie page
        ListView lv = findViewById(R.id.displayList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                // Go to movie details page
                movieDetails(v, i);
            }
        });

        // next page
        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next(v, pageNum + 1);
            }
        });

        // previous page
        Button prev = (Button) findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageNum > 1)
                    prev(v, pageNum - 1);
            }
        });
    }

    // redirects to the specific movie detail page upon tap
    public void movieDetails(View v, int index) {
        Intent redirectMovieDetails = new Intent(this, MovieDetailsActivity.class);
        redirectMovieDetails.putExtra("content", movieInfo.get(index));
        startActivity(redirectMovieDetails);
    }

    public void prev(View view, int pagePrev) {
        titles.clear();
        movieInfo.clear();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                titles);

        ListView lv = findViewById(R.id.displayList);
        lv.setAdapter(adapter);

        titleResults(tParam, String.valueOf(pagePrev));
    }

    public void next(View v, int pageNext) {
        movieInfo.clear();
        titles.clear();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                titles);

        ListView lv = findViewById(R.id.displayList);
        lv.setAdapter(adapter);

        titleResults(tParam, String.valueOf(pageNext));
    }


    public void titleResults(final String title, final String page) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String loginURL = "https://52.15.158.77:8443/project1/api/login";
        try {
            loginURL += "?title=" + URLEncoder.encode(title, "UTF-8") + "&page=" + page;
        } catch (Exception e) {
            return; // there was an error in encoding
        }

        // send request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, loginURL, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    pageNum = response.getInt("pageNum");
                    results = response.getJSONArray("results");
                    displayResults(response);
                } catch (Exception e) {
                    Log.d("ERROR IN PARSING JSON", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("ERROR WITH JSON RET", error.toString());
            }
        });

        queue.add(request);
        return;
    }


    public void displayResults(JSONObject jo) {
        // NAV BUTTONS
        Button prev = findViewById(R.id.prev);
        Button next = findViewById(R.id.next);
        if (pageNum == 1) {
            prev.setVisibility(View.INVISIBLE);
            if (results.length() < 10) {
                next.setVisibility(View.INVISIBLE);
            } else {
                next.setVisibility(View.VISIBLE);
            }
        } else {
            // pageNum > 1
            prev.setVisibility(View.INVISIBLE);
            if (results.length() < 10) {
                next.setVisibility(View.INVISIBLE);
            } else {
                next.setVisibility(View.VISIBLE);
            }
        }

        ListView lv = findViewById(R.id.displayList);

        // build all results lists
        try {
            if (results == null) {
                // do nothing
            } else {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject obj = (JSONObject) results.get(i);

                    titles.add(obj.getString("title")
                            + " (" + obj.getString("year")
                            + ")");
                    movieInfo.add(i, new HashMap<String, String>());
                    movieInfo.get(i).put("id", obj.getString("id"));
                    movieInfo.get(i).put("title", obj.getString("title"));
                    movieInfo.get(i).put("year", obj.getString("year"));
                    movieInfo.get(i).put("director", obj.getString("director"));
                    movieInfo.get(i).put("genres", obj.getString("genres"));
                    movieInfo.get(i).put("stars", obj.getString("stars"));
                    movieInfo.get(i).put("rating", obj.getString("rating"));
                }
            }
        } catch (Exception e) {
            Log.d("ERROR WITH LIST BUILD", e.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, titles);
        lv.setAdapter(adapter);
        return;
    }

}
