CFLAGS = -c -Wall
CC = gcc
LIBS =  -lm 

all: server

server: server.c serverHelper.c caroai.c tic-tac-toe.c checkinput.c linklist.c tictactoeRanking.c caroRanking.c
	${CC} server.c serverHelper.c caroai.c tic-tac-toe.c checkinput.c linklist.c tictactoeRanking.c caroRanking.c -g -o server
clean:
	rm -f *.o *~
