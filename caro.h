#include <stdio.h>
#include <string.h>

#define ROW_SIZE 16
#define COL_SIZE 16
int* moveList;

int getCell(int col, int row);

/*
check win(1)-lose(-1)-draw(0)
*/
int checkWin(int* moveList, int* fd, int col, int row);