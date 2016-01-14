package fr.upem.android.communication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.wifidirect.R;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.MyProfileHandler;
import fr.upem.mdigangi.dreseau.main.MainActivity;
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;
import fr.upem.mdigangi.dreseau.users.UsersDB;

/**
 * Launch an asynchronous server for a group owner in order to communicate with the peers
 */
public class ServerService extends IntentService implements CommunicationProtocol.ProtocolListener {

    public static final String ACTION_START = "com.example.android.wifidirect.action.START";

    private boolean serverOn = false;
    private List<CommunicationProtocol> protocols = new LinkedList<>();

    public ServerService() {
        super("ServerService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("ServerService", "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                if (serverOn) {
                    return;
                }
                serverOn = true;
            }
            Log.d("ServerService", "Starting server");
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
            serverSocket = new ServerSocket(8988);
            while (serverOn) {
                synchronized (ACTION_START) {
                    if (!serverOn) {

                    }
                }
                Socket client = serverSocket.accept();
                new SingleThreadServer().execute(client);
            }
        } catch (IOException e) {
            //TODO add something sensible
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

    @Override
    public void registerProfile(JSONObject jsonProfile) {

    }

    @Override
    public IProfile getProfile() {
        return null;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void treatMessage(String message) {
        //TODO fill this method
    }

    /*
     *Handle the connection with a single client
     */
    private class SingleThreadServer extends AsyncTask<Socket, Void, Boolean> implements CommunicationProtocol.ProtocolListener {

        private Socket socket;
        private CommunicationProtocol protocol;
        private boolean running = true;
        private final Object monitor = new Object();

        private static final int TASK_STACK_BUILDER_CODE = 167;

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
            return MyProfileHandler.getMyProfile();
        }

        @Override
        public void disconnect() {
            synchronized (monitor) {
                running = false;
            }
        }

        @Override
        public void treatMessage(String message) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
            taskStackBuilder.addParentStack(MainActivity.class);
            taskStackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(TASK_STACK_BUILDER_CODE, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setContentTitle("New Message")
                    .setContentText(message)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(getResources().getString(R.string.app_name), TASK_STACK_BUILDER_CODE+1, notification);
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
    }
}