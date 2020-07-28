package sfjl;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Arrays;


public class SFJL_Print {
     private SFJL_Print() {}


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
        printArray(what);
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
        printlnArray(what);
    }
    else {
        out.println(what);
        out.flush();
    }
}

static public void println(Object... variables) {

    for (int i = 0; i < variables.length; i++) {
        Object o = variables[i];
        if (o.getClass().isArray()) {
            printlnArray(o);
        }
        else {
            out.print(o);
            if (i != variables.length-1) out.print(" ");
        }
    }
    out.println();
    out.flush();  
}

static public void printlnArray(Object what) {
    printArray(what);
    out.println();
    out.flush();
}

static public void printArray(Object what) {
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
                    printArray(sub_array);

                    if (i < array_length-1) {
                        println(",");
                    }
                }

                print("]");
            }

        }
        else {
            out.println(what);
        }

    }
    out.flush();
}


}