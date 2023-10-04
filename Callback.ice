

module Demo
{
    interface CallbackReceiver{
        string callback(string message);


    }

    interface CallbackSender{
        void initiateCallback(CallbackReceiver* proxy);
        void shutdown();
        void message(int a);
        bool registerClients(string a, CallbackReceiver* proxy);
        string listClients();
        string mtoX(string hostnameFrom, string hostnameTo, string message);
        string mBC(string hostnameFrom,string message);
    }
    interface Printer
        {
            string printString(string s);
        }

}