/** SFJL_Integral_Image - v0.52
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_Integral_Image_Example.java

*/
package sfjl;
import static java.lang.Math.*;
public class SFJL_Integral_Image {
     private SFJL_Integral_Image() {}
//---------- SFJL_Integral_Image
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Integral_Image {
    public int[] pixels; // (width+1) * (height+1)
    public int width;  // actual width is +1
    public int height; // actual height is +1
    public Integral_Image(int width, int height) {this.pixels = new int[(width+1) * (height+1)]; this.width = width; this.height = height;}
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public interface Extract_Value {
    void exe(int color, int[] extracted);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public Integral_Image[] make_integral_image_from_rgb(int[] pixels, int width, int height) {
    Integral_Image[] integral_images = new Integral_Image[3];
    integral_images[0] = new Integral_Image(width, height);
    integral_images[1] = new Integral_Image(width, height);
    integral_images[2] = new Integral_Image(width, height);

    update_integral_image(integral_images, pixels, (c, extracted)-> {
        extracted[0] = (c >> 16) & 0xff;
        extracted[1] = (c >> 8) & 0xff;
        extracted[2] = c & 0xff;
    });

    return integral_images;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public void update_integral_image(Integral_Image integral_layer, int[] pixels, Extract_Value extract_value) {

    int width  = integral_layer.width;
    int height = integral_layer.height;

    int[] extracted = new int[1];
    int total_this_row;

    for (int y = 0; y < height; y++) {
        total_this_row = 0;

        for (int x = 0; x < width; x++) {
            int color_index = y * width + x;
            int color = pixels[color_index];
            int store_index = (y+1) * (width+1) + (x+1);
            extract_value.exe(color, extracted);
            total_this_row += extracted[0];
            integral_layer.pixels[store_index] = total_this_row + integral_layer.pixels[store_index-(width+1)];
        }
    }
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public void update_integral_image(Integral_Image[] integral_layers, int[] pixels, Extract_Value extract_value) {
    int width  = integral_layers[0].width;
    int height = integral_layers[0].height;

    if (integral_layers.length == 1) {
        update_integral_image(integral_layers[0], pixels, extract_value);
    }
    else if (integral_layers.length == 2) {
        int[] extracted = new int[2];
        int[] total_this_row = new int[2];

        for (int y = 0; y < height; y++) {
            total_this_row[0] = 0;
            total_this_row[1] = 0;

            for (int x = 0; x < width; x++) {
                int color_index = y * width + x;
                int color = pixels[color_index];
                int store_index = (y+1) * (width+1) + (x+1);
                extract_value.exe(color, extracted);
                total_this_row[0] += extracted[0];
                total_this_row[1] += extracted[1];
                integral_layers[0].pixels[store_index] = total_this_row[0] + integral_layers[0].pixels[store_index-(width+1)];
                integral_layers[1].pixels[store_index] = total_this_row[1] + integral_layers[1].pixels[store_index-(width+1)];
            }
        }
    }
    else if (integral_layers.length == 3) {
        int[] extracted = new int[3];
        int[] total_this_row = new int[3];

        for (int y = 0; y < height; y++) {
            total_this_row[0] = 0;
            total_this_row[1] = 0;
            total_this_row[2] = 0;

            for (int x = 0; x < width; x++) {
                int color_index = y * width + x;
                int color = pixels[color_index];
                int store_index = (y+1) * (width+1) + (x+1);
                extract_value.exe(color, extracted);
                total_this_row[0] += extracted[0];
                total_this_row[1] += extracted[1];
                total_this_row[2] += extracted[2];
                integral_layers[0].pixels[store_index] = total_this_row[0] + integral_layers[0].pixels[store_index-(width+1)];
                integral_layers[1].pixels[store_index] = total_this_row[1] + integral_layers[1].pixels[store_index-(width+1)];
                integral_layers[2].pixels[store_index] = total_this_row[2] + integral_layers[2].pixels[store_index-(width+1)];
            }
        }
    }
    else if (integral_layers.length >= 4) {
        int n = integral_layers.length;
        int[] extracted = new int[n];
        int[] total_this_row = new int[n];

        for (int y = 0; y < height; y++) {

            for (int i = 0; i < n; i++) {
                total_this_row[i] = 0;
            }

            for (int x = 0; x < width; x++) {
                int color_index = y * width + x;
                int color = pixels[color_index];
                int store_index = (y+1) * (width+1) + (x+1);
                extract_value.exe(color, extracted);

                for (int i = 0; i < n; i++) {
                    total_this_row[i] += extracted[i];
                    integral_layers[i].pixels[store_index] = total_this_row[i] + integral_layers[i].pixels[store_index-(width+1)];
                }
            }
        }
    }
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int rgb_normalized_bounds(Integral_Image[] layers_rgb, float _x1, float _y1, float _x2, float _y2) {
    int x1 = round(layers_rgb[0].width * _x1);
    int x2 = round(layers_rgb[0].width * _x2);
    int y1 = round(layers_rgb[0].height * _y1);
    int y2 = round(layers_rgb[0].height * _y2);
    int r = (int) integral_image_value(layers_rgb[0], x1, y1, x2, y2);
    int g = (int) integral_image_value(layers_rgb[1], x1, y1, x2, y2);
    int b = (int) integral_image_value(layers_rgb[2], x1, y1, x2, y2);
    return 0xff000000 | r << 16 | g << 8 | b;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public int rgb(Integral_Image[] layers_rgb, int x1, int y1, int x2, int y2) {
    int r = (int) integral_image_value(layers_rgb[0], x1, y1, x2, y2);
    int g = (int) integral_image_value(layers_rgb[1], x1, y1, x2, y2);
    int b = (int) integral_image_value(layers_rgb[2], x1, y1, x2, y2);
    return 0xff000000 | r << 16 | g << 8 | b;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public float integral_image_value_normalized_bounds(Integral_Image integral, float _x1, float _y1, float _x2, float _y2) {
    int x1 = round(integral.width * _x1);
    int x2 = round(integral.width * _x2);
    int y1 = round(integral.height * _y1);
    int y2 = round(integral.height * _y2);
    return integral_image_value(integral, x1, y1, x2, y2);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public float integral_image_value_width_height(Integral_Image img, int _x1, int _y1, int width, int height) {
    int _x2 = _x1 + width;
    int _y2 = _y1 + height;
    return integral_image_value(img, _x1, _y1, _x2, _y2);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public float integral_image_value(Integral_Image img, int _x1, int _y1, int _x2, int _y2) {
    int x1 = min(_x1, _x2);
    int y1 = min(_y1, _y2);
    int x2 = max(_x1, _x2);
    int y2 = max(_y1, _y2);
    
    int n_of_pixels = (x2 - x1) * (y2 - y1);

    int total_count = img.pixels[y2 * (img.width+1) + x2] +
    img.pixels[y1 * (img.width+1) + x1] -
    img.pixels[y2 * (img.width+1) + x1] -
    img.pixels[y1 * (img.width+1) + x2];
    
    return (float) total_count / n_of_pixels;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}
/**
revision history:
    0.52  (2022-02-12) - return a float now instead of an int
                       - actual width and height is now +1 to eliminate certain if statements
                       - constrain now has to be done by the user
                       - **_normalized_bounds functions
                       - Extract_Value for custom integral images (e.g. one for brightness)
    0.51  (2020-08-18) - fixed bug where values where slightly off
                       - out of bounds gives edge value now instead of crash
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