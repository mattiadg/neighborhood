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
 * Provides sql operations for the database.
 * Created by mattia on 27/11/15.
 */
public class UsersDB {

    private Context context;

    public UsersDB(Context context) {
        this.context = context;
    }

    public void addUser(IProfile user) throws IOException {
        ContentValues values = packProfile(user);

        // Insert the new row, returning the primary key value of the new row
        context.getContentResolver().insert(
                UsersProvider.CONTENT_URI, values);
    }

    public Cursor readOne(int uid) {
        String[] projection = {
                UsersDBOpenHelper.FriendEntry.COLUMN_ID,
                UsersDBOpenHelper.FriendEntry.COLUMN_NAME,
                UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME,
                UsersDBOpenHelper.FriendEntry.COLUMN_BIRTHDATE,
                UsersDBOpenHelper.FriendEntry.COLUMN_EMAIL,
                UsersDBOpenHelper.FriendEntry.COLUMN_PHONE,
                UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID,
        };
        String select = UsersDBOpenHelper.FriendEntry.COLUMN_UID + "=?";
        String[] selectArgs = {String.valueOf(uid)};
        Cursor cursor = context.getContentResolver().query(UsersProvider.CONTENT_URI, projection, select, selectArgs, null);
        if (cursor == null) {
            throw new IndexOutOfBoundsException("The database is empty!");
        }
        return cursor;
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
                UsersDBOpenHelper.FriendEntry.COLUMN_UID,
        };

        Cursor cursor = context.getContentResolver().query(UsersProvider.CONTENT_URI, projection, null, null, null);
        if (cursor == null) {
            throw new IndexOutOfBoundsException("The database is empty!");
        }
        return cursor;
    }

    public void updateOne(IProfile profile) {
        ContentValues values = packProfile(profile);
        String where = UsersDBOpenHelper.FriendEntry.COLUMN_UID + "=?";
        String[] whereArgs = {String.valueOf(profile.getUID())};
        context.getContentResolver().update(UsersProvider.CONTENT_URI, values, where, whereArgs);
    }

    public int deleteOne(IProfile profile){
        String[] whereArgs = {String.valueOf(profile.getDbId())};
        return context.getContentResolver().delete(UsersProvider.CONTENT_URI,
                UsersDBOpenHelper.FriendEntry.COLUMN_ID + "=?", whereArgs);
    }

    private ContentValues packProfile(IProfile profile){
        JSONObject userJson = profile.getData();
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
            values.put(UsersDBOpenHelper.FriendEntry.COLUMN_UID, userJson.getInt(UsersDBOpenHelper.FriendEntry.COLUMN_UID));
        } catch (JSONException e) {
            throw new IllegalStateException("User cannot be converted to Json!");
        }
        return values;
    }
}

