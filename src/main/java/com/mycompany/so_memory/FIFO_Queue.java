/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.so_memory;

/**
 *
 * @author Konrad
 */
public class FIFO_Queue { 
    
    FIFO_Queue(int place,int number,PageTable proces_in) 
    {
        page_place = place;
        page_number = number;
        proces = proces_in;
    }
    
   int page_place;  //miejsce strony w pamieci fizycznej
   int page_number; //ktora to strona dla procesuu 
   PageTable proces;
}
