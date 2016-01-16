package fr.upem.android.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.android.wifidirect.ProfileTransferService;
import com.example.android.wifidirect.R;
import com.example.android.wifidirect.WiFiDirectActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.upem.android.communication.BroadcastingService;
import fr.upem.android.communication.CommunicationProtocol;
import fr.upem.android.communication.GroupManager;
import fr.upem.android.communication.Message;
import fr.upem.android.communication.ServerService;
import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.FriendsService;
import fr.upem.mdigangi.dreseau.db.MyProfileService;

public class ChatActivity extends Activity {

    // LogCat tag
    private static final String TAG = ChatActivity.class.getSimpleName();
    public static final String EXTRAS_MESSAGE_AUTHOR = "message_author";
    public static final String EXTRAS_MESSAGE_TEXT = "message_text";
    public static final String EXTRAS_MESSAGE_TIME = "message_time";

    private EditText inputMsg;

    // Chat messages list adapter
    private MessagesListAdapter adapter;
    private List<Message> listMessages;
    private ListView listViewMessages;

    // Communication data
    private IProfile myProfile;
    private boolean bound;
    private String host;
    private int port;
    private MessageShowReceiver receiver = new MessageShowReceiver();
    private boolean isGroupOwner = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bound = true;
            myProfile = ((MyProfileService.MyProfileBinder) service).getProfileProvider().getMyProfile();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private class MessageShowReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Message message;
            try {
                message = Message.Builder.rebuildMessage(
                        intent.getStringExtra(ProfileTransferService.EXTRAS_MESSAGE_SEND));
            } catch (JSONException e) {
                throw new IllegalArgumentException();
            }
            appendMessage(message);
            abortBroadcast();
            playBeep();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        if (GroupManager.getGroupManager().hasSaved()) {
            listMessages = GroupManager.getGroupManager().getSavedMessages();
        } else {
            listMessages = new ArrayList<Message>();
        }
        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        Intent intent = getIntent();
        isGroupOwner = intent.getBooleanExtra(ServerService.EXTRAS_IS_GROUP_OWNER, false);
        if (!isGroupOwner) {
            host = intent.getStringExtra(ProfileTransferService.EXTRAS_GROUP_OWNER_ADDRESS);
            port = intent.getIntExtra(ProfileTransferService.EXTRAS_GROUP_OWNER_PORT, ServerService.SERVER_PORT);
        }
        if(intent.hasExtra(EXTRAS_MESSAGE_AUTHOR)) {
            String author = intent.getStringExtra(EXTRAS_MESSAGE_AUTHOR);
            String text = intent.getStringExtra(EXTRAS_MESSAGE_TEXT);
            long time = intent.getLongExtra(EXTRAS_MESSAGE_TIME, 0);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Message.FIELD_AUTHOR, author);
                jsonObject.put(Message.FIELD_TEXT, text);
                jsonObject.put(Message.FIELD_TIME, time);
                Message message = Message.Builder.rebuildMessage(jsonObject.toString());
                listMessages.add(message);
            } catch (JSONException e) {
                throw new IllegalStateException("Couldn't create a message!!!");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, MyProfileService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        GroupManager.getGroupManager().saveMessages(listMessages);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("fr.upem.android.chat.broadcast.message");
        filter.setPriority(100);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void sendMessage(View view) {
        String msg = inputMsg.getText().toString().trim();
        if (!msg.equals("")) {
            Message message = new Message.Builder()
                    .setProfile(myProfile)
                    .setText(msg)
                    .build();
            if (!isGroupOwner) {
                new ChatAsyncClient(host, port).execute(message);
                appendMessage(message);
            } else {
                Iterator<String> ipIterator = GroupManager.getGroupManager().iterator();
                while (ipIterator.hasNext()) {
                    new ChatAsyncClient(ipIterator.next(), ServerService.CLIENT_PORT).execute(message);
                }
                appendMessage(message);
            }
        }
    }

    /**
     * Appending message to list view
     */
    private void appendMessage(final Message m) {

        listMessages.add(m);

        adapter.notifyDataSetChanged();

        // Playing device's notification
        //playBeep();

    }


    private class ChatAsyncClient extends AsyncTask<Message, Void, Boolean> implements CommunicationProtocol.ProtocolListener {
        private Socket socket;
        //Indicates whether the client is still running
        private boolean running;
        //Monitor used for the concurrent access to running
        private final Object monitor = new Object();
        // Target host
        private String host;
        private int port;

        ChatAsyncClient(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        protected Boolean doInBackground(Message... params) {
            Message message = params[0];
            Log.d(TAG, message.toString());
            socket = new Socket();
            DataOutputStream ostream = null;
            DataInputStream istream = null;
            CommunicationProtocol protocol = new CommunicationProtocol(CommunicationProtocol.Actor.Client, this);
            try {
                Log.d(TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 5000);
                running = true;
                Log.d(TAG, "Client socket - " + String.valueOf(socket.isConnected()));
                ostream = new DataOutputStream(socket.getOutputStream());
                istream = new DataInputStream(socket.getInputStream());
                String received = message.toString();
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
        protected void onPostExecute(Boolean o) {
            inputMsg.setText("");
        }

        @Override
        public void registerProfile(JSONObject jsonProfile) {
            //Not used
        }

        @Override
        public IProfile getProfile() {
            //not used
            return null;
        }

        @Override
        public void disconnect() {
            synchronized (monitor) {
                running = false;
            }
        }

        @Override
        public void treatMessage(String message) {
            //Not used
        }
    }

    /**
     * Plays device's default notification sound
     * */
    public void playBeep() {

        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
