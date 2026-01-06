/** SFJL_Keystone_Example - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;

import processing.core.*;
import sfjl.SFJL_Keystone;
import sfjl.SFJL_Keystone.Keystone_Surface;

public class SFJL_Keystone_Example extends PApplet {    
public static void main(String[] args) {
    PApplet.main(SFJL_Keystone_Example.class, args);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

PImage img;

Keystone_Surface keystone_in_surface = new Keystone_Surface();
Keystone_Surface keystone_out_surface = new Keystone_Surface();

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void settings() {
    size(512, 512, P2D);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void setup() {
    SFJL_Keystone.init(this);
    img = loadImage("smile.png");
    surface.setResizable(true);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

public void draw() {
    background(0);

    textureWrap(REPEAT);

    int panel_width = 200;

    pushMatrix();
    translate(width - panel_width, 0);
    noStroke();
    fill(30);
    rect(0, 0, panel_width, height);

    int padding = 5;
    int img_width = panel_width - (padding*2);
    int img_height = (int) (img.height * (panel_width-(padding*2)) / img.width);
    image(img, padding, padding, img_width, img_height);
    SFJL_Keystone.render_frame(keystone_in_surface, g, padding, padding, img_width, img_height);

    popMatrix();
    
    SFJL_Keystone.render(keystone_in_surface, keystone_out_surface, g, img, padding, padding, (int) (width-panel_width * 1.2f), 300);

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}
/**
revision history:

    0.50  (2025-XX-XX) first numbered version

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