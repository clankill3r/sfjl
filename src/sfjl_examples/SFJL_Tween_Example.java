/** SFJL_Tween_Example - v0.51
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;

import processing.core.*;
import static sfjl.SFJL_Tween.*;

public class SFJL_Tween_Example extends PApplet {

public static void main(String[] args) {
    PApplet.main(SFJL_Tween_Example.class, args);
}

@Override
public void settings() {
    size(1080, 360+3, P2D);
}

@Override
public void setup() {
    surface.setResizable(true);
}


@Override
public void draw() {
    draw(g, 0, 0, width, height);
}

public void draw(PGraphics pg, int _x1, int _y1, int _x2, int _y2) {
    pg.pushMatrix();
    pg.translate(_x1, _y1);
    draw(pg, _x2-_x1, _y2-_y1);
    pg.popMatrix();
}


public void draw(PGraphics pg, int width, int height) {

    pg.fill(30);
    pg.noStroke();
    pg.rect(0, 0, width, height);

    int N_EASE_IN_OUT_TYPES = In_Out_Type.values().length;
    int N_EASE_TYPES = Ease_Type.values().length;

    for (int i = 0; i < N_EASE_IN_OUT_TYPES; i++) { // y-axis
        In_Out_Type in_out_type = In_Out_Type.values()[i];
        for (int j = 0; j < N_EASE_TYPES; j++) { // x-axis
            Ease_Type ease_type = Ease_Type.values()[j];
            
            var x1 = map(j, 0, N_EASE_TYPES, 0, width);
            var y1 = map(i, 0, N_EASE_IN_OUT_TYPES, 0, height);
            int w = round((float) width / N_EASE_TYPES);
            int h = round((float) height / N_EASE_IN_OUT_TYPES);

            int margin = 10;
            x1 += margin;
            y1 += margin;
            w -= margin * 2;
            h -= margin * 2;

            int n_points = w / 2;
            
            draw_graph(pg, x1, y1, x1+w, y1+h, n_points, in_out_type, ease_type);
            
            pg.fill(255);
            pg.textAlign(LEFT, TOP);
            pg.text(in_out_type+"\n"+ease_type, x1, y1);
        }
    }

}


void draw_graph(PGraphics pg, float plot_x1, float plot_y1, float plot_x2, float plot_y2, int n_points, In_Out_Type in_out_type, Ease_Type ease_type) {
    
    pg.pushMatrix();

    pg.fill(50);
    pg.rectMode(CORNERS);
    pg.noStroke();
    pg.rect(plot_x1, plot_y1, plot_x2, plot_y2);

    pg.noFill();
    pg.stroke(255);

    pg.beginShape();

    for (int x = 0; x < n_points; x++) {
        float t = (float) x / n_points;
        float mx = map(x, 0, n_points-1, plot_x1, plot_x2);
        float my = ease(in_out_type, ease_type, plot_y1, plot_y2, t);
        pg.vertex(mx, my);
    }
    pg.endShape(OPEN);

    pg.popMatrix();
}



}
/**
revision history:

    0.51  (2023-06-06) displaying a grid with all the options
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