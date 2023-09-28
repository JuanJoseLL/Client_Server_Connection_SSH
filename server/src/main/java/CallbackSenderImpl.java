public class CallbackSenderImpl implements Demo.CallbackSender{
    public void initiateCallback(CallbackReceiverPrx proxy, com.zeroc.Ice.Current current)
    {
        System.out.println("initiating callback");
        try
        {
            proxy.callback();
        }
        catch(com.zeroc.Ice.LocalException ex)
        {
            ex.printStackTrace();
        }
    }

    public void shutdown(com.zeroc.Ice.Current current)
    {
        System.out.println("Shutting down...");
        try
        {
            current.adapter.getCommunicator().shutdown();
        }
        catch(com.zeroc.Ice.LocalException ex)
        {
            ex.printStackTrace();
        }
    }
}
