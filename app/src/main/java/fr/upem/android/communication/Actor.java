package fr.upem.android.communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mattia on 10/01/16.
 */
abstract class Actor implements CommunicationProtocol.IActor {

    protected CommunicationProtocol.State state;
    protected CommunicationProtocol.ProtocolListener listener;

    public String error() {
        this.state = CommunicationProtocol.State.END;
        return "";
    }

    protected String readyListening(String received) {
        if (CommunicationProtocol.PROTO_SEND_PROFILE.equals(received)) {
            this.state = CommunicationProtocol.State.RECV_PROFILE;
            return CommunicationProtocol.PROTO_RECV_PROFILE;
        } else {
            this.state = CommunicationProtocol.State.END;
            return error();
        }
    }

    protected abstract String init(String recv);

    protected abstract String receiveProfile(String recvd);

    protected abstract String sendNotificationProfile(String recvd);

    protected abstract String sendProfile(String recvd);

    protected abstract String sendMessage(String recvd);

    protected abstract String receiveMessage(String recvd);

    @Override
    public String dispatch(String recvd) {
        switch (this.state) {
            case INIT:
                return init(recvd);
            case READY:
                return readyListening(recvd);
            case RECV_PROFILE:
                return receiveProfile(recvd);
            case NOTIFY_SEND_PROFILE:
                return sendNotificationProfile(recvd);
            case SEND_PROFILE:
                return sendProfile(recvd);
            case END:
                listener.disconnect();
                return "";
            default:
                return error();
        }
    }



}
