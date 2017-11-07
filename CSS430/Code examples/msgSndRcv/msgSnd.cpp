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
	
	cout << message.msgText <<endl;
	strcpy ( message.msgText, "Salam shombooli");
	size_t msgSize = strlen (message.msgText) +1;
	message.msgType =1 ;
	
	int rc = msgsnd (msgID, &message ,msgSize,IPC_NOWAIT); 
}

