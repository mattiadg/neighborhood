/**
 * Neighborhood is an Android app for creating a social network by means
 of WiFiP2P technology.
 Copyright (C) 2016  Di Gangi Mattia Antonino

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package fr.upem.mdigangi.dreseau.users;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.wifidirect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import fr.upem.android.usersprovider.IProfile;

/**
 * A fragment where a profile is shown. It uses the Builder Pattern for construction.
 */
public class ProfileFragment extends Fragment {

    public interface ProfileFragmentListener{
        void itemClicked(long id);
    }

    private ProfileFragmentListener listener;
    private IProfile profile;
    private String name;
    private String surname;
    private String birth;
    private String phone;
    private String email;


    public static ProfileFragment newInstance(IProfile profile) {

        ProfileFragment fragment = new ProfileFragment(profile);

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

    }

    private ProfileFragment(IProfile profile){
        name = profile.getName();
        surname = profile.getSurname();
        birth = profile.getBirthDate();
        phone = profile.getPhoneNumber();
        email = profile.getEmail();
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
        }
    }

    private static Bundle parseProfileData(IProfile profile) throws JSONException {
        Bundle bundle = new Bundle();
        JSONObject object = profile.getData();

        Iterator<String> iter = object.keys();
        while(iter.hasNext()){
            String key = iter.next();
            bundle.putString(key, object.getString(key));
        }

        return bundle;
    }
}
