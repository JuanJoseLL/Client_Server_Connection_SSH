
import com.zeroc.Ice.Current;

public class CallbackReceiverImpl implements Demo.CallbackReceiver{



        @Override
        public String callback(String message, Current current) {
              return message;
        }
}
