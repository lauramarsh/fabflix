package t69.fabflixandroid;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button   login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        NukeSSLCerts.nuke();

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String uParam = username.getText().toString();
                String pParam = password.getText().toString();
                Log.v("username", uParam);
                Log.v("password", pParam);
                connectToTomcat(view, uParam, pParam);
            }
        });

    }

    public void loginSuccess(View view) {
        Intent searchRedirect = new Intent(this, SearchActivity.class);
        startActivity(searchRedirect);
    }

    public void connectToTomcat(final View view, final String username, final String password){

        RequestQueue queue = Volley.newRequestQueue(this);
        String loginURL = "https://52.15.158.77:8443/project1/api/login";
        final Map<String, String> params = new HashMap<String, String>();

        StringRequest postRequest = new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ((TextView)findViewById(R.id.http_response)).setText(response);

                String replaced = response.replaceAll("[{}\"]","");
                String[] responseList = replaced.split(",");
                for (String resp: responseList) {
                    String[] responses = resp.split(":");
                    if (responses[0].toString().equals("status")) {
                        if (responses[1].toString().equals("success")){
                            loginSuccess(view);
                        }
                        break;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("security.error", error.getMessage().toString());
            }
        }){
            @Override
            final protected Map<String, String> getParams()
            {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        // Add request to the Universal RequestQueue.
        queue.add(postRequest);
        return ;
    }
}

