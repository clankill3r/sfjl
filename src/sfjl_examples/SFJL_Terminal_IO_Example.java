/** SFJL_Terminal_IO_Example - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;

import processing.core.PApplet;
import sfjl.SFJL_Terminal_IO;

public class SFJL_Terminal_IO_Example extends PApplet {
    
    
public static void main(String[] args) {
    PApplet.main(SFJL_Terminal_IO_Example.class, args);
}


String sequence = " .`-_':,;^=+/\"|)\\<>)iv%xclrs{*}I?!][1taeo7zjLunT#JCwfy325Fp6mqSghVd4EgXPGZbYkOA&8U$@KHDBWNMR0Q";
char[] brightness_to_char_lookup = new char[256];

int BOX = 0;
int SPHERE = 1;

int shape = SPHERE;


@Override
public void settings() {
    size(600, 600, P3D);
}


@Override
public void setup() {

    // populate brightness_to_char_lookup[]
    float m = (float) sequence.length() / brightness_to_char_lookup.length;
    for (int i = 0; i < brightness_to_char_lookup.length; i++) {
        brightness_to_char_lookup[i] = sequence.charAt((int)Math.floor(i*m));
    }

    SFJL_Terminal_IO.store_terminal_settings();

    Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
            SFJL_Terminal_IO.restore_terminal_settings();
        }
    });

    SFJL_Terminal_IO.put_into_character_mode();
    SFJL_Terminal_IO.disable_echo();
    SFJL_Terminal_IO.hide_cursor();
    SFJL_Terminal_IO.clear();
}



@Override
public void draw() {

    pushMatrix();

    // read_input_key needs some work, a lot of keys are not implemented yet
    // the terminal has to have focus!
    int key;
    while ((key = SFJL_Terminal_IO.read_input_key()) != 0) {
        if (key == 's') {
            shape++;
            if (shape == 2) shape = BOX;
        }
    }

    int cols = SFJL_Terminal_IO.cols();
    int rows = SFJL_Terminal_IO.rows();

    background(0);
    pointLight(255, 0, 0, 0, height/2, 500);
    pointLight(0, 0, 255, width, height/2, 500);
    pointLight(0, 255, 0, width/2, 0, 500);
    translate(width/2, height/2);
    rotateY(0.01f * frameCount);
    rotateX(0.05f * frameCount);
    rotateZ(0.07f * frameCount);
    fill(255);
    if (shape == BOX) {
        box(900);
    }
    else {
        sphere(150);
        box(500, 20, 20);
        box(20, 500, 20);
        box(20, 20, 500);
    }

    loadPixels();

    SFJL_Terminal_IO.turn_off_all_attributes();

    for (int ty = 0; ty < rows; ty++) {
        float py = map(ty, 0, rows, 0, height);
        for (int tx = 0; tx < cols; tx++) {
            float px = map(tx, 0, cols, 0, width);
            int c = get((int)px, (int)py);
            int brightness = (int)brightness(c);
            char the_char = brightness_to_char_lookup[brightness];
            String color_str = SFJL_Terminal_IO.ansi_fill_color(c); 
            SFJL_Terminal_IO.set_cursor(tx, ty);
            SFJL_Terminal_IO.printf(color_str+the_char);
        }
    }

    popMatrix();
    noLights();

    background(0);
    fill(255);
    textSize(20);
    textAlign(LEFT, TOP);
    text(String.join("\n",
    "fps: "+(int)frameRate,
    "",
    "When using VSCode, make sure to set \"console\":",
    "to either \"integratedTerminal\" or \"externalTerminal\"",
    "",
    "Press [s] to change the shape, make sure the terminal\nhas focus."),
    20,
    20);

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