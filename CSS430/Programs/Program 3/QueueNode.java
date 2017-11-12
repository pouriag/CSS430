import java.util.Vector;

public class QueueNode {

    private Vector<Integer> queue;

    public QueueNode(){

        queue = new Vector<>();
    }

    public synchronized int sleep() {
        if (this.queue.size() == 0){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.queue.remove(0);
    }

    public synchronized void wakeup(int tid){
        this.queue.add(tid);
        this.notify();
    }
}
