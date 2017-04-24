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






public class PageTable {
  
    
    public int size; // pole uzupelniane przez zarządca procesów 
    public int page_amount;
    public Table[] T;
    
    PageTable(int new_size)
    {
        size = new_size;
    }
}
