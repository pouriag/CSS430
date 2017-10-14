/* -----------------------------
 * Program 1 
 * Operating system concepts
 * Professor Dimpsey
 * Written by Pouria Ghadimi
 * Date: 10/13/2017
 *------------------------------
*/

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <iostream>
#include <string.h>

using namespace std;

enum {READ,WRITE};

int main(int argc, char * argv[]){
	
	int pipeFD[2];
	char buf [4096];

	int pid = fork();
	
	
	
	//Child
	if (pid == 0)
	{	
		int pid2 = fork();
		close (pipeFD[WRITE]);
		int rc = execlp("/bin/ls", "ls", "-l", (char *) 0);
		
		cout << "I happened " << getpid() << endl;
	
		if (rc == -1)
		{
			cerr << "Error on execl" << endl;
		}

		//grandchild
		if (pid2==0)
		{
			int pid3 = fork();
			close (pipeFD[WRITE]);
			int rc = execlp("/bin/grep", "grep", argv[1], (char *) 0);
			if (rc == -1)
			{
				cerr << "Error on execl" << endl;
			}
		
			//grand grandchild
			if (pid3 == 0)
			{
				close (pipeFD[WRITE]);
				int rc = execlp("/bin/ps", "ps", "-A", (char *) 0);
				if (rc == -1)
				{
					cerr << "Error on execl" << endl;
				}
			}

			//grandchild
			else
			{
				close (pipeFD[WRITE]);
				wait(NULL);
				

			}
		}
		//child
		else
		{
			close (pipeFD[WRITE]);
			wait(NULL);
			
		}

	}
	
	//Parent
	else
	{
		close (pipeFD[WRITE]);
		//char buf[4096];
		wait(NULL);
		
	}
}




