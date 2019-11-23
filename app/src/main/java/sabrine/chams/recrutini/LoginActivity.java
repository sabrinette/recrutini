package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

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
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

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
