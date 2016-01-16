package fr.upem.android.communication;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.example.android.wifidirect.ProfileTransferService;
import com.example.android.wifidirect.R;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.FriendsService;
import fr.upem.mdigangi.dreseau.db.MyProfileHandler;
import fr.upem.mdigangi.dreseau.main.MainActivity;
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;
import fr.upem.mdigangi.dreseau.users.FriendsListActivity;

/**
 * Launch an asynchronous server for a group owner in order to communicate with the peers
 */
public class ServerService extends IntentService{

    public static final String ACTION_START = "com.example.android.wifidirect.action.START";
    public static final int CLIENT_PORT = 1234;
    public static final int SERVER_PORT = 8988;
    public static final String EXTRAS_IS_GROUP_OWNER = "fr.upem.android.communication.extra.GO";

    private boolean isGroupOwner;
    private GroupManager groupManager = GroupManager.getGroupManager();
    private boolean serverOn = false;
    private FriendsService friendsService;
    private boolean bound = false;
    private String go_address;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            bound = true;
            friendsService = ((FriendsService.FriendsServiceBinder)binder).getFriendsService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    public ServerService() {
        super("ServerService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("ServerService", "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            isGroupOwner = intent.getBooleanExtra(EXTRAS_IS_GROUP_OWNER, false);
            if (ACTION_START.equals(action)) {
                if (serverOn) {
                    return;
                }
                serverOn = true;
            }
            Log.d("ServerService", "Starting server");
            if(!bound){
                Intent serviceIntent = new Intent(getApplicationContext(), FriendsService.class);
                bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
            }
            startServer();
        }
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void startServer() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = isGroupOwner ? new ServerSocket(SERVER_PORT) : new ServerSocket(CLIENT_PORT);
            while (serverOn) {
                synchronized (ACTION_START) {
                    if (!serverOn) {
                        return;
                    }
                }
                Socket client = serverSocket.accept();
                if(isGroupOwner) {
                    groupManager.addIp(client.getInetAddress().getHostAddress());
                } else {
                    go_address = client.getInetAddress().getHostAddress();
                }
                new SingleThreadServer().execute(client);
            }
        } catch (IOException e) {
            serverOn = false;
            Log.d("ServerService", "IOException");
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    throw new IllegalStateException("Impossible closing socket!");
                }
            }
        }
    }

    /*
     *Handle the connection with a single client
     */
    private class SingleThreadServer extends AsyncTask<Socket, Void, Boolean> implements CommunicationProtocol.ProtocolListener {

        private Socket socket;
        private CommunicationProtocol protocol;
        private boolean running = true;
        private final Object monitor = new Object();

        @Override
        public void registerProfile(JSONObject jsonProfile) {
            if(bound) {
                try {
                    if(friendsService.insertProfile(new BasicProfileFactory().newProfile(jsonProfile))){
                        sendNotification();
                    }
                    if(isGroupOwner){
                        broadcastData(jsonProfile.toString(), true);
                    }
                } catch (IOException e) {
                    disconnect();
                }
            }
        }

        private void sendNotification() {
            Intent intent = new Intent(getApplicationContext(), FriendsListActivity.class);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addParentStack(MainActivity.class);
            taskStackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = taskStackBuilder
                    .getPendingIntent(1234, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(getApplicationContext())
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.receivedProfile))
                    .setContentText(getResources().getString(R.string.profile_notification_content))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build();

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(684, notification);
        }

        @Override
        public IProfile getProfile() {
            return MyProfileHandler.getMyProfile();
        }

        @Override
        public void disconnect() {
            synchronized (monitor) {
                running = false;
            }
        }

        @Override
        public void treatMessage(String messageAsJson) {
            Intent broadcastIntent = new Intent("fr.upem.android.chat.broadcast.message");
            broadcastIntent.putExtra(EXTRAS_IS_GROUP_OWNER, isGroupOwner);
            if(!isGroupOwner){
                broadcastIntent.putExtra(ProfileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, go_address);
            }
            broadcastIntent.putExtra(ProfileTransferService.EXTRAS_MESSAGE_SEND, messageAsJson);
            sendOrderedBroadcast(broadcastIntent, null, null, null, Activity.RESULT_OK, null, null);
            if(isGroupOwner){
                Log.d("treatMessage", "I'm the GO. Broadcasting");
                broadcastData(messageAsJson, false);
            }
        }

        private void writeToStream(DataOutputStream os, String toSend) throws IOException {
            os.writeUTF(toSend);
        }

        private String readFromStream(DataInputStream is) throws IOException {
            byte[] bytes = new byte[1024];
            return new String(is.readUTF());
        }

        @Override
        protected void onPreExecute() {
            protocol = new CommunicationProtocol(CommunicationProtocol.Actor.Server, this);
            running = true;
        }

        @Override
        protected Boolean doInBackground(Socket... params) {
            socket = params[0];
            DataInputStream is;
            DataOutputStream os;
            try {
                is = new DataInputStream(socket.getInputStream());
                os = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                disconnect();
                return false;
            }
            try {
                while (running) {
                    synchronized (socket) {
                        if (!running) {
                            break;
                        }
                    }
                    String recv = readFromStream(is);
                    Log.d("Server", "Received " + recv);
                    String toSend = protocol.nextMsg(recv);
                    Log.d("Server", "Sending " + toSend);
                    writeToStream(os, toSend);
                }
            } catch (IOException e) {
                disconnect();
                return false;
            }
            return true;
        }

        private void broadcastData(String data, boolean isProfile) {
            if(isProfile){
                BroadcastingService.startActionProfile(getApplicationContext(), data,
                        socket.getInetAddress().getHostAddress());
            } else {
                BroadcastingService.startActionMessage(getApplicationContext(), data,
                        socket.getInetAddress().getHostAddress());
            }
        }

    }

}