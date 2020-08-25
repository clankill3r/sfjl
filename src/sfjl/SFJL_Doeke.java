/** SFJL_Doeke - v0.52
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl;

import java.net.URL;
import java.util.ArrayList;
import static java.lang.Math.*;
import static sfjl.SFJL_Math.*;

public class SFJL_Doeke {
     private SFJL_Doeke() {}
//           SFJL_Doeke


static public <T> T remove_last(ArrayList<T> arr) {
    if (arr.size() == 0)
        return null;
    return arr.remove(arr.size() - 1);
}


static public <T> T swap_remove(ArrayList<T> list, int index_to_remove) {
    list.set(index_to_remove, list.get(list.size() - 1));
    return list.remove(list.size() - 1);
}


static public <T> T swap_remove(ArrayList<T> list, T object_to_remove) {
    int index_to_remove = list.indexOf(object_to_remove);
    if (index_to_remove == -1)
        return null;
    list.set(index_to_remove, list.get(list.size() - 1));
    return list.remove(list.size() - 1);
}


/*
/bananas.txt    --- start at root of jar 
../bananas.txt  --- go up one dir from package folder
bananas.txt     --- load relative from where the class file is located
*/
static public URL get_url_to_jar_resource_file(String file, Class<?> clazz) {

    String absolute_path = null;

    char first_char = file.charAt(0);
    
    if (first_char == '/') { // "/bananas.txt" --- absolute path fram jar root
        
        absolute_path = file;
    }
    else if (first_char != '.') { // "bananas.txt" --- relative from class location
        
        absolute_path = "/"+clazz.getPackageName().replace(".", "/")+"/"+file;
    }
    else { // "../bananas" --- first go back N dirs
        String package_name = clazz.getPackageName().replace(".", "/");

        int pos = 0;
        int split_end_index = package_name.length();

        while (file.charAt(pos+0) == '.' &&
               file.charAt(pos+1) == '.' &&
               file.charAt(pos+2) == '/') 
        {
            pos += 3;
            split_end_index = package_name.lastIndexOf("/", split_end_index);
        }
        absolute_path = "/"+package_name.substring(0, split_end_index)+"/"+file.substring(pos);
    }

    URL url = clazz.getResource(absolute_path);
    return url;
}


//-----------------------------------------------------------------------------
//--------- C O L O R ---------------------------------------------------------
//-----------------------------------------------------------------------------

static float _mod(float x, float y) {
    return x - y * (float)floor(x/y);
}

// range 0-1
static public int hsb_to_rgb(float hue, float saturation, float brightness) {

    float r = constrain(abs(_mod(hue*6f,      6f)-3f)-1f, 0, 1);
    float g = constrain(abs(_mod(hue*6f + 4f, 6f)-3f)-1f, 0, 1);
    float b = constrain(abs(_mod(hue*6f + 2f, 6f)-3f)-1f, 0, 1);

    r = r*r*(3f-2f*r);
    g = g*g*(3f-2f*g);
    b = b*b*(3f-2f*b);

    r = brightness * lerp(1f, r, saturation);
    g = brightness * lerp(1f, g, saturation);
    b = brightness * lerp(1f, b, saturation);

    return 0xff000000 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
}





} 
/**
revision history:
    0.52  (2020-08-25) hsb_to_rgb
    0.51  (2020-08-17) get_url_to_jar_resource_file
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
