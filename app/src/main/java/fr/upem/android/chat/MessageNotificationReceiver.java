/**
 *
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


package fr.upem.android.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.android.wifidirect.ProfileTransferService;
import com.example.android.wifidirect.R;

import org.json.JSONException;

import java.util.List;

import fr.upem.android.communication.GroupManager;
import fr.upem.android.communication.Message;
import fr.upem.android.communication.ServerService;
import fr.upem.mdigangi.dreseau.main.MainActivity;

public class MessageNotificationReceiver extends BroadcastReceiver {

    private static final int TASK_STACK_BUILDER_CODE = 167;
    public static final int MESSAGE_NOTIFICATION_ID = 168;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent backIntent = new Intent(context, ChatActivity.class);
        Message message;
        try {
            message = Message.Builder.rebuildMessage(
                    intent.getStringExtra(ProfileTransferService.EXTRAS_MESSAGE_SEND));
        } catch (JSONException e) {
            throw new IllegalArgumentException();
        }
        boolean isGO = intent.getBooleanExtra(ServerService.EXTRAS_IS_GROUP_OWNER, false);
        backIntent.putExtra(ServerService.EXTRAS_IS_GROUP_OWNER, isGO);
        if(!isGO){
            String go_address = intent.getStringExtra(ProfileTransferService.EXTRAS_GROUP_OWNER_ADDRESS);
            backIntent.putExtra(ProfileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, go_address);
        }
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(ChatActivity.class);
        taskStackBuilder.addNextIntent(backIntent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(TASK_STACK_BUILDER_CODE, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(message.getAuthor())
                .setContentText(message.getText())
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(MESSAGE_NOTIFICATION_ID, notification);
        List<Message> list = GroupManager.getGroupManager().getSavedMessages();
        list.add(message);
        GroupManager.getGroupManager().saveMessages(list);
    }
}
