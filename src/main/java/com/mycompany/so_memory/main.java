/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.so_memory;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
/**
 *
 * @author Konrad
 */
public class main {
    static PageTable tabela ;
    
    static public PageTable give()
    {
       return tabela;
    }
    
     static public void main (String args[]) throws FileNotFoundException 
    {
      Memory obiekt = new Memory();
      Process_manager manager = new Process_manager();
      while(true)
      {
      System.out.println("Wpisz 0 aby zobaczyc liste komend");
      int polecenie;
      Scanner keyboard = new Scanner(System.in);
      
      polecenie = keyboard.nextInt();
      String adress; // adress do pliku
      switch(polecenie)
      {
          
          case 0:
          
      System.out.println("Co chcesz zrobić: ");
      System.out.println("1.Stworz nowy 'proces' (wczytaj plik do pamieci)");
      System.out.println("2.Skasuj 'proces' (zwolnij pamiec po procesie)");
      System.out.println("3.Wyswietl liste 'uruchomionych' procesow");
      System.out.println("4.Wyswietl strony znajdujace sie w pamieci fizycznej");
      System.out.println("5.Wyswietl strony znajdujace sie w pamieci wirtualnej");
      System.out.println("6.Odczytaj komorke pamieci");
      System.out.println("7.Zapisz do pamieci");
          
      break;
          
          case 1:
        
        System.out.println("Podaj sciezke do pliku ktory mam wczytac do pamieci");
        
        adress = keyboard.next();
          File file = new File(adress);
          try
          {
        Scanner read = new Scanner(file);
          }catch(FileNotFoundException e)
          {
              System.out.println("Nie ma takiego pliku");
              break;
          }
         obiekt.all(manager.create(adress),adress);
         break;
         
          case 2:
        
         System.out.println("Podaj nr procesu ktory chcesz skasowac: ");
         int nr;
         nr = keyboard.nextInt();
         obiekt.free(manager.zwolnij(nr));
         break;
         
            case 3:
          manager.show_process_list();
          break;
          
          case 4:
             
          obiekt.show_real();
          break;
          
          case 5:
          obiekt.show_virtual();
          break;
          
          case 6:
          
          System.out.println("Podaj nr procesu z ktorego chcesz czytac: ");
          int process_number = keyboard.nextInt();
          tabela = manager.return_table(process_number);
          System.out.println("Podaj nr komorki ktora chcesz odczytac: ");
          int process_adress_r = keyboard.nextInt();
          System.out.println(obiekt.read(process_adress_r));
          break;
          
          case 7:
              
          System.out.println("Podaj nr procesu dla ktorego chcesz zapisywac: ");
          int process_adress_w = keyboard.nextInt();
          PageTable write = manager.return_table(process_adress_w);
          tabela = write;
          System.out.println("Podaj znak ktory chcesz zapisac: ");
          String c = keyboard.next();
          System.out.println("Podaj komorke w stronie na dane do ktorej zapisac ta wartosc (0-15) ");
          int place = keyboard.nextInt();
          char tab[] = c.toCharArray();
         
          
          obiekt.write(16*(write.page_amount-1)+place,tab[0]);
          System.out.println("Pomyslnie zapisalem dane");
          
          break;
        
      }
      }
              
                  
          
          
          
          
          
              
              
      
      
      
      
    }
}
