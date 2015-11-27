package fr.upem.mdigangi.dreseau.users;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.android.wifidirect.R;

import fr.upem.android.usersprovider.IProfile;

public class MyProfileActivity extends Activity implements ProfileFragment.ProfileFragmentListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        IProfile bp = new BasicProfile("Mattia", "Di Gangi", "0786380225",
                "16/05/1992", "mattiadigangi@gmail.com", R.drawable.default_image);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment newFragment = ProfileFragment.newInstance(bp);
        ft.add(R.id.profFragmentContainer, newFragment);
        ft.commit();
    }

    @Override
    public void itemClicked(long id) {
        //TODO
    }
}
