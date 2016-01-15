package fr.upem.mdigangi.dreseau.db;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;
import fr.upem.mdigangi.dreseau.profiles.DBProfileFactory;
import fr.upem.mdigangi.dreseau.profiles.IProfileFactory;
import fr.upem.mdigangi.dreseau.users.UsersDB;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class FriendsService extends Service {

    public interface FriendsServiceListener {
        FriendsService getFriendsService();
    }

    // Actions that this Service can perform
    private static final String ACTION_ALL_PROFILES = "fr.upem.mdigangi.dreseau.db.action.all";
    private static final String ACTION_READ_ONE = "fr.upem.mdigangi.dreseau.db.action.read_one";

    //Id parameter for reading and inserting
    private static final String EXTRA_ID = "fr.upem.mdigangi.dreseau.db.extra.id";
    private static final String EXTRA_RECEIVER = "fr.upem.mdigangi.dreseau.db.extra.receiver";
    private static final String EXTRA_BUNDLE = "fr.upem.mdigangi.dreseau.db.extra.bundle_profile";
    private static final String EXTRA_JSON = "fr.upem.mdigangi.dreseau.db.extra.json_profile";

    //Class that effectively communicates with the DB. If changes are needed we can evolve it into
    //a strategy pattern
    private UsersDB db = null;
    //Factory version
    private IProfileFactory factory = new BasicProfileFactory();
    private IProfileFactory dbFactory = new DBProfileFactory();

    public class FriendsServiceBinder extends Binder {

        public FriendsService getFriendsService() {
            return FriendsService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(db == null){
            db = new UsersDB(getApplicationContext());
        }
        return new FriendsServiceBinder();
    }


    //TODO Evitare i duplicati!!!
    public void insertProfile(IProfile profile) throws IOException {
        Cursor cursor = db.readOne(profile.getUID());
        if((cursor != null) && (cursor.getCount() > 0)){
            db.updateOne(profile);
        } else {
            db.addUser(profile);
        }
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
    }

    public List<IProfile> getAllFriends() throws JSONException {
        List<IProfile> list = new LinkedList<>();
        Cursor cursor = db.readAll();
        //Moving cursor content into list

        int columnCount = cursor.getColumnCount();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            JSONObject jsonTranslation = new JSONObject();
            for(int i = 0; i < columnCount; i++){
                String columnName = cursor.getColumnName(i);
                try {
                    jsonTranslation.put(columnName, cursor.getString(i));
                } catch (JSONException e) {
                    Log.e("FriendsService.Friends", "Strange JSON error");
                    continue;
                }
            }
            list.add(dbFactory.newProfile(jsonTranslation));
        }
        cursor.close();
        return list;
    }

    public boolean deleteFriend(IProfile profile){
        return db.deleteOne(profile) > 0;
    }

}
