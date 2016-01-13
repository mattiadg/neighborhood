package fr.upem.mdigangi.dreseau.users;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.android.wifidirect.R;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.android.usersprovider.UsersDBOpenHelper;
import fr.upem.mdigangi.dreseau.db.MyProfileService;

public class NewProfileActivity extends Activity {

    private static String TAG = "NEW_PROFILE_ACTIVITY";

    private MyProfileService profileService;
    private boolean bound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            profileService = ((MyProfileService.MyProfileBinder) service).getProfileProvider();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyProfileService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound) {
            unbindService(connection);
            bound = false;
        }
    }

    //TODO inizializzare con i campi già inseriti

    public void register(View view) {
        //TODO controllo sui campi obbligatori
        if (bound) {
            JSONObject bundle = new JSONObject();
            EditText name = (EditText) findViewById(R.id.nameInsert);
            EditText surname = (EditText) findViewById(R.id.surnameInsert);
            EditText email = (EditText) findViewById(R.id.emailInsert);
            EditText birth = (EditText) findViewById(R.id.birthInsert);
            EditText phone = (EditText) findViewById(R.id.phoneInsert);
            try {
                bundle.put(UsersDBOpenHelper.FriendEntry.COLUMN_NAME, name.getText().toString());
                bundle.put(UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME, surname.getText().toString());
                bundle.put(UsersDBOpenHelper.FriendEntry.COLUMN_EMAIL, email.getText().toString());
                bundle.put(UsersDBOpenHelper.FriendEntry.COLUMN_PHONE, phone.getText().toString());
                bundle.put(UsersDBOpenHelper.FriendEntry.COLUMN_BIRTHDATE, birth.getText().toString());
                bundle.put(UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID, String.valueOf(R.drawable.default_image));
            } catch (JSONException e) {
                //TODO aggiustare qua
                throw new IllegalStateException("E' successo qualcosa con JSON");
            }
            Log.d(TAG, bundle.toString());
            profileService.insertMyProfile(bundle);
        }
    }

}
