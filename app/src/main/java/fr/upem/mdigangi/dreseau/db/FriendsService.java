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
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;
import fr.upem.mdigangi.dreseau.profiles.DBProfileFactory;
import fr.upem.mdigangi.dreseau.profiles.IProfileFactory;
import fr.upem.mdigangi.dreseau.users.UsersDB;

/**
 * A bounded service which provides an easy interface to communicate with the database.
 */
public class FriendsService extends Service {

    public interface FriendsServiceListener {
        FriendsService getFriendsService();
    }


    //Class that effectively communicates with the DB. If changes are needed we can evolve it into
    //a strategy pattern
    private UsersDB db = null;
    //Factory version
    private IProfileFactory dbFactory = new DBProfileFactory();

    public class FriendsServiceBinder extends Binder {

        public FriendsService getFriendsService() {
            return FriendsService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(db == null){
            db = new UsersDB(getApplicationContext());
        }
        return new FriendsServiceBinder();
    }


    /**
     * Insert a new Profile in the db. If the profile already exists it is updated
     * @param profile The profile to add
     * @return true whether a new profile has been added
     * @throws IOException if there are problems reading the db
     * @throws NullPointerException if profile is null
     */
    public boolean insertProfile(IProfile profile) throws IOException {
        if(profile == null){
            throw new NullPointerException();
        }
        Cursor cursor = db.readOne(profile.getUID());
        if((cursor != null) && (cursor.getCount() > 0)){
            db.updateOne(profile);
        } else {
            db.addUser(profile);
            return true;
        }
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        return false;
    }

    public List<IProfile> getAllFriends() throws JSONException {
        List<IProfile> list = new LinkedList<>();
        Cursor cursor = db.readAll();
        //Moving cursor content into list

        int columnCount = cursor.getColumnCount();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            JSONObject jsonTranslation = new JSONObject();
            for(int i = 0; i < columnCount; i++){
                String columnName = cursor.getColumnName(i);
                try {
                    jsonTranslation.put(columnName, cursor.getString(i));
                } catch (JSONException e) {
                    Log.e("FriendsService.Friends", "Strange JSON error");
                    continue;
                }
            }
            list.add(dbFactory.newProfile(jsonTranslation));
        }
        cursor.close();
        return list;
    }

    public boolean deleteFriend(IProfile profile){
        return db.deleteOne(profile) > 0;
    }

}
