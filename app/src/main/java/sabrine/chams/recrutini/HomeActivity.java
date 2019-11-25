package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.os.Bundle;
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
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, "http://sabrine-chams.alwaysdata.net/get_offre.php", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int i = 0;
                        for (Iterator it = response.keys(); it.hasNext();)
                        {
                            try {
                                offerNames[i] = response.optJSONObject((String)it.next()).getString("nom");
                                offerDescriptions[i] = response.optJSONObject((String)it.next()).getString("description");
                                offerImgsUrls[i] = response.optJSONObject((String)it.next()).getString("img_url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ListViewAdapter listViewAdapter = new ListViewAdapter(thisActivity,offerNames,offerDescriptions,offerImgsUrls);
                            lst.setAdapter(listViewAdapter);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(homePage, "Cannot get Offers from server", Snackbar.LENGTH_LONG).show();
                    }
                });
        queue.add(jsonRequest);

    }
}
