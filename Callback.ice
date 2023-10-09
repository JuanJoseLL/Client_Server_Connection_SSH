

module Demo
{
    interface CallbackReceiver{
        void callback(string message);
        int getCounter();


    }

    interface CallbackSender{
        void initiateCallback(CallbackReceiver* proxy);
        void shutdown();
        ["amd"] string primeFactors(long a);
        bool registerClients(string a, CallbackReceiver* proxy);
        string listClients();
        void mtoX(string hostnameTo, string message);
        void mBC(string message);
        ["amd"] string command(string command);
    }
    interface Printer
        {
            string printString(string s);
        }

}