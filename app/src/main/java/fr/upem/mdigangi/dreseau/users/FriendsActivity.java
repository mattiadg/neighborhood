package fr.upem.mdigangi.dreseau.users;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import com.example.android.wifidirect.R;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.FriendsService;
import fr.upem.mdigangi.dreseau.main.MainActivity;
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;

/**
 * Created by mattia on 13/01/16.
 */
public class FriendsActivity extends Activity{

    public static final String EXTRA_PROFILE_ASJSON = "fr.upem.mdigangi.reseau.profile_json";

    private IProfile profile;
    private boolean bound = false;
    private FriendsService service;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            bound = true;
            service = ((FriendsService.FriendsServiceBinder) binder).getFriendsService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        String jsonProfile = getIntent().getStringExtra(EXTRA_PROFILE_ASJSON);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ProfileFragment fragment;
        try {
            profile = new BasicProfileFactory()
                    .newProfile(new JSONObject(jsonProfile));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Not a profile!");
        }
        fragment = ProfileFragment.newInstance(profile);
        ft.add(R.id.profFragmentContainer, fragment);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, FriendsService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    public void removeProfile(View view){
        service.deleteFriend(profile);
        Toast.makeText(this, "Friend removed from database", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, FriendsListActivity.class);
        startActivity(intent);
    }

}
