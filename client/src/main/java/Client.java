import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import Demo.CallbackReceiverPrx;
import Demo.CallbackSenderPrx;
import com.zeroc.Ice.LocalException;

public class Client {
    public static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client", extraArgs)) {
            communicator.getProperties().setProperty("Ice.Default.Package", "com.zeroc.demos.Ice.callback");

            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
            } else {
                String status = run(communicator);
            }
        }
    }

    public static String run(com.zeroc.Ice.Communicator communicator) {

        String mes = "entro al metodo run";
        CallbackSenderPrx sender = CallbackSenderPrx.checkedCast(
                communicator.propertyToProxy("CallbackSender.Proxy")).ice_twoway().ice_timeout(-1).ice_secure(false);
        if (sender == null) {
            System.err.println("invalid proxy");
            return "invalid proxy";
        }
        com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Client");
        adapter.add(new CallbackReceiverImpl(), com.zeroc.Ice.Util.stringToIdentity("callbackReceiver"));
        adapter.activate();
        CallbackReceiverPrx receiver =
                CallbackReceiverPrx.uncheckedCast(adapter.createProxy(
                        com.zeroc.Ice.Util.stringToIdentity("callbackReceiver")));
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        String opt = null;

        do {
            try {
                menu();
                System.out.print("==> ");
                System.out.flush();
                opt = in.readLine();
                if (opt == null) {
                    break;
                }

                if (opt.equals("1")) {
                    System.out.println("Registrar host");
                    System.out.println("Introduzca el nombre del host");
                    String host = in.readLine();
                    sender.registerClients(host, receiver);
                    if (sender.registerClients(host, receiver)) {
                        System.out.println("Host registrado");
                    } else {
                        System.out.println("Host no registrado");
                    }
                } else if (opt.equals("2")) {
                    boolean flag = true;
                    while (flag) {
                        System.out.println("Ingrese la instruccion o exit");
                        String message = in.readLine();
                        if (message.equals("exit")) {
                            flag = false;
                        } else {
                            sendMessage(message, sender);
                        }

                    }

                } else if (opt.equals("3")) {
                    String b = in.readLine();
                    long a = Long.parseLong(b);
                    System.out.println(sender.primeFactors(a));
                } else if (opt.equals("4")) {
                    boolean flag = true;
                    while (flag) {
                        System.out.println("|1. Test Throughput                          |");
                        System.out.println("|2. Test Response Time                       |");
                        System.out.println("|3. Test Missing Rate                        |");
                        System.out.println("|4. Test Unprocessed Rate                    |");
                        System.out.println("|5. salir                                    |");
                        String message = in.readLine();
                        switch (message){
                            case "1": testThroughput(sender);
                                break;
                            case "2": testResponseTime(sender);
                                break;
                            case "3": testMissingRate(sender);
                                break;
                            case "4": testUnprocessedRate(sender,receiver);
                                break;
                            case "5": flag=false;
                                break;
                        }

                    }
                } else if (opt.equals("5")) {

                } else {
                    System.out.println("unknown command " + opt + "'");
                    menu();
                }
            } catch (IOException | LocalException ex) {
                ex.printStackTrace();
            }
        }
        while (!opt.equals("5"));
        return mes;
    }


    public static void menu() {

        System.out.println("------------------------");
        System.out.println("|1. Registrar cliente                        |");
        System.out.println("|2. Agregar instruccion                      |");
        System.out.println("|3. Descomposicion numeros primos            |");
        System.out.println("|4. Correr pruebas de calidad                |");
        System.out.println("|5. salir                                    |");
        System.out.println("------------------------");

    }

    public static void sendMessage(String message, Demo.CallbackSenderPrx sender) throws IOException {
        String prefix = getUsernameAndHostname();
        String hostname = java.net.InetAddress.getLocalHost().getHostName();
        if (message.matches("list clients")){
            System.out.println(prefix+"/Hostnames: "+sender.listClients());
        }else if(message.startsWith("to")){
            String[] parts=message.split(" ",3);
            String mens=parts[2];
            String hostnameTo=parts[1];
            sender.mtoX(hostnameTo,mens);
        } else if(message.startsWith("BC")) {
            sender.mBC(message);
        }else if(message.matches("list ports")){
            System.out.println(sender.command("nmap localhost"));
        } else if (message.matches("listifs")) {
            System.out.println(sender.command("ifconfig"));
        } else if (message.startsWith("!")) {
            String command = message.substring(1).trim();
            System.out.println(sender.command(command));
        }
    }

    private static String getUsernameAndHostname() {
        String username = System.getProperty("user.name");
        String hostname = "unknown";
        try {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return username + "@" + hostname;
    }

    //Test Throughput, simulating a number of transactions given by the user
    private static void testThroughput(Demo.CallbackSenderPrx twoway) {
        System.out.print("Enter the number of iterations: ");
        int iterations = sc.nextInt();
        sc.nextLine(); // Consume newline
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            twoway.primeFactors(50L);
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        double seconds = (double) totalTime / 1000000000;
        System.out.println("Test completed. Total time: " + seconds + " seconds");
        double throughput = (double) iterations / seconds;
        System.out.println("Throughput: " + throughput + " iterations/second");
    }


    // Test response time, measure the individual time that takes each transaction
    private static void testResponseTime(Demo.CallbackSenderPrx twoway) {
        System.out.print("Enter the number of iterations: ");
        int iterations = sc.nextInt();
        sc.nextLine(); // Consume newline
        List<Long> lista = new ArrayList<>();
        long totalResponseTime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            twoway.primeFactors(50L);
            long end = System.nanoTime();
            long responseTime = end - start;
            totalResponseTime += responseTime;
            System.out.println("Response time for iteration " + i + ": " + responseTime + " ns");
        }

        double averageResponseTime = (double) totalResponseTime / iterations;
        System.out.println("Average response time: " + averageResponseTime + " ns");
    }


    // Test missing Rate, run multiple transactions and monitor how many fail on receipt of a successful response
    private static void testMissingRate(Demo.CallbackSenderPrx twoway) {
        System.out.print("Enter the number of iterations: ");
        int iterations = sc.nextInt();
        sc.nextLine(); // Consume newline
        int missedCount = 0;
        for (int i = 0; i < iterations; i++) {
            try {
                twoway.primeFactors(50L);
            } catch (Exception e) {
                missedCount++;
            }
        }
        double missingRate = (double) missedCount / iterations;
        System.out.println("Missing rate: " + missingRate);
    }


     // Unprocessed Rate, multiples transactions are sent and monitor how many are left to process
     public static void testUnprocessedRate(Demo.CallbackSenderPrx sender, Demo.CallbackReceiverPrx receiver){
         CountDownLatch latch = new CountDownLatch(1);

         // Register a shutdown hook to notify the latch when the program exits
         Runtime.getRuntime().addShutdownHook(new Thread(() -> latch.countDown()));

         // Read the number of iterations from the user input
         System.out.print("Enter the number of iterations: ");
         int iterations = new java.util.Scanner(System.in).nextInt();

         // Record the start time in nanoseconds
         long start = System.nanoTime();

         // Call the primeFactors method on the twoway proxy for each iteration
         for(int i = 0; i < iterations; i++)
         {
             sender.initiateCallback(receiver);
         }

         // Record the end time in nanoseconds
         long end = System.nanoTime();

         // Calculate and print the total time and throughput
         long totalTime = end - start;
         double seconds = (double)totalTime / 1000000000;
         System.out.println("Total time for processing: " + seconds + " seconds");
         double throughput = (double)iterations / seconds;
         System.out.println("Throughput: " + throughput + " iterations/second");


         // Wait for the latch to be notified or interrupted
         try {
             latch.await(2L, TimeUnit.SECONDS);
         } catch (InterruptedException e) {
             throw new RuntimeException(e);
         }

         // Calculate and print the unprocessed rate
         int unprocessedCount = iterations - receiver.getCounter();
         double unprocessedRate = (double)unprocessedCount / iterations;
         System.out.println("Unprocessed rate: " + unprocessedRate);

     }


}

