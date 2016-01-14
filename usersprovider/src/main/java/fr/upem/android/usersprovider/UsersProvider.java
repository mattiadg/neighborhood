package fr.upem.android.usersprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

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
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        SQLiteDatabase db = openHelper.getWritableDatabase();
        id = db.insert(UsersDBOpenHelper.FriendEntry.TABLE_NAME,
                null, values);
        Log.d("UsersProvider::insert", "Inserimento effettuato, id=" + id);
        return Uri.withAppendedPath(uri, String.valueOf(id));
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
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
