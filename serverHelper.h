#include <stdio.h>
#include <stdlib.h>

typedef struct elementtypeUser{
  char username[50];
  char password[100];
  int fd; //-1 if not login
  char roomId[10]; //null if not in a room
}userProfile;

struct userNode{
  userProfile user;
  struct userNode *next;
};
typedef struct userNode userNode;
userNode *userRoot,*userCur,*userNew;

userNode* makeNewUser(userProfile user);

void insertUser(userProfile user);

// void displayNodeUser(userNode* p);

userNode* findUser(char* username);

userNode* findUserByFd(int fd);

void to_freeUser(userNode* userRoot);

void readFileUser();

void updateUserProfile();

int* checkRoom(char* roomId);

/*
Kiểm tra username, password
Output: 1 - user, pass hợp lệ; 0 - không hợp lệ
*/
int isValid(char* username, char* password);

/*
Đăng kí user mới
*/
void registerUser(char* username, char* password, int* fd);

/*
Cap nhat khi login
*/
void loginUser(char* username, int* fd);

/*
Cap nhat khi logout
*/
void logoutUser(char* username);

/*
Ghi vào file log
*/
void writeLog(char* roomId, int col, int row, int* fd);

/*
Get move List
*/
int* getMoveList(char* roomId);

//roomId
char* createRoomId(char* username);

//save room
void saveRoom(char* username, char* roomId);