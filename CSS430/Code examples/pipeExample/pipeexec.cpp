#include <iostream>    //for cout, endl
#include <unistd.h>    //for fork, pipe
#include <stdlib.h>    //for exit
#include <sys/wait.h>  //for wait
using namespace std;
enum {READ,WRITE};

int main(int argc, char *argv[])
{
	int pipeFD[2];

	int pid = fork();

	if (pid < 0)
	{
		cerr << "Error: Fork Failed" << endl;
		exit(EXIT_FAILURE);
	}
	else if (pid == 0)  // Child
	{
		close (pipeFD[READ]);
		dup2(pipeFD[WRITE],1);
		
		int rc = execlp("/bin/ls", "ls", "-l", (char *) 0);
		if (rc == -1)
		{
			cerr << "Error on execl" << endl;
		}
		exit(EXIT_SUCCESS);
	}
	else  // parent
	{
		close (pipeFD[WRITE]);
		char buf[4096];
		wait(NULL);
		int n = read(pipeFD[READ], buf,4096);
		buf[n]='\0';
		//cout << buf;
		
	}
	exit(EXIT_SUCCESS);
}

