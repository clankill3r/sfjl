/** SFJL_Blobscanner_Example_Heightmap_Mask - v0.5
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;
import processing.core.*;
import sfjl.SFJL_Blobscanner.Contour_Buffer;
import sfjl.SFJL_Math.Vec2;
import static sfjl.SFJL_Blobscanner.*;
public class SFJL_Blobscanner_Example_Heightmap_Mask extends PApplet {    
public static void main(String[] args) {
    PApplet.main(SFJL_Blobscanner_Example_Heightmap_Mask.class, args);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

PImage noise;
Blobscanner_Settings blobscanner_context;
PGraphics mask;

int white = color(255);

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void settings() {
    size(1024, 768, P3D);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void setup() {
    noiseSeed(0);
    noise = createImage(250, 250, RGB);
    noise.loadPixels();

    blobscanner_context = new Blobscanner_Settings();
    blobscanner_context.threshold = 128;
    blobscanner_context.threshold_checker = (clr, threshold)-> { return (clr & 0xff) > threshold;};
    blobscanner_context.y_increment = 1;
    blobscanner_context.border_color = color(0);
    blobscanner_context.contour_settings = Contour_Settings.ALL_PIXELS;

    mask = createGraphics(250, 250);
    mask.beginDraw();
    mask.background(255,255,0);
    mask.fill(255);
    mask.textAlign(LEFT, TOP);
    mask.textSize(170);
    mask.text("SF", -5, -50);
    mask.text("JL", 80, 75);
    mask.noStroke();
    mask.rectMode(CORNERS);
    mask.rect(250-8, 8, 250-8-64, 8+64);
    mask.rect(8, 250-8, 8+64, 250-8-64);
    mask.endDraw();
    mask.loadPixels();

    // 
    add_to_contour_vec2 = (Contour_Buffer<Vec2[]> contour_buffer, int index, int x, int y, Process_Contour<Vec2[]> process_contour) -> {
        if (mask.pixels[index] != white) {
            process_contour.exe(contour_buffer);
            reset_contour_buffer_vec2.exe(contour_buffer);
        }
        else {
            contour_buffer.contour[contour_buffer.contour_length].x = x;
            contour_buffer.contour[contour_buffer.contour_length].y = y;
            contour_buffer.contour_length += 1;
        }
    };
    
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
void update_noise(PImage img, int seed) {
    float x_offset = seed * 0.001f;
    float y_offset = seed * 0.0001f;
    float z_offset = seed * 0.0005f;
    float noise_scale = map(sin(radians(seed * 0.1f)), -1, 1, 0.009f, 0.001f);
    int i = 0;
    for (int y = 0; y < img.height; y++) {
        for (int x = 0; x < img.width; x++) {
            int c = (int) (noise(x_offset + x * noise_scale, y_offset + y * noise_scale, z_offset) * 255);
            noise.pixels[i] = 0xff << 24 | c << 16 | c << 8 | c;
            i++;
        }
    }
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void draw() {
    surface.setTitle(""+frameRate);

    update_noise(noise, frameCount*4);

    background(0);

    pushMatrix();
    translate(width/2, height * 0.5f, -500);

    int levels = 255;
    float scale = 3;

    blobscanner_context.y_increment = 16;
    
    for (int l = 0; l < levels; l++) {
        
        noFill();
        stroke(l, 50, 50);
        float z = map(l, 0, levels-1, 0, 250) * scale;
        blobscanner_context.border_handling = Border_Handling.REPLACE_BORDER;
        blobscanner_context.threshold = l;
        colorMode(HSB, 255, 1, 1);
        find_blobs_vec2(blobscanner_context, noise.pixels, noise.width, noise.height, (c)-> {
            beginShape();
            noFill();
            stroke(blobscanner_context.threshold, 1, 1);

            for (int i = 0; i < c.contour_length; i++) {
                Vec2 v = c.contour[i];
                v.x -= 250/2;
                v.y -= 250/2;
                v.x *= scale;
                v.y *= scale;
                vertex(v.x, v.y, z);

                
            }
            endShape();
        });
        
        blobscanner_context.border_handling = Border_Handling.DONT_BORDER;
    }

    popMatrix();

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