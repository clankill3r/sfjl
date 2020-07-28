package sfjl_examples;

import static sfjl.SFJL_Print.*;


public class SFJL_Print_Example {
    
    public static void main(String[] args) {
        
        println("No System.out typing!");
        println("Use", "commas", "to", "separate");
        
        int[] fibonacci = {1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144};
        println("fibonacci: ", fibonacci);
        
        int[][] array_2d = {{1}, {1, 2}, {1, 2, 3}};
        println("array_2d");
        println(array_2d);
    }
    
}