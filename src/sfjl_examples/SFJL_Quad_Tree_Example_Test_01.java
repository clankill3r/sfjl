package sfjl_examples;

import static sfjl.SFJL_Quad_Tree.*;

import java.util.ArrayList;


import processing.core.PApplet;
import processing.core.PVector;
import sfjl.SFJL_Quad_Tree;


public class SFJL_Quad_Tree_Example_Test_01 extends PApplet {

    public static void main(String[] args) {
        PApplet.main(SFJL_Quad_Tree_Example_Test_01.class, args);
    }

    Quad_Tree<PVector> tree;
    ArrayList<PVector> within = new ArrayList<>();


    @Override
    public void settings() {     
        size(800, 800, P2D);
        pixelDensity(2);
        
    }


    @Override
    public void setup() {
        
        frameRate(60);

        int plot_x1 = 50;
        int plot_x2 = width - 50;
        int plot_y1 = 50;
        int plot_y2 = height - 50;
        int plot_width = plot_x2 - plot_x1;
        int plot_height = plot_y2 - plot_y1;

        tree = new Quad_Tree<>((v)-> v.x, (v)->v.y, 64, plot_x1, plot_y1, plot_x2, plot_y2);

        for (int y = plot_y1; y <  plot_y2; y += 3) {
            for (int x = plot_x1; x < plot_x2; x += 3) {

                float n = dist(x, y, width/2, height/2) / max(plot_width, plot_height);
                
                if (n < 0.5f) {
                    if (noise(x * 0.025f, y * 0.025f) < 0.4) {
                        add(tree, new PVector(x, y));
                    }
                }
            }
        }
        println("points: "+tree.size);

        for (int i = 0; i < precomputed_random_sequence.length; i++) {
            precomputed_random_sequence[i] = random(-1, 1);
        }

    }


    float[] precomputed_random_sequence = new float[2048];
    int precomputed_random_sequence_index = 0;

    float next_random() {
        float r = precomputed_random_sequence[precomputed_random_sequence_index++];
        if (precomputed_random_sequence_index == precomputed_random_sequence.length) {
            precomputed_random_sequence_index = 0;
        }
        return r;
    }



    @Override
    public void draw() {

        surface.setTitle((int)frameRate+"   "+frameCount+"  ");

        background(0);

        noFill();
        stroke(255);
        strokeWeight(0.5f);
        draw_quad_tree(tree);
        
        within.clear();

        
        if (sin(frameCount * 0.01f) > 0) {
            float radius = sin(frameCount * 0.01f) * 250;

            get_within_radius_sq(tree, mouseX, mouseY, sq(radius), within);
            stroke(255,0,0);
            strokeWeight(1f);
            for (PVector v2 : within) {
                point(v2.x, v2.y);
                remove(tree, v2);
                v2.x += next_random() * 3; //random(-3, 3);
                v2.y += next_random() * 3; //random(-3, 3);
                add(tree, v2);
                v2.z = 1; // to avoid drawing again
            }
            merge_update(tree);
            noFill();
            ellipse(mouseX, mouseY, radius*2, radius*2);    
        }
        else {
            
            float s = sin(frameCount * 0.01f) * 250;
            get_within_aabb(tree, mouseX-s, mouseY-s, mouseX+s, mouseY+s, within);
            stroke(255,0,0);
            strokeWeight(1f);
            for (PVector v2 : within) {
                point(v2.x, v2.y);
                v2.z = 1; // to avoid drawing again
            }
            rectMode(CORNERS);
            noFill();
            rect(mouseX-s, mouseY-s, mouseX+s, mouseY+s);
        }


        stroke(255,255,0);
        strokeWeight(1f);
        for (PVector v : tree) {
            if (v.z == 0) {
                point(v.x, v.y);
            }
            else {
                v.z = 0;
            }
        }

        
        PVector v = get_closest(tree, mouseX, mouseY);
        if (v != null) {
            stroke(255,0,0);
            line(mouseX, mouseY, v.x, v.y);
            remove(tree, v);
        }
        
        if (size(tree) > 0) {
            stroke(255,0,0);
            strokeWeight(1f);
            noFill();
            rect(min_x(tree).x, min_y(tree).y, max_x(tree).x, max_y(tree).y);
        }

        fill(255);
        text(size(tree), 20, 20);
        text("lowest_depth_with_items: "+lowest_depth_with_items(tree), 20, 50);
        text("highest_depth_with_items: "+highest_depth_with_items(tree), 20, 80);
        

    }


    public <T> int size(Quad_Tree<T> qt) {
        return SFJL_Quad_Tree.size(qt.root);
    }


    public <T> int size(Quad_Tree_Node<T> qt) {
        return SFJL_Quad_Tree.size(qt);
    }


    public <T> void draw_quad_tree(Quad_Tree<T> tree) {
        rectMode(CORNERS);
        int lowest_depth_with_items = lowest_depth_with_items(tree);
        int highest_depth_with_items = highest_depth_with_items(tree);
        draw_quad_tree(tree.root, 0, lowest_depth_with_items, highest_depth_with_items);
    }


    public void draw_quad_tree(Quad_Tree_Node<?> tree, int level, int lowest_depth_with_items, int highest_depth_with_items) {
        if (has_children(tree)) {
            draw_quad_tree(tree.children[0], level+1, lowest_depth_with_items, highest_depth_with_items);
            draw_quad_tree(tree.children[1], level+1, lowest_depth_with_items, highest_depth_with_items);
            draw_quad_tree(tree.children[2], level+1, lowest_depth_with_items, highest_depth_with_items);
            draw_quad_tree(tree.children[3], level+1, lowest_depth_with_items, highest_depth_with_items);
        }
        else {
            fill(map(level, lowest_depth_with_items, highest_depth_with_items, 0, 50));
            rect(tree.x1, tree.y1, tree.x2, tree.y2);
        }
    }


    @Override
    public void keyPressed() {
        if (key == 'c') {
            SFJL_Quad_Tree.clear(tree);
        }
        if (key == 'r') {
            rebuild(tree);
        }
        if (key == 'e') {
            // TODO, expand root            
        }
    }

    @Override
    public void mouseDragged() {
        if (frameCount % 2 == 0) {
            for (int i = 0; i < 10; i++) {
                add(tree, new PVector(mouseX + random(-50, 50), mouseY + random(-50, 50)));
            }
        }
    }


}