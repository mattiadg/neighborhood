package fr.upem.mdigangi.dreseau.users;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.List;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.FriendsService;

public class FriendsListActivity extends ListActivity {

    //Used to retrieve friend's list
    private FriendsService service;
    //Indicate wether the activity is bound to the service
    private boolean bound = false;
    //List of profiles to show
    List<IProfile> profiles;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((FriendsService.FriendsServiceBinder) binder).getFriendsService();
            bound = true;
            FriendsListActivity.this.showListView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, FriendsService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    private void showListView() {
        ListView listView = getListView();

        try {
            profiles = service.getAllFriends();
        } catch (JSONException e) {
            Toast.makeText(this, "Your friend's list is corrupted. Closing activity", Toast.LENGTH_LONG);
            throw new IllegalStateException("Friend's list corrupted!");
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profiles);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, FriendsActivity.class);
        intent.putExtra(FriendsActivity.EXTRA_PROFILE_ASJSON, profiles.get(position).getData().toString());
        startActivity(intent);
    }
}
