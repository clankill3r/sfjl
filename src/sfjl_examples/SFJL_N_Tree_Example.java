/** SFJL_N_Tree_Example - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import sfjl.SFJL_N_Tree;
import static sfjl.SFJL_N_Tree.*;
             
public class SFJL_N_Tree_Example extends PApplet {

public static void main(String[] args) {
    PApplet.main(SFJL_N_Tree_Example.class, args);
}

static public class Vec4 {
    public float x;
    public float y;
    public float z;
    public float w;
    public Vec4(){}
    public Vec4(float x, float y, float z, float w) {this.x = x; this.y = y; this.z = z;this.w = w;}
}

N_Tree<Vec4> tree;
ArrayList<Vec4> within = new ArrayList<>();

PShape model;

@Override
public void settings() {     
    size(1200, 800, P3D);
    pixelDensity(2);
    
}


@Override
public void setup() {

    frameRate(60);

    // - 400 -180 - 75
    // 400 180 74

    tree = new N_Tree<>((v)-> v.x, (v)->v.y, (v)->v.z, (v)->v.w, 96, 
    -400, -180, -75, 0,
    400, 180, 75, 1);

    /*
    for (int y = plot_y1; y <  plot_y2; y += 20) {
        for (int x = plot_x1; x < plot_x2; x += 20) {
            for (int z = plot_z1; z < plot_z2; z += 20) {

                float _x = random(-dim, dim);
                float _y = random(-dim, dim);
                float _z = random(-dim, dim);
                if (dist(0, 0, 0, _x, _y, _z) < dim) {
                    add(tree, new PVector(_x, _y, _z));
                }
            }
        }
    }
    */
    String[] lines = loadStrings("/Users/dw/github/clankill3r/orb/data/trex2_vertexes.tsv");
    for (int i = 0; i < lines.length; i++) {
        String[] tokens = split(lines[i], '\t');
        float x = Float.parseFloat(tokens[0]);
        float y = Float.parseFloat(tokens[1]);
        float z = Float.parseFloat(tokens[2]);
        add(tree, new Vec4(x, y, z, 0));
        add(tree, new Vec4(-x, y, z, 1));
    }

    println("points: "+tree.size);

    model = createShape();

    model.beginShape(POINTS);
    model.stroke(255);
    for (Vec4 v : tree) {
        model.vertex(v.x, v.y, v.z);
    }
    model.endShape();
    // println(min_x(tree), min_y(tree), min_z(tree));
    // println(max_x(tree), max_y(tree), max_z(tree));
    

    for (int i = 0; i < precomputed_random_sequence.length; i++) {
        precomputed_random_sequence[i] = random(-1, 1);
    }

}


boolean move_points = false;
boolean draw_points = false;
boolean draw_N_Tree = false;

ArrayList<Vec4> update_helper = new ArrayList<>();


float rot_y = 0;
float zoom = 200;



public void draw() {
    background(50);

    pushMatrix();
    translate(width/2, height/2, zoom);
    
     //map(mouseX, 0, width, 0, TWO_PI);
    rotateY(rot_y);

    noFill();
    stroke(0);
    draw_N_Tree(tree);
  
    shape(model);
    
    // rotateY(-rot_y);
    // translate(0, 0, 100 * 1.41f);
    // // fill(255,0,0);
    // // box(5);

    float x = map(mouseX, 0, width, -400, 400);
    float y = map(mouseY, 0, height, -180, 180);
    float z = 90;

    pushMatrix();
    translate(x, y, z);
    fill(0xffff0000);
    noStroke();
    sphere(10);
    popMatrix();

    Vec4 v = get_closest(tree, x, y, z, 0); // WTF
    pushMatrix();
    translate(v.x, v.y, v.z);
    // sphere(10);
    popMatrix();

    ArrayList<Vec4> group = new ArrayList<>(100);

    int start = millis();
    float w = 0; //WTF
    get_closest_n(tree.root, x, y, z, w, 500, group);
    println(millis()-start);

    fill(0xff00ff00);
    ArrayList<Vec4> foo = (ArrayList<Vec4>) debug;
    for (Vec4 v2 : foo) {
        pushMatrix();
        translate(v2.x, v2.y, v2.z);
        box(2);
        popMatrix();
    }

    fill(0xffff0000);
    for (Vec4 v2 : group) {
        pushMatrix();
        translate(v2.x, v2.y, v2.z);
        box(2);
        popMatrix();
    }



    popMatrix();
    
}



public <T> int size(N_Tree<T> qt) {
    return SFJL_N_Tree.size(qt.root);
}


public <T> int size(N_Tree_Node<T> qt) {
    return SFJL_N_Tree.size(qt);
}


public <T> void draw_N_Tree(N_Tree<T> tree) {
    int lowest_depth_with_leafs = lowest_depth_with_leafs(tree);
    int highest_depth_with_leafs = highest_depth_with_leafs(tree);
    draw_N_Tree(tree.root, 0, lowest_depth_with_leafs, highest_depth_with_leafs);
}


public void draw_N_Tree(N_Tree_Node<?> tree, int level, int lowest_depth_with_leafs, int highest_depth_with_leafs) {
    if (has_children(tree)) {
        draw_N_Tree(tree.children[0], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_N_Tree(tree.children[1], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_N_Tree(tree.children[2], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_N_Tree(tree.children[3], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_N_Tree(tree.children[4], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_N_Tree(tree.children[5], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_N_Tree(tree.children[6], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_N_Tree(tree.children[7], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
    }
    else {
        //fill(map(level, lowest_depth_with_leafs, highest_depth_with_leafs, 0, 50));
        // rect(tree.x1, tree.y1, tree.x2, tree.y2);
        pushMatrix();
        translate(center_x(tree), center_y(tree), center_z(tree));
        box(tree.x2-tree.x1, tree.y2-tree.y1, tree.z2-tree.z1);
        popMatrix();
    }
}


@Override
public void keyPressed() {
    if (key == 'c') {
        SFJL_N_Tree.clear(tree);
    }
    if (key == 'm') {
        move_points = !move_points;
    }
    if (key == 'q') {
        draw_N_Tree = !draw_N_Tree;
    }
    if (key == 'p') {
        draw_points = !draw_points;
    }
    if (key == '-') rot_y -= radians(15);
    if (key == '=') rot_y += radians(15);
    if (key == ',') zoom -= 25;
    if (key == '.') zoom += 25;
}

@Override
public void mouseDragged() {
    for (int i = 0; i < 10; i++) {
        // add(tree, new PVector(mouseX + random(-50, 50), mouseY + random(-50, 50), 1));
    }
}


float[] precomputed_random_sequence = new float[2048];
int precomputed_random_sequence_index = 0;

float fast_random() {
    float r = precomputed_random_sequence[precomputed_random_sequence_index++];
    if (precomputed_random_sequence_index == precomputed_random_sequence.length) {
        precomputed_random_sequence_index = 0;
    }
    return r;
}


}
/**
revision history:

   0.50  (2022-01-10) first numbered version

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