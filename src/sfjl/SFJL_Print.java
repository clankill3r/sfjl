/** SFJL_Print - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_Print_Example.java

*/
package sfjl;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Arrays;

public class SFJL_Print {
     private SFJL_Print() {}
//           SFJL_Print


static public PrintStream out = System.out;


static public void print(byte what) {
    out.print(what);
    out.flush();
}

static public void print(boolean what) {
    out.print(what);
    out.flush();
}

static public void print(char what) {
    out.print(what);
    out.flush();
}

static public void print(int what) {
    out.print(what);
    out.flush();
}

static public void print(long what) {
    out.print(what);
    out.flush();
}

static public void print(float what) {
    out.print(what);
    out.flush();
}

static public void print(double what) {
    out.print(what);
    out.flush();
}

static public void print(String what) {
    out.print(what);
    out.flush();
}

static public void print(Object what) {
    if (what.getClass().isArray()) {
        print_array(what);
    }
    else {
        out.print(what);
    }
    out.flush();
}

static public void print(Object... variables) {
    for (Object o : variables) {
        out.print(o);
    }
    out.flush();
}

static public void println() {
    out.println();
}

static public void println(byte what) {
    out.println(what);
    out.flush();
}

static public void println(boolean what) {
    out.println(what);
    out.flush();
}

static public void println(char what) {
    out.println(what);
    out.flush();
}

static public void println(int what) {
    out.println(what);
    out.flush();
}

static public void println(long what) {
    out.println(what);
    out.flush();
}

static public void println(float what) {
    out.println(what);
    out.flush();
}

static public void println(double what) {
    out.println(what);
    out.flush();
}

static public void println(String what) {
    out.println(what);
    out.flush();
}

static public void println(Object what) {
    if (what == null) {
        out.println("null");    
        out.flush();
    }
    else if (what.getClass().isArray()) {
        println_array(what);
    }
    else {
        out.println(what);
        out.flush();
    }
}

// https://stackoverflow.com/questions/63131631/type-int-of-the-last-argument-to-method-printlnobject-doesnt-exactly-m
// Note(Doeke): problem still exists, but atleast we need 3 or more parameters now for
// this to happen, where after the first two parameters it has to be an array.
// I keep it like this to reduce the amount of problems being reported.
static public void println(Object a, Object b, Object... variables) {

    int count = variables.length+2;

    for (int j = 0; j < count; j++) {

        Object o = null;

        if (j == 0) {
            o = a;
        }
        else if (j == 1) {
            o = b;
        }
        else {
            o = variables[j-2];
        }
        
        if (o.getClass().isArray()) {
            println_array(o);
        }
        else {
            out.print(o);
            if (j != count-1) out.print(" ");
        }
    }
    out.println();
    out.flush();  
}

static public void println_array(Object what) {
    print_array(what);
    out.println();
    out.flush();
}

static public void print_array(Object what) {
    if (what == null) {
        out.print(what);
    }
    else {

        Class<?> clazz = what.getClass();

        if (clazz.isArray()) {
            if (clazz == byte[].class)
                out.print(Arrays.toString((byte[]) what));
            else if (clazz == short[].class)
                out.print(Arrays.toString((short[]) what));
            else if (clazz == int[].class)
                out.print(Arrays.toString((int[]) what));
            else if (clazz == long[].class)
                out.print(Arrays.toString((long[]) what));
            else if (clazz == char[].class)
                out.print(Arrays.toString((char[]) what));
            else if (clazz == float[].class)
                out.print(Arrays.toString((float[]) what));
            else if (clazz == double[].class)
                out.print(Arrays.toString((double[]) what));
            else if (clazz == boolean[].class)
                out.print(Arrays.toString((boolean[]) what));
            else {

                int array_length = Array.getLength(what);

                print("[");

                for (int i = 0; i < array_length; i++) {

                    Object sub_array = Array.get(what, i);
                    if (i > 0) print(" ");
                    print_array(sub_array);

                    if (i < array_length-1) {
                        println(",");
                    }
                }

                print("]");
            }

        }
        else {
            if (what instanceof String) {
                out.println("\""+what+"\"");
            }
            else {
                out.println(what);
            }
        }

    }
    out.flush();
}

}
/**
revision history:

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