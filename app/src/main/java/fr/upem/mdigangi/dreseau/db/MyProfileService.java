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

package fr.upem.mdigangi.dreseau.db;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;
import fr.upem.mdigangi.dreseau.profiles.IProfileFactory;

/**
 * A bounded service which provides an easy interface to work on the user's profile.
 *
 * @author mattia
 */
public class MyProfileService extends Service {

    //My profile filename
    private static final String MY_PROFILE_FILE_NAME = "my_profile.txt";

    private IProfileFactory profileFactory = new BasicProfileFactory();
    private MyProfileBinder binder = new MyProfileBinder();

    public class MyProfileBinder extends Binder {
        public MyProfileService getProfileProvider() {
            return MyProfileService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     *
     * @return user's profile if it exists, or null elsewhere
     * @throws IllegalStateException If profile does not exist or the file is corrupted
     */
    public IProfile getMyProfile() {
        //If the profile is loaded, return that one
        IProfile myProfile = MyProfileHandler.getMyProfile();
        if(myProfile != null){
            return myProfile;
        }
        //Else load it
        try {
            myProfile = loadProfileFromFile();
        } catch (IOException e) {
            return null;
        }
        MyProfileHandler.loadProfile(myProfile);
        return myProfile;
    }

    private IProfile loadProfileFromFile() throws IOException {
        String str = "";
        FileInputStream is = openFileInput(MY_PROFILE_FILE_NAME);
        StringBuffer buf = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        if (is != null) {
            while ((str = reader.readLine()) != null) {
                buf.append(str + "\n");
            }
        }
        try {
            return profileFactory.newProfile(new JSONObject(buf.toString()));
        } catch (JSONException e) {
            return null;
        }
    }

    public void insertMyProfile(JSONObject jsonProfile) {
        try {
            FileOutputStream fos = openFileOutput(MY_PROFILE_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(jsonProfile.toString().getBytes());
            fos.close();
            Log.d("MyProfileService", jsonProfile.toString());
        } catch (IOException e){
            Toast.makeText(getApplicationContext(), "Impossible reading file!", Toast.LENGTH_LONG).show();
        }
        MyProfileHandler.invalidProfile();
        try {
            MyProfileHandler.loadProfile(loadProfileFromFile());
        } catch (IOException e) {
            throw new IllegalStateException("Profile file corrupted!");
        }
    }
}
