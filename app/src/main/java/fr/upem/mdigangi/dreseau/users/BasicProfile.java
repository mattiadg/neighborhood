package fr.upem.mdigangi.dreseau.users;


import com.example.android.wifidirect.R;

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
    public String getData() {
        StringBuilder sb = new StringBuilder("{ ");
        sb.append("\"name\":" + "\"" + name + "\", ");
        sb.append("\"surname\":" + "\"" + surname + "\", ");
        sb.append("\"mobile\":" + "\"" + phoneNumber + "\", ");
        sb.append("\"birthDate\":" + "\"" + birthDate + "\", ");
        sb.append("\"email\":" + "\"" + email + "\", ");
        sb.append("\"imageId\":" + "\"" + imageId + "\"");
        sb.append("}");

        return sb.toString();
    }

    public static void main(String[] args){
        BasicProfile bp = new BasicProfile("Mattia", "Di Gangi", "0786380225",
                "16/05/1992", "mattiadigangi@gmail.com", R.drawable.default_image);
        System.out.println(bp.getData());
    }

}
