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
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.example.android.wifidirect.R;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.MyProfileService;


/**
 * Activity which shows user's profile
 */
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

