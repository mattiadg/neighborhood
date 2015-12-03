package fr.upem.mdigangi.dreseau.users;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.android.wifidirect.R;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.DBService;

public class MyProfileActivity extends Activity implements ProfileFragment.ProfileFragmentListener{

    private static String TAG = "MyProfileActivity";
    private class ProfileReceiver extends ResultReceiver {

        public ProfileReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData){
            switch (resultCode){
                case UsersDB.CODE_GET_MY_PROFILE:
                    try {
                        String json = resultData.getString(UsersDB.EXTRA_MY_PROFILE);
                        Log.d(TAG, json);
                        JSONObject object = new JSONObject(json);
                        my_profile = factory.newProfile(object);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment newFragment = ProfileFragment.newInstance(my_profile);
                        ft.add(R.id.profFragmentContainer, newFragment);
                        ft.commit();
                    } catch (JSONException e) {
                        my_profile = null;
                        Log.e(TAG, e.getMessage());
                    }
            }
        }
    }

    private IProfile my_profile;
    private IProfileFactory factory = new BasicProfileFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //Handler used to run ResultReceiver.onReceiveResult()
        Handler handler = new Handler();
        //IProfile bp = new BasicProfile("Mattia", "Di Gangi", "0786380225",
        //        "16/05/1992", "mattiadigangi@gmail.com", R.drawable.default_image);

        ResultReceiver resultReceiver = new ProfileReceiver(handler);
        DBService.startActionMyProfile(this, resultReceiver);

    }

    @Override
    public void itemClicked(long id) {
        //TODO
    }
}
