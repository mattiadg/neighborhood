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
 * A basic implementation of a profile. It's an immutable class whit the only aim of storing data
 * and converting them to JSON.
 * Created by mattia on 21/11/15.
 */
public class BasicProfile implements IProfile {

    private final String name;
    private final String surname;
    private final String phoneNumber;
    private final String birthDate;
    private final String email;
    private final int imageId;
    private final int dbId;
    //Unique global identifier
    private final int UID;

    public BasicProfile(String name, String surname, String phoneNumber, String birthDate, String email, int imageId, int dbId, int UID) {
        this.dbId = dbId;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.email = email;
        this.imageId = imageId;
        this.UID = UID;
    }

    @Override
    public JSONObject getData() {
        JSONObject sb = new JSONObject();
        try {
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_NAME, name);
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME, surname);
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_PHONE, phoneNumber );
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_BIRTHDATE, birthDate);
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_EMAIL, email);
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID, imageId);
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_UID, UID);
        } catch (JSONException e) {
            throw new IllegalStateException("Profile cannot be transformed in JSON!");
        }

        return sb;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public int getImageId() {
        return imageId;
    }

    public int getDbId() {
        return dbId;
    }

    public int getUID() { return UID; }

    @Override
    public String toString() {
        return name + " " + surname + " " + phoneNumber;
    }

    /*
    public static void main(String[] args){
        BasicProfile bp = new BasicProfile("Mattia", "Di Gangi", "0786380225",
                "16/05/1992", "mattiadigangi@gmail.com", R.drawable.default_image);
        System.out.println(bp.getData());
    }
    */
}
