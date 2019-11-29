package sabrine.chams.recrutini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout homePage;
    ActionBarDrawerToggle mToggle;
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
        mToggle = new ActionBarDrawerToggle(this, homePage, R.string.open, R.string.close);
        homePage.addDrawerListener(mToggle);
        mToggle.syncState();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_login:
                    {
                        Intent loginActivity = new Intent( getApplicationContext(), LoginActivity.class);
                        startActivity(loginActivity);
                    }
                    break;
                    case R.id.nav_Register:
                    {
                        Intent registerrActivity = new Intent( getApplicationContext(), RegisterActivity.class);
                        startActivity(registerrActivity);
                    }
                    break;
                    case R.id.nav_offer_lst:
                    {
                        Intent homeActivity = new Intent( getApplicationContext(), HomeActivity.class);
                        startActivity(homeActivity);
                    }
                    break;
                }
                homePage.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                            if(res.length() > 0)
                            {
                                for (int i = 0; i < res.length(); i++)
                                {
                                    offerNames[i] = res.getJSONObject(i).getString("nom");
                                    offerDescriptions[i] = res.getJSONObject(i).optString("description");
                                    offerImgsUrls[i] = res.getJSONObject(i).optString("img_url");
                                }
                                ListViewAdapter listViewAdapter = new ListViewAdapter(thisActivity,offerNames,offerDescriptions,offerImgsUrls);
                                lst.setAdapter(listViewAdapter);
                            }
                            else
                            {
                                RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                TextView tv=new TextView(homePage.getContext());
                                tv.setLayoutParams(lparams);
                                tv.setText("No offers");
                                homePage.addView(tv);
                            }
                        } catch (JSONException e) {
                            Snackbar.make(homePage, "JSON parsing filed !", 15000).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        TextView tv=new TextView(homePage.getContext());
                        tv.setLayoutParams(lparams);
                        tv.setText("Server Error");
                        homePage.addView(tv);
                        Snackbar.make(homePage, error.getMessage(), 15000).show();
                    }
                });
        queue.add(jsonRequest);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
}
