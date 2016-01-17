/**
 * Neighborhood is an Android app for creating a social network by means
 of WiFiP2P technology.
 Copyright (C) 2016  Di Gangi Mattia Antonino

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package fr.upem.android.communication;

import android.util.Log;

/**
 * Actor contains the common implementation details between ClientActor and ServerActor and it
 * works also as a Template Method for creating Actors.
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
            Log.d("readyListening", "In state RECV_PROFILE");
            return CommunicationProtocol.PROTO_RECV_PROFILE;
        } else if(CommunicationProtocol.PROTO_SEND_MESSAGE.equals(received)){
            this.state = CommunicationProtocol.State.RECV_MESSAGE;
            Log.d("readyListening", "In state RECV_MESSAGE");
            return CommunicationProtocol.PROTO_RECV_MESSAGE;
        } else {
            this.state = CommunicationProtocol.State.END;
            return error();
        }
    }

    protected String sendNotificationMessage(String recvd) {
        this.state = CommunicationProtocol.State.SEND_MESSAGE;
        return CommunicationProtocol.PROTO_SEND_MESSAGE;
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
            case NOTIFY_SEND_MESSAGE:
                return sendNotificationMessage(recvd);
            case SEND_MESSAGE:
                return sendMessage(recvd);
            case RECV_MESSAGE:
                return receiveMessage(recvd);
            case END:
                listener.disconnect();
                return "";
            default:
                return error();
        }
    }



}
