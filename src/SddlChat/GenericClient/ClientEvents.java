package SddlChat.GenericClient;

import SddlChat.Dados.GroupList;
import lac.cnclib.net.groups.Group;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gabriel on 16/03/16.
 */
public interface ClientEvents {

    void error(Exception e);

    void status(String s);

    void onRefresh(GroupList groupList);

    void newGroupMessage(Serializable content, Group group);

    void newIndividualMessage(Serializable content, UUID sender);
}
