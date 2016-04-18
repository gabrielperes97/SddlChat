package SddlChat.Dados;

import lac.cnclib.net.groups.Group;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by gabriel on 13/02/16.
 */
public class DadosGrupo extends Group{

    private HashSet<UUID> uuids = new HashSet<>();

    public DadosGrupo(int groupType, int groupID) {
        super(groupType, groupID);
    }

    public DadosGrupo(Group group)
    {
        super(group.getGroupType(), group.getGroupID());
    }

    public int getQuantidade() {
        return this.uuids.size();
    }

    public HashSet<UUID> getUuids() {
        return uuids;
    }

    public void setUuids(HashSet<UUID> uuids) {
        this.uuids = uuids;
    }

    public boolean addUuid(UUID uuid)
    {
        return this.uuids.add(uuid);
    }

    public boolean removeUuid(UUID uuid)
    {
        return this.uuids.remove(uuid);
    }

    public Group getGroup()
    {
        return this;
    }

    @Override
    public boolean equals(Object otherGroup) {
        if (this == otherGroup) return true;
        if (otherGroup == null || !(otherGroup instanceof Group)) return false;
        Group oGroup = (Group) otherGroup;
        if ((groupID == oGroup.getGroupID()) && groupType == oGroup.getGroupType())
        {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "<"+groupType+", "+groupID+"> "+getQuantidade()+" Membros";
    }
}
