/** SFJL_Octree_Example - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

import static sfjl.SFJL_K_D_Tree.*;
import static sfjl.SFJL_Math.*;

public class SFJL_K_D_Tree_Example extends PApplet {

public static void main(String[] args) {
    PApplet.main(SFJL_K_D_Tree_Example.class, args);
}

K_D_Tree<PVector> k_d_tree;

PShape ps_points;

float rot_y = 0;

float scale = 250;


@Override
public void settings() {     
    size(1200, 800, P3D);
    pixelDensity(2);
    
}


@Override
public void setup() {
    frameRate(60);
}

public void draw_setup() {

    Get_Value<PVector>[] d_get_value = new Get_Value[3];
    d_get_value[0] = v->v.x;
    d_get_value[1] = v->v.y;
    d_get_value[2] = v->v.z;

    k_d_tree = new K_D_Tree<>(3, d_get_value);

    ArrayList<PVector> points = create_points();
    
    add_all(k_d_tree, points);

    // PShape for points
    ps_points = createShape();

    ps_points.beginShape(POINTS);
    ps_points.stroke(255);
    for (PVector v : points) {
        ps_points.vertex(v.x, v.y, v.z);
    }
    ps_points.endShape();
}

ArrayList<PVector> create_points() {

    ArrayList<PVector> tree = new ArrayList<>();

    float scale = 7f;
    int steps = 120;
    for (int z = 0; z < steps; z++) {
        for (int y = 0; y < steps; y++) {
            for (int x = 0; x < steps; x++) {
                float nx = norm(x, 0, steps);
                float ny = norm(y, 0, steps);
                float nz = norm(z, 0, steps);
                float n = noise(nx * scale, ny * scale, nz * scale);
                if (n > 0.29f) continue;
                tree.add(new PVector(nx * 2 - 1, ny * 2 - 1, nz * 2 - 1));
            }
        }
    }
    return tree;
}

public void draw() {

    if (frameCount == 1) {
        draw_setup();
    }

    background(50);

    pushMatrix();
    translate(width/2, height/2, 100);
    
    rotateY(rot_y);

    pushMatrix();
    scale(scale);
    shape(ps_points);
    popMatrix();

    float x = sin(frameCount * 0.01f);
    float y = cos(frameCount * 0.01f);
    float z = sin((frameCount * 0.01f) + HALF_PI);


    PVector closest = get_closest(k_d_tree, x, y, z);

    fill(0xffff0000);
    noStroke();

    pushMatrix();
    translate(x * scale, y * scale, z * scale);
    sphere(10);
    popMatrix();

    stroke(255,0,0);
    line(x * scale, y * scale, z * scale, closest.x * scale, closest.y * scale, closest.z * scale);

    popMatrix();

    rot_y += 0.005f;
}

}
/**
revision history:

   0.50  (2023-01-03) first numbered version

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