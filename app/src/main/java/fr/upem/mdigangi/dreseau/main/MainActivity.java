package fr.upem.mdigangi.dreseau.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.wifidirect.R;
import com.example.android.wifidirect.WiFiDirectActivity;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.DBService;
import fr.upem.mdigangi.dreseau.users.BasicProfile;
import fr.upem.mdigangi.dreseau.users.MyFriendsActivity;
import fr.upem.mdigangi.dreseau.users.MyProfileActivity;
import fr.upem.mdigangi.dreseau.users.NewProfileActivity;


public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IProfile profile = new BasicProfile("mattia", "DiGangi", "wefwe", "we+fwe", "wòegg", 8);
        DBService.startActionInsert(this, profile.getData());
    }

    public void wifiConnect(View view) {
        Intent intent = new Intent(this, WiFiDirectActivity.class);
        startActivity(intent);
    }

    public void myProfile(View view) {
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }


    public void editProfile(View view) {
        Intent intent = new Intent(this, NewProfileActivity.class);
        startActivity(intent);
    }

    public void friends(View view) {
        Intent intent = new Intent(this, MyFriendsActivity.class);
        startActivity(intent);
    }

    //TODO Exchange profiles between users


}
