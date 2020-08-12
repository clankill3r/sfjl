/** SFJL_Tween_Example - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;

import processing.core.PApplet;
import processing.core.PVector;
import static sfjl.SFJL_Tween.*;

public class SFJL_Tween_Example extends PApplet {


Ease_In_Out_Type[] ease_in_out_types = {
    Ease_In_Out_Type.EASE_IN,
    Ease_In_Out_Type.EASE_OUT,
    Ease_In_Out_Type.EASE_IN_OUT
};

int ease_in_out_type_index = 0;


Ease_Type[] ease_types = {
    Ease_Type.SINE,
    Ease_Type.LINEAR,
    Ease_Type.BOUNCE,
    Ease_Type.CIRC,
    Ease_Type.CUBIC,
    Ease_Type.EXPO,
    Ease_Type.QUAD,
    Ease_Type.QUART,
    Ease_Type.QUINT
};

int ease_type_index = 0;


PVector pos = new PVector();
PVector target_pos = new PVector();

int start_time_for_tween;
float duration = 1000;



public static void main(String[] args) {
    PApplet.main(SFJL_Tween_Example.class, args);
}

@Override
public void settings() {
    size(800, 600);
}

@Override
public void setup() {
    
}

@Override
public void draw() {
    background(0);

    Ease_In_Out_Type in_out_type = ease_in_out_types[ease_in_out_type_index];
    Ease_Type ease_type = ease_types[ease_type_index];

    float time = min(duration, millis()-start_time_for_tween);

    float x = ease(in_out_type, ease_type, time, pos.x, target_pos.x, duration);
    float y = ease(in_out_type, ease_type, time, pos.y, target_pos.y, duration);

    if (time == duration) {
        pos.x = x;
        pos.y = y;
    }
    
    fill(255,255,0);
    ellipse(x, y, 125, 125);

    if (mousePressed) {
        pos.x = x;
        pos.y = y;
        target_pos.x = mouseX;
        target_pos.y = mouseY;
        start_time_for_tween = millis();
    }

    fill(255);
    textSize(40);
    text(""+in_out_type+"\n"+ease_type, 50, 50);
}

@Override
public void keyPressed() {
    
    if (key == 'q') {
        ease_in_out_type_index--;
        if (ease_in_out_type_index < 0) {
            ease_in_out_type_index = ease_in_out_types.length;
        }
    }
    if (key == 'w') {
        ease_in_out_type_index++;
        if (ease_in_out_type_index == ease_in_out_types.length) {
            ease_in_out_type_index = 0;
        }
    }
    if (key == 'a') {
        ease_type_index--;
        if (ease_type_index < 0) {
            ease_type_index = ease_types.length;
        }
    }
    if (key == 's') {
        ease_type_index++;
        if (ease_type_index == ease_types.length) {
            ease_type_index = 0;
        }
    }
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