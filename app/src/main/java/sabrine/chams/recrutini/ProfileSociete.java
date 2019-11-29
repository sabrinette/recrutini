package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileSociete extends AppCompatActivity {

    TextView name ;
    TextInputLayout adresse ;
    TextInputLayout numero ;
    TextInputLayout pwd ;
    TextInputLayout confirm_pwd ;
    Button update ;
    ConstraintLayout profilePage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_societe);
        name = findViewById(R.id.nom_entreprise);
        adresse = findViewById(R.id.address);
        numero = findViewById(R.id.num_telephone);
        pwd = findViewById(R.id.num_telephone);
        confirm_pwd = findViewById(R.id.confirm_mot_de_passe);
        update = findViewById(R.id.update);
        profilePage = findViewById(R.id.profile_page);

    }
}
