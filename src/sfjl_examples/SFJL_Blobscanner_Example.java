package sfjl_examples;

import processing.core.*;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.Texture;
import sfjl.SFJL_Math.Vec2;

import static sfjl.SFJL_Blobscanner.*;
import static sfjl.SFJL_Doeke.*;

public class SFJL_Blobscanner_Example extends PApplet {
    
public static void main(String[] args) {
    PApplet.main(SFJL_Blobscanner_Example.class, args);
}

PImage img;

Blobscanner_Settings blobscanner_context;

@Override
public void settings() {
    size(640, 640);
    noSmooth();
}

@Override
public void setup() {

    int which = 2;

    if (which == 1) {
        img = create_image_from_string( 
            "########",
            "###  ###",
            "##    ##",
            "#     ##",
            "##   ###",
            "###  ###",
            "#    ###",
            "########"
        );
    }
    else if (which == 2){
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
    }
    else if (which == 3) {
        img = create_image_from_string( 
            "####################################################################",
            "####                                                               #",
            "################################################################   #",
            "################################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#   ############################################################   #",
            "#                                                                  #",
            "####################################################################"
        );
    }
    else {
        img = create_image_from_string( 
            "###########",
            "####      #",
            "#######   #",
            "#   ###   #",
            "#   ###   #",
            "#         #",
            "###########"
        );
    }

    println(img.width, img.height);
    
    // gl_nearest_for_texture(this, img);

    blobscanner_context = new Blobscanner_Settings();
    blobscanner_context.border_handling = Border_Handling.REPLACE_BORDER_AND_RESTORE_BORDER;
    blobscanner_context.threshold = 128;
    blobscanner_context.threshold_checker = (clr, threshold)-> {return (clr & 0xff) > threshold;};
    blobscanner_context.y_increment = 1;
    blobscanner_context.border_color = color(0);
    blobscanner_context.contour_settings = Contour_Settings.ONLY_CORNERS;
}


String dbg;

int draw_index = 0;

@Override
public void draw() {

    surface.setTitle(""+frameRate);

    dbg = "";

    float sx = (float)  width / img.width;
    float sy = (float)  height / img.height;

    image(img, 0, 0, width, height);

    img.loadPixels();

    draw_index = 0;

    // for (int j = 0; j < 1_000; j++) {
    find_blobs_vec2(blobscanner_context, img.pixels, img.width, img.height, (c)-> {
        // // beginShape();
        // // noFill();
        // // stroke(255,0,0);
        // // for (int i = 0; i < c.contour_length; i++) {
        // //     Vec2 v = c.contour[i];
        // //     vertex(v.x * sx, v.y * sy);
        // // }
        // // endShape();

        // // dbg += c.contour_length + "\n";
        
        // // return true;

        
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

        dbg += c.contour_length + "\n";

        // draw_index++;
        // // if (draw_index == 9) return false;
        
        return true;
    });
    // }
    
    
    fill(255);
    text(dbg, 25, 25);
    // make_roi_in_pixels(blobscanner_context.roi, img.width, img.height);

    // prepare_border(img.pixels, img.width, img.height, blobscanner_context.roi, color(255,0,0), /* do_backup_border = */true, blobscanner_context.border_backup);


    
    // img.updatePixels();
    
    // // restore_border_from_backup(img.pixels, img.width, img.height, blobscanner_context.roi, blobscanner_context.border_backup);
    
    // img.updatePixels();

    // noLoop();
}



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


/*


####################################################################
#                                                                  #
#   ############################################################   #
#   #######      ################            ###################   #
#   ####            ##########      #####    ######     ########   #
#   #####          ##########    ###   ####    ###      ########   #
#   #######       #######      ####   ###      #####    ########   #
#   #########    ##########     #######          ###############   #
#   #########################                   ################   #
#   ####   ####################       ####      ################   #
#   ####          #################            #################   #
#   ############################################################   #
#                                                                  #
####################################################################

*/