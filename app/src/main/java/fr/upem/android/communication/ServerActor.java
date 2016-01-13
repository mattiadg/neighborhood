package fr.upem.android.communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mattia on 10/01/16.
 */
class ServerActor extends Actor {

    public ServerActor(CommunicationProtocol.ProtocolListener listener) {
        this.state = CommunicationProtocol.State.INIT;
        this.listener = listener;
    }

    @Override
    protected String init(String received) {
        if (CommunicationProtocol.PROTO_HELLO.equals(received)) {
            this.state = CommunicationProtocol.State.READY;
            return CommunicationProtocol.PROTO_HELLO;
        } else {
            this.state = CommunicationProtocol.State.END;
            return error();
        }
    }


    protected String init() {
        return null;
    }

    protected String receiveProfile(String received) {
        JSONObject json;
        try {
            json = new JSONObject(received);
        } catch (JSONException e) {
            return error();
        }
        listener.registerProfile(json);
        this.state = CommunicationProtocol.State.NOTIFY_SEND_PROFILE;

        return dispatch(null);
    }

    @Override
    protected String sendNotificationProfile(String recvd) {
        this.state = CommunicationProtocol.State.SEND_PROFILE;
        return CommunicationProtocol.PROTO_SEND_PROFILE;
    }

    protected String sendProfile(String received) {
        this.state = CommunicationProtocol.State.END;
        if (received.equals(CommunicationProtocol.PROTO_RECV_PROFILE)) {
            return listener.getProfile().getData().toString();
        } else {
            return error();
        }
    }

    @Override
    protected String sendMessage(String recvd) {
        //TODO FILL THIS METHOD
        return null;
    }

    @Override
    protected String receiveMessage(String recvd) {
        //TODO FILL THIS METHOD
        return null;
    }

}
