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

import android.util.Log;

import fr.upem.android.usersprovider.IProfile;

/**
 * A global point of access for user's profile. It's used by MyProfileService and classes which cannot
 * access to services.
 * Created by mattia on 09/12/15.
 */
public class MyProfileHandler {

    private static final String TAG = "MyProfileHandler";
    private static IProfile myProfile = null;


    static void loadProfile(IProfile profile){
        synchronized (TAG) {
            myProfile = profile;
        }
    }

    public static IProfile getMyProfile(){
        Log.d(TAG, myProfile == null ? "null" : myProfile.getData().toString());
        synchronized (TAG){
            return myProfile;
        }
    }

    static void invalidProfile() {
        synchronized (TAG) {
            myProfile = null;
        }
    }

}
