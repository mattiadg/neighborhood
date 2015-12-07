package fr.upem.mdigangi.dreseau.users;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.android.wifidirect.R;

import fr.upem.android.usersprovider.UsersDBOpenHelper;
import fr.upem.mdigangi.dreseau.db.DBService;

public class NewProfileActivity extends Activity {

    private static String TAG = "NEW_PROFILE_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //TODO inizializzare con i campi già inseriti
    //TODO Aumentare i margini tra i campi per la visualizzazione

    public void register(View view) {
        //TODO controllo sui campi obbligatori
        Bundle bundle = new Bundle();
        EditText name = (EditText) findViewById(R.id.nameInsert);
        EditText surname = (EditText) findViewById(R.id.surnameInsert);
        EditText email = (EditText) findViewById(R.id.emailInsert);
        EditText birth = (EditText) findViewById(R.id.birthInsert);
        EditText phone = (EditText) findViewById(R.id.phoneInsert);
        bundle.putString(UsersDBOpenHelper.FriendEntry.COLUMN_NAME, name.getText().toString());
        bundle.putString(UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME, surname.getText().toString());
        bundle.putString(UsersDBOpenHelper.FriendEntry.COLUMN_EMAIL, email.getText().toString());
        bundle.putString(UsersDBOpenHelper.FriendEntry.COLUMN_PHONE, phone.getText().toString());
        bundle.putString(UsersDBOpenHelper.FriendEntry.COLUMN_BIRTHDATE, birth.getText().toString());
        bundle.putString(UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID, String.valueOf(R.drawable.default_image));
        Log.d(TAG, bundle.toString());
        DBService.startActionInsertMyProfile(this, bundle);
    }
}
