package fr.upem.mdigangi.dreseau.profiles;

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
    
    public IProfile newProfile(JSONObject json){
        String name;
        String surname;
        String email;
        String phone;
        String birth;
        int imageId;
        int uid;
        try {
            name = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_NAME);
            surname = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME);
            email = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_EMAIL);
            phone = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_PHONE);
            birth = json.getString(UsersDBOpenHelper.FriendEntry.COLUMN_BIRTHDATE);
            imageId = json.getInt(UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID);
            uid = json.getInt(UsersDBOpenHelper.FriendEntry.COLUMN_UID);
        } catch (JSONException e){
            throw new IllegalArgumentException("JSONObject must be a profile!");
        }
        int dbId = 0;
        try {
            dbId = json.getInt(UsersDBOpenHelper.FriendEntry.COLUMN_ID);
        } catch (JSONException e) {
            //this has not a id for the db, it's not severe
            dbId = -1;
        }

        return new BasicProfile(name, surname, phone, birth, email, imageId, dbId, uid);
    }
}
