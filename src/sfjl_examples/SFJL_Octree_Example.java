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
import sfjl.SFJL_Octree;
import sfjl.SFJL_Quad_Tree;

import static sfjl.SFJL_Octree.*;

public class SFJL_Octree_Example extends PApplet {

public static void main(String[] args) {
    PApplet.main(SFJL_Octree_Example.class, args);
}

Octree<PVector> tree;
ArrayList<PVector> within = new ArrayList<>();

PShape ps_points;
PShape ps_boxes;

float scale = 250;


ArrayList<PVector> buffer = new ArrayList<>();


float rot_y = 0;

int N = 15_000;



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

    tree = new Octree<>((v)-> v.x, (v)->v.y, (v)->v.z, 128, 
    -1, -1, -1, 1, 1, 1);

    create_tree();
    println("points: "+tree.size);


    // PShape for points
    ps_points = createShape();

    ps_points.beginShape(POINTS);
    ps_points.stroke(255);
    for (PVector v : tree) {
        ps_points.vertex(v.x, v.y, v.z);
    }
    ps_points.endShape();

    // PShape for boxes
    ps_boxes = createShape();

    ps_boxes.beginShape(QUADS);
    ps_boxes.noFill();
    ps_boxes.stroke(0);

    var itr = SFJL_Octree.get_iterator(tree.root, Iterator_Type.DEPTH_FIRST);
    while (itr.hasNext()) {
        var cell = itr.next();
        
        float x1 = cell.x1;
        float y1 = cell.y1;
        float z1 = cell.z1;

        float x2 = cell.x2;
        float y2 = cell.y2;
        float z2 = cell.z2;

        // front
        ps_boxes.vertex(x1, y1, z1);
        ps_boxes.vertex(x2, y1, z1);
        ps_boxes.vertex(x2, y2, z1);
        ps_boxes.vertex(x1, y2, z1);

        // right
        ps_boxes.vertex(x2, y1, z1);
        ps_boxes.vertex(x2, y1, z2);
        ps_boxes.vertex(x2, y2, z2);
        ps_boxes.vertex(x2, y2, z1);

        // back
        ps_boxes.vertex(x2, y1, z2);
        ps_boxes.vertex(x1, y1, z2);
        ps_boxes.vertex(x1, y2, z2);
        ps_boxes.vertex(x2, y2, z2);

        // left
        ps_boxes.vertex(x1, y1, z2);
        ps_boxes.vertex(x1, y1, z1);
        ps_boxes.vertex(x1, y2, z1);
        ps_boxes.vertex(x1, y2, z2);

        // top
        ps_boxes.vertex(x1, y1, z2);
        ps_boxes.vertex(x2, y1, z2);
        ps_boxes.vertex(x2, y1, z1);
        ps_boxes.vertex(x1, y1, z1);

        // bottom
        ps_boxes.vertex(x1, y2, z1);
        ps_boxes.vertex(x2, y2, z1);
        ps_boxes.vertex(x2, y2, z2);
        ps_boxes.vertex(x1, y2, z2);

    }
    
    ps_boxes.endShape();
}


void create_tree() {
    float scale = 7f;
    int steps = 120;
    for (int z = 0; z < steps; z++) {
        for (int y = 0; y < steps; y++) {
            for (int x = 0; x < steps; x++) {
                float nx = norm(x, 0, steps);
                float ny = norm(y, 0, steps);
                float nz = norm(z, 0, steps);
                float n = noise(nx * scale, ny * scale, nz * scale);
                if (n > 0.31f) continue;
                add(tree, new PVector(nx * 2 - 1, ny * 2 - 1, nz * 2 - 1));
            }
        }
    }
}



public void draw() {

    if (frameCount == 1) {
        draw_setup();
    }

    if (keyPressed) {
        if (key == '-') rot_y -= radians(1.5f);
        if (key == '=') rot_y += radians(1.5f);
        if (key == '[') N -= 100;
        if (key == ']') N += 100;
        if (N < 0) N = 0;
    }
    background(50);

    pushMatrix();
    translate(width/2, height/2, 100);
    
    rotateY(rot_y);

    noFill();
    stroke(0);

    pushMatrix();
    scale(scale);
    shape(ps_points);
    shape(ps_boxes);
    popMatrix();

    float x = map(mouseX, 0, width, -1, 1);
    float y = map(mouseY, 0, height, -1, 1);
    float z = 0.15f;

    pushMatrix();
    translate(x * scale, y * scale, z * scale);
    fill(0xffff0000);
    noStroke();
    sphere(10);
    popMatrix();


    ArrayList<PVector> group = new ArrayList<>(N);

    int start = millis();
    get_closest_n(tree.root, x, y, z, N, group, buffer);
    int time =  millis()-start;

    fill(0xffff0000);
    stroke(0xffff0000);
    for (PVector v2 : group) {
        point(v2.x * scale, v2.y * scale, v2.z * scale);
    }

    popMatrix();

    surface.setTitle("fps: "+(int)frameRate+" x: "+x+" y: "+y+" group.size(): "+group.size()+" time: "+time);
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