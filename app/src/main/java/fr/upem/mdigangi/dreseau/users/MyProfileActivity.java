package fr.upem.mdigangi.dreseau.users;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.android.wifidirect.R;

import org.json.JSONException;

import java.io.IOException;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.MyProfileService;

public class MyProfileActivity extends Activity implements ProfileFragment.ProfileFragmentListener {

    private static String TAG = "MyProfileActivity";

    private IProfile my_profile;
    private MyProfileService service;
    private boolean bound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MyProfileService.MyProfileBinder myBinder = (MyProfileService.MyProfileBinder) binder;
            service = myBinder.getProfileProvider();
            bound = true;
            showProfile();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
            bound = false;
        }
    }

    @Override
    public void itemClicked(long id) {
        //TODO
    }

    private void showProfile() {
        my_profile = service.getMyProfile();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment newFragment = ProfileFragment.newInstance(my_profile);
        ft.add(R.id.profFragmentContainer, newFragment);
        ft.commit();
    }
}

