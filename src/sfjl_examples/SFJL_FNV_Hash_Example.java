/** SFJL_Base64_Example - v0.51
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
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
        "64b from []short   "+fnv_hash_64(some_shors),
        "64b from []double  "+fnv_hash_64(some_doubles),
        "64b from []float   "+fnv_hash_64(some_floats),
        "64b from []string  "+fnv_hash_64(some_strings),
        "64b from []char    "+fnv_hash_64(some_chars)
    ));

    System.out.println(String.join("\n", "\n",
        "32b from []long    "+fnv_hash_32(some_longs),
        "32b from []int     "+fnv_hash_32(some_ints),
        "32b from []byte    "+fnv_hash_32(some_bytes),
        "32b from []short   "+fnv_hash_32(some_shors),
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

    //
    // Varargs
    //
    System.out.println(String.join("\n", "\n",
        "32b from []long    "+fnv_hash_32(1L, 2L, 3L),
        "32b from []int     "+fnv_hash_32(1, 2, 3),
        "32b from []byte    "+fnv_hash_32(0xb1, 0xb2, 0xb3),
        "32b from []short   "+fnv_hash_32(1, 2, 3),
        "32b from []double  "+fnv_hash_32(1., 2., 3.),
        "32b from []float   "+fnv_hash_32(1f, 2f, 3f),
        "32b from []string  "+fnv_hash_32("12", "3"),
        "32b from []char    "+fnv_hash_32('1', '2', '3')
    ));
    // same for _64
}

}
/**
revision history:

   0.51  (2022-02-04) varargs examples
   0.50  (2020-08-12) first numbered version

*/

/**
------------------------------------------------------------------------------
This software is available under 2 licenses -- choose whichever you prefer.
------------------------------------------------------------------------------
ALTERNATIVE A - MIT License
Copyright (c) 2020 Doeke Wartena
Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
------------------------------------------------------------------------------
ALTERNATIVE B - Public Domain (www.unlicense.org)
This is free and unencumbered software released into the public domain.
Anyone is free to copy, modify, publish, use, compile, sell, or distribute this
software, either in source code form or as a compiled binary, for any purpose,
commercial or non-commercial, and by any means.
In jurisdictions that recognize copyright laws, the author or authors of this
software dedicate any and all copyright interest in the software to the public
domain. We make this dedication for the benefit of the public at large and to
the detriment of our heirs and successors. We intend this dedication to be an
overt act of relinquishment in perpetuity of all present and future rights to
this software under copyright law.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
------------------------------------------------------------------------------
*/