/** SFJL_Quad_Tree_Example - v0.51
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;
import processing.opengl.Texture;
import sfjl.SFJL_Quad_Tree;
import static sfjl.SFJL_Quad_Tree.*;
public class SFJL_Quad_Tree_Example extends PApplet {
public static void main(String[] args) {
    PApplet.main(SFJL_Quad_Tree_Example.class, args);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

Quad_Tree<PVector> tree;
ArrayList<PVector> within = new ArrayList<>();

PShader many_points_shader;
PImage points_map;

boolean draw_quad_tree = false;
ArrayList<PVector> update_helper = new ArrayList<>();

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void settings() {     
    size(1024, 768, P2D);
    pixelDensity(2);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void setup() {

    frameRate(999);

    int plot_x1 = 50;
    int plot_x2 = width - 50;
    int plot_y1 = 50;
    int plot_y2 = height - 50;
    int plot_width = plot_x2 - plot_x1;
    int plot_height = plot_y2 - plot_y1;

    tree = new Quad_Tree<>((v)-> v.x, (v)->v.y, 128, plot_x1, plot_y1, plot_x2, plot_y2);

    for (int y = plot_y1; y <  plot_y2; y += 1) {
        for (int x = plot_x1; x < plot_x2; x += 1) {

            float n = dist(x, y, width/2, height/2) / max(plot_width, plot_height);
            
            if (n < 0.5f) {
                if (noise(x * 0.025f, y * 0.025f) < 0.6) {
                    add(tree, new PVector(x, y));
                }
            }
        }
    }
    println("points: "+tree.size);

    many_points_shader = loadShader("shaders/quadtree_example_many_points.glsl");
    points_map = createImage(pow_2_ceil(width/16), pow_2_ceil(height), ARGB);
    points_map.loadPixels();
    gl_nearest_for_texture(this, points_map);

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
int pow_2_ceil(int n) {
    if ((n & (n - 1)) == 0) return n; // already pow 2
    int v = n;
    int r = 0;
    while ((v >>= 1) != 0) {
        r++;
    }
    return 1 << (r+1);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void draw() {

    surface.setTitle("fps: "+(int)frameRate);

    background(0);

    shader(many_points_shader);
    many_points_shader.set("iResolution", (float) width, (float) height);
    many_points_shader.set("points_map", points_map);
    rectMode(CORNER);
    noStroke();
    fill(0);
    rect(0, 0, width, height);
    resetShader();

    noFill();
    stroke(255);
    strokeWeight(0.5f);
    if (draw_quad_tree) draw_quad_tree(tree);

    within.clear();

    float radius = sin(frameCount * 0.01f) * 250;

    if (sin(frameCount * 0.01f) > 0) {
        get_within_radius_sq(tree, mouseX, mouseY, sq(radius), within);
        stroke(255,0,0);
        strokeWeight(1f);
        noFill();
        ellipse(mouseX, mouseY, radius*2, radius*2);  
    }
    else {
        get_within_aabb(tree, mouseX-radius, mouseY-radius, mouseX+radius, mouseY+radius, within);
        stroke(255,0,0);
        strokeWeight(1f);
        rectMode(CORNERS);
        noFill();
        rect(mouseX-radius, mouseY-radius, mouseX+radius, mouseY+radius);
    }


    // ArrayList<PVector> out_of_bounds = new ArrayList<>();
    // update(tree, update_helper, out_of_bounds);

    PVector v = get_closest(tree, mouseX, mouseY);
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

    //
    // update points_map
    //

    // reset points_map
    for (int i = 0; i < points_map.pixels.length; i++) {
        points_map.pixels[i] = 0;
    }
    // update points_map
    for (PVector v2 : tree) {
        int x = (int) v2.x;
        int y = (int) v2.y;
        int index_here = (y * width + x);
        int index_in_map = index_here / 16;
        int bit = 1 << (x & 0xF);
        points_map.pixels[index_in_map] |= bit;
    }
    // set colors 
    for (PVector v2 : within) {
        int x = (int) v2.x;
        int y = (int) v2.y;
        int index_here = (y * width + x);
        int index_in_map = index_here / 16;
        int color_bit = (1 << (x & 0xF) << 16);
        points_map.pixels[index_in_map] |= color_bit;
    }
    points_map.updatePixels();
    
    fill(255);
    text(size(tree), 20, 20);
    text("highest_depth_with_leafs: "+highest_depth_with_leafs(tree), 20, 80);
    text("lowest_depth_with_leafs: "+lowest_depth_with_leafs(tree), 20, 100);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public <T> int size(Quad_Tree<T> qt) {
    return SFJL_Quad_Tree.size(qt.root);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public <T> int size(Quad_Tree_Node<T> qt) {
    return SFJL_Quad_Tree.size(qt);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public <T> void draw_quad_tree(Quad_Tree<T> tree) {
    rectMode(CORNERS);
    int lowest_depth_with_leafs = lowest_depth_with_leafs(tree);
    int highest_depth_with_leafs = highest_depth_with_leafs(tree);
    draw_quad_tree(tree.root, 0, lowest_depth_with_leafs, highest_depth_with_leafs);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void draw_quad_tree(Quad_Tree_Node<?> tree, int level, int lowest_depth_with_leafs, int highest_depth_with_leafs) {
    if (has_children(tree)) {
        draw_quad_tree(tree.children[0], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_quad_tree(tree.children[1], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_quad_tree(tree.children[2], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        draw_quad_tree(tree.children[3], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
    }
    else {
        // fill(map(level, lowest_depth_with_leafs, highest_depth_with_leafs, 0, 50));
        stroke(255);
        noFill();
        rect(tree.x1, tree.y1, tree.x2, tree.y2);
    }
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void keyPressed() {
    if (key == 'c') {
        SFJL_Quad_Tree.clear(tree);
    }
    if (key == 'q') {
        draw_quad_tree = !draw_quad_tree;
    }
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void mouseDragged() {
    for (int i = 0; i < 10; i++) {
        add(tree, new PVector(mouseX + random(-50, 50), mouseY + random(-50, 50), 1));
    }
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public void gl_nearest_for_texture(PApplet p, PImage image) {
    PGL pgl = p.beginPGL();
    Texture image_tex = ((PGraphicsOpenGL)p.g).getTexture(image);
    pgl.bindTexture(PGL.TEXTURE_2D, image_tex.glName);
    pgl.texParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_MIN_FILTER, PGL.NEAREST);
    pgl.texParameteri(PGL.TEXTURE_2D, PGL.TEXTURE_MAG_FILTER, PGL.NEAREST);
    pgl.bindTexture(PGL.TEXTURE_2D, 0);
    p.endPGL();
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}
/**
revision history:

   0.51  (2022-02-05) drawing the points with a shader
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