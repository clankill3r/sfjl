package sfjl_examples;

import static sfjl.SFJL_FNV_Hash.*;


public class SFJL_FNV_Hash_Example {


public static void main(String[] args) {

    //
    // Arrays
    //
    
    long   []some_longs      = {1, 2, 3};
    int    []some_ints       = {1, 2, 3};
    byte   []some_bytes      = {1, 2, 3};
    double []some_doubles    = {1, 2, 3};
    float  []some_floats     = {1, 2, 3};
    short  []some_shors      = {1, 2, 3};
    String []some_strings    = {"ac", "dc"};
    char   []some_chars      = {'a', 'c', 'd', 'c'};

    
    System.out.println(String.join("\n",
        "64b from []long    "+fnv_hash_64(some_longs),
        "64b from []int     "+fnv_hash_64(some_ints),
        "64b from []byte    "+fnv_hash_64(some_bytes),
        "64b from []shor    "+fnv_hash_64(some_shors),
        "64b from []double  "+fnv_hash_64(some_doubles),
        "64b from []float   "+fnv_hash_64(some_floats),
        "64b from []string  "+fnv_hash_64(some_strings),
        "64b from []char    "+fnv_hash_64(some_chars)
    ));

    System.out.println(String.join("\n", "\n",
        "32b from []long    "+fnv_hash_32(some_longs),
        "32b from []int     "+fnv_hash_32(some_ints),
        "32b from []byte    "+fnv_hash_32(some_bytes),
        "32b from []shor    "+fnv_hash_32(some_shors),
        "32b from []double  "+fnv_hash_32(some_doubles),
        "32b from []float   "+fnv_hash_32(some_floats),
        "32b from []string  "+fnv_hash_32(some_strings),
        "32b from []char    "+fnv_hash_32(some_chars)
    ));

    //
    // String
    //

    System.out.println(String.join("\n", "\n",
        "64b from String    "+fnv_hash_64("Bananas"),
        "32b from String    "+fnv_hash_32("Bananas")
    ));

    //
    // Building a hash
    //

    int  h32 = FNV_START_HASH_32; // any non 0 number will actually do
    long h64 = FNV_START_HASH_64; // any non 0 number will actually do

    for (int i = 0; i < 10; i++) {
        h32 = fnv_hash_32_builder(h32, i);
        h64 = fnv_hash_64_builder(h64, i);
    }

    System.out.println(String.join("\n", "\n",
        "64b from builder   "+h64,
        "32b from builder   "+h32
    ));
    
    //
    // Reducing a hash
    //
    
    System.out.println(String.join("\n", "\n",
        "16b from 64b"+fnv_hash_64b_to_n_bits(h64, 16),
        "16b from 32b"+fnv_hash_32b_to_n_bits(h32, 16)
    ));

}


}