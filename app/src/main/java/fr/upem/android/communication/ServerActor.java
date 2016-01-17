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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implements the client-side protocol for client/server architecture. The details must be provided
 * by means of ProtocolListener.
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
            Log.d("init", "in State READY");
            return CommunicationProtocol.PROTO_HELLO;
        } else {
            this.state = CommunicationProtocol.State.END;
            return error();
        }
    }

    //TODO Add broadcasting
    @Override
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

    //TODO ADD BROADCASTING
    @Override
    protected String receiveMessage(String recvd) {
        listener.treatMessage(recvd);
        this.state = CommunicationProtocol.State.READY;
        return CommunicationProtocol.PROTO_ACK;
    }



}
