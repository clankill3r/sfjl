/** SFJL_Integral_Image_Example - v0.51
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

NOTES:
    The following functions are not used in this example:
        - rgb_normalized_bounds
        - integral_image_value_normalized_bounds
        - integral_image_value_width_height

*/
package sfjl_examples;
import processing.core.*;
import static sfjl.SFJL_Integral_Image.*;
public class SFJL_Intergral_Image_Example extends PApplet {
public static void main(String[] args) {
    PApplet.main(SFJL_Intergral_Image_Example.class, args);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

PGraphics pg;


Integral_Image[] integral_rgb;
Integral_Image integral_brightness;

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void settings() {
    size(640, 640, P3D);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void setup() {
    pg = create_some_graphics(640, 640);
    pg.loadPixels();
    // we have some built in functions for rgb
    integral_rgb = make_integral_image_from_rgb(pg.pixels, pg.width, pg.height);
    // here we show how to make a custom one just for brightness
    integral_brightness = new Integral_Image(pg.width, pg.height);
    // we have to extract the value(s), and store them in the array.
    // since we only pass one Integral Image, the array has a dimension of one.
    // If we would pass in an array of 3 integral images for example, as we
    // would with RGB, then the array for the values would have a size of 3.
    // One for red, one for green and one for blue.
    update_integral_image(integral_brightness, pg.pixels, (c, v)-> {v[0] = (int)brightness(c);});
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void draw() {
    background(0);

    image(pg, 0, 0);

    int size = 128;
    int h_size = size/2;

    int x1 = mouseX-h_size;
    int y1 = mouseY-h_size;
    int x2 = mouseX+h_size;
    int y2 = mouseY+h_size;
    
    x1 = constrain(x1, 0, width);
    y1 = constrain(y1, 0, height);
    x2 = constrain(x2, 0, width);
    y2 = constrain(y2, 0, height);

    fill(rgb(integral_rgb, x1, y1, x2, y2));
    rectMode(CENTER);
    noStroke();
    rect(mouseX, mouseY, size, size);
    
    float brightness = integral_image_value(integral_brightness, x1, y1, x2, y2);
    fill(255);
    text("brightness: "+(int)brightness, 10, 10);


}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
PGraphics create_some_graphics(int width, int height) {
    PGraphics pg = createGraphics(width, height, P3D);
    
    pg.loadPixels(); // work around processing loadPixel bug

    pg.beginDraw();
    pg.background(0);
    pg.pointLight(255, 0, 0, 0, pg.height/2, 500);
    pg.pointLight(0, 0, 255, pg.width, pg.height/2, 500);
    pg.pointLight(0, 255, 0, pg.width/2, 0, 500);
    pg.translate(pg.width/2, pg.height/2);
    pg.fill(255);
    pg.noStroke();
    pg.sphere(250);
    pg.endDraw();
    return pg;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}
/**
revision history:
   0.51  (2022-02-15) new functions
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