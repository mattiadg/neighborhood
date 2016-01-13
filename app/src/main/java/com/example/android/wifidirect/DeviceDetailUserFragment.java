/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wifidirect;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.wifidirect.DeviceListFragment.DeviceActionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import fr.upem.android.communication.CommunicationProtocol;
import fr.upem.android.communication.ServerService;
import fr.upem.android.usersprovider.IProfile;
import fr.upem.mdigangi.dreseau.db.FriendsService;
import fr.upem.mdigangi.dreseau.db.MyProfileHandler;
import fr.upem.mdigangi.dreseau.profiles.BasicProfileFactory;
import fr.upem.mdigangi.dreseau.users.UsersDB;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailUserFragment extends Fragment implements ConnectionInfoListener {

    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    private TextView statusText;

    FriendsService.FriendsServiceListener listener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true,
                        new DialogInterface.OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                ((DeviceActionListener) getActivity()).cancelDisconnect();
                            }
                        }
                );

                ((DeviceActionListener) getActivity()).connect(config);
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Send its own profile to other user
                        Log.d("DeviceDetailFragment", "clicked on send");
                        connectToSend(MyProfileHandler.getMyProfile().getData().toString());
                    }
                });
        mContentView.findViewById(R.id.btn_send_message).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );

        listener = (FriendsService.FriendsServiceListener) getActivity();
        statusText = (TextView) mContentView.findViewById(R.id.status_text);
        return mContentView;
    }


    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            Intent intent = new Intent(getActivity(), ServerService.class);
            intent.setAction(ServerService.ACTION_START);
            getActivity().startService(intent);
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.btn_send_message).setVisibility(View.VISIBLE);
            setStatus(getResources()
                    .getString(R.string.client_text));
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Used from encapsulating activity for setting the status
     *
     * @param status The status to print
     */
    public void setStatus(String status) {
        statusText.setText(status);
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        setStatus(getResources().getString(R.string.empty));
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    /*
    private IProfile readProfile(Context context, InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        byte buf[] = new byte[1024];
        int len;
        JSONObject profileAsJson;
        while ((len = in.read(buf)) != -1) {
            builder.append(new String(buf, "UTF-8"));
        }
        in.close();
        try {
            Log.i("DevicDetailUserFragment", builder.toString());
            profileAsJson = new JSONObject(builder.toString());
            return new BasicProfileFactory().newProfile(profileAsJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
*/

    public void connectToSend(String toSend) {
        setStatus("Sending profile...");
        Bundle bundle = new Bundle();
        bundle.putString(ProfileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        bundle.putInt(ProfileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        bundle.putString(ProfileTransferService.EXTRAS_PROFILE_SEND, toSend);
        new AsyncClient().execute(bundle);
    }


    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     * <p/>
     * public class ProfileServerAsyncTask extends AsyncTask<Void, Void, IProfile> {
     * <p/>
     * private Context context;
     * <p/>
     * /**
     *
     * @param context
     * @param statusText /
     *                   public ProfileServerAsyncTask(Context context, View statusText) {
     *                   this.context = context;
     *                   }
     * @Override protected IProfile doInBackground(Void... params) {
     * try {
     * ServerSocket serverSocket = new ServerSocket(8988);
     * Socket client = serverSocket.accept();
     * <p/>
     * InputStream inputstream = client.getInputStream();
     * IProfile profile = readProfile(context, inputstream);
     * Log.d(WiFiDirectActivity.TAG, "Profile: received");
     * serverSocket.close();
     * return profile;
     * } catch (IOException e) {
     * Toast.makeText(getActivity(), "Received a corrupted profile", Toast.LENGTH_LONG);
     * return null;
     * }
     * }
     * <p/>
     * /*
     * (non-Javadoc)
     * @Override protected void onPostExecute(IProfile result) {
     * if (result != null) {
     * setStatus("Profile copied - " + result);
     * try {
     * listener.getFriendsService().insertProfile(result);
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * }
     * <p/>
     * }
     * <p/>
     * /*
     * (non-Javadoc)
     * @Override protected void onPreExecute() {
     * setStatus("Opening a server socket");
     * }
     * <p/>
     * }
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     * @see android.os.AsyncTask#onPreExecute()
     */

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
            String host = bundle.getString(ProfileTransferService.EXTRAS_GROUP_OWNER_ADDRESS);
            int port = bundle.getInt(ProfileTransferService.EXTRAS_GROUP_OWNER_PORT);
            boolean isProfile = bundle.containsKey(ProfileTransferService.EXTRAS_PROFILE_SEND);
            String toSend;
            if(isProfile) {
                toSend = bundle.getString(ProfileTransferService.EXTRAS_PROFILE_SEND);
                try {
                    profile = new BasicProfileFactory().newProfile(new JSONObject(toSend));
                } catch (JSONException e) {
                    throw new IllegalArgumentException("Didn't receive a JSON! A JSON profile was expected");
                }
            }
            socket = new Socket();
            DataOutputStream ostream = null;
            DataInputStream istream = null;
            String received = null;
            CommunicationProtocol protocol = new CommunicationProtocol(CommunicationProtocol.Actor.Client, this);
            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 5000);
                running = true;
                Log.d(WiFiDirectActivity.TAG, "Client socket - " + String.valueOf(socket.isConnected()));
                ostream = new DataOutputStream(socket.getOutputStream());

                istream = new DataInputStream(socket.getInputStream());
                //byte[] buffer = new byte[1024];
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
            UsersDB db = new UsersDB(getActivity());
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
            throw new UnsupportedOperationException("This client sends only profiles!");
        }
    }
}
