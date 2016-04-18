package SddlChat.MessageTypes;

import SddlChat.Dados.GroupList;
import lac.cnclib.net.groups.Group;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gabriel on 13/02/16.
 */
public class CtrlMessage implements Serializable {

    public enum tiposControle
    {
        LEAVE_GROUP, JOIN_GROUP, INITIAL_MESSAGE, GROUP_CONTROL
    }

    private tiposControle tipo;
    private Group group;
    private UUID uuid;
    private GroupList content;

    public CtrlMessage(tiposControle tipo, Group group) {
        this.tipo = tipo;
        this.group = group;
    }

    public CtrlMessage(tiposControle tipo, UUID uuid) {
        this.tipo = tipo;
        this.uuid = uuid;
    }

    public CtrlMessage(tiposControle tipo, GroupList content) {
        this.tipo = tipo;
        this.content = content;
    }

    public CtrlMessage(tiposControle tipo) {
        this.tipo = tipo;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public tiposControle getTipo() {
        return tipo;
    }

    public void setTipo(tiposControle tipo) {
        this.tipo = tipo;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public GroupList getContent() {
        return content;
    }

    public void setContent(GroupList content) {
        this.content = content;
    }
}
