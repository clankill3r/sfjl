/** SFJL_Tween_Example - v0.52
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;

import processing.core.*;
import sfjl.SFJL_Tween.Ease_Type;
import sfjl.SFJL_Tween.In_Out_Type;

import static sfjl.SFJL_Tween.*;

public class SFJL_Tween_Example extends PApplet {

public static void main(String[] args) {
    PApplet.main(SFJL_Tween_Example.class, args);
}

int N_EASE_IN_OUT_TYPES = In_Out_Type.values().length;
int N_EASE_TYPES = Ease_Type.values().length;
int MARGIN = 2;
int HEADER_HEIGHT = 20;


@Override
public void settings() {
    size(512, 192, P2D);
}

@Override
public void setup() {
    surface.setResizable(true);
}


@Override
public void draw() {

    background(0);

    float plot_x1 = 50;
    float plot_x2 = width;

    float plot_y1 = HEADER_HEIGHT;
    float plot_y2 = height;

    float plot_width = (plot_x2-plot_x1);
    float plot_height = (plot_y2-plot_y1);

    float graph_width = round((float) plot_width / N_EASE_TYPES);
    float graph_height = round((float) plot_height / N_EASE_IN_OUT_TYPES);

    float x1 = plot_x1;
    float y1 = plot_y1;

    for (Ease_Type ease_type : Ease_Type.values()) { // x-axis
        for (In_Out_Type in_out_type : In_Out_Type.values()) { // y-axis
            draw_graph(g, x1 + MARGIN, y1 + MARGIN, x1+graph_width - MARGIN, y1+graph_height - MARGIN, (int)(graph_width/2), in_out_type, ease_type);
            y1 += graph_height;
        }
        x1 += graph_width;
        y1 = plot_y1;
    }

    // draw labels on the top
    x1 = plot_x1;
    for (Ease_Type ease_type : Ease_Type.values()) { // x-axis
        fill(255);
        textAlign(LEFT, TOP);
        text(""+ease_type, x1 + MARGIN, 2);
        x1 += graph_width;
    }

    // draw labels on the side
    y1 = plot_y1;
    for (In_Out_Type in_out_type : In_Out_Type.values()) { // y-axis
        fill(255);
        textAlign(RIGHT, TOP);
        text(""+in_out_type, plot_x1 - MARGIN, y1 + MARGIN);
        y1 += graph_height;
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

    0.52  (2023-08-11) simplified example
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