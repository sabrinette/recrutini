package sabrine.chams.recrutini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    String[] offerIDs;
    String   company_id = "null";
    String   type = "null";
    ListViewAdapter listViewAdapter;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homePage = findViewById(R.id.home_page);
        sharedPreferences = getSharedPreferences(LoginActivity.pref, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("id")) {
            company_id = sharedPreferences.getString("id", null);
        }
        Intent prevIntent = getIntent();
        type = prevIntent.getStringExtra("type");
        if(type == null)
            type = "null";
        if (prevIntent.getStringExtra("login") != null)
            Snackbar.make(homePage,"You are loged in",Snackbar.LENGTH_LONG).show();
        lst = findViewById(R.id.listview);
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

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, "https://sabrine-chams.alwaysdata.net/get_offre.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("reponse complete",response);
                        JSONArray res = null;
                        try {
                            res = new JSONArray(response);
                            offerNames = new String[res.length()];
                            offerDescriptions = new String[res.length()];
                            offerImgsUrls = new String[res.length()];
                            offerIDs = new String[res.length()];
                            if(res.length() > 0)
                            {
                                for (int i = 0; i < res.length(); i++)
                                {
                                    offerNames[i] = res.getJSONObject(i).getString("nom");
                                    offerDescriptions[i] = res.getJSONObject(i).optString("description");
                                    offerImgsUrls[i] = res.getJSONObject(i).optString("img_url");
                                    offerIDs[i] = res.getJSONObject(i).optString("id");
                                }
                                listViewAdapter = new ListViewAdapter(thisActivity,offerNames,offerDescriptions,offerImgsUrls,offerIDs);
                                lst.setAdapter(listViewAdapter);
                            }
                            else
                            {
                                RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                TextView tv=new TextView(homePage.getContext());
                                tv.setLayoutParams(lparams);
                                tv.setText("No offers");
                                lst.setEmptyView(tv);
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
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (type != "null")
                    params.put("type", type);
                Log.d("idddd", company_id);
                params.put("id", company_id);
                return params;
            }};
        queue.add(jsonRequest);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView offer_id = view.findViewById(R.id.offer_id);
                Intent offerActivity = new Intent( getApplicationContext(), Offre.class);
                offerActivity.putExtra("id", offer_id.getText().toString());
                startActivity(offerActivity);
            }
        });
        lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                if (sharedPreferences.contains("id")) {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Delete Offer")
                            .setMessage("Do you really want to delete this offer ?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final TextView offer_id = view.findViewById(R.id.offer_id);
                                    StringRequest jsonRequest = new StringRequest(Request.Method.POST, "https://sabrine-chams.alwaysdata.net/delete_offre_by_id.php",
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    JSONObject res = null;
                                                    try {
                                                        res = new JSONObject(response);
                                                        if (res.getString("success").equals("1")) {
                                                            Snackbar.make(homePage, "Offer deleted successfully !", 5000).show();
                                                            listViewAdapter.remove(Integer.toString(position));
                                                            listViewAdapter.notifyDataSetChanged();
                                                        }
                                                        else
                                                            Snackbar.make(homePage, "Error when deleting the Offer", 5000).show();

                                                    } catch (JSONException e) {
                                                        Snackbar.make(homePage, "JSON parsing filed !", 15000).show();
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Snackbar.make(homePage, error.getMessage(), 15000).show();
                                                }
                                            }){
                                        @Override
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("id", offer_id.getText().toString());
                                            return params;
                                        }};
                                    queue.add(jsonRequest);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    public void logout(MenuItem item)
    {
        sharedPreferences = getSharedPreferences(LoginActivity.pref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Intent loginActivity = new Intent( getApplicationContext(), LoginActivity.class);
        startActivity(loginActivity);
    }

    public void profile(MenuItem item)
    {
        Intent profileSociete = new Intent( getApplicationContext(), ProfileSociete.class);
        startActivity(profileSociete);
    }

    public void goToAddOffer(MenuItem item)
    {
        Intent addOffer = new Intent( getApplicationContext(), AjoutOffre.class);
        startActivity(addOffer);
    }

    public void offerLstJob(MenuItem item)
    {
        Intent homeActivity = new Intent( getApplicationContext(), HomeActivity.class);
        homeActivity.putExtra("type", "Job");
        startActivity(homeActivity);
    }
    public void offerLstInternship(MenuItem item)
    {
        Intent homeActivity = new Intent( getApplicationContext(), HomeActivity.class);
        homeActivity.putExtra("type", "Internship");
        startActivity(homeActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        sharedPreferences = getSharedPreferences(LoginActivity.pref, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("id")) {
            getMenuInflater().inflate(R.menu.right_menu, menu);
        }
        return true;
    }
}
