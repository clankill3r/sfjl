package sfjl_examples;

import static sfjl.SFJL_Quad_Tree.*;

import java.util.ArrayList;


import processing.core.PApplet;
import processing.core.PVector;
import sfjl.SFJL_Quad_Tree;


public class SFJL_Quad_Tree_Example extends PApplet {

    public static void main(String[] args) {
        PApplet.main(SFJL_Quad_Tree_Example.class, args);
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

        randomSeed(1);
        
        frameRate(60);
        strokeCap(SQUARE);

        int plot_x1 = 50;
        int plot_x2 = width - 50;
        int plot_y1 = 50;
        int plot_y2 = height - 50;
        int plot_width = plot_x2 - plot_x1;
        int plot_height = plot_y2 - plot_y1;

        tree = new Quad_Tree<>((v)-> v.x, (v)->v.y, 96, plot_x1, plot_y1, plot_x2, plot_y2);

        for (int y = plot_y1; y <  plot_y2; y += 1) {
            for (int x = plot_x1; x < plot_x2; x += 1) {

                float n = dist(x, y, width/2, height/2) / max(plot_width, plot_height);
                
                if (n < 0.5f) {
                    if (noise(x * 0.025f, y * 0.025f) < 0.4) {
                        add(tree, new PVector(x, y, random(9)));
                        add(tree, new PVector(x, y, random(9)));
                        add(tree, new PVector(x, y, random(9)));
                        add(tree, new PVector(x, y, random(9)));
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


    boolean move_points = false;
    boolean draw_points = false;
    boolean draw_quad_tree = false;

    ArrayList<PVector> update_helper = new ArrayList<>();


    @Override
    public void draw() {

        surface.setTitle((int)frameRate+"   "+frameCount+"  ");

        background(0);

        noFill();
        stroke(255);
        strokeWeight(0.5f);
        if (draw_quad_tree) draw_quad_tree(tree);
        
        within.clear();

        
        if (sin(frameCount * 0.01f) > 0) {
            float radius = sin(frameCount * 0.01f) * 250;

            get_within_radius_sq(tree, mouseX, mouseY, sq(radius), within);
            stroke(255,0,0);
            strokeWeight(1f);
            for (PVector v2 : within) {
                if (draw_points) point(v2.x, v2.y);

                if (move_points) {
                    v2.x += next_random() * abs(v2.z) * abs(v2.z); //random(-3, 3);
                    v2.y += next_random() * abs(v2.z) * abs(v2.z); //random(-3, 3);
                }
                v2.z = -abs(v2.z); // to avoid drawing again
            }

            ArrayList<PVector> out_of_bounds = new ArrayList<>();

            update(tree, update_helper, out_of_bounds);
            noFill();
            ellipse(mouseX, mouseY, radius*2, radius*2);    
        }
        else {
            
            float s = sin(frameCount * 0.01f) * 250;
            get_within_aabb(tree, mouseX-s, mouseY-s, mouseX+s, mouseY+s, within);
            stroke(255,0,0);
            strokeWeight(1f);
            for (PVector v2 : within) {
                if (draw_points) point(v2.x, v2.y);
                v2.z = -abs(v2.z); // to avoid drawing again
            }
            rectMode(CORNERS);
            noFill();
            rect(mouseX-s, mouseY-s, mouseX+s, mouseY+s);
        }


        stroke(255,255,0);
        strokeWeight(1f);
        for (PVector v : tree) {
            if (v.z >= 0) {
                if (draw_points) point(v.x, v.y);
            }
            else {
                v.z = -v.z;
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
            rectMode(CORNERS);
            rect(min_x(tree).x, min_y(tree).y, max_x(tree).x, max_y(tree).y);
        }


        fill(255);
        text(size(tree), 20, 20);
        text("highest_depth_with_leafs: "+highest_depth_with_leafs(tree), 20, 80);
        text("lowest_depth_with_leafs: "+lowest_depth_with_leafs(tree), 20, 100);
    }


    public <T> int size(Quad_Tree<T> qt) {
        return SFJL_Quad_Tree.size(qt.root);
    }


    public <T> int size(Quad_Tree_Node<T> qt) {
        return SFJL_Quad_Tree.size(qt);
    }


    public <T> void draw_quad_tree(Quad_Tree<T> tree) {
        rectMode(CORNERS);
        int lowest_depth_with_leafs = lowest_depth_with_leafs(tree);
        int highest_depth_with_leafs = highest_depth_with_leafs(tree);
        draw_quad_tree(tree.root, 0, lowest_depth_with_leafs, highest_depth_with_leafs);
    }


    public void draw_quad_tree(Quad_Tree_Node<?> tree, int level, int lowest_depth_with_leafs, int highest_depth_with_leafs) {
        if (has_children(tree)) {
            draw_quad_tree(tree.children[0], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
            draw_quad_tree(tree.children[1], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
            draw_quad_tree(tree.children[2], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
            draw_quad_tree(tree.children[3], level+1, lowest_depth_with_leafs, highest_depth_with_leafs);
        }
        else {
            fill(map(level, lowest_depth_with_leafs, highest_depth_with_leafs, 0, 50));
            rect(tree.x1, tree.y1, tree.x2, tree.y2);
        }
    }


    @Override
    public void keyPressed() {
        if (key == 'c') {
            SFJL_Quad_Tree.clear(tree);
        }
        if (key == 'e') {
            // TODO, expand root            
        }
        if (key == 'm') {
            move_points = !move_points;
        }
        if (key == 'q') {
            draw_quad_tree = !draw_quad_tree;
        }
        if (key == 'p') {
            draw_points = !draw_points;
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