/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.so_memory;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author Konrad
 */
public class Process_manager {
    
    
    
    PageTable T[] = new PageTable[100]; // maksymalnie mozna uruchomic 100 procesow
    public boolean[] processmap = new boolean[100];
    
    
    Process_manager()
    {
        Arrays.fill(processmap, false);
    }
    
    
    public PageTable create(String adress) throws FileNotFoundException
    {
        int i;
        for(i=0;i<100;i++)
        {
            if(processmap[i] == false) break;
        }
        
        processmap[i] = true;
        System.out.format("Numer twojego procesu to: %d\n",i);
       
        
        File file = new File(adress);
        Scanner read = new Scanner(file);
        
        String buffor = new String();
        int letter_counter = 0;
        while(read.hasNext())
        {
              buffor = read.nextLine();
              letter_counter += buffor.length();
        }
        
        T[i] = new PageTable(letter_counter);
        return T[i];
        
 
        
        
    }
    public PageTable zwolnij(int i)
    {
        
        processmap[i] = false;
        return T[i];
    }
    public PageTable return_table(int i)
    {
        return T[i];
    }
    public void show_process_list()
    {
        System.out.print("Uruchomione procesy: ");
        for(int i =0;i<100;i++)
        {
            if(processmap[i] == true) System.out.format(" | %d", i);
        }
        System.out.println(" |");
    }
    
}
