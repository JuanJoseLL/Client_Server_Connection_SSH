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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CallbackSenderImpl implements Demo.CallbackSender{

    private final Map<String, CallbackReceiverPrx> registeredClients = new HashMap<>();

    @Override
    public void initiateCallback(CallbackReceiverPrx proxy, Current current) {
        System.out.println("initiating callback");

        try
        {
            proxy.callback("Callback realizado");
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
    public CompletionStage<String> primeFactorsAsync(long a, Current current)
    {
        System.out.println("Current thread: " + Thread.currentThread().getName());
        CompletableFuture<String> futureResult = new CompletableFuture<>();
         long number = a;
        CompletableFuture.runAsync(() -> {
            int num = 2;
            String factors = "";
            long temp = number;
            while(temp != 1)
            {
                while (temp % num == 0)
                {
                    factors += num + ", ";
                    temp /= num;
                }
                num++;
            }
            futureResult.complete(factors);
        });
        return futureResult;
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
    public CompletionStage<Void> mtoXAsync(String hostnameTo, String message, Current current) {
        CompletableFuture<Void> futureResult = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            StringBuilder output = new StringBuilder();
        CallbackReceiverPrx receiver = registeredClients.get(hostnameTo);
        if(registeredClients.get(hostnameTo)==null){
            System.out.println("nulo");
        }else{
            String answer = message;
            System.out.println("entro");
            receiver.callback(answer);


        }
            futureResult.complete(null);

        });
     return futureResult;
    }

    @Override
    public CompletionStage<Void> mBCAsync(String message, Current current) {
        CompletableFuture<Void> futureResult = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            StringBuilder output = new StringBuilder();
        Set<String> hostnames = registeredClients.keySet();
        for (String host : hostnames) {
            CallbackReceiverPrx receiver = registeredClients.get(host);
            receiver.callback(message);
            System.out.println("BC realizado");
        }
            futureResult.complete(null);
        });
            return futureResult;
    }

    @Override
    public CompletionStage<String> commandAsync(String command, Current current) {
            CompletableFuture<String> futureResult = new CompletableFuture<>();
            CompletableFuture.runAsync(() -> {
                StringBuilder output = new StringBuilder();
                java.lang.Process process;


                try {
                    System.out.println("Current thread: " + Thread.currentThread().getName());

                    process = Runtime.getRuntime().exec(command);
                    process.waitFor();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                    futureResult.complete(output.toString());
                } catch (IOException | InterruptedException e) {
                    futureResult.completeExceptionally(e);
                }
            });

            return futureResult;
        }

}
