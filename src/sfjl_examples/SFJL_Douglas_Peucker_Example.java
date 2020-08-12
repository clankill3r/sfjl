/** SFJL_Base64_Example - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;

import processing.core.PApplet;
import processing.core.PGraphics;
import static sfjl.SFJL_Douglas_Peucker.*;

public class SFJL_Douglas_Peucker_Example extends PApplet {

public static void main(String[] args) {
    PApplet.main(SFJL_Douglas_Peucker_Example.class, args);
}


// =========================================================
// ============== E X A M P L E   2 ========================


static public class Example_2 {
    public float[][] points_to_simplify;
    public Path_Buffer result;
    public boolean use_fast_mode;
}



static public void create_shape(float dist_between_points, float dist_between_spirals, int randomness, float box_size, float[][] result) {
    
    float min_x = MAX_FLOAT;
    float max_x = MIN_FLOAT;
    float min_y = MAX_FLOAT;
    float max_y = MIN_FLOAT;
    float min_z = MAX_FLOAT;
    float max_z = MIN_FLOAT;
    
    int N = result.length;
    
    float r = dist_between_points;
    float b = dist_between_spirals / (2 * PI);
    float phi = r / b;
    
    for (int i = 0; i < N; i++) {

        phi += dist_between_points / r;
        r = b * phi;
        
        float r2 = ((sin( ((float)i/(N*1.5f)/2) * TWO_PI) + 1) / 2) * r;
        
        float[] v = polar_to_cartesian(r2, phi);
        
        
        result[i][0] = v[0] + (rand(randomness) * 2) - randomness;
        result[i][1] = v[1] + (rand(randomness) * 2) - randomness;
        result[i][2] = r    + (rand(randomness) * 2) - randomness;
        
        if (result[i][0] < min_x) min_x = result[i][0];
        if (result[i][0] > max_x) max_x = result[i][0];
        if (result[i][1] < min_y) min_y = result[i][1];
        if (result[i][1] > max_y) max_y = result[i][1];
        if (result[i][2] < min_z) min_z = result[i][2];
        if (result[i][2] > max_z) max_z = result[i][2];
    }
    
    
    // float x_range = max_x-min_x;
    // float y_range = max_y-min_y;
    // float z_range = max_z-min_z;
    // float max_range = max(x_range, max(y_range, z_range));
    // float x_scale = x_range / max_range;
    // float y_scale = y_range / max_range;
    // float z_scale = z_range / max_range;
    
    float half_box_size = box_size/2f;
    
    for (int i = 0; i < N; i++) {
        
        // WRONG!
        //points[i][0] = norm(points[i][0], min_x, max_x) * 2 - 1 * x_scale;
        //points[i][1] = norm(points[i][1], min_y, max_y) * 2 - 1 * y_scale;
        //points[i][2] = norm(points[i][2], min_z, max_z) * 2 - 1 * z_scale;
        
        result[i][0] = map(result[i][0], min_x, max_x, -1, 1) * half_box_size;
        result[i][1] = map(result[i][1], min_y, max_y, -1, 1) * half_box_size;
        result[i][2] = map(result[i][2], min_z, max_z, -1, 1) * half_box_size;
    }
    
    
}


static public void setup_example_2(Example_2 e) {

    int MAX_POINTS = 25_000;
    float dist_between_points = 10;
    float dist_between_spirals = 20;
    int randomness = 20;
    float size = 1000;

    e.points_to_simplify = new float[MAX_POINTS][3];
    e.result = new Path_Buffer(MAX_POINTS, 3);
    create_shape(dist_between_points, dist_between_spirals, randomness, size, e.points_to_simplify);

}






static public void draw_example_2(PGraphics g, PApplet p, Example_2 e) {

    g.background(0);

    g.pushMatrix();

    g.beginCamera();
    g.camera();
    g.rotateX(-PI/6);
    g.endCamera();
    
    
    g.translate(g.width/2, g.height*0.7f, 0);
    g.rotateY(radians(90+45));
    
    // 
    // DOUGLAS PEUCKER
    //
    float threshold = map(p.mouseX, 0, p.width, 0, 100f);

    int start = p.millis();
    douglas_peucker_3d(e.points_to_simplify, e.points_to_simplify.length, threshold, e.use_fast_mode, e.result);
    int time = p.millis()-start;

    g.beginShape();
    g.strokeWeight(1);
    g.noFill();
    for (int i = 0; i < e.result.length; i++) {
        float[] xyz = e.result.points[i];
        g.stroke(255);
        g.vertex(xyz[0], xyz[1], xyz[2]);
    }
    g.endShape();


    g.popMatrix();

    float reduction = 100-((float)e.result.length / e.points_to_simplify.length) * 100;

    g.fill(255);
    g.textSize(30);
    g.textAlign(LEFT, TOP);
    g.text(String.join("\n",
        "threshold: "+nfc(threshold, 2),
        "original: "+e.points_to_simplify.length, 
        "douglas: "+e.result.length,
        "reduction: "+nfc(reduction, 2)+"%",
        "time: "+time+"ms",
        "fast mode [f]: "+e.use_fast_mode,
        "fps: "+nfc(p.frameRate, 1)
    ), 50, 50);


}


@Override
public void keyPressed() {
    if (key == 'f' || key == 'F') {
        ex2.use_fast_mode = !ex2.use_fast_mode;
    }
}




static public float[] polar_to_cartesian(float r, float phi) {
    return new float[] {
        r * cos(phi), 
        r * sin(phi)
    };
}

static public int rand_seed = 1;

static public int rand() {
    rand_seed ^= (rand_seed << 21);
    rand_seed ^= (rand_seed >>> 35);
    rand_seed ^= (rand_seed << 4);
    return rand_seed;
}


static public int rand(int max) {
    return abs(rand() % max);
}





// ===========================================================

Example_2 ex2;


@Override
public void settings() {
    size(1200, 800, P3D);
    pixelDensity(displayDensity());
}



@Override
public void setup() {
    ex2 = new Example_2();
    setup_example_2(ex2);
}



@Override
public void draw() {
    draw_example_2(g, this, ex2);
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