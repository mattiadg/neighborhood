package fr.upem.android.communication;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.android.wifidirect.ProfileTransferService;
import com.example.android.wifidirect.WiFiDirectActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BroadcastingService extends IntentService {

    private static final String ACTION_PROFILE = "fr.upem.android.communication.action.PROFILE";
    private static final String ACTION_MESSAGE = "fr.upem.android.communication.action.MESSAGE";

    private static final String DEST_PORT = "fr.upem.android.communication.extra.PORT";
    private static final String DEST_IP = "fr.upem.android.communication.extra.IP";

    private static final String EXTRA_PARAM_DATA = "fr.upem.android.communication.extra.data";
    private static final String EXTRA_PARAM_SENDER_IP = "fr.upem.android.communication.extra.sender";

    private GroupManager groupManager;

    public BroadcastingService() {
        super("BroadcastingService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionProfile(Context context, String data, String senderIP) {
        Intent intent = new Intent(context, BroadcastingService.class);
        intent.setAction(ACTION_PROFILE);
        intent.putExtra(EXTRA_PARAM_DATA, data);
        intent.putExtra(EXTRA_PARAM_SENDER_IP, senderIP);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionMessage(Context context, String data, String senderIP) {
        Intent intent = new Intent(context, BroadcastingService.class);
        intent.setAction(ACTION_MESSAGE);
        intent.putExtra(EXTRA_PARAM_DATA, data);
        intent.putExtra(EXTRA_PARAM_SENDER_IP, senderIP);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            groupManager = GroupManager.getGroupManager();
            final String action = intent.getAction();
            final String data = intent.getStringExtra(EXTRA_PARAM_DATA);
            final String ip = intent.getStringExtra(EXTRA_PARAM_SENDER_IP);
            GroupManager.getGroupManager().addIp(ip);
            if (ACTION_PROFILE.equals(action)) {
                handleActions(data, ip, true);
            } else if (ACTION_MESSAGE.equals(action)) {
                handleActions(data, ip, false);
            }
        }
    }

    private Bundle prepareBundle(String data, String ip, boolean isProfile) {
        Bundle bundle = new Bundle();
        bundle.putInt(DEST_PORT, ServerService.CLIENT_PORT);
        bundle.putString(DEST_IP, ip);
        if(isProfile){
            bundle.putString(ProfileTransferService.EXTRAS_PROFILE_SEND, data);
        } else {
            bundle.putString(ProfileTransferService.EXTRAS_MESSAGE_SEND, data);
        }
        return bundle;
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActions(String dataToSend, String senderIP, boolean isProfile) {
        Iterator<String> iterator = groupManager.iterator();
        while (iterator.hasNext()){
            String ip = iterator.next();
            if(!ip.equals(senderIP)) {
                Bundle bundle = prepareBundle(dataToSend, ip, isProfile);
                new AsyncClient().execute(bundle);
            }
        }
    }


    class AsyncClient extends AsyncTask<Bundle, Void, Boolean> implements CommunicationProtocol.ProtocolListener {

        private IProfile profile;
        private Socket socket;
        //Indicates whether the client is still running
        private boolean running;
        //Monitor used for the concurrent access to running
        private final Object monitor = new Object();

        @Override
        protected Boolean doInBackground(Bundle... params) {
            Bundle bundle = params[0];
            String host = bundle.getString(DEST_IP);
            int port = bundle.getInt(DEST_PORT);
            boolean isProfile = bundle.containsKey(ProfileTransferService.EXTRAS_PROFILE_SEND);
            String toSend;
            //If a profile is sent, load it and put toSend to Null according to ClientActor
            //specifications
            if (isProfile) {
                toSend = bundle.getString(ProfileTransferService.EXTRAS_PROFILE_SEND);
                try {
                    profile = new BasicProfileFactory().newProfile(new JSONObject(toSend));
                    toSend = null;
                } catch (JSONException e) {
                    throw new IllegalArgumentException("Didn't receive a JSON! A JSON profile was expected");
                }
            } else {
                boolean isMessage = bundle.containsKey(ProfileTransferService.EXTRAS_MESSAGE_SEND);
                //else, if it's a message prepare the message
                if (isMessage) {
                    toSend = bundle.getString(ProfileTransferService.EXTRAS_MESSAGE_SEND);
                } else {
                    //Else it's an error, return
                    return false;
                }
            }
            socket = new Socket();
            DataOutputStream ostream = null;
            DataInputStream istream = null;
            String received = toSend;
            CommunicationProtocol protocol = new CommunicationProtocol(CommunicationProtocol.Actor.Client, this);
            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                try {
                    socket.connect((new InetSocketAddress(host, port)), 5000);
                } catch (IOException e){
                    //The address in not valid anymore. Deleting it
                    groupManager.removeIp(host);
                }
                running = true;
                Log.d(WiFiDirectActivity.TAG, "Client socket - " + String.valueOf(socket.isConnected()));
                ostream = new DataOutputStream(socket.getOutputStream());

                istream = new DataInputStream(socket.getInputStream());
                //Allows concurrent deconnection
                while (running) {
                    synchronized (monitor) {
                        if (!running) {
                            return false;
                        }
                    }
                    String nextString = protocol.nextMsg(received);
                    Log.d("Client", "Sending " + nextString);
                    ostream.writeUTF(nextString);
                    received = new String(istream.readUTF());
                    Log.d("Client", "Received " + received);
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
                if (ostream != null) {
                    try {
                        ostream.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
                if (istream != null) {
                    try {
                        istream.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }

        @Override
        public void registerProfile(JSONObject jsonProfile) {
            //Nothing to do here
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
            //Nothing to do here
        }
    }

}
