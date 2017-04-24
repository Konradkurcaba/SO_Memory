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
public class Memory {
 
    private char[] realmem = new char[256]; // pamiec fizyczna
    private char[] virtualmem = new char[1024]; //pamiec wirtualna
    private boolean[] realmap = new boolean[16]; // zajete i wolne obszary w pamieci fizycznej
    private boolean[] virtualmap = new boolean[64]; //zajete i wolne obszary w pamieci wirtualnej 0 - wolne 1 - zajete
    private LinkedList<FIFO_Queue> FIFO = new LinkedList<FIFO_Queue>();// kolejka fifo dla funkcji pageswap, informuje o tym ktora strona jest najstarsza
                                                                   // oraz jakiego procesu dotyczy ( by mozna bylo dokonac z mian w PageTable )
    Memory()
    {
        Arrays.fill(realmap, false);    //zerowanie tablic
        Arrays.fill(virtualmap, false);
    }
    public int all(PageTable T,String file_location) throws FileNotFoundException // allokacja pamieci dla nowego procesu, zwraca -1 gdy brakuje pamieci 
    {
        int page_amount;
        if(T.size % 16 == 0) // gdy wielkosc programu dzieli sie przez 16 bez reszty 
        {
            page_amount = (T.size / 16 ) + 1;
        }
        else 
        {
            page_amount = (T.size / 16) + 2;
        }
        T.page_amount = page_amount; // wpisanie liczby stron do struktury pageTable
        T.T = new Table[page_amount]; // stworzenie tablicy stron
        
        
        for(int i = 0;i<page_amount;i++)
        {
            T.T[i] = new Table();
        }
        
        
        
        
        int current_page = 0; // zmienna przechowuje info ktora strona w tablicy stron jest umieszczana w pamieci wirtualnej
        int current_frame = -1; // zmienna przechowuje informacje o aktualnie przetwarzanej ramce 
        File file_place = new File(file_location); //wskazanie lokacji pliku
        Scanner read = new Scanner(file_place); // przekazanie lokacji do odczytu 
        String line_buffor; // buffor na aktualnie uzywana linie pliku 
        int letter_counter = 0 ; 
        
        
        line_buffor = read.nextLine();
        while(page_amount != 1) // petla kopiujaca program do pamieci virtualnej //ostatnia strona jest na dane i nie chcemy do niej kopiowac kodu programu 
        {
            current_frame = -1;
            for(int i =0;i<64;i++) // szukanie wolnych stron w pamieci wirtualnej
            {
              
                if(virtualmap[i] == false )
                {
                    current_frame = i;
                    break;
                }
                
                
            }
            if (current_frame == -1) return -1;
            virtualmap[current_frame] = true;
          
            
            T.T[current_page].v = false;
            T.T[current_page].page_number = current_frame; // zapisanie informacji w tablicy stron o miejscu przechowywania strony
            
            current_frame *= 16; // zamiana nr ramki na jej adres fizyczny
            for(int i = 0;i< 16;i++) // wpisywanie kodu programu do wolnej strony w pamieci wirtualnej
            {
                
                if(line_buffor.length() == letter_counter) // sprawdzanie czy jest cos jeszcze w danej lini
                {
                    if(!read.hasNextLine()) break; // wyskakiwanie z petli jesli plik nie ma kolejnej lini
                    line_buffor = read.nextLine();
                    letter_counter = 0;
                }
                virtualmem[current_frame++] = line_buffor.charAt(letter_counter++);
                
                
                    
            }
            current_page++;
            page_amount--;
            
        }
        
        // zarezerwowanie miejsca na ostatnia strone ( z danymi ) 
        for(int i =0;i<64;i++) // szukanie wolnych stron w pamieci wirtualnej
            {
                current_frame = -1;
                if(virtualmap[i] == false )
                {
                    current_frame = i;
                    break;
                }
      
            }
        
          if (current_frame == -1) return -1;
                
               virtualmap[current_frame] = true; // informacja o tym ze ramka bedzie zajeta 
               T.T[current_page].page_number = current_frame; //przypisanie tej ramki do tablicy stron 
               T.T[current_page].v = false;
               
                    
        // wrzucenie pierwszej i ostatniej strony do pamięci fizycznej
        int physic_place = newpage(T.T[0].page_number); // sprowadzenie strony do pamieci fizycznej
        T.T[0].page_number = physic_place; // wpisanie do tablicy stron informacji o tym gdzie jest teraz strona 0
        T.T[0].v = true; // ustawienie informacji ze strona jest w pamieci fizycznej
        FIFO.offer(new FIFO_Queue(physic_place,0,T)); //dodanie strony do kolejki FIFO
        physic_place = newpage(T.T[current_page].page_number);
        T.T[current_page].page_number = physic_place; // wpisanie do tablicy stron informacji o tym gdzie jest teraz strona z danymi 
        T.T[current_page].v = true; // ustawienie informacji ze strona jest w pamieci fizycznej
        FIFO.offer(new FIFO_Queue(physic_place,current_page,T)); //dodanie strony do kolejki FIFO
        
        return 1;
    }
    
    public int free(PageTable T)        
    {
        for(int i = 0;i<T.page_amount;i++) // petla sie zakonczy gdy przejedzie przez wszystkie strony danego procesu 
        {
            if(T.T[i].v == true)
            {
                realmap[T.T[i].page_number] = false;
                System.out.format("Zwalniam ramke nr %d z pamieci fizycznej\n",T.T[i].page_number);
            }
            else
            {
            virtualmap[T.T[i].page_number] = false;
            System.out.format("Zwalniam ramke nr %d z pamieci wirtualnej\n",T.T[i].page_number);
            }
        }
        FIFO_Queue spr;
        
        for(int i = 0;i < FIFO.size();i++) // petla czyszcaca kolejke FIFO po zwolnionym programie 
        {
           spr=FIFO.get(i);
           if(spr.proces == T) 
           {
               FIFO.remove(i);
               i--;
           }
        }
        return 1;
    }
    
    public char read(int adress)
    {
        int page_number = adress / 16; // numer strony w PageTable 
        int cell_number = adress % 16;  // i wartosc przesuniecia 
        
        PageTable current_process = main.give(); // znalezienie struktury PageTable aktualnie wykonywanego procesu
        int page_location = current_process.T[page_number].page_number; // pobranie informacji o tym gdzie jest strona
       
        if(current_process.T[page_number].v == true) //jesli strona jest w pamieci fizycznej
        {
            return realmem[(page_location * 16) + cell_number]; // zwróć żądaną komórkę pamięci 
        }
        else
        {
            int physic_number = newpage(page_location); // sprowadza strone z pamieci wirtualnej i zapisujemy jej nowe miejsce w pamieci fizycznej
            FIFO.offer(new FIFO_Queue(physic_number,page_number,current_process));
            current_process.T[page_number].page_number = physic_number; // zapisuje informacje o nowej lokalizacji stony 
            current_process.T[page_number].v = true;
            return realmem[(physic_number*16 ) + cell_number]; // zwraca żądaną komórkę pamięci
        }
        
        
    }
    
    
    public int write(int adress,char value)
    {
        int page_number = adress / 16; // numer strony w PageTable 
        int cell_number = adress % 16;  // i wartosc przesuniecia 
        
         PageTable current_process = main.give(); // znalezienie struktury PageTable aktualnie wykonywanego procesu
        int page_location = current_process.T[page_number].page_number; // pobranie informacji o tym gdzie jest strona
       
        if(current_process.T[page_number].v == true) //jesli strona jest w pamieci fizycznej
        {
            realmem[(page_location * 16) + cell_number] = value; // zapisujemy wartosc w pamieci fizycznej 
        }
        else
        {
            int physic_number = newpage(page_location); // sprowadza strone z pamieci wirtualnej i zapisujemy jej nowe miejsce w pamieci fizycznej
            FIFO.offer(new FIFO_Queue(physic_number,page_number,current_process));
            current_process.T[page_number].page_number = physic_number; // zapisuje informacje o nowej lokalizacji stony 
            current_process.T[page_number].v = true;
            realmem[(physic_number*16 ) + cell_number] = value; // zapisujemy wartosc w pamieci fizycznej
        }
        
        
        
        return 1;
        
        
    }
    
    
    public int pageswap() // funkcja wyrzuca strony z pamieci fizycznej, zwraca nr zwolnionego miejsca; 
    {
        int free_place = -1;
        
        for(int i = 0;i<64;i++)   //szukanie wolnych miejsc w pamieci virtualnej
        {
            if(virtualmap[i] == false)
            {
                if(free_place != -1)
                {
                    free_place = i;
                }
                else
                {
                    free_place = i;
                    break;
                }  
            }
        }
       if(free_place == -1) return -1; // jesli nie ma wolnego miejsca zwroc -1
       else virtualmap[free_place] = true;
       FIFO_Queue page = FIFO.poll();
       page.proces.T[page.page_number].page_number = free_place;  //zmiana w strukturze page table, aby wiedziec ze strona zostala
       page.proces.T[page.page_number].v = false;           //przeniesiona do pamieci wirtualnej
       
       
       free_place *= 16;  //okreslenie adresu pierwszej komorki w ramce
       int page_adres = page.page_place;
       realmap[page_adres] = false; 
       int return_value = page_adres;
       page_adres*=16;
       System.out.format("Zwolnilem ramke nr %d w pamieci fizycznej, zajalem ramke nr %d w wirtualnej\n",return_value,free_place/16);
        for(int i = 0;i<16;i++)
        {
           virtualmem[free_place++] = realmem[page_adres++];
        }
        
        return return_value;
    }
    
    public int newpage(int page_number) // sprowadza nowa strone z pamieci virtualnej, zwraca jej nowe miejsce w pamieci fizycznej
    {
        int space_count = 0;
        int free_space = -1;
        for(int i = 0;i<16;i++) // sprawdzenie ile ramek jest wolnych i wyznaczenie wolnej
        {
            if(realmap[i] == true)
            {
                space_count += 1;
               
            }else free_space = i;
         
        }
        if(space_count > 14)free_space = pageswap(); // page swap zwraca nr ramki ktora zwolnil 
        
        realmap[free_space] = true;
        virtualmap[page_number] = false;
        int return_value = free_space;
        free_space *= 16;   
        page_number *= 16;
       for(int i = 0;i<16;i++) // przpisywanie strony z pamieci virtualnej do fizycznej
       {
            realmem[free_space++] = virtualmem[page_number++];
       }
        
        
   
        
        
        return return_value;
    }
    
   public void show_real()
   {
       for(int i = 0;i<16;i++)
       {
           if(realmap[i] == true)
           {
               System.out.format("Strona nr %d - ",i);
             for(int j = 0;j<16;j++)
             {
                 System.out.format(" | %c", realmem[i*16+j]);
             }
             System.out.println("");
           }
       }
   }
   public void show_virtual()
   {
       for(int i = 0;i<64;i++)
       {
           if(virtualmap[i] == true)
           {
               System.out.format("Strona nr %d - ",i);
             for(int j = 0;j<16;j++)
             {
                 System.out.format(" | %c", virtualmem[i*16+j]);
             }
             System.out.println("");
           }
       }
   }
   
}
