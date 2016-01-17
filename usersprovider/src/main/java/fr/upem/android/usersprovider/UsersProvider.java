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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * Content Provider for accessing SQLite database.
 */
public class UsersProvider extends ContentProvider {
    public UsersProvider() {
    }

    public static final Uri CONTENT_URI = Uri.parse("content://fr.upem.android.usersprovider.provider/" + UsersDBOpenHelper.FriendEntry.TABLE_NAME);

    private SQLiteOpenHelper openHelper = new UsersDBOpenHelper(getContext());

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db.delete(UsersDBOpenHelper.FriendEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        openHelper = new UsersDBOpenHelper(getContext());
        openHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(UsersDBOpenHelper.FriendEntry.TABLE_NAME, projection, selection, selectionArgs, null,
                null, sortOrder, null);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        SQLiteDatabase db = openHelper.getWritableDatabase();
        id = db.insert(UsersDBOpenHelper.FriendEntry.TABLE_NAME,
                null, values);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int n = db.update(UsersDBOpenHelper.FriendEntry.TABLE_NAME, values, selection, selectionArgs);
        return n;
    }
}
