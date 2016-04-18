package SddlChat.ProcessingNode;

import SddlChat.MessageTypes.CtrlMessage;
import SddlChat.Dados.GroupList;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by gabriel on 13/02/16.
 */
public class NodeServer implements NodeConnectionListener {

    public static final String UUID_PROCESSING_NODE = "e4e140e2-a30a-41d6-9ebd-bd31ba7ba200";
    private MrUdpNodeConnection connection;
    private static final String GATEWAY_IP = "127.0.0.1";
    private static final int GATEWAY_PORT = 5500;

    private GroupList grupos;
    private HashSet<UUID> dispositivos;

    public NodeServer(String ip, int port)
    {
        try {
            connection = new MrUdpNodeConnection(UUID.fromString(UUID_PROCESSING_NODE));
            connection.connect(new InetSocketAddress(ip, port));
            connection.addNodeConnectionListener(this);
            grupos = new GroupList();
            dispositivos = new HashSet<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NodeServer()
    {
        try {
            connection = new MrUdpNodeConnection(UUID.fromString(UUID_PROCESSING_NODE));
            connection.connect(new InetSocketAddress(GATEWAY_IP, GATEWAY_PORT));
            connection.addNodeConnectionListener(this);
            grupos = new GroupList();
            dispositivos = new HashSet<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected(NodeConnection nodeConnection) {
        ApplicationMessage appMsg = new ApplicationMessage();
        appMsg.setContentObject("Registrando");
        try {
            nodeConnection.sendMessage(appMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Executando\n UUID:"+connection.getClientUUID());
    }

    @Override
    public void reconnected(NodeConnection nodeConnection, SocketAddress socketAddress, boolean b, boolean b1) {
        System.out.println("Reconectado");
    }

    @Override
    public void disconnected(NodeConnection nodeConnection) {
        System.out.println("Disconectado");
    }

    @Override
    public void newMessageReceived(NodeConnection nodeConnection, Message message) {
        dispositivos.add(message.getSenderID());
        if (message.getContentObject() instanceof GroupList)
        {
            ApplicationMessage answer = new ApplicationMessage();
            answer.setRecipientID(message.getSenderID());
            answer.setContentObject(grupos);
            try {
                connection.sendMessage(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            if (message.getContentObject() instanceof CtrlMessage)
            {
                CtrlMessage ctrlMessage = (CtrlMessage) message.getContentObject();
                if (ctrlMessage.getTipo() == CtrlMessage.tiposControle.JOIN_GROUP)
                    grupos.addUUIDtoGroup(message.getSenderID(), ctrlMessage.getGroup());
                else
                    grupos.removeUIIDtoGroup(message.getSenderID(), ctrlMessage.getGroup());
                mandarAtualizacaoParaTodos();
            }
        }

    }

    @Override
    public void unsentMessages(NodeConnection nodeConnection, List<Message> list) {

    }

    @Override
    public void internalException(NodeConnection nodeConnection, Exception e) {

    }

    private void mandarAtualizacaoParaTodos()
    {
        ApplicationMessage answer = new ApplicationMessage();
        answer.setContentObject(grupos);
        for (UUID dispositivo : dispositivos) {
            answer.setRecipientID(dispositivo);
            try {
                connection.sendMessage(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        if (args.length == 0)
            new NodeServer();
        else
        {
            new NodeServer(args[0], Integer.parseInt(args[1]));
        }
    }
}
