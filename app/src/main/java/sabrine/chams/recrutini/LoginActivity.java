package sabrine.chams.recrutini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class LoginActivity extends AppCompatActivity {

    ConstraintLayout loginPage ;
    DrawerLayout loginPageDr;
    ActionBarDrawerToggle mToggle;
    TextInputEditText email;
    TextInputEditText password;
    TextView goRegiter;
    Button confirmLogin;
    SharedPreferences sharedPreferences;
    public static final String pref = "user_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(pref, Context.MODE_PRIVATE);
        final Intent homeActivity = new Intent( getApplicationContext(), HomeActivity.class);
        if (sharedPreferences.contains("id")) {
            homeActivity.putExtra("login","true");
            startActivity(homeActivity);
        }
        email = findViewById(R.id.email);
        loginPage = findViewById(R.id.login_page);
        loginPageDr = findViewById(R.id.login_page_dr);
        mToggle = new ActionBarDrawerToggle(this, loginPageDr, R.string.open, R.string.close);
        loginPageDr.addDrawerListener(mToggle);
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
                loginPageDr.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        password = findViewById(R.id.password);
        confirmLogin = findViewById(R.id.confirm_login);
        confirmLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDigest md = null ;
                try {
                     md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String emailText = email.getText().toString();
                final String passwordText = new BigInteger(1,md.digest(password.getText().toString().getBytes())).toString(16);
                StringRequest postRequest = new StringRequest(Request.Method.POST, "https://sabrine-chams.alwaysdata.net/login.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject user = null;
                                try {
                                    user = new JSONObject(response);
                                    String userExists = user.getString("user_exists");
                                    String validUser = user.getString("valid_user");
                                    if ( userExists.equals("1") && validUser.equals("1") )
                                    {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("id", user.getString("id_societe"));
                                        editor.commit();
                                        homeActivity.putExtra("login", "true");
                                        startActivity(homeActivity);
                                    }
                                    else if (userExists.equals("1") && validUser.equals("0")){
                                        password.setError("Wrong password ! Try again");
                                        password.setText(null);
                                    }
                                    else if (userExists.equals("0") && validUser.equals("0")) {
                                        email.setError("User not found !");
                                        password.setText(null);
                                        email.setText(null);
                                    }
                                    else {
                                        Snackbar.make(loginPage ," Login failed !" , Snackbar.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Snackbar.make(loginPage ," parsing Json failed !" , Snackbar.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Snackbar.make(loginPage ," Login failed !" , Snackbar.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String,String>();
                        params.put("email" , emailText);
                        params.put("password" , passwordText);
                        return params ;
                    }
                } ;
                queue.add(postRequest);
            }
        });
        goRegiter = findViewById(R.id.go_to_register);
        goRegiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent( getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivity);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
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
