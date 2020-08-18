/** SFJL_Integral_Image - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_Integral_Image_Example.java

*/
package sfjl;

public class SFJL_Integral_Image {
     private SFJL_Integral_Image() {}
//---------- SFJL_Integral_Image


static public class Integral_Image {
    public int[] values;
    public int width;
    public int height;
    public Integral_Image(int width, int height) {this.values = new int[width * height]; this.width = width; this.height = height;}
}


static public Integral_Image[] make_integral_image_from_rgb(int[] pixels, int width, int height) {

    Integral_Image[] integral_images = new Integral_Image[3];
    integral_images[0] = new Integral_Image(width, height);
    integral_images[1] = new Integral_Image(width, height);
    integral_images[2] = new Integral_Image(width, height);

    int[] values_r = integral_images[0].values;
    int[] values_g = integral_images[1].values;
    int[] values_b = integral_images[2].values;

    // first row
    values_r[0] = (pixels[0] >> 16) & 255;
    values_g[0] = (pixels[1] >> 8)  & 255;
    values_b[0] = (pixels[2] >> 0)  & 255;
    for (int x = 1; x < width; x++) {
        values_r[x] = values_r[x-1] + ((pixels[x] >> 16) & 255);
        values_g[x] = values_g[x-1] + ((pixels[x] >> 8)  & 255);
        values_b[x] = values_b[x-1] + ((pixels[x] >> 0)  & 255);
    }
    
    // 2nd row till end   
    for (int y = 1; y < height; y++) {
        
        int index = y*width;
        int total_r = 0;
        int total_g = 0;
        int total_b = 0;
        values_r[index] = values_r[index-width] + ((pixels[index] >> 16) & 255);
        values_g[index] = values_g[index-width] + ((pixels[index] >> 8)  & 255);
        values_b[index] = values_b[index-width] + ((pixels[index] >> 0)  & 255);

        for (int x = 1; x < width; x++) {
            index = y * width + x;
            total_r += ((pixels[index] >> 16) & 255);
            total_g += ((pixels[index] >> 8)  & 255);
            total_b += ((pixels[index] >> 0)  & 255);
            values_r[index] = total_r + values_r[index-width];
            values_g[index] = total_g + values_g[index-width];
            values_b[index] = total_b + values_b[index-width];
        }
    }
    return integral_images;
}


static public int rgb_for_aabb(Integral_Image[] layers_rgb, int x1, int y1, int x2, int y2) {
    int r = integral_image_value(layers_rgb[0], x1, y1, x2, y2);
    int g = integral_image_value(layers_rgb[1], x1, y1, x2, y2);
    int b = integral_image_value(layers_rgb[2], x1, y1, x2, y2);
    return 0xff000000 | r << 16 | g << 8 | b;
}


static public int integral_image_value(Integral_Image img, int x1, int y1, int x2, int y2) {

    int min_x = Math.max(Math.min(x1, x2), 0);
    int min_y = Math.max(Math.min(y1, y2), 0);
    int max_x = Math.min(Math.max(x1, x2), img.width-1);
    int max_y = Math.min(Math.max(y1, y2), img.height-1);
    
    int n_of_pixels = (max_x - min_x) * (max_y - min_y);
    
    // TODO NaN or specified error color
    // we can base it on clamp, repeat or stretch
    if (n_of_pixels == 0) return -1; 
    
    int w = img.width;
    
    int total_count = 0; // total count
    int i;
    
    i = max_y * w + max_x; // right bottom
    total_count += img.values[i];
    i = min_y * w + min_x; // left top
    total_count += img.values[i];
    i = max_y * w + min_x; // left bottom
    total_count -= img.values[i];
    i = min_y * w + max_x; // right top
    total_count -= img.values[i];
    
    return total_count / n_of_pixels;
}


}
/**
revision history:

   0.50  (2020-08-18) first numbered version

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