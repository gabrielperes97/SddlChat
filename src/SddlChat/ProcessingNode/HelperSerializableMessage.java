package SddlChat.ProcessingNode;

import lac.cnclib.net.groups.Group;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;

import java.io.Serializable;
import java.util.UUID;

public class HelperSerializableMessage {

    private SddlLayer core;
    private UDIDataReaderListener<ApplicationObject> dataReaderListener;

    public HelperSerializableMessage(UDIDataReaderListener<ApplicationObject> dataReaderListener)
    {
        this.dataReaderListener = dataReaderListener;

        core = UniversalDDSLayerFactory.getInstance();
        core.createParticipant(UniversalDDSLayerFactory.CNET_DOMAIN);

        core.createPublisher();
        core.createSubscriber();

        Object topicObject = core.createTopic(PrivateMessage.class, PrivateMessage.class.getSimpleName());
        core.createDataWriter(topicObject);

        if (this.dataReaderListener != null)
        {
            Object messageObject = core.createTopic(Message.class, Message.class.getSimpleName());
            core.createDataReader(this.dataReaderListener, messageObject);
        }
    }

    public void sendMessageToAll(Serializable serializableMessage)
    {
        PrivateMessage msg = createBroadcastMessage();
        writeTopic(serializableMessage, msg);
    }

    public void sendGroupMessage(Serializable serializableMessage, Group group)
    {
        PrivateMessage msg = createGroupMessage(group);

        writeTopic(serializableMessage, msg);
    }

    public void sendNodeMessage(Serializable serializableMessage, UUID nodeId, UUID gatewayId)
    {
        PrivateMessage msg = createBaseMessage(nodeId, gatewayId);

        writeTopic(serializableMessage, msg);
    }

    private void writeTopic(Serializable content, PrivateMessage message)
    {
        ApplicationMessage appMsg = new ApplicationMessage();
        appMsg.setContentObject(content);
        message.setMessage(Serialization.toProtocolMessage(appMsg));

        core.writeTopic(PrivateMessage.class.getSimpleName(), message);
    }

    private PrivateMessage createBaseMessage()
    {
        PrivateMessage msg = new PrivateMessage();
        msg.setGatewayId(UniversalDDSLayerFactory.BROADCAST_ID);
        msg.setNodeId(UniversalDDSLayerFactory.BROADCAST_ID);
        return msg;
    }

    private PrivateMessage createBaseMessage(UUID nodeId, UUID gatewayId)
    {
        PrivateMessage msg = new PrivateMessage();
        msg.setNodeId(nodeId);
        msg.setGatewayId(gatewayId);
        return msg;
    }

    private PrivateMessage createGroupMessage(Group group)
    {
        PrivateMessage msg = createBaseMessage();
        msg.setGroupType(group.getGroupType());
        msg.setGroupId(group.getGroupID());
        return msg;
    }

    private PrivateMessage createBroadcastMessage()
    {
        PrivateMessage msg = createBaseMessage();

        msg.setGroupType(UniversalDDSLayerFactory.BROADCAST_FLAG);
        msg.setGroupId(UniversalDDSLayerFactory.BROADCAST_FLAG);

        return msg;
    }
}
