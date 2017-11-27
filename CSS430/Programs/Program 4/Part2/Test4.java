import java.util.Arrays;
import java.util.Date;
import java.util.Random;

class Test4 extends Thread {

    private byte[] writes;
    private byte[] reads;
    private Random rand;

    private boolean enabled;
    private int testcase;
    private long startTime;
    private long endTime;


    private void read(int block, byte[] buffer) {

        if (this.enabled) {
            SysLib.cread(block, buffer);
        } else {
            SysLib.rawread(block, buffer);
        }
    }

    private void write(int block, byte[] buffer) {

        if (this.enabled) {
            SysLib.cwrite(block, buffer);
        } else {
            SysLib.rawwrite(block, buffer);
        }
    }

    private void randomAccess() {
        int[] intArr = new int[200];

        for (int i = 0; i < 200; ++i) {
            intArr[i] = Math.abs(this.rand.nextInt() % 512);
            write(intArr[i], writes);
        }


        for (int i = 0; i < 200; ++i) {
            read(intArr[i], reads);
            if (this.reads[i] != this.writes[i]) {
                SysLib.cerr("ERROR\n");
                SysLib.exit();
            }
        }

    }

    private void localizedAccess() {

        for (int i = 0; i < 10; ++i) {

            for (int j = 0; j < 200; ++j) {
                write(i + j, writes);
            }
        }

        for (int i= 0; i < 10; i++){
            for (int j = 0; j < 200; ++j) {

                read(i + j, reads);
                if (this.reads[j] != this.writes[j]) {
                    SysLib.cerr("ERROR\n");
                    SysLib.exit();
                }
            }
        }
    }

    private void mixedAccess() {

        int[] intArr = new int[200];

        for (int i = 0; i < 200; ++i) {

            if (Math.abs(this.rand.nextInt() % 10) > 8) {
                intArr[i] = Math.abs(this.rand.nextInt() % 512);
                write(intArr[i], writes);
            } else {
                intArr[i] = Math.abs(this.rand.nextInt() % 10);
                write(intArr[i], writes);
            }
        }


        for (int i = 0; i < 200; ++i) {

            read(intArr[i], reads);

            for (int j = 0; j < 512; ++j) {
                if (this.reads[j] != this.writes[j]) {
                    SysLib.cerr("ERROR\n");
                    SysLib.exit();
                }
            }
        }
    }

    private void adversaryAccess() {

        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < 512; ++j) {
                this.writes[j] = (byte) j;
            }

            for (int j = 0; j < 10; ++j) {
                write(i * 10 + j, writes);
            }
        }

        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < 10; ++j) {

                read(i * 10 + j, reads);

                for (int k  = 0; k < 512; ++k) {
                    if (this.reads[k] != this.writes[k]) {
                        SysLib.cerr("ERROR\n");
                        SysLib.exit();
                    }
                }
            }
        }
    }

    public void outPut(String test) {
        if (this.enabled) {
            SysLib.cout("Test " + test + " accesses (cache enabled): " + (this.endTime - this.startTime) + "\n");
        } else {
            SysLib.cout("Test " + test + " accesses (cache disabled): " + (this.endTime - this.startTime) + "\n");
        }
    }

    public Test4(String[] args) {

        this.enabled = args[0].equals("enabled");
        this.testcase = Integer.parseInt(args[1]);
        this.writes = new byte[512];
        this.reads = new byte[512];
        this.rand = new Random();
    }

    @Override
    public void run() {
        SysLib.flush();
        this.startTime = (new Date()).getTime();
        switch (this.testcase) {

            case 1:
                this.randomAccess();
                this.endTime = (new Date()).getTime();
                outPut("random");

                break;

            case 2:
                this.localizedAccess();
                this.endTime = (new Date()).getTime();
                outPut("localized");
                break;

            case 3:
                this.mixedAccess();
                this.endTime = (new Date()).getTime();
                outPut("mixed");
                break;

            case 4:
                this.adversaryAccess();
                this.endTime = (new Date()).getTime();
                outPut("adversary");
                break;
        }
        SysLib.exit();
    }
}