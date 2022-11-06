#include <stdio.h>
#include <stdlib.h>

typedef struct elementtypeCaro{
  char username[50]; // Khai bao username
  int numberOfWin;
  int numberOfLose;
  int numberOfDraws;
  float point;
}userInforCaro;

struct caronode{
  userInforCaro user;
  struct caronode *next;
};
typedef struct caronode caronode;
caronode *caroroot,*carocur,*caronew;

caronode* makeNewNodeCaro(userInforCaro user);

void insertCaro(userInforCaro user);

void displayNodeCaro(caronode* p);

void traversingListCaro();

void to_freeCaro(caronode* caroroot);

void readFileCaroRanking();

void updateFileCaroRanking();

caronode* checkUserCaro(char name[100]);

int getInforUserCaro(char name[100]);

void sortCaroRanking();

void updateCaroRanking( char* user, float point, int status);  //1: win, 0: draw, -1: lose