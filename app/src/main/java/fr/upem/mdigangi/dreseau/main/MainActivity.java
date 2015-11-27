package fr.upem.mdigangi.dreseau.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.wifidirect.R;
import com.example.android.wifidirect.WiFiDirectActivity;

import fr.upem.mdigangi.dreseau.users.MyProfileActivity;


public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void wifiConnect(View view) {
        Intent intent = new Intent(this, WiFiDirectActivity.class);
        startActivity(intent);
    }

    public void myProfile(View view) {
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }

}
