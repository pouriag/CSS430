/*
*  example program which shows usage of a pipe for communication
*/
#include <stdlib.h>  //exit
#include <stdio.h>   //perror
#include <unistd.h>  //fork, pipe
#include <sys/wait.h>   //wait
#include <iostream>
#include <string>
using namespace std;

int main()
{

	enum {READ, WRITE};

	int pipeFD[2];
	
	int pid = fork();

	cout << "PID: " << pid<< endl;


	if (pid == 0)  //Child
	{
		close(pipeFD[WRITE]);
		dup2(pipeFD[READ], 0);   //stdin is now child's read pipe
		char buf[256];
		dup2(pipeFD[READ],0);
		string message;
		cin >> message;
		//    would cin work here?   Why or why not?
		int n = read(pipeFD[READ], buf, 256);
		buf[n] = '\0';

		sleep(5);
	}

	else   //Parent
	{
		close(pipeFD[READ]);
		dup2(pipeFD[WRITE], 2);  //stderr is now parents write
		cerr << "Please go to sleep for 5" << endl;
		wait( NULL );
		cout << "Parent exiting" <<endl;
	}

	exit(EXIT_SUCCESS);
}
