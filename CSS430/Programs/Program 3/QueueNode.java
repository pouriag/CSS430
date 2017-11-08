import java.util.Vector;

public class QueueNode {

    private Vector<Integer> queue;

    public QueueNode(){
        queue = new Vector<Integer>();
    }

    public synchronized int sleep(){
    if (queue.size() == 0){
        
    }
        return 0;
    }

    public synchronized int wakeup(int tid){
        queue.add(tid);
        return 0;
    }
}
