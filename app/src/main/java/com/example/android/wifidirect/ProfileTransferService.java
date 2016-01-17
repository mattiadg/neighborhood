
package com.example.android.wifidirect;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import fr.upem.android.communication.CommunicationProtocol;
import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;
import fr.upem.mdigangi.dreseau.users.UsersDB;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class ProfileTransferService extends IntentService implements CommunicationProtocol.ProtocolListener{

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_EXCHANGE_PROFILE_1 = "com.example.android.wifidirect.EXCHANGE_PROFILE_1";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    private static final String TAG = "ProfileTransferService";
    public static final String EXTRAS_PROFILE_SEND = "com.example.android.wifidirect.isprofile";
    public static final String EXTRAS_MESSAGE_SEND = "com.example.android.wifidirect.ismessage";

    public ProfileTransferService() {
        super("ProfileTransferService");
    }

    private IProfile profile;
    private Socket socket;
    //Indicates whether the client is still running
    private boolean running;
    //Monitor used for the concurrent access to running
    private final Object monitor = new Object();

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
        if (intent.getAction().equals(ACTION_EXCHANGE_PROFILE_1)) {
            String toSend = intent.getExtras().getString(EXTRAS_PROFILE_SEND);
            Log.d("Client", "Profile: " + toSend);
            try {
                profile = new BasicProfileFactory().newProfile(new JSONObject(toSend));
            } catch (JSONException e) {
                throw new IllegalArgumentException("Didn't receive a JSON! A JSON profile was expected");
            }
            socket = new Socket();
            DataOutputStream ostream = null;
            DataInputStream istream = null;
            String received = "";
            CommunicationProtocol protocol = new CommunicationProtocol(CommunicationProtocol.Actor.Client, ProfileTransferService.this);
            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                running = true;
                Log.d(WiFiDirectActivity.TAG, "Client socket - " + String.valueOf(socket.isConnected()));
                ostream = new DataOutputStream(socket.getOutputStream());
                Log.d(TAG, "ostream openened");
                istream = new DataInputStream(socket.getInputStream());
                Log.d(TAG, "istream openend");
                //byte[] buffer = new byte[1024];
                while (running) {
                    synchronized (monitor) {
                        if (!running) {
                            return;
                        }
                    }
                    String nextString = protocol.nextMsg(received);
                    Log.d("Client", "Sending " + nextString);
                    ostream.writeUTF(nextString);
                    Log.d(TAG, "sending data...");
                    received = new String(istream.readUTF());
                    Log.d("Client", "Received " + received );
                }
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
                if(ostream != null) {
                    try {
                        ostream.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
                if(istream != null) {
                    try {
                        istream.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void registerProfile(JSONObject jsonProfile) {
        UsersDB db = new UsersDB(getApplicationContext());
        try {
            db.addUser(new BasicProfileFactory().newProfile(jsonProfile));
        } catch (IOException e) {
            disconnect();
        }
    }

    @Override
    public IProfile getProfile() {
        Log.d("CLient", "getProfile: " + profile.getData().toString());
        return profile;
    }

    @Override
    public void disconnect() {
        synchronized (monitor) {
            running = false;
        }
    }

    @Override
    public void treatMessage(String message) {
        //TODO
    }
}
