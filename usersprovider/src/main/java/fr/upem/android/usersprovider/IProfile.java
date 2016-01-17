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


package fr.upem.android.usersprovider;

/**
 * Created by mattia on 21/11/15.
 */

import org.json.JSONObject;

/**
 * Interface to be implemented by user profile classes. It allows decoration.
 */
public interface IProfile {

    //Returns user data in Json format
    JSONObject getData();

    String getName();

    String getSurname();

    String getPhoneNumber();

    String getBirthDate();

    String getEmail();

    int getImageId();

    int getDbId();

    int getUID();
}
