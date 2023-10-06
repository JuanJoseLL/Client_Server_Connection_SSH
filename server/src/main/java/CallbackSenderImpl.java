import Demo.CallbackReceiverPrx;
import com.zeroc.Ice.Current;

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
    public String mtoX(String hostnameFrom ,String hostnameTo, String message, Current current) {
        CallbackReceiverPrx receiver = registeredClients.get(hostnameTo);
        if(registeredClients.get(hostnameTo)==null){
            System.out.println("hola");
            String state="Hostname "+hostnameTo+"no esta registrado";
           return state;
        } else {
            String answer = "El host "+hostnameFrom +"dice "+message;
            return receiver.callback(answer);
        }
    }


    @Override
    public String mBC(String hostnameFrom , String message,Current current) {
        Set<String> hostnames = registeredClients.keySet();
            String answer=" ";
        for (String host : hostnames) {
            CallbackReceiverPrx receiver = registeredClients.get(host);
            answer += "El host "+hostnameFrom +"les dice"+receiver.callback(message);
        }

        return answer;

    }

    @Override
    public String command(String command, Current current) {
        StringBuilder output = new StringBuilder();
        Process process;

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
