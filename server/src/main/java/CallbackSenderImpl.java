import Demo.CallbackReceiverPrx;
import com.zeroc.Ice.Current;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CallbackSenderImpl implements Demo.CallbackSender{

    private final Map<String, CallbackReceiverPrx> registeredClients = new HashMap<>();
    @Override
    public void initiateCallback(CallbackReceiverPrx proxy, Current current) {
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

    @Override
    public void message(int a, Current current) {
        int num = 2;
        String factors ="";
        while(a!=1){
            while (a%num==0){
                factors+=num+", ";
                a /= num;
            }
            num++;
        }
        System.out.println(factors);
    }

    @Override
    public void registerClients(String hostname, CallbackReceiverPrx proxy, Current current) {
        synchronized (registeredClients){
            registeredClients.put(hostname, proxy);
        }
    }

    @Override
    public String listClients(Current current) {
        Set<String> claves = registeredClients.keySet();

        StringBuilder resultado = new StringBuilder();

        for (String clave : claves) {
            resultado.append(clave).append(", ");
        }

        return resultado.toString();
    }



}
