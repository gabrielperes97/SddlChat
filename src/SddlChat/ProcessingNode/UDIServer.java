package SddlChat.ProcessingNode;

import SddlChat.Dados.GroupList;
import SddlChat.MessageTypes.CtrlMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gabriel on 28/02/16.
 */
public class UDIServer implements UDIDataReaderListener<ApplicationObject> {

    private HelperSerializableMessage hsm;
    private GroupList groupList;


    public static void main(String[] args)
    {
        new UDIServer();
        System.out.println("UDI Server started...");
    }

    public UDIServer()
    {
        hsm = new HelperSerializableMessage(this);
        groupList = new GroupList();
    }

    @Override
    public void onNewData(ApplicationObject applicationObject) {
        Message msg = (Message) applicationObject;
        Serializable serializable = Serialization.fromJavaByteStream(msg.getContent());
        if (serializable instanceof CtrlMessage)
        {
            CtrlMessage ctrl = (CtrlMessage) serializable;
            if (ctrl.getTipo() == CtrlMessage.tiposControle.INITIAL_MESSAGE)
            {
                ctrl.setContent(groupList);
                hsm.sendNodeMessage(ctrl, msg.getSenderId(), msg.getGatewayId());
            }
            if (ctrl.getTipo() == CtrlMessage.tiposControle.JOIN_GROUP) {
                groupList.addUUIDtoGroup(msg.getSenderId(), ctrl.getGroup());
                mandarAtualizacaoGrupos();
            }
            if (ctrl.getTipo() == CtrlMessage.tiposControle.LEAVE_GROUP) {
                groupList.removeUIIDtoGroup(msg.getSenderId(), ctrl.getGroup());
                mandarAtualizacaoGrupos();
            }
            if (ctrl.getTipo() == CtrlMessage.tiposControle.GROUP_CONTROL)
            {
                mandarAtualizacaoGrupos();
            }
        }
    }

    private void mandarAtualizacaoNode(UUID node, UUID nodeGateway)
    {
        hsm.sendNodeMessage(groupList, node, nodeGateway);
    }

    private void mandarAtualizacaoGrupos()
    {
        CtrlMessage ctrl = new CtrlMessage(CtrlMessage.tiposControle.GROUP_CONTROL, groupList);
        hsm.sendMessageToAll(ctrl);
    }
}
