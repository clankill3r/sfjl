/** SFJL_Blobscanner_Example - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;
import processing.core.*;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.Texture;
import sfjl.SFJL_Math.Vec2;
import static sfjl.SFJL_Blobscanner.*;
public class SFJL_Blobscanner_Example extends PApplet {
public static void main(String[] args) {
    PApplet.main(SFJL_Blobscanner_Example.class, args);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
Blobscanner_Settings blobscanner_context;
PImage img;
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void settings() {
    size(640, 640, P2D);
    noSmooth();
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void setup() {

    img = create_image_from_string( 
        "####################################################################",
        "####                                                               #",
        "################################################################   #",
        "###########      ################            ###################   #",
        "#   ####            ##########      #####    ######     ########   #",
        "#   #####          ##########    ###   ####    ###      ########   #",
        "#   #######       #######      ####   ###      #####    ########   #",
        "#   #########    ##########     #######          ###############   #",
        "#   #########################                   ################   #",
        "#   ####   ####################       ####      ################   #",
        "#   ####   ####################       ####      ################   #",
        "#   ####   ####################       ####      ################   #",
        "#   ####   ####################       ####      ################   #",
        "#   ####   ####################       ####      ################   #",
        "#   ####          #################            #################   #",
        "#   ####         ###################            ############ ###   #",
        "#   ############################################################   #",
        "#                                                                  #",
        "####################################################################"
    );
    gl_nearest_for_texture(this, img);

    blobscanner_context = new Blobscanner_Settings();
    blobscanner_context.border_handling = Border_Handling.REPLACE_BORDER_AND_RESTORE_BORDER;
    blobscanner_context.threshold = 128;
    blobscanner_context.threshold_checker = (clr, threshold)-> {return (clr & 0xff) > threshold;};
    blobscanner_context.y_increment = 1;
    blobscanner_context.border_color = color(0);
    blobscanner_context.contour_settings = Contour_Settings.ONLY_CORNERS;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public void draw() {

    float sx = (float)  width / img.width;
    float sy = (float)  height / img.height;

    image(img, 0, 0, width, height);

    img.loadPixels();

    find_blobs_vec2(blobscanner_context, img.pixels, img.width, img.height, (c)-> {
        
        Vec2 last = null;
        for (int i = 0; i < c.contour_length; i++) {
            Vec2 v = c.contour[i];
            fill(255,255,0,50);
            stroke(255,0,0);
            rect(v.x * sx, v.y * sy, sx, sy);
            noFill();
            line(v.x * sx, v.y * sy, (v.x+1) * sx, (v.y+1) * sy);
            line((v.x+1) * sx, v.y * sy, v.x * sx, (v.y+1) * sy);
            if (last != null) {
                stroke(0,255,0);
                line((v.x+.5f) * sx, (v.y+.5f) * sy, (last.x+.5f) * sx, (last.y+.5f) * sy);
            }
            else {
                fill(0,0,255);
                circle((v.x+.5f) * sx, (v.y+.5f) * sy, 10);
            }
            last = v;
        }

    });

    blobscanner_context.contour_settings = Contour_Settings.ALL_PIXELS;
    find_blobs_index(blobscanner_context, img.pixels, img.width, img.height, (c)-> {
        pushMatrix();
        translate(sx/2, sy/2);
        for (int i = 0; i < c.contour_length; i++) {
            int index = c.contour[i];
            int x = index % img.width;
            int y = (index-x) / img.width;
            fill(0xffff0000);
            ellipse(x * sx, y * sy, sx, sy);
        }
        popMatrix();

    });    
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
PImage create_image_from_string(String... strings) {

    int black = color(0);
    int white = color(255);

    int w = strings[0].length();
    int h = strings.length;

    PImage img = createImage(w, h, RGB);

    img.loadPixels();

    int i = 0;
    for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
            if (strings[y].charAt(x) == ' ') {
                img.pixels[i] = white;
            }
            else {
                img.pixels[i] = black;
            }
            i += 1;
        }
    }
    return img;
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