package fr.upem.mdigangi.dreseau.db;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.users.BasicProfileFactory;
import fr.upem.mdigangi.dreseau.users.IProfileFactory;
import fr.upem.mdigangi.dreseau.users.UsersDB;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DBService extends IntentService {
    // Actions that this Service can perform
    private static final String ACTION_MY_PROFILE = "fr.upem.mdigangi.dreseau.db.action.my_profile";
    private static final String ACTION_ALL_PROFILES = "fr.upem.mdigangi.dreseau.db.action.all";
    private static final String ACTION_READ_ONE = "fr.upem.mdigangi.dreseau.db.action.read_one";
    private static final String ACTION_INSERT = "fr.upem.mdigangi.dreseau.db.action.insert";
    private static final String ACTION_INSERT_MY_PROFILE = "fr.upem.mdigangi.dreseau.db.action.insert_mine";

    //Id parameter for reading and inserting
    private static final String EXTRA_ID = "fr.upem.mdigangi.dreseau.db.extra.id";
    private static final String EXTRA_RECEIVER = "fr.upem.mdigangi.dreseau.db.extra.receiver";
    private static final String EXTRA_BUNDLE = "fr.upem.mdigangi.dreseau.db.extra.bundle_profile";
    private static final String EXTRA_JSON = "fr.upem.mdigangi.dreseau.db.extra.json_profile";

    //My profile filename
    private static final String MY_PROFILE_FILE_NAME = "my_profile.txt";


    /**
     * Starts this service to perform action ReadMyProfile with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionMyProfile(Context context, ResultReceiver receiver) {
        Intent intent = new Intent(context, DBService.class);
        intent.setAction(ACTION_MY_PROFILE);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action InsertMyProfile with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionInsertMyProfile(Context context, Bundle profile) {
        Intent intent = new Intent(context, DBService.class);
        intent.setAction(ACTION_INSERT_MY_PROFILE);
        intent.putExtra(EXTRA_BUNDLE, profile);
        context.startService(intent);
    }

    public static void startActionInsert(Context context, JSONObject profile){
        Intent intent = new Intent(context, DBService.class);
        intent.setAction(ACTION_INSERT);
        intent.putExtra(EXTRA_JSON, profile.toString());
        context.startService(intent);
    }

    public DBService() {
        super("DBService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MY_PROFILE.equals(action)) {
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
                handleActionMyProfile(receiver);
            } else if (ACTION_INSERT_MY_PROFILE.equals(action)) {
                final Bundle data = intent.getBundleExtra(EXTRA_BUNDLE);
                handleActionInsertMyProfile(data);
            } else if (ACTION_INSERT.equals(action)) {
                final String data = intent.getStringExtra(EXTRA_JSON);
                try {
                    JSONObject profile = new JSONObject(data);
                    handleActionInsert(profile);
                } catch (JSONException e){
                    Toast.makeText(getApplicationContext(),"Profile inserted isn't valid", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"Database error!", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void handleActionInsertMyProfile(Bundle data) {
        try {
            FileOutputStream fos = openFileOutput(MY_PROFILE_FILE_NAME, Context.MODE_PRIVATE);
            fos.write("{\n\t".getBytes());
            Iterator<String> keys = data.keySet().iterator();
            while( keys.hasNext()){
                String key = keys.next();
                String str = "\"" + key + "\":\"" + data.getString(key) + "\"";
                Log.d("DBSERVICE", str);
                if(keys.hasNext()){
                    str += ",\n";
                }
                fos.write(str.getBytes());
            }
            fos.write("\n}".getBytes());
            fos.close();
        } catch (IOException e){
            Toast.makeText(getApplicationContext(), "Impossible reading file!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handle action getMyProfile in the provided background thread with the provided
     * parameters.
     */
    private void handleActionMyProfile(ResultReceiver receiver) {
        try {
            String str = "";
            FileInputStream is = openFileInput(MY_PROFILE_FILE_NAME);
            StringBuffer buf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is!=null) {
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n" );
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString(UsersDB.EXTRA_MY_PROFILE, buf.toString());
            receiver.send(UsersDB.CODE_GET_MY_PROFILE, bundle);
        } catch (IOException e) {
            receiver.send(UsersDB.CODE_ERROR, null);
        }
    }

    private void handleActionInsert(JSONObject jsonProfile) throws JSONException, IOException {
        IProfileFactory factory = new BasicProfileFactory();
        IProfile profile = factory.newProfile(jsonProfile);
        UsersDB db = new UsersDB(this.getApplicationContext());
        db.addUser(profile);
    }

}
