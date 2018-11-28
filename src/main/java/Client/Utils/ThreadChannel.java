package Client.Utils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ThreadChannel {
    private BlockingQueue<Data> mMainThread;
    private BlockingQueue<Data> mOtherThread;

    public ThreadChannel() {
        mMainThread = new ArrayBlockingQueue<Data>(1);
        mOtherThread = new ArrayBlockingQueue<Data>(1);
    }

    public void sendToMainThread(String action, List<String> args) {
        Data data = new Data(action, args);
        System.out.println("Sending action '" + action + "' to main thread!");

        while (!mMainThread.add(data)) {
            continue;
        }
    }

    public Data receiveOnMainThread() {
        try {
            return mMainThread.take();
        } catch (InterruptedException e) {
            System.out.println("Failed to receive data on main thread!");
            System.exit(-1);
            return null;
        }
    }

    public void sendToOtherThread(String action, List<String> args) {
        Data data = new Data(action, args);
        System.out.println("Sending action '" + action + "' to api thread!");

        while (!mOtherThread.add(data)) {
            continue;
        }
    }

    public Data receiveOnOtherThread() {
        try {
            return mOtherThread.take();
        } catch (InterruptedException e) {
            System.out.println("Failed to receive data on main thread!");
            System.exit(-1);
            return null;
        }
    }

    public class Data {
        private String mAction;
        private List<String> mArgs;

        public Data(String mAction, List<String> mArgs) {
            this.mAction = mAction;
            this.mArgs = mArgs;
        }

        public String getAction() {
            return mAction;
        }

        public void setAction(String mAction) {
            this.mAction = mAction;
        }

        public List<String> getArgs() {
            return mArgs;
        }

        public void setArgs(List<String> mArgs) {
            this.mArgs = mArgs;
        }
    }
}
