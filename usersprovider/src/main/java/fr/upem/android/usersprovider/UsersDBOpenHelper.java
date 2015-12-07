package fr.upem.android.usersprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
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
                    FriendEntry.COLUMN_TIMESTAMP + DATE_TYPE +
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
