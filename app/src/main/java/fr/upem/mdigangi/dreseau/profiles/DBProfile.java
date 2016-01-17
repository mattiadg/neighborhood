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

/**
 * A Decorator around an Iprofile which propagates the id in the local db. It is used only by a few classes.
 * Created by mattia on 08/01/16.
 */
public class DBProfile implements IProfile {

    private IProfile profile;


    DBProfile(IProfile profile){
        this.profile = profile;
    }

    @Override
    public JSONObject getData() {
        JSONObject jsonObject = profile.getData();
        try {
            jsonObject.put(UsersDBOpenHelper.FriendEntry.COLUMN_ID, profile.getDbId());
        } catch (JSONException e) {
            throw new IllegalArgumentException("You must pass a vlid profile!");
        }
        return jsonObject;
    }

    @Override
    public String getName() {
        return profile.getName();
    }

    @Override
    public String getSurname() {
        return profile.getSurname();
    }

    @Override
    public String getPhoneNumber() {
        return profile.getPhoneNumber();
    }

    @Override
    public String getBirthDate() {
        return profile.getBirthDate();
    }

    @Override
    public String getEmail() {
        return profile.getEmail();
    }

    @Override
    public int getImageId() {
        return profile.getImageId();
    }

    @Override
    public int getDbId() {
        return profile.getDbId();
    }

    @Override
    public String toString() {
        return profile.toString();
    }

    @Override
    public int getUID() { return profile.getUID(); }
}
