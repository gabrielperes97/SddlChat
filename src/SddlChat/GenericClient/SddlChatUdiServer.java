package SddlChat.GenericClient;

import SddlChat.MessageTypes.CtrlMessage;
import SddlChat.MessageTypes.GroupMessage;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.groups.Group;
import lac.cnclib.net.groups.GroupCommunicationManager;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gabriel on 17/04/16.
 */
public class SddlChatUdiServer extends SddlChat {

    private UUID serverUUID;
    private GroupMessage lastGroupMessage;

    public SddlChatUdiServer(String ip, int porta, ClientEvents clientEvents) {
        super(ip, porta, clientEvents);
    }

    public SddlChatUdiServer(String ip, int porta, ClientEvents clientEvents, UUID uuid) {
        super(ip, porta, clientEvents, uuid);
    }

    public SddlChatUdiServer(String ip, int porta, ClientEvents clientEvents, int predef) {
        super(ip, porta, clientEvents, predef);
    }

    private void sendInicialMessage()
    {
        CtrlMessage ctrl = new CtrlMessage(CtrlMessage.tiposControle.INITIAL_MESSAGE);
        sendControlMessage(ctrl);
    }

    @Override
    protected void sendControlMessage(Serializable content)
    {
        sendDspMessage(content, null);
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
    public void connected(NodeConnection nodeConnection) {
        groupManager = new GroupCommunicationManager(nodeConnection);
        groupManager.addMembershipListener(this);
        uuid = nodeConnection.getUuid();
        sendInicialMessage();
        clientEvents.status("Conectado");
    }

    @Override
    public void newMessageReceived(NodeConnection nodeConnection, Message message) {
        Serializable content = message.getContentObject();
        if (content instanceof CtrlMessage)
        {
            CtrlMessage ctrl = (CtrlMessage) content;
            if (ctrl.getTipo() == CtrlMessage.tiposControle.INITIAL_MESSAGE)
            {
                this.serverUUID = message.getSenderID();
                newControlMessage(ctrl.getContent()); //GroupList
            }
            if (ctrl.getTipo() == CtrlMessage.tiposControle.GROUP_CONTROL)
            {
                newControlMessage(ctrl.getContent()); //GroupList
            }
        }
        else {
            if (content instanceof GroupMessage){
                GroupMessage gp = (GroupMessage) content;
                if (!gp.equals(lastGroupMessage)) {
                    clientEvents.newGroupMessage(gp.getObj(), gp.getGroup());
                }
                else
                    lastGroupMessage = new GroupMessage("");
            } else {
                clientEvents.newIndividualMessage(content, message.getSenderID());
            }
        }
    }
}
