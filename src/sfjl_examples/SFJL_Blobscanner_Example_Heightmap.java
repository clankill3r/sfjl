package sfjl_examples;
import processing.core.*;
import sfjl.SFJL_Math.Vec2;
import static sfjl.SFJL_Blobscanner.*;
public class SFJL_Blobscanner_Example_Heightmap extends PApplet {    
public static void main(String[] args) {
    PApplet.main(SFJL_Blobscanner_Example_Heightmap.class, args);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
PImage noise;
Blobscanner_Settings blobscanner_context;
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
    blobscanner_context.threshold_checker = (clr, threshold)-> {return (clr & 0xff) > threshold;};
    blobscanner_context.y_increment = 1;
    blobscanner_context.border_color = color(0);
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
    update_noise(noise, frameCount*4);

    background(0);

    pushMatrix();
    translate(width/2, height * 0.7f, -500);
    rotateX(map(0.14f, 0, 1, 0, TWO_PI));
    rotateZ(map(mouseX, 0, width, 0, TWO_PI));

    int levels = 255;
    float scale = 3;

    blobscanner_context.y_increment = 16;
    
    for (int l = 0; l <= levels; l++) {
        noFill();
        stroke(l, 50, 50);
        float z = map(l, 0, levels, 0, 250) * scale;
        blobscanner_context.border_handling = Border_Handling.REPLACE_BORDER;
        blobscanner_context.threshold = l;
        find_blobs_vec2(blobscanner_context, noise.pixels, noise.width, noise.height, (c)-> {
            beginShape();
            noFill();
            for (int i = 1; i < c.contour_length; i++) {
                Vec2 v = c.contour[i];
                if ((v.x == 1 || v.x == noise.width-2 || v.y == 1 || v.y == noise.height-2)) {
                    stroke(0);
                }
                else {
                    stroke(blobscanner_context.threshold, 50, 50);
                }
                v.x -= 250/2;
                v.y -= 250/2;
                v.x *= scale;
                v.y *= scale;
                vertex(v.x, v.y, z);
            }
            endShape();
        });
        // prevent replacing the border with another color 255 times each frame
        blobscanner_context.border_handling = Border_Handling.DONT_BORDER;

        
    }
    popMatrix();

    fill(255);
    textSize(20);
    text("fps: "+((int)frameRate), 10, 20);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}