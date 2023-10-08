

module Demo
{
    interface CallbackReceiver{
        void callback(string message);
        int getCounter();


    }

    interface CallbackSender{
        void initiateCallback(CallbackReceiver* proxy);
        void shutdown();
        string primeFactors(long a);
        bool registerClients(string a, CallbackReceiver* proxy);
        string listClients();
        string mtoX(string hostnameFrom, string hostnameTo, string message);
        string mBC(string hostnameFrom,string message);
        string command(string command);
    }
    interface Printer
        {
            string printString(string s);
        }

}