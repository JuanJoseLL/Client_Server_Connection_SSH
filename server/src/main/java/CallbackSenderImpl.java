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
            proxy.callback("try catch");
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
    public boolean registerClients(String hostname, CallbackReceiverPrx proxy, Current current) {
        boolean flag= false;
        synchronized (registeredClients){
            registeredClients.put(hostname, proxy);
        }
        if(registeredClients.get(hostname)!=null){
            flag=true;
        }
        return flag;
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

    @Override
    public String mtoX(String hostnameTo, String message, Current current) {
        CallbackReceiverPrx receiver = registeredClients.get(hostnameTo);
            String answer = message;
            String mens="vacio";
            if(receiver.callback(answer)==null){
            System.out.println("nulo");
            }else {

                System.out.println("entro"+receiver.callback(answer));
                 mens=receiver.callback(answer);
            }

            return mens;
    }


    @Override
    public String mBC( String message,Current current) {
        Set<String> hostnames = registeredClients.keySet();
            String answer="";
        for (String host : hostnames) {
            CallbackReceiverPrx receiver = registeredClients.get(host);
            answer += receiver.callback(message)+"\n";
        }
        return answer;

    }


    }
