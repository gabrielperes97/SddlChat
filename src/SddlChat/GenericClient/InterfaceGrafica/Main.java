package SddlChat.GenericClient.InterfaceGrafica;

import SddlChat.Dados.DadosGrupo;
import SddlChat.GenericClient.*;
import SddlChat.Dados.GroupList;
import lac.cnclib.net.groups.Group;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gabriel on 10/02/16.
 */
public class Main extends JFrame{
    private JPanel rootPanel;
    private JTabbedPane tabbedPane1;
    private JList lGrupos;
    private JSpinner sTipo;
    private JSpinner sGrupo;
    private JButton btCriar;
    private JTextField tfIp;
    private JTextField tfPorta;
    private JButton btConectar;
    private JTextField tfUuidDsp;
    private JButton btIniciarDp;
    private JTextPane tpConsole;
    private JRadioButton nodeServerRadioButton;
    private JRadioButton UDIServerRadioButton;
    private JRadioButton randomUUIDRadioButton;
    private JRadioButton predef3RadioButton;
    private JRadioButton predef4RadioButton;
    private JRadioButton predef1RadioButton;
    private JRadioButton predef2RadioButton;
    private JRadioButton predef5RadioButton;

    private SddlChat client;
    private List<Group> gruposAbertos;
    private List<UUID> dispositivosAbertos;

    private int predef=-1;

    public Main() {
        super("SddlChat");
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setInicials();
        setVisible(true);
    }

    public Main(int predef)
    {
        super("SddlChat");
        this.predef = predef;
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setInicials();
        setVisible(true);
    }

    private Main getInstance()
    {
        return this;
    }

    private void setInicials()
    {
        gruposAbertos = new ArrayList<>();
        dispositivosAbertos = new ArrayList<>();
        sTipo.setValue(1);
        sTipo.setEnabled(false);
        btCriar.setEnabled(false);
        btConectar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(client != null)
                {
                    client.disconect();
                    client = null;
                    tfIp.setEnabled(true);
                    tfPorta.setEnabled(true);
                    btConectar.setText("Conectar");
                    btCriar.setEnabled(true);
                    setInicials();
                }else {
                    ClientEvents clientEvents = new ClientEvents() {
                        @Override
                        public void error(Exception e) {
                            consolePrint(e.toString());
                        }

                        @Override
                        public void status(String s) {
                            consolePrint(s);
                        }

                        @Override
                        public void onRefresh(GroupList groupList) {
                            DefaultListModel lista = new DefaultListModel();
                            for (DadosGrupo dadosGrupo : groupList) {
                                lista.addElement(dadosGrupo);
                                for (Component component : tabbedPane1.getComponents()) {
                                    if (component instanceof TabGrupo) {
                                        TabGrupo tab = (TabGrupo) component;
                                        if (tab.getGroup().equals(dadosGrupo.getGroup())) {
                                            tab.refresh(dadosGrupo);
                                        }
                                    }
                                }
                            }
                            lGrupos.setModel(lista);
                            lGrupos.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    if (e.getClickCount() == 2) {
                                        DadosGrupo dado = (DadosGrupo) lGrupos.getSelectedValue();
                                        abrirGrupo(dado.getGroup());
                                    }
                                    super.mouseClicked(e);
                                }
                            });
                        }

                        @Override
                        public void newGroupMessage(Serializable content, Group group) {
                            abrirGrupo(group);
                            for (Component component : tabbedPane1.getComponents()) {
                                if (component instanceof TabGrupo) {
                                    TabGrupo tab = (TabGrupo) component;
                                    if (tab.getGroup().equals(group)) {
                                        tab.receiveMessage(content);
                                    }
                                }
                            }
                        }

                        @Override
                        public void newIndividualMessage(Serializable content, UUID sender) {
                            abrirDispositivo(sender);
                            for (Component component : tabbedPane1.getComponents()) {
                                if (component instanceof TabDispositivo) {
                                    TabDispositivo tab = (TabDispositivo) component;
                                    if (tab.getRecipient().equals(sender)) {
                                        tab.receiveMessage(content);
                                    }
                                }
                            }
                        }
                    };
                    predef = getPredefByRadio();
                    if (nodeServerRadioButton.isSelected() && !UDIServerRadioButton.isSelected())
                        client = new SddlChatNodeServer(tfIp.getText(), Integer.parseInt(tfPorta.getText()), clientEvents, predef);
                    else
                        client = new SddlChatUdiServer(tfIp.getText(), Integer.parseInt(tfPorta.getText()), clientEvents, predef);

                    if (client != null) {
                        tfIp.setEnabled(false);
                        tfPorta.setEnabled(false);
                        btConectar.setText("Desconectar");
                        btCriar.setEnabled(true);
                        consolePrint("UUID: " + client.getUuid());
                    }
                }
            }
        });

        btCriar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirGrupo(new Group(Integer.parseInt(sTipo.getValue().toString()), Integer.parseInt(sGrupo.getValue().toString())));
            }
        });
        btIniciarDp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirDispositivo(UUID.fromString(tfUuidDsp.getText()));
                tfUuidDsp.setText("");
            }
        });
    }

    public void abrirDispositivo(UUID uuid)
    {
        if (!client.getUuid().equals(uuid)) {
            TabDispositivo tabDispositivo = new TabDispositivo(uuid, client) {
                @Override
                public void onClose(TabDispositivo tabDispositivo) {
                    dispositivosAbertos.remove(uuid);
                    fecharTab(tabDispositivo);
                }
            };
            if (!dispositivosAbertos.contains(uuid)) {
                tabbedPane1.addTab(uuid.toString(), tabDispositivo);
                dispositivosAbertos.add(uuid);
            }
        }
    }

    public void abrirGrupo(Group group)
    {
        TabGrupo tabGrupo = new TabGrupo(group, client, getInstance()) {
            @Override
            public void onClose(TabGrupo tabGrupo) {
                gruposAbertos.remove(group);
                fecharTab(tabGrupo);
            }
        };
        if (!gruposAbertos.contains(group))
        {
            client.joinGroup(group);
            tabbedPane1.addTab("<"+group.getGroupType()+", "+group.getGroupID()+">", tabGrupo);
            gruposAbertos.add(group);
        }
    }

    private void consolePrint(String s)
    {
        tpConsole.setText(tpConsole.getText() +"\n"+ s);
    }

    private int getPredefByRadio()
    {
        if (predef1RadioButton.isSelected())
            return 1;
        if (predef2RadioButton.isSelected())
            return 2;
        if (predef3RadioButton.isSelected())
            return 3;
        if (predef4RadioButton.isSelected())
            return 4;
        if (predef5RadioButton.isSelected())
            return 5;
        return -1;
    }

    public void fecharTab(Component tab)
    {
        tabbedPane1.remove(tab);
    }

    public static void main(String[] args)
    {
        if (args.length == 1)
            new Main(Integer.parseInt(args[0]));
        else
            new Main();
    }
}
