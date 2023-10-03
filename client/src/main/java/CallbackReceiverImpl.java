
import com.zeroc.Ice.Current;

public class CallbackReceiverImpl implements Demo.CallbackReceiver{
        public void callback(com.zeroc.Ice.Current current)
        {
                System.out.println("received callback");
        }
}
