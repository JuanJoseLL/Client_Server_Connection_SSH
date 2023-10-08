
import com.zeroc.Ice.Current;

public class CallbackReceiverImpl implements Demo.CallbackReceiver{


        private int counter;

        public CallbackReceiverImpl()
        {
            counter = 0;
        }

        @Override
        public void callback(String message, Current current) {
            System.out.println(message);
            counter ++;
        }

    @Override
    public int getCounter(Current current) {
        return counter;
    }


}
