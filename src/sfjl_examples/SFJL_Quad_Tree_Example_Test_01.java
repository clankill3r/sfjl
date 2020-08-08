package sfjl_examples;

import static sfjl.SFJL_Quad_Tree.*;

import java.util.ArrayList;


import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.*;


public class SFJL_Quad_Tree_Example_Test_01 extends PApplet {

    public static void main(String[] args) {
        PApplet.main(SFJL_Quad_Tree_Example_Test_01.class, args);
    }


    // quad tree
    // kd - tree
    // bvh tree



    Quad_Tree<PVector> tree;

    //ArrayList<PVector> vecs = new ArrayList<>();

    @Override
    public void settings() {     
        size(800, 800, P2D);
        pixelDensity(2);
        
    }

    

    @Override
    public void setup() {
        
        frameRate(999);

        int plot_x1 = 50;
        int plot_x2 = width - 50;
        int plot_y1 = 50;
        int plot_y2 = height - 50;
        int plot_width = plot_x2 - plot_x1;
        int plot_height = plot_y2 - plot_y1;

        tree  = new Quad_Tree<>((v)-> v.x, (v)->v.y, 
                                null, 
                                plot_x1, plot_y1, plot_x2, plot_y2,
                                32);

        for (int y = plot_y1; y <  plot_y2; y += 3) {
            for (int x = plot_x1; x < plot_x2; x += 3) {

                float n = dist(x, y, width/2, height/2) / max(plot_width, plot_height);
                // println(n);
                if (n < 0.5f) {

                    if (noise(x * 0.025f, y * 0.025f) < 0.4) {
                            tree.add(new PVector(x, y));
                            //vecs.add(new PVector(x, y));
                     }
                 }
            }
        }

    }

    @Override
    public void draw() {

        surface.setTitle((int)frameRate+"   "+frameCount+"  ");

        background(0);

        noFill();
        stroke(255);
        strokeWeight(0.5f);
        draw_quad_tree(tree);

     

        // what if we insert outside of bounds?
        // ignore out of bounds?
        // OUT_OF_BOUNDS_IGNORE
        // OUT_OF_BOUNDS_EXPAND
        // OUT_OF_BOUNDS_REJECT


        
        if (sin(frameCount * 0.01f) > 0) {
            float radius = sin(frameCount * 0.01f) * 250;
            ArrayList<PVector> within = new ArrayList<>();
            tree.get_within_radius_sq(mouseX, mouseY, sq(radius), within);
            stroke(255,0,0);
            strokeWeight(1f);
            for (PVector v2 : within) {
                point(v2.x, v2.y);
                tree.remove(v2);
                v2.x += random(-3, 3);
                v2.y += random(-3, 3);
                tree.add(v2);
                v2.z = 1; // to avoid drawing again
            }
            tree.merge_update();
            noFill();
            ellipse(mouseX, mouseY, radius*2, radius*2);    
        }
        else {
            ArrayList<PVector> within = new ArrayList<>();
            float s = sin(frameCount * 0.01f) * 250;
            tree.get_within_aabb(mouseX-s, mouseY-s, mouseX+s, mouseY+s, within);
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
            if (v.z == 0) point(v.x, v.y);
            else v.z = 0;
        }
        
        PVector v = tree.get_closest(mouseX, mouseY);
        if (v != null) {
            stroke(255,0,0);
            line(mouseX, mouseY, v.x, v.y);
            tree.remove(v);
        }
        
        if (tree.size() > 0) {
            stroke(255,0,0);
            strokeWeight(1f);
            noFill();
            rect(tree.min_x().x, tree.min_y().y, tree.max_x().x, tree.max_y().y);
        }

        fill(255);
        text(tree.size(), 20, 20);
        text("lowest_depth_with_items: "+tree.lowest_depth_with_items(), 20, 50);
        text("highest_depth_with_items: "+tree.highest_depth_with_items(), 20, 80);
        

    }


    public void draw_quad_tree(Quad_Tree<?> tree) {
        rectMode(CORNERS);
        int lowest_depth_with_items = tree.lowest_depth_with_items();
        int highest_depth_with_items = tree.highest_depth_with_items();
        draw_quad_tree(tree, 0, lowest_depth_with_items, highest_depth_with_items);
    }

    public void draw_quad_tree(Quad_Tree<?> tree, int level, int lowest_depth_with_items, int highest_depth_with_items) {
        if (tree.has_children()) {
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
            tree.clear();
        }
        if (key == 'r') {
            tree.rebuild();
        }
        if (key == 'e') {
            // expand root
            //Quad_Tree<PVector> old_tree = tree;
            //int where = tree.get_index(mouseX, mouseY);
            // if (where == CK3_Quad_Tree.TL) {

            // }
            
        }
    }

    @Override
    public void mouseDragged() {
        if (frameCount % 2 == 0) {
            for (int i = 0; i < 10; i++) {
                tree.add(new PVector(mouseX + random(-50, 50), mouseY + random(-50, 50)));
            }
        }
    }


}
