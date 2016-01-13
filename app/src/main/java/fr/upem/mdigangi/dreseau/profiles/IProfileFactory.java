package fr.upem.mdigangi.dreseau.profiles;

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

/**
 * Created by mattia on 30/11/15.
 */
public interface IProfileFactory {

    IProfile newProfile(JSONObject json);
}
