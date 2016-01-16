package fr.upem.android.communication;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mattia on 15/01/16.
 */
public class GroupManager {

    public interface GroupManagerListener{
        void onAddedIp();
    }

    private final List<String> groupIps = new LinkedList<>();

    private static final GroupManager instance = new GroupManager();
    private List<Message> messages = new LinkedList<>();
    private boolean saved = false;

    //One only instance of this class is allowed at a time
    private GroupManager() {}

    public static GroupManager getGroupManager() {
        return instance;
    }

    public void addIp(String ip){
        if(ip == null){
            throw new NullPointerException("IP must be not null!");
        }
        if(!groupIps.contains(ip)) {
            groupIps.add(ip);
        }
    }

    public Iterator<String> iterator() {
        return groupIps.iterator();
    }

    public void removeIp(String host) {
        groupIps.remove(host);
    }

    public boolean hasIp(){
        return (groupIps != null && groupIps.size() > 0);
    }
    /**
     * Save a list of messages for the lasting of a connection
     * @param list the list to save
     * @throws NullPointerException if list is null
     */
    public void saveMessages(List<Message> list){
        if(list == null){
            throw new NullPointerException();
        }
        Log.d("GroupManager", "saving list - " + list);
        this.messages = list;
        saved = true;
    }

    /**
     * Return the saved list of messages. To ensure the correct working, a call to hasSaved is
     * needed.
     * @return
     */
    public List<Message> getSavedMessages() {
        List<Message> proxy = messages;
        Log.d("GroupManager", messages.toString());
        messages = null;
        saved = false;
        return proxy;
    }

    /**
     *
     * @return whether or not it has saved a list
     */
    public boolean hasSaved(){
        Log.d("GroupManager", "hasSaved - " + saved);
        return saved;
    }

    /**
     * Erase the list of message.To call when the connection is closed.
     * @throws NullPointerException whether the list is null.
     */
    public void clearMessageList(){
        if(saved) {
            messages.clear();
            saved = false;
        }
    }

    public void clearIpList(){
        if(groupIps != null) {
            groupIps.clear();
        }
    }
}
