package fr.upem.android.communication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import fr.upem.mdigangi.dreseau.db.MyProfileHandler;

/**
 * Created by mattia on 10/01/16.
 */
class ClientActor extends Actor {

    private String data;

    ClientActor(CommunicationProtocol.ProtocolListener listener) {
        this.state = CommunicationProtocol.State.INIT;
        this.listener = listener;
    }

    @Override
    protected String init(String data) {
        this.data = data;
        if(data == null) {
            this.state = CommunicationProtocol.State.NOTIFY_SEND_PROFILE;
        } else {
            this.state = CommunicationProtocol.State.NOTIFY_SEND_MESSAGE;
        }
        return CommunicationProtocol.PROTO_HELLO;
    }

    @Override
    protected String sendNotificationProfile(String received) {
        if (received.equals(CommunicationProtocol.PROTO_HELLO)) {
            this.state = CommunicationProtocol.State.SEND_PROFILE;
            return CommunicationProtocol.PROTO_SEND_PROFILE;
        } else {
            return error();
        }
    }



    @Override
    protected String sendProfile(String received) {
        this.state = CommunicationProtocol.State.READY;
        if (received.equals(CommunicationProtocol.PROTO_RECV_PROFILE)) {
            String toRet = listener.getProfile().getData().toString();
            return toRet;
        } else {
            return error();
        }
    }


    @Override
    protected String receiveProfile(String received) {
        JSONObject json;
        try {
            json = new JSONObject(received);
        } catch (JSONException e) {
            return error();
        }
        listener.registerProfile(json);
        this.state = CommunicationProtocol.State.END;

        return dispatch(listener.getProfile().getData().toString());
    }

    @Override
    protected String sendMessage(String recvd) {
        this.state = CommunicationProtocol.State.READY;
        if(recvd.equals(CommunicationProtocol.PROTO_RECV_MESSAGE)){
            return data;
        } else {
            return error();
        }
    }

    @Override
    protected String receiveMessage(String recvd) {
        this.state = CommunicationProtocol.State.READY;
        listener.treatMessage(recvd);
        return "";
    }

}
