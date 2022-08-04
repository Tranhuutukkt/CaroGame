#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "caroRanking.h"

char inforCaroRankingOfUser[1000];

caronode* makeNewNodeCaro(userInforCaro user){
  caronode *caronew=(caronode*)malloc(sizeof(caronode));
  caronew->user=user;
  caronew->next=NULL;
  return caronew;
}

void insertCaro(userInforCaro user){
  caronode* caronew=makeNewNodeCaro(user);
  if( caroroot == NULL ) {
    caroroot = caronew;
    carocur = caroroot;
  }else {
    caronew->next=carocur->next;
    carocur->next = caronew;
    carocur = carocur->next;
  }
}

void displayNodeCaro(caronode* p){ // hien thi 1 node
  if (p==NULL){
    printf("Loi con tro NULL\n");
    return; 
  }
  userInforCaro tmp = p->user;
  printf("%-20s%-10d%-10d%-10d%-10.1f\n", tmp.username, tmp.numberOfWin, tmp.numberOfLose, tmp.numberOfDraws, tmp.point);
}

void traversingListCaro(){ // duyet ca list
  caronode* p;
  for ( p = caroroot; p!= NULL; p = p->next )
    displayNodeCaro(p);
}

void to_freeCaro(caronode* caroroot){ // giai phong list
  caronode *to_free;
  to_free=caroroot;
  while(to_free!=NULL){
    caroroot=caroroot->next;
    free(to_free);
    to_free=caroroot;
  }
}

void readFileCaroRanking(){
  FILE *f;
  f = fopen("caroRanking.txt", "r+");
  if(f==NULL){// check file
    printf("Cannot open file caroRanking.txt!!!\n");
    return;
  }
  userInforCaro user;
  while(!feof(f)){
    // dinh dang file: username win lose draw point
    fscanf(f, "%s %d %d %d %f\n", user.username, &user.numberOfWin, &user.numberOfLose, &user.numberOfDraws, &user.point);
    insertCaro(user);
    if(feof(f)) break;
  }
}

void updateFileCaroRanking(){
  FILE *f = fopen("caroRanking.txt", "w");
  caronode* p;
  for ( p = caroroot; p!= NULL; p = p->next ){
    fprintf(f, "%s %d %d %d %.1f\n", p->user.username, p->user.numberOfWin, p->user.numberOfLose, p->user.numberOfDraws, p->user.point);
  }
  fclose(f);
}

caronode* checkUserCaro(char name[100]){// kiem tra user co trong list chua
  caronode* p;
  for ( p = caroroot; p!= NULL; p = p->next ){
    if(strcmp(p->user.username,name)==0){ // =0 la giong nhau
      // printf("%s %d\n", p->user.username, p->user.point);
      return p; // tra ve 1 la user da ton tai
    }
  
  }
  return NULL; // tra ve 0 la user chua ton tai
}

// sort_by_point
void sortCaroRanking(){
  caronode *p, *q;
  userInforCaro tmp;
  for (p = caroroot; p->next != NULL; p = p->next)
    for (q = p->next; q != NULL; q = q->next)
      if (p->user.point < q->user.point)
      {
        tmp = p->user;
        p->user = q->user;
        q->user = tmp;
      }
}

/*
update caro ranking
*/
void updateCaroRanking( char* user, int point, int status){
  readFileCaroRanking();
  caronode* tmp = checkUserCaro(user);
  if(tmp == NULL ){ // user ko co trong danh sach
    userInforCaro caroUser;
    strcpy(caroUser.username, user);
    caroUser.numberOfWin=0; caroUser.numberOfLose=0; caroUser.numberOfDraws=0; caroUser.point=0;
    insertCaro(caroUser);
    updateFileCaroRanking();
    tmp = checkUserCaro(user);
  }
  

  if( status == 1){ // 0 hòa, -1 thua, 1 thắng
    tmp->user.numberOfWin++;
    tmp->user.point += point;
    updateFileCaroRanking();
  }
  else if ( status == -1){
    tmp->user.numberOfLose++;
    tmp->user.point += point;
    updateFileCaroRanking();
  }
  else if ( status == 0 ){
    tmp->user.numberOfDraws++;
    tmp->user.point += point;
    updateFileCaroRanking();
  }

  // traversingListCaro();
  caroroot = NULL; carocur = NULL; caronew = NULL; tmp = NULL;
}