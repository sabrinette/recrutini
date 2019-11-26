package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    ConstraintLayout homePage;
    ListView lst;
    String[] offerNames;
    String[] offerDescriptions;
    String[] offerImgsUrls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lst = findViewById(R.id.listview);
        homePage = findViewById(R.id.home_page);
        final Activity thisActivity = this;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(Request.Method.GET, "https://sabrine-chams.alwaysdata.net/get_offre.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray res = null;
                        try {
                            res = new JSONArray(response);
                            offerNames = new String[res.length()];
                            offerDescriptions = new String[res.length()];
                            offerImgsUrls = new String[res.length()];
                            Log.d("res_len", Integer.toString(res.length()));
                            for (int i = 0; i < res.length(); i++)
                            {
                                offerNames[i] = res.getJSONObject(i).getString("nom");
                                offerDescriptions[i] = res.getJSONObject(i).optString("description");
                                offerImgsUrls[i] = res.getJSONObject(i).optString("img_url");
                                Log.d("img_url_ha",res.getJSONObject(i).optString("img_url"));
                            }
                            ListViewAdapter listViewAdapter = new ListViewAdapter(thisActivity,offerNames,offerDescriptions,offerImgsUrls);
                            lst.setAdapter(listViewAdapter);
                            //todo add "no offers" text view to the list view in case of no data returned
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //todo add "connection problems" text view to the list view
                        Snackbar.make(homePage, error.getMessage(), 15000).show();
                    }
                });
        queue.add(jsonRequest);

    }
}
