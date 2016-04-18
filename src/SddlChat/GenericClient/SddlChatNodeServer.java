package SddlChat.GenericClient;

import SddlChat.MessageTypes.GroupMessage;
import SddlChat.ProcessingNode.NodeServer;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.groups.Group;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gabriel on 17/04/16.
 */
public class SddlChatNodeServer extends SddlChat {

    private GroupMessage lastGroupMessage;

    public SddlChatNodeServer(String ip, int porta, ClientEvents clientEvents) {
        super(ip, porta, clientEvents);
    }

    public SddlChatNodeServer(String ip, int porta, ClientEvents clientEvents, UUID uuid) {
        super(ip, porta, clientEvents, uuid);
    }

    public SddlChatNodeServer(String ip, int porta, ClientEvents clientEvents, int predef) {
        super(ip, porta, clientEvents, predef);
    }

    @Override
    public void sendGroupMessage(Serializable obj, Group group)
    {
        GroupMessage groupMessage = new GroupMessage();
        ApplicationMessage appMsg = new ApplicationMessage();
        groupMessage.setGroup(group);
        groupMessage.setObj(obj);
        appMsg.setContentObject(groupMessage);
        lastGroupMessage = groupMessage;
        try {
            groupManager.sendGroupcastMessage(appMsg, group);
        } catch (IOException e) {
            clientEvents.error(e);
            e.printStackTrace();
        }
    }

    @Override
    public void newMessageReceived(NodeConnection nodeConnection, Message message) {
        Serializable content = message.getContentObject();
        if (message.getSenderID().equals(UUID.fromString(NodeServer.UUID_PROCESSING_NODE)))
            newControlMessage(content);
        else {
            if (content instanceof GroupMessage) {
                GroupMessage gp = (GroupMessage) content;
                if (!gp.equals(lastGroupMessage))
                    clientEvents.newGroupMessage(gp.getObj(), gp.getGroup());
                else
                    lastGroupMessage = new GroupMessage("");
            } else
            {
                clientEvents.newIndividualMessage(content, message.getSenderID());
            }
        }
    }
}
