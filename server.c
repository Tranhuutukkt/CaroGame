
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
/*
Library of socket
*/
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <errno.h>

#include "linklist.h"
#include "checkinput.h"
#include "caro.h"
#include "serverHelper.h"
#include "caroRanking.h"

#define BUFF_SIZE 1024
#define ROW_SIZE 16
#define COL_SIZE 16

//connect
#define SIGNAL_OK "SIGNAL_OK"
#define SIGNAL_CLOSE "SIGNAL_CLOSE"

// login
#define SIGNAL_CHECKLOGIN "SIGNAL_CHECKLOGIN"
#define SIGNAL_CREATEUSER "SIGNAL_CREATEUSER"
#define SIGNAL_LOGOUT "SIGNAL_LOGOUT"
#define SIGNAL_MENU "SIGNAL_MENU"

//room
#define CREATE_ROOM "CREATE_ROOM"
#define JOIN_ROOM "JOIN_ROOM"
#define LEAVE_ROOM "LEAVE_ROOM"

// caro game
#define START_GAME "START_GAME"
#define GAME_MOVE "GAME_MOVE"
#define GAME_WIN "GAME_WIN"

//set point
#define SET_POINT "SET_POINT"

//chat
#define CHAT "CHAT"

// caro ranking
#define RANKING "RANKING"
#define DETAIL "DETAIL"
#define USER_INFO "USER_INFO"

// server connect to client
int PORT;
struct sockaddr_in server_addr,client_addr;  
fd_set master;
char send_msg[BUFF_SIZE], recv_msg[BUFF_SIZE];

// server variable
char token[] ="#";
char *str;

/*
Xử lí dữ liệu gửi từ client
*/
int handleDataFromClient(int fd){
  char *user, *pass, *roomId, *message, *time;
  int recieved, col, row;
  userProfile *userInfo;
  int* list;

  recieved = recv( fd, recv_msg, BUFF_SIZE, 0);
  recv_msg[recieved] = '\0';
  
  printf("%s|\n", recv_msg);
  str = strtok( recv_msg, token);
  if( strcmp(str, SIGNAL_CLOSE) == 0){
    FD_CLR(fd, &master); // Clears the bit for the file descriptor fd in the file descriptor set fdset.
    printf("Close connection from fd = %d\n", fd );    
  }
  else if (strcmp(str, SIGNAL_OK) == 0)
  {
    printf("Connect with fd = %d successfully!\n", fd);
  }
  else if(strcmp(str, SIGNAL_CREATEUSER) == 0){
    printf("%s\n", str);
    // Create new user
    user = strtok(NULL, token);
    pass = strtok(NULL, token);
    if(isValid(user, NULL)){
      sprintf(send_msg,"%s#%s#%s\n", SIGNAL_CREATEUSER, "error", "Account existed");
    } else{
      registerUser(user, pass);
      sprintf( send_msg,"%s#%s\n", SIGNAL_CREATEUSER, "ok");
    }

    write( fd, send_msg, strlen(send_msg));
    printf("Send message: %s|\n", send_msg);
  }
  else if( strcmp(str, SIGNAL_CHECKLOGIN) == 0){  
    // str = strtok( recv_msg, token); 
    printf("%s\n", str);
    // Login
    user = strtok(NULL, token);
    pass = strtok(NULL, token);
    printf("%s%sabc\n", user, pass);
    if(isValid(user, pass)){
      userNode* p = findUser(user);
      if (p->user.fd != -1){
        sprintf( send_msg,"%s#%s#%s\n", SIGNAL_CHECKLOGIN, "error", "This user is online now!");
      }
      else{
        loginUser(user, &fd);
        sprintf( send_msg,"%s#%s#%s\n", SIGNAL_CHECKLOGIN, "ok", user);
      }
    }
    else sprintf( send_msg,"%s#%s#%s\n", SIGNAL_CHECKLOGIN, "error", "Username or Password is incorrect!");
    // while(1); // test timeout
    send(fd, send_msg, strlen(send_msg), 0);
    printf("Send message: %s|\n", send_msg);
  }
  else if (strcmp(str, SIGNAL_LOGOUT) == 0){
    // str = strtok( recv_msg, token); 
    printf("%s\n", str);

    user = strtok(NULL, token);
    logoutUser(user);
    sprintf( send_msg,"%s\n", SIGNAL_LOGOUT);
    write(fd, send_msg, strlen(send_msg));
    printf("Send message: %s|\n", send_msg);
  }
  else if (strcmp(str, CREATE_ROOM) == 0)
  {
    printf("%s\n", str);

    user = strtok(NULL, token);
    char* roomID = createRoomId(user);
    sprintf( send_msg,"%s#%s\n", CREATE_ROOM, roomID);
    write(fd, send_msg, strlen(send_msg));
    printf("Send message: %s|\n", send_msg);
  }
  else if (strcmp(str, JOIN_ROOM) == 0)
  {
    printf("%s\n", str);
    user = strtok(NULL, token);
    roomId = strtok(NULL, token);
    list = checkRoom(roomId);

    if ( *(list)== 0 || *(list + 1) != 0){
      sprintf( send_msg,"%s#%s#%s\n", JOIN_ROOM, "error", "This room is not available!");
      write(fd, send_msg, strlen(send_msg));
    }
    else if (*(list)!= 0 && *(list + 1) == 0)
    {
      userNode* p = findUserByFd(*(list));
      char* opUser = p->user.username;
      saveRoom(user, roomId);
      sprintf( send_msg,"%s#%s#%s#%s\n", JOIN_ROOM, "ok", user, opUser);
      list = checkRoom(roomId);
      write(*(list + 0), send_msg, strlen(send_msg));
      write(*(list + 1), send_msg, strlen(send_msg));
    }

    printf("Send message: %s|\n", send_msg);
  }
  else if (strcmp(str, LEAVE_ROOM) == 0)
  {
    printf("%s\n", str);
    user = strtok(NULL, token);
    roomId = strtok(NULL, token);
    //send message
    list = checkRoom(roomId);
    sprintf( send_msg,"%s#%s\n", LEAVE_ROOM, user);
    if (*(list) != -1) write(*(list + 0), send_msg, strlen(send_msg));
    if (*(list + 1) != -1) write(*(list + 1), send_msg, strlen(send_msg));

    //update Room
    saveRoom(user, "null");
    list = checkRoom(roomId);
    if (*(list) == 0){
      char buf[11];
      snprintf(buf, sizeof(buf), "%s.txt", roomId);
      remove(buf);
    }
    printf("Send message: %s|\n", send_msg);
  }
  else if (strcmp(str, START_GAME) == 0)
  {
    user = strtok(NULL, token);
    roomId = strtok(NULL, token);
    list = checkRoom(roomId);
    
    sprintf( send_msg,"%s#%s\n", START_GAME, user);
    write(*(list + 0), send_msg, strlen(send_msg));
    write(*(list + 1), send_msg, strlen(send_msg));

    char buf[11];
    snprintf(buf, sizeof(buf), "%s.txt", roomId);
    remove(buf);
  }
  else if (strcmp(str, GAME_MOVE) == 0)
  {
    roomId = strtok(NULL, token);
    user = strtok(NULL, token);
    row = atoi(strtok(NULL, token));
    col = atoi(strtok(NULL, token));
    list = checkRoom(roomId);

    char buf[11];
    snprintf(buf, sizeof(buf), "%s.txt", roomId);

    writeLog(buf, col, row, &fd);
    sprintf(send_msg, "%s#%s#%d#%d\n", GAME_MOVE, user, row, col);
    write(*(list + 0), send_msg, strlen(send_msg));
    write(*(list + 1), send_msg, strlen(send_msg));

    moveList = getMoveList(roomId);
    int isWin = checkWin(moveList, &fd, col, row);
    if (isWin == 1){
      sprintf(send_msg, "%s#%s#%d#%d#%d#%d#%d\n", GAME_WIN, user, *(winMoveList), *(winMoveList + 1), *(winMoveList + 2), *(winMoveList + 3), *(winMoveList + 4));
      write(*(list + 0), send_msg, strlen(send_msg));
      write(*(list + 1), send_msg, strlen(send_msg));
    }
  }
  else if (strcmp(str, SET_POINT) == 0)
  {
    user = strtok(NULL, token);
    roomId = strtok(NULL, token);
    int status = atoi(strtok(NULL, token));
    time = localTime();

    moveList = getMoveList(roomId);
    int moveNumber = getNumberOfMove(moveList, &fd);
    printf("Move: %d", moveNumber);
    float point = 0;
    if (status == 1) point += (float)(9 + (moveNumber*0.01));
    else if (status == -1) point += (float)moveNumber*0.01;
    else point += (float)9/2;
    updateCaroRanking(user, point, status);

    sprintf(send_msg,"%s#%s#%d#%.2f\n", SET_POINT, time, status, point);
    write(fd, send_msg, strlen(send_msg));
  }
  
  else if (strcmp(str, CHAT) == 0)
  {
    user = strtok(NULL, token);
    roomId = strtok(NULL, token);
    message = strtok(NULL, "\n");
    time = localTime();

    sprintf(send_msg, "%s#%s#%s#%s\n", CHAT, user, time, message);
    list = checkRoom(roomId);
    write(*(list + 0), send_msg, strlen(send_msg));
    write(*(list + 1), send_msg, strlen(send_msg));
    printf("Send message: %s|\n", send_msg);
  }
  else if (strcmp(str, RANKING) == 0)
  {
    user = strtok(NULL, token);

    caronode* p;
    p = checkUserCaro(user);

    if (p == NULL){
      sprintf(send_msg, "%s#%d#%d#%d#%.2f\n", USER_INFO, 0, 0, 0, (float)0.00);
      write(fd, send_msg, strlen(send_msg));
    }
    else {
      sprintf(send_msg, "%s#%d#%d#%d#%.2f\n", USER_INFO, p->user.numberOfWin, p->user.numberOfDraws, p->user.numberOfLose, p->user.point);
      write(fd, send_msg, strlen(send_msg));
    }

    int i = 0;
    for ( p = caroroot; p!= NULL; p = p->next ){
      sprintf(send_msg, "%s#%d#%s#%d#%d#%d#%.2f\n", RANKING, ++i, p->user.username, p->user.numberOfWin, p->user.numberOfDraws, p->user.numberOfLose, p->user.point);
      write(fd, send_msg, strlen(send_msg));
      sleep(0.75);
    }

  }
  
}

int main(int argc, char *argv[]){
  if(argc != 2){
    printf("Syntax Error.\n");
    printf("Syntax: ./server PortNumber\n");
    return 0;
  }
  if(check_port(argv[1]) == 0){
    printf("Port invalid\n");
    return 0;
  }
  PORT = atoi(argv[1]);

  int sock, connected, sin_size, true = 1;
  int fdmax, i, rc;
  fd_set read_fds;
  
  FD_ZERO(&master);
  FD_ZERO(&read_fds);
  initList();
  userRoot = NULL; userCur = NULL; userNew = NULL;
  caroroot = NULL; carocur = NULL; caronew = NULL;
  readFileUser();
  readFileCaroRanking();
  
  // Step 1: Construct a TCP socket to listen connection request
  if((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
    perror("Socket Error!!!\n");
    exit(-1);
  }
  if(setsockopt(sock,SOL_SOCKET,SO_REUSEADDR,&true,sizeof(int)) == -1) {
    perror("Setsockopt error!!!\n");
    exit(-2);
  }
  
  //Step 2: Bind address to socket
  server_addr.sin_family = AF_INET;         
  server_addr.sin_port = htons(PORT);     
  server_addr.sin_addr.s_addr = INADDR_ANY; 
  bzero(&(server_addr.sin_zero),8); 
  
  if (bind(sock, (struct sockaddr *)&server_addr, sizeof(struct sockaddr)) == -1) {
    perror("Unable to bind\n");
    exit(-3);
  }
  
  //Step 3: Listen request from client
  if (listen(sock, 5) == -1) {
    perror("Listen error\n");
    exit(-4);
  }  
  printf("FUNGAME waiting for client on port %d\n", PORT);
  fflush(stdout);
  
  FD_SET(sock, &master);
  fdmax = sock;

  // Set timeout
  struct timeval timeout;
  timeout.tv_sec  = 1000;  // after 1000 seconds will timeout
  timeout.tv_usec = 0;
  //Step 4: Communicate with clients
  while(1){
    read_fds = master;
    rc = select(fdmax + 1, &read_fds, NULL, NULL, &timeout);
    if( rc == -1){
      perror("select() error!\n");
      exit(-6);
    }
    // else if (rc == 0){
    //  printf("  select() timed out. End program.\n");
    //  exit(-5);
    // }
    for(i = 0; i <= fdmax; i++){
      if(FD_ISSET(i, &read_fds)){
        if(i == sock){
          sin_size = sizeof(struct sockaddr_in);
          connected = accept(sock, (struct sockaddr*)&client_addr, &sin_size);
          if(connected == -1){
            perror("accept error!\n");
            exit(-7);
          }
          else{
            FD_SET(connected, &master);
            if(connected > fdmax)
              fdmax = connected;
            printf("Got a connection from (%s , %d) with fd = %d\n", inet_ntoa(client_addr.sin_addr),ntohs(client_addr.sin_port), connected);
            handleDataFromClient(connected);
          }
        }
        else{
          handleDataFromClient(i);
        }
      }
    }
  }
  close(sock);
  return 0;
}