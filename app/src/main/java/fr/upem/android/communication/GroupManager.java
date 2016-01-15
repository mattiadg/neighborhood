package fr.upem.android.communication;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by mattia on 15/01/16.
 */
public class GroupManager {

    private final List<String> groupIps = new LinkedList<>();

    private static final GroupManager instance = new GroupManager();

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
}
