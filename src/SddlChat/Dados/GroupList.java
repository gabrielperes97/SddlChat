package SddlChat.Dados;

import lac.cnclib.net.groups.Group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by gabriel on 13/02/16.
 */
public class GroupList extends ArrayList<DadosGrupo> implements Serializable{

    public GroupList()
    {
        super();
    }

    public void addUUIDtoGroup(UUID uuid, Group group)
    {
        int id = indexOf(group);
        if (id < 0) {
            DadosGrupo dadosGrupo = new DadosGrupo(group);
            dadosGrupo.addUuid(uuid);
            add(dadosGrupo);
        }
        else
        {
            DadosGrupo dadosGrupo = get(id);
            dadosGrupo.addUuid(uuid);
            set(id, dadosGrupo);
        }
    }

    public void removeUIIDtoGroup(UUID uuid, Group group)
    {
        int id = indexOf(group);
        if (id >= 0) {
            DadosGrupo dadosGrupo = get(id);
            dadosGrupo.removeUuid(uuid);
            set(id, dadosGrupo);
        }
    }

    public DadosGrupo get(Group group)
    {
        int id = indexOf(group);
        if (id >= 0)
            return get(id);
        else
            return null;
    }
}
