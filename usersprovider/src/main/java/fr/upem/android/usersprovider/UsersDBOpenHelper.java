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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Helper class for managing a SQLite DB. It defines all the costants containing the names of fields,
 * of db and its version.
 * Created by mattia on 27/11/15.
 */
public class UsersDBOpenHelper extends SQLiteOpenHelper{

    public static final String DB_NAME = "friends.db";
    public static final int VERSION = 1;

    public static abstract class FriendEntry implements BaseColumns {
        public static final String TABLE_NAME = "friends";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SURNAME = "surname";
        public static final String COLUMN_BIRTHDATE = "birthdate";
        public static final String COLUMN_PHONE = "phone_number";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_IMAGE_ID = "imageid";
        public static final String COLUMN_TIMESTAMP = "last_update";
        public static final String COLUMN_UID = "uid";
    }

    private static final String TEXT_TYPE = " Text";
    private static final String INT_TYPE = " integer";
    private static final String DATE_TYPE = " date";
    private static final String COMMA_SEP = ",\n";
    private static final String NOT_NULL = " not null";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FriendEntry.TABLE_NAME + " (" +
                    FriendEntry.COLUMN_ID + " Integer Primary Key" + COMMA_SEP +
                    FriendEntry.COLUMN_NAME + TEXT_TYPE + NOT_NULL +COMMA_SEP +
                    FriendEntry.COLUMN_SURNAME + TEXT_TYPE + NOT_NULL +COMMA_SEP +
                    FriendEntry.COLUMN_BIRTHDATE + TEXT_TYPE + COMMA_SEP +
                    FriendEntry.COLUMN_PHONE + TEXT_TYPE + COMMA_SEP +
                    FriendEntry.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    FriendEntry.COLUMN_IMAGE_ID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    FriendEntry.COLUMN_TIMESTAMP + DATE_TYPE + COMMA_SEP +
                    FriendEntry.COLUMN_UID + INT_TYPE + NOT_NULL +
                    " );";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FriendEntry.TABLE_NAME;


    public UsersDBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
