package SddlChat.GenericClient;

import SddlChat.Dados.GroupList;
import SddlChat.MessageTypes.CtrlMessage;
import SddlChat.MessageTypes.GroupMessage;
import SddlChat.ProcessingNode.NodeServer;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.groups.Group;
import lac.cnclib.net.groups.GroupCommunicationManager;
import lac.cnclib.net.groups.GroupMembershipListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

/**
 * Created by gabriel on 17/04/16.
 */
public abstract class SddlChat implements NodeConnectionListener, GroupMembershipListener{

    protected MrUdpNodeConnection connection;
    protected GroupCommunicationManager groupManager;
    protected UUID uuid;
    protected ClientEvents clientEvents;

    public static final String UUID_PREDEF1 = "f3276dbb-3c91-4228-8b5b-c55ce70c3e3e";
    public static final String UUID_PREDEF2 = "72c94797-c182-4db6-9a46-a21bd61e7dc1";
    public static final String UUID_PREDEF3 = "f64865d4-f5e4-4306-9164-3e0e71e2dede";
    public static final String UUID_PREDEF4 = "6b7c21ba-1225-40bb-aead-ad6326920474";
    public static final String UUID_PREDEF5 = "4fc6aabe-7d6f-4ecd-8d11-179487ff3112";

    public SddlChat(String ip, int porta, ClientEvents clientEvents)
    {
        uuid = UUID.randomUUID();
        this.clientEvents = clientEvents;
        construtor(uuid, ip, porta);
    }

    public SddlChat(String ip, int porta, ClientEvents clientEvents, UUID uuid)
    {
        this.uuid = uuid;
        this.clientEvents = clientEvents;
        construtor(uuid, ip, porta);
    }

    public SddlChat(String ip, int porta, ClientEvents clientEvents, int predef)
    {
        switch (predef)
        {
            case 1:
                this.uuid = UUID.fromString(UUID_PREDEF1);
                break;
            case 2:
                this.uuid = UUID.fromString(UUID_PREDEF2);
                break;
            case 3:
                this.uuid = UUID.fromString(UUID_PREDEF3);
                break;
            case 4:
                this.uuid = UUID.fromString(UUID_PREDEF4);
                break;
            case 5:
                this.uuid = UUID.fromString(UUID_PREDEF5);
                break;
            default:
                this.uuid = UUID.randomUUID();
                break;
        }
        this.clientEvents = clientEvents;
        construtor(this.uuid, ip, porta);
    }

    protected void construtor(UUID uuid, String ip, int porta)
    {
        try
        {
            connection = new MrUdpNodeConnection(uuid);
            connection.connect(new InetSocketAddress(ip, porta));
            connection.addNodeConnectionListener(this);
        } catch (IOException e) {
            clientEvents.error(e);
            e.printStackTrace();
        }
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    protected void newControlMessage(Serializable content)
    {
        if (content instanceof GroupList)
            clientEvents.onRefresh((GroupList) content);
    }

    public void callRefresh()
    {
        sendControlMessage(new GroupList());
    }

    protected void sendControlMessage(Serializable content)
    {
        sendDspMessage(content, UUID.fromString(NodeServer.UUID_PROCESSING_NODE));
    }

    public void sendDspMessage(Serializable obj, UUID uuid)
    {
        ApplicationMessage appMsg = new ApplicationMessage();
        appMsg.setRecipientID(uuid);
        appMsg.setContentObject(obj);
        try {
            connection.sendMessage(appMsg);
        } catch (IOException e) {
            clientEvents.error(e);
            e.printStackTrace();
        }
    }

    public void sendGroupMessage(Serializable obj, Group group)
    {
        GroupMessage groupMessage = new GroupMessage();
        ApplicationMessage appMsg = new ApplicationMessage();
        groupMessage.setGroup(group);
        groupMessage.setObj(obj);
        appMsg.setContentObject(groupMessage);
        try {
            groupManager.sendGroupcastMessage(appMsg, group);
        } catch (IOException e) {
            clientEvents.error(e);
            e.printStackTrace();
        }
    }

    public void joinGroup(Group group)
    {
        try {
            sendControlMessage(new CtrlMessage(CtrlMessage.tiposControle.JOIN_GROUP, group));
            groupManager.joinGroup(group);
            clientEvents.status("Entrou no grupo "+group.toString());
        } catch (IOException e) {
            clientEvents.error(e);
            e.printStackTrace();
        }
    }

    public void leaveGroup(Group group)
    {
        try {
            sendControlMessage(new CtrlMessage(CtrlMessage.tiposControle.LEAVE_GROUP, group));
            groupManager.leaveGroup(group);
            clientEvents.status("Saiu do grupo "+group.toString());
        } catch (IOException e) {
            clientEvents.error(e);
            e.printStackTrace();
        }
    }

    public void disconect()
    {
        try {
            if (groupManager.getAllGroups() != null)
                groupManager.leaveGroup(groupManager.getAllGroups());
            connection.disconnect();
        } catch (IOException e) {
            clientEvents.error(e);
            e.printStackTrace();
        }
        clientEvents.status("Desconectado");
    }

    @Override
    public void enteringGroups(List<Group> list) {
        callRefresh();
    }

    @Override
    public void leavingGroups(List<Group> list) {
        callRefresh();
    }

    @Override
    public void connected(NodeConnection nodeConnection) {
        groupManager = new GroupCommunicationManager(nodeConnection);
        groupManager.addMembershipListener(this);
        callRefresh();
        clientEvents.status("Conectado");
    }

    @Override
    public void reconnected(NodeConnection nodeConnection, SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {
        callRefresh();
        clientEvents.status("Reconectado\n Novo endereço: "+endPoint.toString());
        if (wasHandover)
            clientEvents.status("Ocorreu handover");
        if (wasMandatory)
            clientEvents.status("Handover é obrigatorio");
    }

    @Override
    public void disconnected(NodeConnection nodeConnection) {
        clientEvents.status("Disconectado");
    }

    @Override
    public void unsentMessages(NodeConnection nodeConnection, List<Message> list) {
        clientEvents.status("Temos "+list.size()+" mensagens não enviadas");
    }

    @Override
    public void internalException(NodeConnection nodeConnection, Exception e) {
        clientEvents.error(e);
    }
}
