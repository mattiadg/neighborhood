package fr.upem.android.communication;

import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import fr.upem.android.usersprovider.IProfile;

/**
 * A message that can be sent between two peers. This class includes a Builder class to create
 * an unmodifiable Message class. The actual class will store the data and transform the message in
 * JSON format.
 * Created by mattia on 15/01/16.
 */
public class Message {

    private static final String FIELD_AUTHOR = "author";
    private static final String FIELD_TEXT = "text";
    private static final String FIELD_TIME = "time";
    private final String author;
    private final String text;
    private final long creationTime;

    public static class Builder {
        private String text = null;
        private IProfile profile = null;

        /**
         * Insert the text
         * @param message the message to send
         * @return a reference to this in order to allow fluent idiom
         * @throws NullPointerException if message is null
         */
        public Builder setText(String message){
            if(message == null){
                throw new NullPointerException();
            }
            this.text = message;
            return this;
        }

        /**
         * Insert the author of the message
         * @param profile The profile of the author
         * @return a reference to this in order to allow fluent idiom
         * @throws NullPointerException if profile is null
         */
        public Builder setProfile(IProfile profile){
            if(profile == null){
                throw new NullPointerException();
            }
            this.profile = profile;
            return this;
        }

        /**
         * Create a Message instance from the set fields
         * @return the instance of Message
         * @throws NullPointerException if profile or text is not set.
         */
        public Message build(){
            return new Message(profile.getName() + " " + profile.getSurname(),
                    text.toString(), System.nanoTime());
        }

        /**
         * Rebuild a message from a received message as a JSON
         * @param messageAsJson the message in JSON format
         * @return the corresponding Message instance
         * @throws JSONException if the string isn't a message
         */
        public static Message rebuildMessage(String messageAsJson) throws JSONException {
            JSONObject jsonMessage = new JSONObject(messageAsJson);
            return new Message(jsonMessage.getString(FIELD_AUTHOR),
                    jsonMessage.getString(FIELD_TEXT),
                    jsonMessage.getLong(FIELD_TIME));
        }
    }

    private Message(String author, String text, long creationTime) {
        this.author = author;
        this.text = text;
        this.creationTime = creationTime;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put(FIELD_AUTHOR, this.author);
            jsonMessage.put(FIELD_TEXT, this.text);
            jsonMessage.put(FIELD_TIME, creationTime);
        } catch (JSONException e){
            throw new IllegalStateException("Couldn't create a valid message!");
        }
        return jsonMessage.toString();
    }
}
