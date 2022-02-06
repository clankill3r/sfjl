package sfjl_examples;


import static sfjl.SFJL_Quad_Tree.*;


// import static nl.doekewartena.sf_libs.SF_Integral_Image.*;


// import processing.core.PApplet;
// import processing.core.PImage;
// import processing.core.PVector;
// import processing.opengl.*;


// public class Quad_Tree_Test_Image extends PApplet
// {
//     public static void main( String[] args )
//     {
        
//         PApplet.main(Quad_Tree_Test_Image.class, args);
//     }
    
    
//     Quad_Tree<PVector> tree;
//     SF_Integral_Image avg_image;
    
//     PImage img;
    
    
//     @Override
//     public void settings() {     
//         size(612, 614, P2D);
//         pixelDensity(2);
//     }
    
    
//     @Override
//     public void setup() {
        
//         frameRate(999);
        
//         tree  = new Quad_Tree<>((v)->v.x, (v)->v.y,
//         null, 
//         0, 0, width, height,
//         1);
        
//         img = loadImage("/Data/screenshots/Screen Shot 2018-01-02 at 02.07.31.png");
//         img.loadPixels();
        
//         avg_image = new SF_Integral_Image(img.width, img.height, img.pixels, 4, 255 << 24);
        
        
        
//         //create(tree, avg_image, 0, 0, width, height, 50);
        
        
//         // split atleast 2 times
//         tree.split();
//         tree.children[0].split();
//         tree.children[1].split();
//         tree.children[2].split();
//         tree.children[3].split();
        
//         int threshold = 135; // lower is more detail
//         create(tree, avg_image, img, threshold);
        
        
        
        
//     }
    
    
//     void create(Quad_Tree<PVector> tree, SF_Integral_Image avg_image, PImage img, int threshold) {
        
//         if (tree.x2 - tree.x1 < 6) {
//             return;
//         }
        
//         if (tree.has_children()) {
//             create(tree.children[0], avg_image, img, threshold);
//             create(tree.children[1], avg_image, img, threshold);
//             create(tree.children[2], avg_image, img, threshold);
//             create(tree.children[3], avg_image, img, threshold);
//         }
//         else {
            
//             int detail = detail(avg_image, img, (int) tree.x1, (int) tree.y1,(int) tree.x2, (int) tree.y2);
//             println(detail);
            
//             if (detail > threshold) {   
//                 tree.split();
//                 create(tree.children[0], avg_image, img, threshold);
//                 create(tree.children[1], avg_image, img, threshold);
//                 create(tree.children[2], avg_image, img, threshold);
//                 create(tree.children[3], avg_image, img, threshold);
//             }
//         }
//     }
    
    
    
//     int detail(SF_Integral_Image avg_image, PImage img, int x1, int y1, int x2, int y2) {
        
//         int average_color = avg_image.color_for_aabb(x1, y1, x2, y2);
//         int red   = (int) red(average_color);
//         int green = (int) green(average_color);
//         int blue  = (int) blue(average_color);
        
//         long sum_red = 0;
//         long sum_green = 0;
//         long sum_blue = 0;
        
//         int width = x2 - x1;
        
//         for (int py = y1; py < y2; py++) {
//             for (int px = x1; px < x2; px++) {
                
//                 int index = py * width + px;
//                 sum_red   += abs(red   - red(img.pixels[index]));
//                 sum_green += abs(green - green(img.pixels[index]));
//                 sum_blue  += abs(blue  - blue(img.pixels[index]));
//             }
//         }
        
//         long area = (x2-x1) * (y2-y1);
        
//         return (int) ((sum_red / area) + (sum_green / area) + (sum_blue / area));
//     }
    
    
    
//     @Override
//     public void draw() {
        
//         surface.setTitle((int)frameRate+"   "+frameCount+"  ");
        
//         noStroke();
//         draw_quad_tree(tree, avg_image);
        
//     }
    
    
//     public void draw_quad_tree(Quad_Tree<PVector> tree, SF_Integral_Image avg_image) {
        
//         if (tree.has_children()) {
//             draw_quad_tree(tree.children[0], avg_image);
//             draw_quad_tree(tree.children[1], avg_image);
//             draw_quad_tree(tree.children[2], avg_image);
//             draw_quad_tree(tree.children[3], avg_image);
//         }
//         else {
//             rectMode(CORNERS);
//             fill(avg_image.color_for_aabb((int)tree.x1, (int)tree.y1, (int)tree.x2, (int)tree.y2));
//             rect(tree.x1, tree.y1, tree.x2, tree.y2);
//         }
//     }
    
    
//     @Override
//     public void keyPressed() {
//     }
    
//     @Override
//     public void mousePressed() {
//         // tree.add(new PVector(mouseX, mouseY));
//         // println("added!");
//         Quad_Tree current = tree;
//         while (current.has_children()) {
//             int where = current.get_index(mouseX, mouseY);
//             current = current.children[where];
//         }
//         current.split();
//     }
    
    
// }
