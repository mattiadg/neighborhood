package fr.upem.mdigangi.dreseau.users;


import com.example.android.wifidirect.R;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.android.usersprovider.UsersDBOpenHelper;

/**
 * Created by mattia on 21/11/15.
 */
public class BasicProfile implements IProfile {

    private String name;
    private String surname;
    private String phoneNumber;
    private String birthDate;
    private String email;
    private int imageId;

    public BasicProfile(String name, String surname, String phoneNumber, String birthDate, String email, int imageId) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.email = email;
        this.imageId = imageId;
    }

    @Override
    public JSONObject getData() {
        JSONObject sb = new JSONObject();
        try {
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_NAME, name);
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_SURNAME, surname);
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_PHONE, phoneNumber );
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_BIRTHDATE, birthDate);
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_EMAIL, email);
            sb.put(UsersDBOpenHelper.FriendEntry.COLUMN_IMAGE_ID, imageId);
        } catch (JSONException e) {
            throw new IllegalStateException("Profile cannot be transformed in JSON!");
        }

        return sb;
    }

    public static void main(String[] args){
        BasicProfile bp = new BasicProfile("Mattia", "Di Gangi", "0786380225",
                "16/05/1992", "mattiadigangi@gmail.com", R.drawable.default_image);
        System.out.println(bp.getData());
    }

}
