#include <stdio.h>
#include <string.h>
#include "caro.h"

//board 16x16
#define ROW_SIZE 16
#define COL_SIZE 16

int* moveList;
int winList[5];
int* winMoveList = winList;

int getCell(int col, int row){
    if(col < 0 || col >= COL_SIZE)
        return -1;
    if(row < 0 || row >= ROW_SIZE)
        return -1;
    return moveList[row * COL_SIZE + col]; 
}

/*
check win(1)-not know(-1)-draw(0)
*/
int checkWin(int* moveList, int* fd, int col, int row){
    int check = 0, cot = col, hang;
    
    //kiem tra hang
    while (getCell(cot, row) == getCell(col, row))
    {
        *(winMoveList + check) = row * COL_SIZE + cot;
        check++;
        cot++;
    }
    cot = col - 1;
    while (getCell(cot, row) == getCell(col, row))
    {
        *(winMoveList + check) = row * COL_SIZE + cot;
        check++;
        cot--;
    }
    if (check > 4) return 1;

    //kiem tra cot
    check = 0; hang = row;
    while (getCell(col, hang) == getCell(col, row))
    {
        *(winMoveList + check) = hang * COL_SIZE + col;
        check++;
        hang++;
    }
    hang = row - 1;
    while (getCell(col, hang) == getCell(col, row))
    {
        *(winMoveList + check) = hang * COL_SIZE + col;
        check++;
        hang--;
    }
    if (check > 4) return 1;

    //kiem tra duong cheo 1
    hang = row; cot = col; check = 0;
    while (getCell(cot, hang) == getCell(col, row))
    {
        *(winMoveList + check) = hang * COL_SIZE + cot;
        check++;
        hang++;
        cot++;
    }
    hang = row - 1; cot = col - 1;
    while (getCell(cot, hang) == getCell(col, row))
    {
        *(winMoveList + check) = hang * COL_SIZE + cot;
        check++;
        hang--;
        cot--;
    }
    if (check > 4) return 1;

    //kiem tra duong cheo 2
    hang = row; cot = col; check = 0;
    while (getCell(cot, hang) == getCell(col, row))
    {
        *(winMoveList + check) = hang * COL_SIZE + cot;
        check++;
        hang++;
        cot--;
    }
    hang = row - 1; cot = col - 1;
    while (getCell(cot, hang) == getCell(col, row))
    {
        *(winMoveList + check) = hang * COL_SIZE + cot;
        check++;
        hang--;
        cot++;
    }
    if (check > 4) return 1;

    return -1; 
}

int getNumberOfMove(int* moveList, int* fd){
    static int moveNumber;
    moveNumber = 0;

    int i;
    for (i = 0; i < COL_SIZE*ROW_SIZE; i++){
      if (*(moveList + i) == *fd) moveNumber++;
    }

    return moveNumber;
}


