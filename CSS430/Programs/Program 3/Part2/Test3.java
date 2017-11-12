import java.util.Date;

public class Test3 extends Thread {
    private int input;

    public Test3(String[] args){
        input = Integer.parseInt(args [0]);
    }

    public void run(){
        String [] argA = SysLib.stringToArgs("TestThread3 A");
        String [] argB = SysLib.stringToArgs("TestThread3 B");
        long initTime =  (new Date()).getTime();

        for (int i = 0; i < input ; i++){
            SysLib.exec(argA);
            SysLib.exec(argB);
        }

        for (int i = 0; i< (2*input);i++){
            SysLib.join();
        }

        long finTime = (new Date()).getTime();
        long elapsedTime = finTime - initTime;
        SysLib.cout("elapsed time: " + elapsedTime + " ms\n");
        SysLib.exit();
    }

}
