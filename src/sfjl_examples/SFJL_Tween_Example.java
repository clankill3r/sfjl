package sfjl_examples;

import processing.core.PApplet;
import processing.core.PVector;

import static sfjl.SFJL_Tween.*;


public class SFJL_Tween_Example extends PApplet {

    
    Ease_In_Out_Type[] ease_in_out_types = {
        Ease_In_Out_Type.EASE_IN,
        Ease_In_Out_Type.EASE_OUT,
        Ease_In_Out_Type.EASE_IN_OUT
    };

    int ease_in_out_type_index = 0;


    Ease_Type[] ease_types = {
        Ease_Type.SINE,
        Ease_Type.LINEAR,
        Ease_Type.BOUNCE,
        Ease_Type.CIRC,
        Ease_Type.CUBIC,
        Ease_Type.EXPO,
        Ease_Type.QUAD,
        Ease_Type.QUART,
        Ease_Type.QUINT
    };

    int ease_type_index = 0;


    PVector pos = new PVector();
    PVector target_pos = new PVector();

    int start_time_for_tween;
    float duration = 1000;



    public static void main(String[] args) {
        PApplet.main(SFJL_Tween_Example.class, args);
    }

    @Override
    public void settings() {
        size(800, 600);
    }

    @Override
    public void setup() {
        
    }

    @Override
    public void draw() {
        background(0);

        Ease_In_Out_Type in_out_type = ease_in_out_types[ease_in_out_type_index];
        Ease_Type ease_type = ease_types[ease_type_index];

        float time = min(duration, millis()-start_time_for_tween);

        float x = ease(in_out_type, ease_type, time, pos.x, target_pos.x, duration);
        float y = ease(in_out_type, ease_type, time, pos.y, target_pos.y, duration);

        if (time == duration) {
            pos.x = x;
            pos.y = y;
        }
        
        fill(255,255,0);
        ellipse(x, y, 125, 125);

        if (mousePressed) {
            pos.x = x;
            pos.y = y;
            target_pos.x = mouseX;
            target_pos.y = mouseY;
            start_time_for_tween = millis();
        }

        fill(255);
        textSize(40);
        text(""+in_out_type+"\n"+ease_type, 50, 50);
    }

    @Override
    public void keyPressed() {
        
        if (key == 'q') {
            ease_in_out_type_index--;
            if (ease_in_out_type_index < 0) {
                ease_in_out_type_index = ease_in_out_types.length;
            }
        }
        if (key == 'w') {
            ease_in_out_type_index++;
            if (ease_in_out_type_index == ease_in_out_types.length) {
                ease_in_out_type_index = 0;
            }
        }
        if (key == 'a') {
            ease_type_index--;
            if (ease_type_index < 0) {
                ease_type_index = ease_types.length;
            }
        }
        if (key == 's') {
            ease_type_index++;
            if (ease_type_index == ease_types.length) {
                ease_type_index = 0;
            }
        }


    }

   
}