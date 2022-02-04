/** SFJL_FNV_Hash - v0.52
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_FNV_Hash_Example.java

DOCUMENTATION:
    Fowler–Noll–Vo Hash:
    http://www.isthe.com/chongo/tech/comp/fnv/

NOTES:
    - Note(Doeke): I don't trust the JITC to inline that well, that's why the usage of fnv_hash_64_builder is very litle.

*/
package sfjl;

public class SFJL_FNV_Hash {
     private SFJL_FNV_Hash() {}
//           SFJL_FNV_Hash {


// "In the general case, almost any offset_basis will serve so long as it is non-zero."
//      quote by: - http://www.isthe.com/chongo/tech/comp/fnv/
// Note(Doeke): I can't remember how I got to 1083068131, neither I care (I sure did some effort)
static public final int FNV_START_HASH_32 = 1083068131;
static public final int FNV_START_HASH_64 = FNV_START_HASH_32;

static public final long _FNV_64_MULTIPLIER = 1099511628211L;
static public final int  _FNV_32_MULTIPLIER = 16777619;

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public long fnv_hash_64b_to_n_bits(long hash_64, int n_bits) {
    long MASK_X = (1L<<n_bits)-1;
    return (hash_64>>>n_bits) ^ (hash_64 & MASK_X);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int fnv_hash_32b_to_n_bits(int hash_32, int n_bits) {
    int MASK_X = (1<<n_bits)-1;
    return (hash_32>>>n_bits) ^ (hash_32 & MASK_X);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
// start with anything but 0, START_HASH_64 is a good start
static public long fnv_hash_64_builder(long h, long i) {
    assert h != 0;
    h = ( h ^ i ) * _FNV_64_MULTIPLIER;
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
// start with anything but 0, START_HASH_32 is a good start
static public int fnv_hash_32_builder(int h, int i) {
    assert h != 0;
    h = ( h ^ i ) * _FNV_32_MULTIPLIER;
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
//
// 64
//
static public long fnv_hash_64(int start, int length, long[] p) {
    long h = FNV_START_HASH_64;
    for (int i = start; i < length; i++ ) {
        h = ( h ^ p[i] ) * _FNV_64_MULTIPLIER;
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public long fnv_hash_64(int start, int length, double[] p) {
    long h = FNV_START_HASH_64;
    for (int i = start; i < length; i++ ) {
        h = ( h ^ Double.doubleToLongBits(p[i])) * _FNV_64_MULTIPLIER;
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public long fnv_hash_64(int start, int length, int[] p) {
    long h = FNV_START_HASH_64;
    for (int i = start; i < length; i++ ) {
        h = ( h ^ p[i] ) * _FNV_64_MULTIPLIER;
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public long fnv_hash_64(int start, int length, float[] p) {
    long h = FNV_START_HASH_64;
    for (int i = start; i < length; i++ ) {
        h = ( h ^ Float.floatToIntBits(p[i])) * _FNV_64_MULTIPLIER;
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public long fnv_hash_64(int start, int length, byte[] p) {
    long h = FNV_START_HASH_64;
    for (int i = start; i < length; i++ ) {
        h = ( h ^ p[i]) * _FNV_64_MULTIPLIER;
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public long fnv_hash_64(int start, int length, short[] p) {
    long h = FNV_START_HASH_64;
    for (int i = start; i < length; i++ ) {
        h = ( h ^ p[i]) * _FNV_64_MULTIPLIER;
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public long fnv_hash_64(int start, int length, char[] p) {
    long h = FNV_START_HASH_64;
    for (int i = start; i < length; i++ ) {
        h = ( h ^ p[i]) * _FNV_64_MULTIPLIER;
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public long fnv_hash_64(int start, int length, String s) {
    long h = FNV_START_HASH_64;
    for (int i = start; i < length; i++ ) {
        int c = s.charAt(i);
        h = ( h ^ c ) * _FNV_64_MULTIPLIER;
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public long fnv_hash_64(long   ...p) { return fnv_hash_64(0, p.length, p); }
static public long fnv_hash_64(double ...p) { return fnv_hash_64(0, p.length, p); }
static public long fnv_hash_64(int    ...p) { return fnv_hash_64(0, p.length, p); }
static public long fnv_hash_64(float  ...p) { return fnv_hash_64(0, p.length, p); }
static public long fnv_hash_64(byte   ...p) { return fnv_hash_64(0, p.length, p); }
static public long fnv_hash_64(short  ...p) { return fnv_hash_64(0, p.length, p); }
static public long fnv_hash_64(char   ...p) { return fnv_hash_64(0, p.length, p); }
static public long fnv_hash_64(String ...p) {
    long h = FNV_START_HASH_64;
    for (String s : p) {
        for (int i = 0; i < s.length(); i++) {
            h = fnv_hash_64_builder(h, s.charAt(i));
        }
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
//
// 32
//
static public int fnv_hash_32(int start, int length, long[] p) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(start, length, p), 32);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int fnv_hash_32(int start, int length, double[] p) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(start, length, p), 32);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int fnv_hash_32(int start, int length, int[] p) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(start, length, p), 32);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int fnv_hash_32(int start, int length, float[] p) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(start, length, p), 32);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int fnv_hash_32(int start, int length, byte[] p) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(start, length, p), 32);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int fnv_hash_32(int start, int length, short[] p) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(start, length, p), 32);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int fnv_hash_32(int start, int length, char[] p) {
    // Note(Doeke): hash to match String, therefor we don't reduce
    int h = FNV_START_HASH_32;
    for (int i = 0; i < length; i++) {
        h = fnv_hash_32_builder(h, p[i]);
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int fnv_hash_32(String s) {
    int h = FNV_START_HASH_32;
    for (int i = 0; i < s.length(); i++) {
        int c = s.charAt(i);
        h = ( h ^ c ) * _FNV_32_MULTIPLIER;
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int fnv_hash_32(long   ...p) { return fnv_hash_32(0, p.length, p); }
static public int fnv_hash_32(double ...p) { return fnv_hash_32(0, p.length, p); }
static public int fnv_hash_32(int    ...p) { return fnv_hash_32(0, p.length, p); }
static public int fnv_hash_32(float  ...p) { return fnv_hash_32(0, p.length, p); }
static public int fnv_hash_32(byte   ...p) { return fnv_hash_32(0, p.length, p); }
static public int fnv_hash_32(short  ...p) { return fnv_hash_32(0, p.length, p); }
static public int fnv_hash_32(char   ...p) { return fnv_hash_32(0, p.length, p); }
static public int fnv_hash_32(String ...p) { 
    
    int h = FNV_START_HASH_32;
    for (String s : p) {
        for (int i = 0; i < s.length(); i++) {
            h = fnv_hash_32_builder(h, s.charAt(i));
        }
    }
    return h;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}
/**
revision history:

    0.52  (2022-02-04) fnv_hash_32 return a int now instead of a long...
    0.51  (2022-02-04) allowed for varargs for easier use
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