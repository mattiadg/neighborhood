package fr.upem.mdigangi.dreseau.users;

/**
 * Created by mattia on 21/11/15.
 */

/**
 * Interface to be implemented by user profile classes. It allows decoration
 */
public interface IProfile {

    //Returns user data in Json format
    String getData();
}
