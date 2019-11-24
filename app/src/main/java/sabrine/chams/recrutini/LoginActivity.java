package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    TextInputEditText email;
    TextInputEditText password;
    TextView goRegiter;
    Button confirmLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
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
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://sabrine-chams.alwaysdata.net/login.php",
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
                                        int id = Integer.parseInt(user.getString("id_societe"));
                                        Intent registerActivity = new Intent( getApplicationContext(), RegisterActivity.class);
                                        startActivity(registerActivity);
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
                                        email.setError("Erreur serveur !");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

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
}
