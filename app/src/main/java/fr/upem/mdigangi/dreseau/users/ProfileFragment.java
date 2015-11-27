package fr.upem.mdigangi.dreseau.users;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.wifidirect.R;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public static interface ProfileFragmentListener{
        void itemClicked(long id);
    }

    private ProfileFragmentListener listener;
    private String name;
    private String surname;
    private String birth;
    private String phone;
    private String email;


    public static ProfileFragment newInstance(IProfile profile) {

        Bundle bundle = parseProfileData(profile);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        Log.i("setArguments: ", bundle.toString());
        Log.i("JSON: ",profile.getData());
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

            name = bundle.getString("name");
            surname = bundle.getString("surname");
            birth = bundle.getString("birthDate");
            phone = bundle.getString("mobile");
            email = bundle.getString("email");
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

    private static Bundle parseProfileData(IProfile profile){
        Bundle bundle = new Bundle();
        JsonProfileParser parser = new JsonProfileParser();
        List<JsonProfileParser.Pair> pairs = null;
        try {
           pairs  = parser.readProfile(profile.getData());
        } catch (IOException e){
            return bundle;
        }
        for(JsonProfileParser.Pair pair : pairs){
            bundle.putString(pair.key, pair.value);
        }
        return bundle;
    }
}
