package fr.upem.mdigangi.dreseau.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.example.android.wifidirect.R;
import com.example.android.wifidirect.WiFiDirectActivity;

import org.json.JSONException;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.MyProfileService;
import fr.upem.mdigangi.dreseau.users.FriendsListActivity;
import fr.upem.mdigangi.dreseau.users.MyProfileActivity;
import fr.upem.mdigangi.dreseau.users.NewProfileActivity;


public class MainActivity extends Activity{

    private final Object monitor = new Object();
    private MyProfileService service;
    private boolean bound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MyProfileService.MyProfileBinder myBinder = (MyProfileService.MyProfileBinder) binder;
            service = myBinder.getProfileProvider();
            bound = true;
            loadProfile();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void wifiConnect(View view) throws JSONException {
        Intent intent = new Intent(this, WiFiDirectActivity.class);
        //MyProfileSingleton.createProfileService(this);
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
        Intent intent = new Intent(this, FriendsListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyProfileService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
        }
    }

    private void loadProfile() {
        if (bound) {
            IProfile profile = service.getMyProfile();
            if (profile == null) {
                Intent intent1 = new Intent(this, NewProfileActivity.class);
                startActivity(intent1);
            }
        }
    }


}
