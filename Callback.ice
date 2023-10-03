

module Demo
{
    interface CallbackReceiver{
        void callback();
    }

    interface CallbackSender{
        void initiateCallback(CallbackReceiver* proxy);
        void shutdown();
        void message(int a);
        void registerClients(string a, CallbackReceiver* proxy);
        string listClients();
    }
    interface Printer
        {
            string printString(string s);
        }

}