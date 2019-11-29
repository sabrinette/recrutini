package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

    TextView name ;
    TextInputEditText adresse ;
    TextInputEditText numero ;
    TextInputEditText pwd ;
    TextInputEditText confirm_pwd ;
    Button update ;
    ConstraintLayout profilePage ;
    int id = 3 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_societe);
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
                            JSONObject offre = res.getJSONObject(0);
                            String nom_societe = offre.getString("nom");
                            String adresse_societe = offre.getString("adresse");
                            final String number = offre.getString("num_tel");
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
                params.put("id", Integer.toString(id));
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
                                        Intent loginActivity = new Intent( getApplicationContext(), LoginActivity.class);
                                        startActivity(loginActivity);
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
                        params.put("id", Integer.toString(id));
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
}
