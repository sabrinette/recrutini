package sabrine.chams.recrutini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ProfileSociete extends AppCompatActivity {

    DrawerLayout profilePageDr;
    ActionBarDrawerToggle mToggle;
    TextView name ;
    TextInputEditText adresse ;
    TextInputEditText numero ;
    TextInputEditText pwd ;
    TextInputEditText confirm_pwd ;
    ImageView img;
    Button update ;
    ConstraintLayout profilePage ;
    String id;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_societe);
        profilePageDr = findViewById(R.id.profile_page_dr);
        sharedPreferences = getSharedPreferences(LoginActivity.pref, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("id"))
            id = sharedPreferences.getString("id", null);
        mToggle = new ActionBarDrawerToggle(this, profilePageDr, R.string.open, R.string.close);
        profilePageDr.addDrawerListener(mToggle);
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
                profilePageDr.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img = findViewById(R.id.profile_img);
        name = findViewById(R.id.nom_entreprise);
        adresse = findViewById(R.id.address);
        numero = findViewById(R.id.num_telephone);
        pwd = findViewById(R.id.mot_de_passe);
        confirm_pwd = findViewById(R.id.confirm_mot_de_passe);
        update = findViewById(R.id.update);
        profilePage = findViewById(R.id.profile_page);

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final StringRequest postRequest = new StringRequest(Request.Method.POST, "https://sabrine-chams.alwaysdata.net/get_societe_by_id.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray res = null;
                        try {
                            res = new JSONArray(response);
                            JSONObject societe = res.getJSONObject(0);
                            String nom_societe = societe.getString("nom");
                            String adresse_societe = societe.getString("adresse");
                            final String number = societe.getString("num_tel");
                            String img_url = societe.getString("img_url");
                            SendHttpRequestTask sh = new SendHttpRequestTask();
                            sh.setImageView(img);
                            sh.execute(img_url);
                            name.setText(nom_societe);
                            adresse.setText(adresse_societe);
                            numero.setText(number);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make( profilePage ," parsing Json failed !" , Snackbar.LENGTH_LONG).show();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }} ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }

        };
        queue.add(postRequest);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                final String nameText = name.getText().toString();
                final String addressText = adresse.getText().toString();
                final String phoneNumberText = numero.getText().toString();
                String passwordText = null ;
                if ( !pwd.getText().toString().equals("")){
                    passwordText = new BigInteger(1, md.digest(pwd.getText().toString().getBytes())).toString(16); }
                final String finalPasswordText = passwordText;
                StringRequest postRequest = new StringRequest(Request.Method.POST, "https://sabrine-chams.alwaysdata.net/update_societe.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject res = null;
                                try {
                                    res = new JSONObject(response);
                                    String success = res.getString("success");
                                    if ( success.equals("1"))
                                    {
                                        Intent homeActivity = new Intent( getApplicationContext(), HomeActivity.class);
                                        startActivity(homeActivity);
                                    }
                                    else if (success.equals("2")){
                                        Snackbar.make(profilePage ," This account already exists !" , Snackbar.LENGTH_LONG).show();
                                    }
                                    else {
                                        Snackbar.make(profilePage ," Registration failed !" , Snackbar.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Snackbar.make(profilePage ," parsing Json failed !" , Snackbar.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Snackbar.make(profilePage ," Registration failed !" , Snackbar.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        if( finalPasswordText != null){
                        params.put("password", finalPasswordText);}
                        params.put("adresse", addressText);
                        params.put("nom", nameText);
                        params.put("num_tel", phoneNumberText);
                        params.put("id", id);
                        return params;
                    }
                };

                if ( confirm_pwd.getText().toString().equals(pwd.getText().toString()) ) {

                    queue.add(postRequest);
                }
                else {confirm_pwd.setError(" Not same password !");}
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        sharedPreferences = getSharedPreferences(LoginActivity.pref, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("id")) {
            getMenuInflater().inflate(R.menu.right_menu, menu);
        }
        return true;
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
}
