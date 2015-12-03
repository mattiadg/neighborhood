package fr.upem.mdigangi.dreseau.users;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.android.usersprovider.UsersDBOpenHelper;

/**
 * Created by mattia on 30/11/15.
 */
public class BasicProfileFactory implements IProfileFactory {

    private static String TAG = "BasicProfileFactory";
    
    public IProfile newProfile(JSONObject json) throws JSONException {
        String name = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_NAME);
        Log.d(TAG, name);
        String surname = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME);
        Log.d(TAG, surname);
        String email = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_EMAIL);
        Log.d(TAG, email);
        String phone = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_PHONE);
        Log.d(TAG, phone);
        String birth = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_BIRTHDATE);
        Log.d(TAG, birth);
        int imageId = json.getInt(UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID);

        return new BasicProfile(name, surname, phone, birth, email, imageId);
    }
}
