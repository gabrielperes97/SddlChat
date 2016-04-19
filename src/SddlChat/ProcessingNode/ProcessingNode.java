package SddlChat.ProcessingNode;

/**
 * Created by gabriel on 17/04/16.
 */
public class ProcessingNode {

    public static void main(String[] args)
    {
        if (args[0].equals("node"))
        {
            if(args.length == 3)
            {
                new NodeServer(args[1], Integer.parseInt(args[2]));
            }
            else
            {
                new NodeServer();
            }
            System.out.println("Node server started...");
        }
        if (args[0].equals("udi"))
        {
            new UDIServer();
            System.out.println("UDI server started...");
        }
    }
}
