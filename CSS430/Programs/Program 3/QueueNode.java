import java.util.Vector;

public class QueueNode {

    private Vector<Integer> queue;

    public QueueNode(){

        queue = new Vector<>();
    }

    public synchronized int sleep() throws InterruptedException {
        if (this.queue.size() == 0){
            wait();
            return this.queue.remove(0);
        }
        else
            return -1;
    }

    public synchronized void wakeup(int tid){
        this.queue.add(tid);
        notifyAll();

    }
}
