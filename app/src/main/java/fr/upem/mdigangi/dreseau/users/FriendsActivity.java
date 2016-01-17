/**
 * Neighborhood is an Android app for creating a social network by means
 of WiFiP2P technology.
 Copyright (C) 2016  Di Gangi Mattia Antonino

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;

/**
 * Shows a friend's profile and allow to delete it from db.
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
