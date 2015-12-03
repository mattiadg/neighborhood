package fr.upem.mdigangi.dreseau.users;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;

/**
 * Created by mattia on 30/11/15.
 */
public interface IProfileFactory {

    IProfile newProfile(JSONObject json) throws JSONException;
}
