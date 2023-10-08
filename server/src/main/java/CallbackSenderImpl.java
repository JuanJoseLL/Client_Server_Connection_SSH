import Demo.CallbackReceiver;
import Demo.CallbackReceiverPrx;
import com.zeroc.Ice.*;
import com.zeroc.Ice.Object;
import org.w3c.dom.ls.LSOutput;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public String primeFactors(long a, Current current) {
        int num = 2;
        String factors ="";
        while(a!=1){
            while (a%num==0){
                factors+=num+", ";
                a /= num;
            }
            num++;
        }
        return factors;
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
    public void mtoX(String hostnameTo, String message, Current current) {
        /*new Thread(()->{

        }).start();*/
        CallbackReceiverPrx receiver = registeredClients.get(hostnameTo);
        if(registeredClients.get(hostnameTo)==null){
            System.out.println("nulo");
        }else{
            String answer = message;
            System.out.println("entro");
            receiver.callback(answer);
        }


    }


    @Override
    public void mBC( String message,Current current) {
        Set<String> hostnames = registeredClients.keySet();
        for (String host : hostnames) {
            CallbackReceiverPrx receiver = registeredClients.get(host);
            receiver.callback(message);
        }

    }
    @Override
    public String command(String command, Current current) {
        StringBuilder output = new StringBuilder();
        java.lang.Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error executing command.";
        }
        return output.toString();

    }


}
