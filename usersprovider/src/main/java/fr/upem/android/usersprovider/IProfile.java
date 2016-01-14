package fr.upem.android.usersprovider;

/**
 * Created by mattia on 21/11/15.
 */

import org.json.JSONObject;

/**
 * Interface to be implemented by user profile classes. It allows decoration
 */
public interface IProfile {

    //Returns user data in Json format
    JSONObject getData();

    public String getName();

    public String getSurname();

    public String getPhoneNumber();

    public String getBirthDate();

    public String getEmail();

    public int getImageId();

    public int getDbId();
}
