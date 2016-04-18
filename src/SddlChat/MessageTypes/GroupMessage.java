package SddlChat.MessageTypes;

import lac.cnclib.net.groups.Group;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import java.io.Serializable;

/**
 * Created by gabriel on 14/02/16.
 */
public class GroupMessage implements Serializable {

    private Group group;
    private Serializable obj;

    public GroupMessage(Group group, Serializable obj) {
        this.group = group;
        this.obj = obj;
    }

    public GroupMessage(Serializable obj) {
        this.obj = obj;
    }

    public GroupMessage() {
    }

    public Serializable getObj() {
        return obj;
    }

    public void setObj(Serializable obj) {
        this.obj = obj;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupMessage that = (GroupMessage) o;

        if (that.getGroup() != group)
        {
            if (group == null && that.group != null)
                return false;
            if (group != null && that.group == null)
                return false;
            if (!group.equals(that.group))
                return false;
        }

        return obj != null ? obj.equals(that.obj) : that.obj == null;

    }

    @Override
    public int hashCode() {
        int result = group != null ? group.hashCode() : 0;
        result = 31 * result + (obj != null ? obj.hashCode() : 0);
        return result;
    }
}
