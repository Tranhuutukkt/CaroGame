#include <stdio.h>
#include <string.h>
#include <time.h>
#include "serverHelper.h"

userNode* makeNewUser(userProfile user){
  userNode *newUser = (userNode*)malloc(sizeof(userNode));
  newUser->user=user;
  newUser->next=NULL;
  return newUser;
}

void insertUser(userProfile user){
  userNode* newUser = makeNewUser(user);
  if( userRoot == NULL ) {
    userRoot = newUser;
    userCur = userRoot;
  }else {
    newUser->next=userCur->next;
    userCur->next = newUser;
    userCur = userCur->next;
  }
}

userNode* findUser(char* username){ // tim user dua theo username
  userNode* p;
  for ( p = userRoot; p!= NULL; p = p->next ){
    if (strcmp(username, p->user.username) == 0){
    return p;
    break;
  }
  }
  return NULL;
}

userNode* findUserByFd(int fd){ // tim user dua theo fd
  userNode* p;
  for ( p = userRoot; p!= NULL; p = p->next ){
    if (p->user.fd == fd){
    return p;
    break;
  }
  }
  return NULL;
}

int* checkRoom(char* roomId){ //kiem tra xem phong con slot hay khong
  userNode* p;
  static int list[2] = {0, 0};
  int count = 0;
  for ( p = userRoot; p!= NULL; p = p->next ){
    if (strcmp(roomId, p->user.roomId) == 0)
      list[count++] = p->user.fd;
  }
  return list;
}

void to_freeUser(userNode* userRoot){ // giai phong list
  userNode *to_free;
  to_free = userRoot;
  while(to_free != NULL){
    userRoot = userRoot->next;
    free(to_free);
    to_free = userRoot;
  }
}

void readFileUser(){
  printf("Loading data from file...\n");
  int count = 0;
  FILE *f;
  f = fopen("user.txt", "r+");
  if(f==NULL){// check file
    printf("Cannot open file user.txt!!!\n");
    return;
  }
  userProfile user;
  while(!feof(f)){
    // dinh dang file: username#pass#fd#roomId
    fscanf(f, "%[^#]#%[^#]#%d#%s\n", user.username, user.password, &user.fd, user.roomId);
    insertUser(user);
    count++;
    printf("%d. %s - %s - %d - %s\n", count, user.username, user.password, user.fd, user.roomId);
    if(feof(f)) break;
  }
  printf("LOADED SUCCESSFULY %d ACCOUNT(S)\n", count);
}

void updateUserProfile(){
  FILE *f = fopen("user.txt", "w");
  userNode* p;
  for ( p = userRoot; p!= NULL; p = p->next ){
    fprintf(f, "%s#%s#%d#%s\n", p->user.username, p->user.password, p->user.fd, p->user.roomId);
  }
  fclose(f);
}

/*
Kiểm tra username, password
Output: 1 - user, pass hợp lệ; 0 - không hợp lệ
*/
int isValid(char* username, char* password){
  FILE* f = fopen("user.txt", "r+");
  if(f == NULL ){
    printf("Error open file!!!\n");
    return 0;
  }
  char line[100];
  char* temp;
  char* temp2;  
  if( password != NULL){
    while(fgets( line, 100, f) != NULL){
      temp = line;
      while(temp[0] != '#') temp++; // get user, gap # thi dung
      temp[0] = '\0';
      temp++;
      if(temp[strlen(temp) - 1] == '\n') temp[strlen(temp) - 1] = '\0';

      temp2 = temp;
      while(temp2[0] != '#') temp2++; // get pass, gap # thi dung
      temp2[0] = '\0';
      temp2++;
      if(temp2[strlen(temp2) - 1] == '\n') temp2[strlen(temp2) - 1] = '\0';

      if(strcmp(line, username) == 0 && strcmp(temp, password) == 0){
      	fclose(f);
      	return 1;
      }
    }
    fclose(f);
    return 0;
  } else{
    while(fgets(line, 100, f) != NULL){
      temp = line;
      while(temp[0] != '#') temp++;
      temp[0] = '\0';      
      if(strcmp(line, username) == 0){
      	fclose(f);
      	return 1;
      }
    }
    fclose(f);
    return 0;
  }  
}

/*
Đăng kí user mới
*/
void registerUser(char* username, char* password, int* fd){
  userProfile user;
  strcpy(user.username, username);
  strcpy(user.password, password);
  strcpy(user.password, "null");
  user.roomId[0] = '\0';
  user.fd = *fd;
  insertUser(user);
  updateUserProfile();
}

/*
Cap nhat khi login
*/
void loginUser(char* username, int* fd){
  userNode* p = findUser(username);
  p->user.fd = *fd;
  updateUserProfile();
}

/*
Cap nhat khi logout
*/
void logoutUser(char* username){
  userNode* p = findUser(username);
  p->user.fd = -1;
  updateUserProfile();
}

/*
Ghi vào file log
*/
void writeLog(char* roomId, int col, int row, char* user){
  FILE* f = fopen(roomId, "a");
    fprintf(f, "%s-%d-%d\n", user, col, row); //format: user - col - row
  fclose(f);
}

//roomId
char* createRoomId(char* username){
  userNode* p = findUser(username);
  srand((unsigned int)(time(NULL)));
	int index = 0;
  static char roomID[6];

	char char1[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	for(index = 0; index < 6; index++)
	{
    roomID[index] = char1[rand() % (sizeof char1 - 1)];
	}
	roomID[strlen(roomID)] = '\0';
  saveRoom(username, roomID);
  
	return roomID;
}

//save room
void saveRoom(char* username, char* roomId){
  userNode* p = findUser(username);
  strcpy(p->user.roomId, roomId);
  updateUserProfile();
}