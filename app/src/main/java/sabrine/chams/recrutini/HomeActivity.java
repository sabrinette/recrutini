package sabrine.chams.recrutini;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class HomeActivity extends AppCompatActivity {

    ListView lst;
    int[] offerIDs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lst = findViewById(R.id.listview);

    }
}
