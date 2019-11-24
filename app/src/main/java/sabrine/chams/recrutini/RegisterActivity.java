package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ConstraintLayout registerPage ;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText name;
    TextInputEditText confirmPassword;
    TextInputEditText address;
    TextInputEditText phoneNumber;
    TextView goLogin ;
    Button confirmRegister ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        confirmPassword = findViewById(R.id.confirm_password);
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone_number);
        registerPage = findViewById(R.id.register_page);
        confirmRegister = findViewById(R.id.confirm_register);
        goLogin = findViewById(R.id.go_to_login);
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(getApplicationContext() , LoginActivity.class);
                startActivity(loginActivity);
            }
        });
        confirmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( confirmPassword.getText().toString().equals(password.getText().toString()) ) {
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    final String emailText = email.getText().toString();
                    final String nameText = name.getText().toString();
                    final String addressText = address.getText().toString();
                    final String phoneNumberText = phoneNumber.getText().toString();
                    final String confirmPasswordText = new BigInteger(1, md.digest(confirmPassword.getText().toString().getBytes())).toString(16);
                    final String passwordText = new BigInteger(1, md.digest(password.getText().toString().getBytes())).toString(16);
                    StringRequest postRequest = new StringRequest(Request.Method.POST, "http://sabrine-chams.alwaysdata.net/insert_societe.php",
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
                                            Snackbar.make(registerPage ," This account already exists !" , Snackbar.LENGTH_LONG).show();
                                        }
                                        else {
                                            Snackbar.make(registerPage ," Registration failed !" , Snackbar.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Snackbar.make(registerPage ," parsing Json failed !" , Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Snackbar.make(registerPage ," Registration failed !" , Snackbar.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("email", emailText);
                            params.put("password", passwordText);
                            params.put("adresse", addressText);
                            params.put("nom", nameText);
                            params.put("num_tel", phoneNumberText);
                            return params;
                        }
                    };
                    queue.add(postRequest);
                }
                else {confirmPassword.setError(" Not same password !");}
            }
        });

    }
}
