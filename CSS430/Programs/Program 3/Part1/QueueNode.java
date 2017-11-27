import java.util.Vector;

public class QueueNode {

    private Vector<Integer> queue;

    public QueueNode(){

        queue = new Vector<Integer>();
    }

    public synchronized int sleep() {
        if (this.queue.size() == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
	return queue.remove(0);
        }
        return -1;
    }

    public synchronized void wakeup(int tid){
        queue.add(tid);
        notify();
    }
}
