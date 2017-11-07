#include <stdlib.h>
#include <stdio.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <iostream>
#include <string.h>

using namespace std;

const int MSG_SIZE = 120;
typedef struct
{
	long msgType;
	char msgText[MSG_SIZE];
} message_buf;
int main ()
{
	message_buf message;
	
	key_t key = 777;
	int msgFlags = IPC_CREAT | 0666;
	
	int msgID = msgget(key, msgFlags);
	
	int rc = msgrcv (msgID, &message, MSG_SIZE, 1,0);
	
	cout << message.msgText <<endl;
	 
}

	
	
	
	
