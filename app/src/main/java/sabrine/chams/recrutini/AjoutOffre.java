package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AjoutOffre extends AppCompatActivity {

    ConstraintLayout offerPage ;
    TextInputEditText email;
    TextInputEditText offer;
    TextInputEditText dateEmbauche;
    TextInputEditText phoneNumber;
    TextInputEditText description;
    RadioGroup type ;
    Button confirmAjout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_offre);
        email = findViewById(R.id.email);
        offer = findViewById(R.id.nom_offre);
        dateEmbauche = findViewById(R.id.date_embauche);
        phoneNumber = findViewById(R.id.phone_number);
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
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://sabrine-chams.alwaysdata.net/insert_offre.php",
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
                                            Snackbar.make(offerPage ," Addition failed !" , Snackbar.LENGTH_LONG).show();
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
                            return params;
                        }
                    };
                    queue.add(postRequest);
            }
        });

    }
}
