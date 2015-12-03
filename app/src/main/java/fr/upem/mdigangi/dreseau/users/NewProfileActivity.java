package fr.upem.mdigangi.dreseau.users;

import android.app.Activity;
import android.os.Bundle;

import com.example.android.wifidirect.R;

public class NewProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
