package sabrine.chams.recrutini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AjoutOffre extends AppCompatActivity {

    DrawerLayout offerPageDr;
    ActionBarDrawerToggle mToggle;
    ConstraintLayout offerPage ;
    TextInputEditText email;
    TextInputEditText offer;
    TextInputEditText dateEmbauche;
    TextInputEditText phoneNumber;
    TextInputEditText description;
    RadioGroup type ;
    Button confirmAjout ;
    String id;
    Calendar myCalendar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_offre);
        sharedPreferences = getSharedPreferences(LoginActivity.pref, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("id"))
            id = sharedPreferences.getString("id", null);
        offerPageDr = findViewById(R.id.offer_page_dr);
        mToggle = new ActionBarDrawerToggle(this, offerPageDr, R.string.open, R.string.close);
        offerPageDr.addDrawerListener(mToggle);
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
                offerPageDr.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        email = findViewById(R.id.email);
        offer = findViewById(R.id.nom_offre);
        dateEmbauche = findViewById(R.id.date_embauche);
        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        dateEmbauche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AjoutOffre.this, date, myCalendar.get(Calendar.YEAR)
                        ,myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        phoneNumber = findViewById(R.id.num_tel);
        description= findViewById(R.id.description);
        offerPage = findViewById(R.id.offer_page);
        type = findViewById(R.id.type);
        confirmAjout = findViewById(R.id.confirm_ajout);
        confirmAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                final String emailText = email.getText().toString();
                final String offerText = offer.getText().toString();
                final String descriptionText = description.getText().toString();
                final String phoneNumberText = phoneNumber.getText().toString();
                final String dateEmbaucheText = dateEmbauche.getText().toString();
                RadioButton  btn = (RadioButton) type.getChildAt(type.indexOfChild(findViewById(type.getCheckedRadioButtonId()))) ;
                final String typeText = btn.getText().toString();
                StringRequest postRequest = new StringRequest(Request.Method.POST, "https://sabrine-chams.alwaysdata.net/insert_offre.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    JSONObject res = null;
                                    try {
                                        res = new JSONObject(response);
                                        String success = res.getString("success");
                                        if ( success.equals("1"))
                                        {
                                            Intent loginActivity = new Intent( getApplicationContext(), LoginActivity.class);
                                            startActivity(loginActivity);
                                        }
                                        else {
                                            Log.d("toute la requete", response);
                                            Snackbar.make(offerPage ," Addition failed !!" , Snackbar.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Snackbar.make(offerPage ," parsing Json failed !" , Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Snackbar.make(offerPage," Addition failed !" , Snackbar.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("email", emailText);
                            params.put("nom", offerText);
                            params.put("description", descriptionText);
                            params.put("debut_embauche", dateEmbaucheText);
                            params.put("num_tel", phoneNumberText);
                            params.put("type", typeText);
                            params.put("id_societe", id);
                            return params;
                        }
                    };
                    queue.add(postRequest);
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
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEmbauche.setText(sdf.format(myCalendar.getTime()));
    }
}
