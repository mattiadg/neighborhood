package fr.upem.mdigangi.dreseau.users;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.wifidirect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.android.usersprovider.UsersDBOpenHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public interface ProfileFragmentListener{
        void itemClicked(long id);
    }

    private ProfileFragmentListener listener;
    private String name;
    private String surname;
    private String birth;
    private String phone;
    private String email;


    public static ProfileFragment newInstance(IProfile profile) throws JSONException {

        Bundle bundle = parseProfileData(profile);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        Log.i("setArguments: ", bundle.toString());
        Log.i("JSON: ",profile.getData().toString());
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            Log.i("setArguments: ", bundle.toString());

            name = bundle.getString(UsersDBOpenHelper.FriendEntry.COLUMN_NAME);
            surname = bundle.getString(UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME);
            birth = bundle.getString(UsersDBOpenHelper.FriendEntry.COLUMN_BIRTHDATE);
            phone = bundle.getString(UsersDBOpenHelper.FriendEntry.COLUMN_PHONE);
            email = bundle.getString(UsersDBOpenHelper.FriendEntry.COLUMN_EMAIL);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if(view != null){
            TextView name = (TextView) view.findViewById(R.id.nameText);
            TextView phone = (TextView) view.findViewById(R.id.phoneText);
            TextView email = (TextView) view.findViewById(R.id.emailText);
            TextView date = (TextView) view.findViewById(R.id.dateText);

            name.setText(this.name + " " + this.surname);
            phone.setText(this.phone);
            email.setText(this.email);
            date.setText(this.birth);

            Log.i("onStart():", "name: " + this.name + " " + this.surname);
            Log.i("onStart():", "phone: " + this.phone);
            Log.i("onStart():", "email: " + this.email);
            Log.i("onStart():", "date: " + this.birth);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (ProfileFragmentListener) activity;
    }

    private static Bundle parseProfileData(IProfile profile) throws JSONException {
        Bundle bundle = new Bundle();
        JSONObject object = profile.getData();

        Iterator<String> iter = object.keys();
        for(String key = iter.next(); iter.hasNext(); key = iter.next()){
            bundle.putString(key, object.getString(key));
        }

        return bundle;
    }
}
