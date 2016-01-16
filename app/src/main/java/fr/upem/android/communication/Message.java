package fr.upem.android.communication;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;

/**
 * A message that can be sent between two peers. This class includes a Builder class to create
 * an unmodifiable Message class. The actual class will store the data and transform the message in
 * JSON format.
 * Created by mattia on 15/01/16.
 */
public class Message {

    public static final String FIELD_AUTHOR = "author";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_TIME = "time";

    private final String author;
    private final String text;
    private final long creationTime;
    private final boolean isSelf;

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
                    text.toString(), System.nanoTime(),
                    true); //We are making the message, it is by ourselves
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
                    jsonMessage.getLong(FIELD_TIME),
                    false); //A rebuilt message has been received
        }
    }

    private Message(String author, String text, long creationTime, boolean isSelf) {
        this.author = author;
        this.text = text;
        this.creationTime = creationTime;
        this.isSelf = isSelf;
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

    public boolean isSelf() { return isSelf; }

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
