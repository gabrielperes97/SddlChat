package SddlChat.GenericClient.InterfaceGrafica;

import SddlChat.Dados.DadosGrupo;
import SddlChat.GenericClient.SddlChat;
import lac.cnclib.net.groups.Group;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gabriel on 10/02/16.
 */
public abstract class TabGrupo extends JPanel{
    private JTextField tfMensagemGp;
    private JButton btEnviarGp;
    private JButton btSairGp;
    private JList lConectadosGp;
    private JPanel rootPanelGrupo;
    private JList lMensagens;

    private Group group;
    private SddlChat client;
    private Main tabMain;

    private DefaultListModel msgList;

    public TabGrupo(Group group, SddlChat client, Main tabMain)
    {
        add(rootPanelGrupo);
        setInicial();
        setVisible(true);
        this.group = group;
        this.client = client;
        this.tabMain = tabMain;
    }

    private TabGrupo getInstance()
    {
        return this;
    }

    private void setInicial()
    {
        btEnviarGp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendGroupMessage(tfMensagemGp.getText(), group);
                inserirNovaMensagem("Eu: "+tfMensagemGp.getText());
                tfMensagemGp.setText("");
            }
        });
        btSairGp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.leaveGroup(group);
                onClose(getInstance());
            }
        });
        lConectadosGp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2)
                {
                    UUID uuid = (UUID) lConectadosGp.getSelectedValue();
                    if (uuid != client.getUuid())
                        tabMain.abrirDispositivo(uuid);
                }
                super.mouseClicked(e);
            }
        });
        msgList = new DefaultListModel();
        lMensagens.setModel(msgList);
    }

    public void refresh(DadosGrupo dadosGrupo)
    {
        DefaultListModel list = new DefaultListModel();
         for (UUID uuid : dadosGrupo.getUuids())
         {
             list.addElement(uuid);
         }
        lConectadosGp.setModel(list);
    }

    public abstract void onClose(TabGrupo tabGrupo);

    private void inserirNovaMensagem(String str)
    {
        msgList.addElement(str);
        lMensagens.setModel(msgList);
    }

    public void receiveMessage(Serializable content)
    {
        inserirNovaMensagem("Alguem: "+content);
    }

    public Group getGroup() {
        return group;
    }
}
