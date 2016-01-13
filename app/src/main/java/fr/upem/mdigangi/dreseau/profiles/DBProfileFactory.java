package fr.upem.mdigangi.dreseau.profiles;

import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;

/**
 * Created by mattia on 08/01/16.
 */
public class DBProfileFactory implements IProfileFactory {
    @Override
    public IProfile newProfile(JSONObject json) {
        return new DBProfile(new BasicProfileFactory().newProfile(json));
    }
}
