/** SFJL_Blobscanner_Example - v0.5
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;
import processing.core.*;
import processing.opengl.*;
import sfjl.SFJL_Math.Vec2;
import static sfjl.SFJL_Blobscanner.*;

import java.util.HashMap;
public class SFJL_Blobscanner_Example extends PApplet {    
public static void main(String[] args) {
    PApplet.main(SFJL_Blobscanner_Example.class, args);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
PImage input;
Blobscanner_Settings blobscanner_context;



static class Blur_Pass {
    static PGraphics pg_blurred;
    static PShader s_blur;
    static float u_blur_size = 15;
    static float u_sigma = 5;
}

static class Color_Distance_Pass {
    static PGraphics pg_color_distance;
    static PShader s_color_dist;
    static int target_color = 0xFFFFF580;
}

static class Threshold_Pass {
    static PGraphics pg_threshold;
    static PShader s_threshold;
    static float u_threshold = 0.10f;
}

enum Show_Pass {
    INPUT,
    BLUR,
    COLOR_DISTANCE,
    THRESHOLD,
    BLOBS;

    private static final Show_Pass[] values = values();
    
    public Show_Pass next() {
        int next = this.ordinal() + 1;
        if (next == values.length) next -= 1; 
        return values[next];
    }

    public Show_Pass previous() {
        int previous = this.ordinal() - 1;
        if (previous == -1) previous = 0;
        return values[previous];
    }
}
Show_Pass show_pass = Show_Pass.BLOBS;

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void settings() {
    size(1024, 768, P2D);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void setup() {

    blobscanner_context = new Blobscanner_Settings();
    blobscanner_context.threshold = 128;
    blobscanner_context.threshold_checker = (clr, threshold)-> {return (clr & 0xff) > threshold;};
    blobscanner_context.y_increment = 16;
    blobscanner_context.border_color = color(0);
    blobscanner_context.border_handling = Border_Handling.REPLACE_BORDER;

    input = loadImage("sketch_01.png");

    Blur_Pass.s_blur = loadShader("shaders/blur_frag.glsl", "shaders/blur_vert.glsl");
    Blur_Pass.pg_blurred = createGraphics(width, height, P2D);
    
    Color_Distance_Pass.s_color_dist = loadShader("shaders/color_distance_frag.glsl", "shaders/color_distance_vert.glsl");
    Color_Distance_Pass.pg_color_distance = createGraphics(width, height, P2D);

    Threshold_Pass.s_threshold = loadShader("shaders/threshold_frag.glsl", "shaders/threshold_vert.glsl");
    Threshold_Pass.pg_threshold = createGraphics(width, height, P2D);

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void draw() {

    if (keyPressed && key == 't') {
        Threshold_Pass.u_threshold = norm(mouseX, 0, width);
    }

    PImage img = input;

    img = blur(img, Blur_Pass.u_blur_size, Blur_Pass.u_sigma, Blur_Pass.pg_blurred);
    img = color_distance(img, Color_Distance_Pass.target_color, Color_Distance_Pass.pg_color_distance);
    img = threshold(img, Threshold_Pass.u_threshold, Threshold_Pass.pg_threshold);


    switch (show_pass) {
        case INPUT:
            image(input, 0, 0);
            break;
        case BLUR:
            image(Blur_Pass.pg_blurred, 0, 0);
            if (mousePressed) {
                Blur_Pass.u_sigma = map(mouseX, 0, width, 2, 5);
                Blur_Pass.u_blur_size = map(mouseY, 0, height, 0, 50);
            }
            break;
        case COLOR_DISTANCE:
            if (mousePressed) {
                image(Blur_Pass.pg_blurred, 0, 0);
                loadPixels();
                Color_Distance_Pass.target_color = get(mouseX, mouseY);
            }
            else {
                image(Color_Distance_Pass.pg_color_distance, 0, 0);
            }
            break;
        case THRESHOLD:
            image(Threshold_Pass.pg_threshold, 0, 0);
            if (mousePressed) {
                Threshold_Pass.u_threshold = norm(mouseX, 0, width);
            }
            break;
        case BLOBS:
            image(Threshold_Pass.pg_threshold, 0, 0);
            img.loadPixels();

            find_blobs_vec2(blobscanner_context, img.pixels, img.width, img.height, (c)-> {
                beginShape();
                noFill();
                for (int i = 1; i < c.contour_length; i++) {
                    Vec2 v = c.contour[i];
                    stroke(0xffff0000);
                    vertex(v.x, v.y);
                }
                endShape();

                PVector blob_center = blob_center(c);
                stroke(0,255,0);
                noFill();
                ellipse(blob_center.x, blob_center.y, 16, 16);
            });
            break;
    }
    
    surface.setTitle(show_pass+"   fps: "+(int)frameRate);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
PVector blob_center(Contour_Buffer<Vec2[]> c) {

    float min_x = MAX_FLOAT;
    float min_y = MAX_FLOAT;
    float max_x = MIN_FLOAT;
    float max_y = MIN_FLOAT;

    PVector result = new PVector();

    for (int i = 1; i < c.contour_length; i++) {
        Vec2 v = c.contour[i];
        if (v.x < min_x) min_x = v.x;
        if (v.y < min_y) min_y = v.y;
        if (v.x > max_x) max_x = v.x;
        if (v.y > max_y) max_y = v.y;
    }
    result.x = min_x + (max_x - min_x) / 2;
    result.y = min_y + (max_y - min_y) / 2;

    return result;
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

HashMap<String, PGraphics> blurr_pass1 = new HashMap<>();

PGraphics blur(PImage img, float blur_size, float sigma, PGraphics out) {
    String target = ""+out.width+""+out.height;
    PGraphics pass1 = blurr_pass1.get(target);
    if (pass1 == null) {
        pass1 = createGraphics(out.width, out.height, P2D);
        blurr_pass1.put(target, pass1);
    }

    Blur_Pass.s_blur.set("blurSize", blur_size);
    Blur_Pass.s_blur.set("sigma", sigma);

    // pass 1
    Blur_Pass.s_blur.set("horizontalPass", 0);
    pass1.beginDraw();
    pass1.shader(Blur_Pass.s_blur);
    pass1.image(img, 0, 0);
    pass1.endDraw();
    // pass 2
    Blur_Pass.s_blur.set("horizontalPass", 1);
    out.beginDraw();
    out.shader(Blur_Pass.s_blur);
    out.image(pass1, 0, 0);
    out.endDraw();

    return out;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
PGraphics color_distance(PImage img, int clr, PGraphics out) {
    Color_Distance_Pass.s_color_dist.set("target_color", red(clr) / 255f, green(clr) / 255f, blue(clr) / 255f, 1f);

    out.beginDraw();
    out.shader(Color_Distance_Pass.s_color_dist);
    out.image(img, 0, 0);
    out.endDraw();

    return out;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
PGraphics threshold(PImage img, float threshold, PGraphics out) {
    Threshold_Pass.s_threshold.set("midPoint", threshold);

    out.beginDraw();
    out.shader(Threshold_Pass.s_threshold);
    out.image(img, 0, 0);
    out.endDraw();

    return out;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void keyPressed() {
    if (key == '-') {
        show_pass = show_pass.previous();
    }
    if (key == '=') {
        show_pass = show_pass.next();
    }
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
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