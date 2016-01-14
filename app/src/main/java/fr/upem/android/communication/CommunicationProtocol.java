package fr.upem.android.communication;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.android.usersprovider.IProfile;

/**
 * Created by mattia on 09/01/16.
 */
public class CommunicationProtocol {

    static final String PROTO_HELLO = "Hello";
    static final String PROTO_SEND_PROFILE = "Sending profile...";
    static final String PROTO_RECV_PROFILE = "Waiting for your profile...";
    static final String PROTO_SEND_MESSAGE = "Sending a message...";
    static final String PROTO_RECV_MESSAGE = "Waiting for your message";
    static final String PROTO_ACK = "OK. Received";

    public enum Actor {Client, Server }
    enum State {INIT, READY, RECV_PROFILE, NOTIFY_SEND_PROFILE, SEND_PROFILE,
        RECV_MESSAGE, NOTIFY_SEND_MESSAGE, SEND_MESSAGE, END}

    interface IActor {
        String dispatch(String recvd);
    }

    /**
     * This interface must be implemented by every class which wants to run the protocol
     */
    public interface ProtocolListener {
        /**
         * Save the profile passed as a parameter
         * @param jsonProfile The profile as a JSON
         */
        void registerProfile(JSONObject jsonProfile);

        /**
         *
         * @return The owner's profile
         */
        IProfile getProfile();

        /**
         * Ends the connection with the other actor
         */
        void disconnect();

        /**
         * Callback function for treating received messages
         * @param message
         */
        void treatMessage(String message);
    }

    private final IActor actor;

    public CommunicationProtocol(Actor actor, ProtocolListener listener) {
        switch (actor){
            case Client:
                this.actor = new ClientActor(listener);
                break;
            case Server:
                this.actor = new ServerActor(listener);
                break;
            default:
                throw new IllegalArgumentException("Must provide a valid actor");
        }
    }

    /**
     *
     * @param recvd This generally is the last received message, but it's also used by the client in order to start
     * the communication. In that case, if recvd is null, then the client will send a profile, else
     * recvd will be the message to send.
     * @return
     */
    public String nextMsg(String recvd) {
        return actor.dispatch(recvd);
    }
}
