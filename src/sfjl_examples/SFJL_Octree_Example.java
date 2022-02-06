/** SFJL_Octree_Example - v0.50
 
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
import sfjl.SFJL_Octree;
import static sfjl.SFJL_Octree.*;

public class SFJL_Octree_Example extends PApplet {

public static void main(String[] args) {
    PApplet.main(SFJL_Octree_Example.class, args);
}

Octree<PVector> tree;
ArrayList<PVector> within = new ArrayList<>();

PShape model;

@Override
public void settings() {     
    size(1200, 800, P3D);
    pixelDensity(2);
    
}


@Override
public void setup() {

    frameRate(60);

    int dim = min(width, height) / 2;

    int plot_x1 = -dim;
    int plot_x2 = dim;
    int plot_y1 = -dim;
    int plot_y2 = dim;
    int plot_z1 = -dim;
    int plot_z2 = dim;

    // - 400 -180 - 75
    // 400 180 74

    //tree = new Octree<>((v)-> v.x, (v)->v.y, (v)->v.z, 96, plot_x1, plot_y1, plot_z1, plot_x2, plot_y2, plot_z2);
    tree = new Octree<>((v)-> v.x, (v)->v.y, (v)->v.z, 96, 
    -400, -180, -75, 400, 180, 75);

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
        add(tree, new PVector(x, y, z));
    }

    println("points: "+tree.size);

    model = createShape();

    model.beginShape(POINTS);
    model.stroke(255);
    for (PVector v : tree) {
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
boolean draw_Octree = false;

ArrayList<PVector> update_helper = new ArrayList<>();


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
    draw_Octree(tree);
  
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

    PVector v = get_closest(tree, x, y, z);
    pushMatrix();
    translate(v.x, v.y, v.z);
    // sphere(10);
    popMatrix();

    ArrayList<PVector> group = new ArrayList<>(100);

    int start = millis();
    get_closest_n(tree.root, x, y, z, 500, group);
    println(millis()-start);

    fill(0xff00ff00);
    ArrayList<PVector> foo = (ArrayList<PVector>) debug;
    for (PVector v2 : foo) {
        pushMatrix();
        translate(v2.x, v2.y, v2.z);
        box(2);
        popMatrix();
    }

    fill(0xffff0000);
    for (PVector v2 : group) {
        pushMatrix();
        translate(v2.x, v2.y, v2.z);
        box(2);
        popMatrix();
    }



    popMatrix();
    
}



PVector screenPointToWorld(PVector sp) {
    PGraphicsOpenGL _g = ((PGraphicsOpenGL)g);

    // cam.worldToCameraMatrix?? Not sure about this one
    PMatrix3D final_matrix = new PMatrix3D(_g.modelview);
    final_matrix.apply(_g.projection);
    final_matrix.invert();

    float[] in = new float[4];

    in[0] = sp.x;
    in[1] = sp.y;
    in[2] = sp.z; //_g.cameraNear; 
    in[3] = 1.0f;  

    /* Map x and y from window coordinates */
    // in[0] = (in[0] - viewport[0]) / viewport[2];
    // in[1] = (in[1] - viewport[1]) / viewport[3];
    in[0] = in[0] / width;
    in[1] = in[1] / height;

    /* Map to range -1 to 1 */
    in[0] = in[0] * 2 - 1;
    in[1] = in[1] * 2 - 1;
    in[2] = in[2] * 2 - 1;

    float[] out = new float[4];

    final_matrix.mult(in, out);

    out[0] /= out[3];
    out[1] /= out[3];
    out[2] /= out[3];
    
    return new PVector(out[X], out[Y], out[Z]);
}



PVector screenPointToWorld_2(PVector sp) {
    PGraphicsOpenGL _g = ((PGraphicsOpenGL)g);

    // cam.worldToCameraMatrix?? Not sure about this one
    PMatrix3D worldToCameraMatrix = new PMatrix3D(_g.camera);
    worldToCameraMatrix.invert();

    PMatrix3D world2Screen = new PMatrix3D(_g.projection);
    world2Screen.apply(worldToCameraMatrix);
    
    PMatrix3D screen2World = new PMatrix3D(world2Screen);
    screen2World.invert();

    float[] inn = new float[4];

    inn[0] = 2.0f * (sp.x / width) - 1.0f;
    inn[1] = 2.0f * (sp.y / height) - 1.0f;
    inn[2] = _g.cameraNear;
    inn[3] = 1.0f;  

    float[] pos = new float[4];
    screen2World.mult(inn, pos);

    int X = 0;
    int Y = 1;
    int Z = 2;
    int W = 3;

    pos[W] = 1.0f / pos[W];

    pos[X] *= pos[W];
    pos[Y] *= pos[W];
    pos[Z] *= pos[W];

    return new PVector(pos[X], pos[Y], pos[Z]);
}




public void draw_xx() {
    background(0);

    translate(width/2, height/2, -100);

    pushMatrix();

    float rot_y = map(mouseX, 0, width, 0, TWO_PI);
    rotateY(rot_y);

    int dim = min(width, height) / 2;
    noFill();
    stroke(255);
    box(dim);

    PMatrix3D mat3 = (PMatrix3D) getMatrix();
    mat3.invert();

    popMatrix();

    PVector mouse = new PVector(mouseX, mouseY, 0);
    mat3.mult(mouse, mouse);

    // now using the same transformations, the mouse should appear at the mouse position
    pushMatrix();
    rotateY(rot_y);
    translate(mouse.x, mouse.y, mouse.z);
    box(3);
    popMatrix();

    


    // pushMatrix();
    // rotateY(rot_y);
    // translate(0, 0, dim);
    // noStroke();
    // fill(255,255,0);
    // box(5);

    // float x = modelX(0, 0, dim);
    // float y = modelY(0, 0, dim);
    // float z = modelZ(0, 0, dim);
    // popMatrix();

    // println(x, y, z);


}


public void old_draw() {

    surface.setTitle("fps: "+(int)frameRate);

    background(0);

    noFill();
    stroke(255);
    strokeWeight(0.5f);
    if (draw_Octree) draw_Octree(tree);
    
    within.clear();

    float radius = sin(frameCount * 0.01f) * 250;

    if (sin(frameCount * 0.01f) > 0) {
        // get_within_radius_sq(tree, mouseX, mouseY, sq(radius), within);
        stroke(255,0,0);
        strokeWeight(1f);
        noFill();
        ellipse(mouseX, mouseY, radius*2, radius*2);  
    }
    else {
        // get_within_aabb(tree, mouseX-radius, mouseY-radius, mouseX+radius, mouseY+radius, within);
        stroke(255,0,0);
        strokeWeight(1f);
        rectMode(CORNERS);
        noFill();
        rect(mouseX-radius, mouseY-radius, mouseX+radius, mouseY+radius);
    }

    strokeCap(SQUARE);
    stroke(255,0,0);
    strokeWeight(1f);
    for (PVector v2 : within) {
        if (draw_points) point(v2.x, v2.y);
        if (move_points) {
            v2.x += fast_random() * abs(v2.z) * abs(v2.z);
            v2.y += fast_random() * abs(v2.z) * abs(v2.z);
        }
        v2.z = -abs(v2.z); // we set z to a negative value to indicate that we have drawn it
    }

    ArrayList<PVector> out_of_bounds = new ArrayList<>();
    update(tree, update_helper, out_of_bounds);
    
    strokeCap(SQUARE);
    stroke(255,255,0);
    strokeWeight(1f);
    for (PVector v : tree) {
        if (v.z >= 0) {
            if (draw_points) point(v.x, v.y);
        }
        else {
            v.z = -v.z; // reset for next frame
        }
    }

    PVector v = get_closest(tree, mouseX, mouseY, 0);
    if (v != null) {
        stroke(255,0,0);
        line(mouseX, mouseY, v.x, v.y);
        remove(tree, v);
    }
    
    if (size(tree) >= 2) {
        stroke(255,0,0);
        strokeWeight(1f);
        noFill();
        rectMode(CORNERS);
        rect(min_x(tree).x, min_y(tree).y, max_x(tree).x, max_y(tree).y);
    }

    fill(255);
    text(size(tree), 20, 20);
    text("highest_depth_with_leafs: "+highest_depth_with_leafs(tree), 20, 80);
    text("lowest_depth_with_leafs: "+lowest_depth_with_leafs(tree), 20, 100);
}


public <T> int size(Octree<T> qt) {
    return SFJL_Octree.size(qt.root);
}


public <T> int size(Octree_Node<T> qt) {
    return SFJL_Octree.size(qt);
}


public <T> void draw_Octree(Octree<T> tree) {
    int lowest_depth_with_leafs = lowest_depth_with_leafs(tree);
    int highest_depth_with_leafs = highest_depth_with_leafs(tree);
    draw_Octree(tree.root, 0, lowest_depth_with_leafs, highest_depth_with_leafs);
}


public void draw_Octree(Octree_Node<?> tree, int level, int lowest_depth_with_leafs, int highest_depth_with_leafs) {
    if (has_children(tree)) {
        draw_Octree(tree.children[0], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_Octree(tree.children[1], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_Octree(tree.children[2], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_Octree(tree.children[3], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_Octree(tree.children[4], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_Octree(tree.children[5], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_Octree(tree.children[6], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_Octree(tree.children[7], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
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
        SFJL_Octree.clear(tree);
    }
    if (key == 'm') {
        move_points = !move_points;
    }
    if (key == 'q') {
        draw_Octree = !draw_Octree;
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
        add(tree, new PVector(mouseX + random(-50, 50), mouseY + random(-50, 50), 1));
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

   0.50  (2022-01-08) first numbered version

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