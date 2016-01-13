package fr.upem.mdigangi.dreseau.profiles;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.android.usersprovider.UsersDBOpenHelper;

/**
 * Created by mattia on 08/01/16.
 */
public class DBProfile implements IProfile {

    private IProfile profile;


    DBProfile(IProfile profile){
        this.profile = profile;
    }

    @Override
    public JSONObject getData() {
        JSONObject jsonObject = profile.getData();
        try {
            jsonObject.put(UsersDBOpenHelper.FriendEntry.COLUMN_ID, profile.getDbId());
        } catch (JSONException e) {
            throw new IllegalArgumentException("You must pass a vlid profile!");
        }
        return jsonObject;
    }

    @Override
    public String getName() {
        return profile.getName();
    }

    @Override
    public String getSurname() {
        return profile.getSurname();
    }

    @Override
    public String getPhoneNumber() {
        return profile.getPhoneNumber();
    }

    @Override
    public String getBirthDate() {
        return profile.getBirthDate();
    }

    @Override
    public String getEmail() {
        return profile.getEmail();
    }

    @Override
    public int getImageId() {
        return profile.getImageId();
    }

    @Override
    public int getDbId() {
        return profile.getDbId();
    }

    @Override
    public String toString() {
        return profile.toString();
    }
}
