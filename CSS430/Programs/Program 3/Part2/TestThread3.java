public class TestThread3 extends Thread {
    private String thread;

    public TestThread3(String [] args){
        thread = args[0];
    }

    public void run (){

        if (thread.equals("A")){
            double D = 0.0;
            for (int i = 0 ; i < 30000000; i++ ){
                D = Math.pow(Math.sqrt(Math.sqrt(i*D)),D);

            }
        }
        else if (thread.equals("B")){

            byte [] buff = new byte[512];
            for (int i = 0 ; i < 1000; i++){
                SysLib.rawwrite(i,buff);
                SysLib.rawread(i,buff);
            }
        }
        SysLib.cout("TestThread "+ thread +" finished!\n");
        SysLib.exit();
    }
}
