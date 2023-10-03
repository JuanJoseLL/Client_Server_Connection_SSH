import java.io.*;


public class Server
{
    public static void main(String[] args)
    {
        java.util.List<String> extraArgs = new java.util.ArrayList<String>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args,"config.server",extraArgs))
        {
            if(!extraArgs.isEmpty())
            {
                System.err.println("too many arguments");
                for(String v:extraArgs){
                    System.out.println(v);
                }
            }else{
                com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Server");
                adapter.add(new CallbackSenderImpl(), com.zeroc.Ice.Util.stringToIdentity("callbackSender"));
                adapter.activate();

                communicator.waitForShutdown();
            }

           /** com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Printer");
            com.zeroc.Ice.Object object = new PrinterI();
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimplePrinter"));
            adapter.activate();
            communicator.waitForShutdown();*/
        }
    }
}