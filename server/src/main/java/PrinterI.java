import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class PrinterI implements Demo.Printer
{
    public void printString(String s, com.zeroc.Ice.Current current) {
        boolean flag = false;
        int num = 0;
        String[] parts=s.split(": ",2);
        String username = parts[0];
        String content = parts[1];
        try{
            num = Integer.parseInt(content);
            flag=true;
        }catch (NumberFormatException e){
            flag=false;
        }
        if(flag){
            System.out.println(username+": "+primeFactors(num));
        }
        if(content.matches("listports")){
            System.out.println(username+"\n "+executeCommand("nmap localhost"));
        } else if (content.matches("listifs")) {
            System.out.println(username+"\n");
            System.out.println(username+"\n "+executeCommand("ifconfig"));
        } else if (content.startsWith("!")){
            String command = content.substring(1).trim();
            System.out.println(executeCommand(command));
        }

    }
    private String executeCommand(String command) {
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

    private String primeFactors(int a){
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

}