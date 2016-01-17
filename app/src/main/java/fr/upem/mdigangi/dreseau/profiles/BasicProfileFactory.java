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

package fr.upem.mdigangi.dreseau.profiles;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.android.usersprovider.UsersDBOpenHelper;
import fr.upem.android.usersprovider.IProfileFactory;

/**
 * A facility to build a BasicProfile by a JSON file. It's an implementation of IProfileFactory.
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
