import java.util.Vector;

public class QueueNode {

    private Vector<Integer> queue;

    public QueueNode(){
        queue = new Vector<Integer>();
    }

    public synchronized int sleep() throws InterruptedException {
        if (queue.size() == 0){
            wait();
            return queue.remove(0);
        }
        else
            return -1;
    }

    public synchronized int wakeup(int tid){
        queue.add(tid);
        return 0;
    }
}
