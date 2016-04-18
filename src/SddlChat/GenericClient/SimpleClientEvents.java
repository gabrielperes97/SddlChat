package SddlChat.GenericClient;

import SddlChat.Dados.GroupList;
import lac.cnclib.net.groups.Group;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gabriel on 16/03/16.
 */
public class SimpleClientEvents implements ClientEvents {


    @Override
    public void error(Exception e) {
        System.out.println("Error: "+e.toString());
    }

    @Override
    public void status(String s) {
        System.out.println("Status: "+s);
    }

    @Override
    public void newGroupMessage(Serializable content, Group group) {
        System.out.println("Group <"+group.getGroupType()+", "+group.getGroupID()+">: "+content.toString());
    }

    @Override
    public void newIndividualMessage(Serializable content, UUID sender) {
        System.out.println("UUID "+sender+": "+content.toString());
    }

    @Override
    public void onRefresh(GroupList groupList) {

    }
}
