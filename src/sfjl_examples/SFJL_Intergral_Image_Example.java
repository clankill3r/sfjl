package sfjl_examples;

import processing.core.*;
import static sfjl.SFJL_Integral_Image.*;


public class SFJL_Intergral_Image_Example extends PApplet {

public static void main(String[] args) {
    PApplet.main(SFJL_Intergral_Image_Example.class, args);
}


PGraphics pg;

Integral_Image[] rgb;


@Override
public void settings() {
    size(512, 512, P3D);
}

@Override
public void setup() {
    pg = create_some_graphics();
    pg.loadPixels();
    rgb = make_integral_image_from_rgb(pg.pixels, pg.width, pg.height);
}


@Override
public void draw() {
    background(0);
    image(pg, 0, 0);

    int size = 150;
    int h_size = size/2;
    
    noStroke();
    fill(rgb_for_aabb(rgb, mouseX-h_size, mouseY-h_size, mouseX+h_size, mouseY+h_size));
    rectMode(CENTER);
    rect(mouseX, mouseY, size, size);
}



PGraphics create_some_graphics() {
    PGraphics pg = createGraphics(512, 512, P3D);
    
    pg.loadPixels(); // work around processing loadPixel bug

    pg.beginDraw();
    pg.background(0);
    pg.pointLight(255, 0, 0, 0, height/2, 500);
    pg.pointLight(0, 0, 255, width, height/2, 500);
    pg.pointLight(0, 255, 0, width/2, 0, 500);
    pg.translate(width/2, height/2);
    pg.fill(255);
    pg.noStroke();
    pg.sphere(300);
    pg.endDraw();
    return pg;
}


}