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

public class SFJL_Quad_Tree_Example_DBG extends PApplet {

public static void main(String[] args) {
    PApplet.main(SFJL_Quad_Tree_Example_DBG.class, args);
}

PImage img;

@Override
public void settings() {     
    size(1024, 512);
    // pixelDensity(2);
}


@Override
public void setup() {
    img = loadImage("../debug_points_map.png");
    img.loadPixels();
    println(img.width, img.height);
  
}

@Override
public void draw() {
    background(0);
    loadPixels();
    
    // int index = 0;
    // int bit_shift = 0;

    // for (int i = 0; i < pixels.length; i++) {
    //     int clr = img.pixels[index];
    //     int bit_mask = 1 << bit_shift;

    //     if ((clr & bit_mask) != 0) {
    //         pixels[i] = 0xffff0000; 
    //     }
    //     bit_shift++;
    //     if (bit_shift == 16) {
    //         bit_shift = 0;
    //         index++;
    //     }
    // }

    int bit = 0;
    for (int i = 0; i < pixels.length; i++) {
        int mod = i%16;
        int index = (i - mod) / 16;
        int clr = img.pixels[index];

        // int x = 

        int bit_shift = mod & 0xf;
        int bit_mask = 1 << bit_shift;

        if ((clr & bit_mask) != 0) {
            pixels[i] = 0xffff0000; 
        }

        // assert index < 2048;
        bit++;
    }
    println(bit);

    noLoop();

    updatePixels();
    // for (int y = 0; y < height; y++) {
    //     for (int x = 0; x < width; x++) {

    //     }
    // }
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}