package fr.upem.mdigangi.dreseau.db;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    //Id parameter for reading and inserting
    private static final String EXTRA_ID = "fr.upem.mdigangi.dreseau.db.extra.id";
    private static final String EXTRA_RECEIVER = "fr.upem.mdigangi.dreseau.db.extra.receiver";

    //My profile filename
    private static final String MY_PROFILE_FILE_NAME = "my_profile.txt";


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionMyProfile(Context context, ResultReceiver receiver) {
        Intent intent = new Intent(context, DBService.class);
        intent.setAction(ACTION_MY_PROFILE);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    /*
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DBService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }
    */

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
            } else if (ACTION_READ_ONE.equals(action)) {
               /* final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2); */
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionMyProfile(ResultReceiver receiver) {
        try {
            String str = "";
            InputStream is = getAssets().open(MY_PROFILE_FILE_NAME);
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

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
