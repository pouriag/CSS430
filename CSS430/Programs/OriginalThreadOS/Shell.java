
public class Shell extends Thread {

    public Shell() {    //constructor
        SysLib.cout("Shell has been called ....\nWELCOME TO THE BEST SHELL EVER)" +
                "\nOK YOU GOT ME, NOT THE BEST ONE\nWELCOME TO AN OKAY SHELL")
    }

    public void run() {


        int shellCounter = 1; //counter for shell to display

        while (true) {
            String check = "";  //String to check new Entries

            do { //while true run the shell

                StringBuffer inBuf = new StringBuffer();
                SysLib.cout("Shell[" + shellCounter + "]% ");
                SysLib.cin(inBuf);
                check = inBuf.toString();

            } while (check.length() == 0);

        }



            String[] cmd = SysLib.stringToArgs(check); //to store commands and ready for execute
            int cmdCount = 0; // to keep track of the counter


            for (int i = 0; i < cmd.length; i++) {

                if (cmd[i].equals("&") || cmd[i].equals(";")|| i == cmd.length -1){

                    String[] finalCmd;
                     if (i == cmd.length -1) {
                         finalCmd  = this.runCmd(cmd, cmdCount,i+1);
                     }

                     else {
                         finalCmd = this.runCmd(cmd, cmdCount, i);
                     }


                    if (finalCmd != null) {

                        SysLib.cout(finalCmd[0] + "\n");

                        if (finalCmd[0].equals("exit")) {

                            SysLib.exit();
                            return;
                        }

                        int result = SysLib.exec(finalCmd);

                        if (!cmd[i].equals("&")) {

                            while (true) {
                                if (SysLib.exec(cmd) != result) {
                                    continue;
                                }
                            }
                        }
                    }
                }


                shellCounter++;
            }
        }
    }

    private String[] runCmd(String[] cmd, int cmdCount, int iteration) {

        if (cmd [iteration - 1].equals(";") || cmd[iteration - 1].equals("&")) {
            iteration --;
        }

        if (iteration - cmdCount <= 0) {

            return null;

        }

        else {

            String[] retCmd = new String[iteration - cmdCount];

            for(int i = cmdCount; i < iteration; i++) {

                retCmd[i - cmdCount] = cmd[i];
            }

            return retCmd;
        }
    }
}



