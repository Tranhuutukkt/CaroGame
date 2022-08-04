CFLAGS = -c -Wall
CC = gcc
LIBS =  -lm 

all: server

server: server.c serverHelper.c caro.c checkinput.c linklist.c caroRanking.c
	${CC} server.c serverHelper.c caro.c checkinput.c linklist.c caroRanking.c -g -o server
clean:
	rm -f *.o *~
