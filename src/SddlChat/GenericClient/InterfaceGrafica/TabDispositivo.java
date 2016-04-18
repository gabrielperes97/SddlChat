package SddlChat.GenericClient.InterfaceGrafica;

import SddlChat.GenericClient.SddlChat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gabriel on 10/02/16.
 */
public abstract class TabDispositivo extends JPanel{
    private JTextField tfMensagemDp;
    private JButton btEnviarDp;
    private JButton btSairDp;
    private JPanel rootPanelDispositivo;
    private JList lMensagens;

    private UUID recipient;
    private SddlChat client;
    private DefaultListModel msgList;

    public TabDispositivo(UUID recipient, SddlChat client)
    {
        add(rootPanelDispositivo);
        setInicial();
        setVisible(true);
        this.client = client;
        this.recipient = recipient;
        msgList = new DefaultListModel();
    }

    private TabDispositivo getInstance()
    {
        return this;
    }

    private void setInicial()
    {
        btEnviarDp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendDspMessage(tfMensagemDp.getText(), recipient);
                inserirNovaMensagem("Eu: "+tfMensagemDp.getText());
                tfMensagemDp.setText("");
            }
        });
        btSairDp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClose(getInstance());
            }
        });
    }

    private void inserirNovaMensagem(String str)
    {
        msgList.addElement(str);
        lMensagens.setModel(msgList);
    }

    public abstract void onClose(TabDispositivo tabDispositivo);

    public void receiveMessage(Serializable content)
    {
        inserirNovaMensagem("Ele: "+content);
    }

    public UUID getRecipient() {
        return recipient;
    }
}
