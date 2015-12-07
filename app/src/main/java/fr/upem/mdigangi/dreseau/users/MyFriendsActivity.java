package fr.upem.mdigangi.dreseau.users;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import fr.upem.android.usersprovider.UsersDBOpenHelper;

public class MyFriendsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = getListView();
        Cursor friendsCursor = new UsersDB(this).readAll();
        String[] from = {UsersDBOpenHelper.FriendEntry.COLUMN_NAME, UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME};
        CursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, friendsCursor,
                from,
                new int[] { android.R.id.text1, android.R.id.text2},
                0);
        listView.setAdapter(adapter);
    }

}
