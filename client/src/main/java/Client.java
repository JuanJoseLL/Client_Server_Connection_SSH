import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;


import Demo.CallbackReceiverPrx;
import Demo.CallbackSenderPrx;
import com.zeroc.Ice.LocalException;

public class Client {
    public static void main(String[] args) {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();
        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client", extraArgs)) {
            communicator.getProperties().setProperty("Ice.Default.Package", "com.zeroc.demos.Ice.callback");

            if(!extraArgs.isEmpty())
            {System.err.println("too many arguments");}
            else
            {String status = run(communicator);}
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
                }else if (opt.equals("2")) {
                    boolean flag = true;
                    while (flag){
                        System.out.println("Ingrese la instruccion o exit");
                        String message = in.readLine();
                        if (message.equals("exit")){
                            flag=false;
                        }else{
                            sendMessage(message, sender);
                        }

                    }

                }else if (opt.equals("3")) {
                    String b = in.readLine();
                    long a = Long.parseLong(b);
                    System.out.println(sender.primeFactors(a));
                }else if (opt.equals("4")) {

                }else if (opt.equals("5")) {

                }else
                    {
                        System.out.println("unknown command " + opt + "'");
                        menu();
                    }
                }
            catch(IOException | LocalException ex)
                {
                    ex.printStackTrace();
                }
            }
            while (!opt.equals("4")) ;
            return mes;
        }



    public static void menu(){

        System.out.println("------------------------");
        System.out.println("|1. Registrar cliente                        |");
        System.out.println("|2. Agregar instruccion                      |");
        System.out.println("|3. Descomposicion numeros primos            |");
        System.out.println("|4. salir                                    |");
        System.out.println("------------------------");

    }

    public static void sendMessage(String message, Demo.CallbackSenderPrx sender) throws IOException {
        System.out.println("Ingrese el mensaje a enviar ");
        String prefix = getUsernameAndHostname();
        String hostname = java.net.InetAddress.getLocalHost().getHostName();
        if (message.matches("list clients")){
            System.out.println(prefix+"/Hostnames: "+sender.listClients());
        }else if(message.startsWith("to")){
            String[] parts=message.split(" ",3);
            System.out.println("from"+parts[1]);
            System.out.println("to"+parts[2]);

        } else if(message.startsWith("BC")) {
           // sender.mBC(hostname, message);
        }else if(message.matches("list ports")){
            System.out.println(sender.command("nmap localhost"));
        } else if (message.matches("listifs")) {
            System.out.println(sender.command("ifconfig"));
        }else if(message.startsWith("!")){
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
}


    
//     //Original method to see the factorial number and write messages
//     private static boolean sendMessage(Demo.PrinterPrx twoway, Scanner sc) {
//         boolean flag = true;
//         System.out.print("Enter a message to send (or 'exit' to quit): ");
//         String message = sc.nextLine();
//         if (!message.equalsIgnoreCase("exit")) {
//             String prefix = getUsernameAndHostname();
//             String fullMessage = prefix + ": " + message;

//             String a= twoway.printString(fullMessage);
//             System.out.println("Response from server: " + a);
//         } else {
//             flag = false;
//         }
//         return flag;
//     }



//     //Test Throughput, simulating a number of transactions given by the user
//     private static void testThroughput(Demo.PrinterPrx twoway, Scanner sc) {
//         System.out.print("Enter the number of iterations: ");
//         int iterations = sc.nextInt();
//         sc.nextLine(); // Consume newline
//         long startTime = System.nanoTime();
//         for (int i = 0; i < iterations; i++) {
//             String message = "Test message " + i;
//             String prefix = getUsernameAndHostname();
//             String fullMessage = prefix + ": " + message;
//             twoway.printString(fullMessage);
//         }
//         long endTime = System.nanoTime();
//         long totalTime = endTime - startTime;
//         double seconds = (double) totalTime / 1000000000;
//         System.out.println("Test completed. Total time: " + seconds + " seconds");
//         double throughput = (double) iterations / seconds;
//         System.out.println("Throughput: " + throughput + " iterations/second");
//     }


//     // Test response time, measure the individual time that takes each transaction
//     private static void testResponseTime(Demo.PrinterPrx twoway, Scanner sc) {
//         System.out.print("Enter the number of iterations: ");
//         int iterations = sc.nextInt();
//         sc.nextLine(); // Consume newline
//         long totalResponseTime = 0;
//         for (int i = 0; i < iterations; i++) {
//             String message = "Test message " + i;
//             String prefix = getUsernameAndHostname();
//             String fullMessage = prefix + ": " + message;
//             long start = System.nanoTime();
//             twoway.printString(fullMessage);
//             long end = System.nanoTime();
//             long responseTime = end - start;
//             totalResponseTime += responseTime;
//             System.out.println("Response time for iteration " + i + ": " + responseTime + " ns");
//         }
//         double averageResponseTime = (double) totalResponseTime / iterations;
//         System.out.println("Average response time: " + averageResponseTime + " ns");
//     }
    
//     // Test missing Rate, run multiple transactions and monitor how many fail on receipt of a successful response
//     private static void testMissingRate(Demo.PrinterPrx twoway, Scanner sc) {
//         System.out.print("Enter the number of iterations: ");
//         int iterations = sc.nextInt();
//         sc.nextLine(); // Consume newline
//         int missedCount = 0;
//         for (int i = 0; i < iterations; i++) {
//             String message = "Test message " + i;
//             String prefix = getUsernameAndHostname();
//             String fullMessage = prefix + ": " + message;
//             try {
//                 twoway.printString(fullMessage);
//             } catch (Exception e) {
//                 missedCount++;
//             }
//         }
//         double missingRate = (double) missedCount / iterations;
//         System.out.println("Missing rate: " + missingRate);
//     }

//     // Unprocessed Rate, multiples transactions are sent and monitor how many are left to process
//     private static void testUnprocessedRate(Demo.PrinterPrx twoway, Scanner sc) {
//         System.out.print("Enter the number of iterations: ");
//         int iterations = sc.nextInt();
//         sc.nextLine(); // Consume newline

//         List<String> sentMessages = new ArrayList<>();
//         long start = System.nanoTime();
//         for (int i = 0; i < iterations; i++) {
//             String message = "Test message " + i;
//             String prefix = getUsernameAndHostname();
//             String fullMessage = prefix + ": " + message;
//             twoway.printString(fullMessage);
//             sentMessages.add(fullMessage);
//         }
//         long end = System.nanoTime();
//         long totalTime = end - start;
//         double seconds = (double) totalTime / 1000000000;
//         System.out.println("Total time for processing: " + seconds + " seconds");
//         // Simulate a random portion of unprocessed messages
//         Random random = null;
//         int unprocessedCount = (int) (iterations * random.nextDouble() * 0.2);
//         List<String> unprocessedMessages = sentMessages.subList(0, unprocessedCount);
//         System.out.println("Unprocessed rate: " + unprocessedCount + " unprocessed messages");
//     }




