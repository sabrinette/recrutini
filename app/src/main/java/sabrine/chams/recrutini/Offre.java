package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Offre extends AppCompatActivity {

    private static final int REQUEST_CALL =1;
    TextView name;
    TextView type;
    TextView description;
    TextView address;
    TextView date;
    ConstraintLayout OffrePage ;
    ImageButton mail;
    ImageButton phone;
    int id = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offre);
        name = findViewById(R.id.name);
        type = findViewById(R.id.type);
        description = findViewById(R.id.description);
        address = findViewById(R.id.address);
        mail = findViewById(R.id.mail);
        phone = findViewById(R.id.phone);
        date = findViewById(R.id.date_expiration);
        OffrePage = findViewById(R.id.page_offre);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, "https://sabrine-chams.alwaysdata.net/get_offre_by_id.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray res = null;
                        try {
                            res = new JSONArray(response);
                            JSONObject offre = res.getJSONObject(0);
                            String nom = offre.getString("nom");
                            String adresse = offre.getString("adresse");
                            String type_offre = offre.getString("type");
                            String  description_offre = offre.getString("description");
                            String date_offre = offre.getString("debut_embauche");
                            final String e_mail = offre.getString("email");
                            final String number = offre.getString("num_tel");
                            name.setText(nom);
                            address.setText(adresse);
                            type.setText(type_offre);
                            description.setText(description_offre);
                            date.setText(date_offre);
                            mail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Snackbar.make( OffrePage ," parsing Json  !" , Snackbar.LENGTH_LONG).show();
                                    Intent intent = null ;
                                    intent = new Intent(Intent.ACTION_SENDTO);
                                    intent.setData(Uri.parse("mailto:"+ e_mail));
                                    startActivity(intent);
                                }
                            });

                            phone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    if (number.trim().length()>0) {
                                        if(ContextCompat.checkSelfPermission(Offre.this , Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED){
                                            ActivityCompat.requestPermissions(Offre.this , new String[] {Manifest.permission.CALL_PHONE} , REQUEST_CALL);
                                        }
                                        else {
                                            String dial = "tel:" + number ;
                                            startActivity( new Intent(Intent.ACTION_CALL , Uri.parse(dial))); }

                                    } else {
                                        Snackbar.make( OffrePage ," Invalid phone number !" , Snackbar.LENGTH_LONG).show();
                                    }

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make( OffrePage ," parsing Json failed !" , Snackbar.LENGTH_LONG).show();

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
    }
}