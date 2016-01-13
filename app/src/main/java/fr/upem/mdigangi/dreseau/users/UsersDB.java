package fr.upem.mdigangi.dreseau.users;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.android.usersprovider.UsersDBOpenHelper;
import fr.upem.android.usersprovider.UsersProvider;

/**
 * Created by mattia on 27/11/15.
 */
public class UsersDB {

    //Result code for DB operations
    public static final int CODE_GET_MY_PROFILE = 1;
    public static final int CODE_READ_ONE_PROFILE = 2;
    public static final int CODE_READ_ALL_PROFILES = 3;
    public static final int CODE_INSERT_PROFILE = 4;
    public static final int CODE_ERROR = -1;

    //Return bundle parameters names
    public static final String EXTRA_MY_PROFILE = "fr.upem.mdigangi.dreseau.extra.my_profile";


    private Context context;

    public UsersDB(Context context) {
        this.context = context;
    }

    public void addUser(IProfile user) throws IOException {

        JSONObject userJson = user.getData();
        ContentValues values = new ContentValues();

        for (Iterator<String> iter = userJson.keys(); iter.hasNext(); ) {
            try {
                String key = iter.next();
                values.put(key, userJson.getString(key));
                Log.d("UsersDB::addUser", key + " : " + userJson.getString(key));
            } catch (JSONException e) {
                throw new IllegalStateException("User cannot be converted to Json!");
            }
        }
        try {
            values.put(UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID, userJson.getInt(UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID));
        } catch (JSONException e) {
            throw new IllegalStateException("User cannot be converted to Json!");
        }

        // Insert the new row, returning the primary key value of the new row
        context.getContentResolver().insert(
                UsersProvider.CONTENT_URI, values);
    }

    public Cursor readAll() throws IndexOutOfBoundsException {
        String[] projection = {
                UsersDBOpenHelper.FriendEntry.COLUMN_ID,
                UsersDBOpenHelper.FriendEntry.COLUMN_NAME,
                UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME,
                UsersDBOpenHelper.FriendEntry.COLUMN_BIRTHDATE,
                UsersDBOpenHelper.FriendEntry.COLUMN_EMAIL,
                UsersDBOpenHelper.FriendEntry.COLUMN_PHONE,
                UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID,
        };

        Cursor cursor = context.getContentResolver().query(UsersProvider.CONTENT_URI, projection, null, null, null);
        if (cursor == null) {
            throw new IndexOutOfBoundsException("The database is empty!");
        }
        return cursor;
    }

    public int deleteOne(IProfile profile){
        String[] whereArgs = {String.valueOf(profile.getDbId())};
        return context.getContentResolver().delete(UsersProvider.CONTENT_URI,
                UsersDBOpenHelper.FriendEntry.COLUMN_ID + "=?", whereArgs);
    }
}

