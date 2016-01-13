package fr.upem.mdigangi.dreseau.db;

import android.util.Log;

import fr.upem.android.usersprovider.IProfile;

/**
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
