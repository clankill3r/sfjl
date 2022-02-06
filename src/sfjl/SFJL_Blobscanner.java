package sfjl;
import static java.lang.Math.*;
import static sfjl.SFJL_Math.*;
import java.util.Arrays;
public class SFJL_Blobscanner {
     private SFJL_Blobscanner() {}
//---------- SFJL_Blobscanner
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public enum Border_Handling {
    BORDER_CHECK_BOUNDS,
    BORDER_REPLACE,
    BORDER_REPLACE_AND_RESTORE
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Contour_Buffer {
    // public int[][] corners;
    // public int     corners_length;
    // public int[][] contour;
    // public int     contour_length;
    public Vec2[] contour = new Vec2[0];
    public int    contour_length;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Border_Backup {
    public int[] top     = new int[0];
    public int[] right   = new int[0];
    public int[] bottom  = new int[0];
    public int[] left    = new int[0];
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public interface Threshold_Checker {
    boolean is_walkable(int color, float threshold); 
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public interface Process_Contour {
    boolean process_contour(Contour_Buffer contour_buffer);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Blobscanner_Context {
    public Border_Handling blobscan_method = Border_Handling.BORDER_REPLACE_AND_RESTORE;
    public Border_Backup border_backup = new Border_Backup();
    public int border_color;
    public AABB  roi = new AABB(0, 0, 1, 1);
    public Threshold_Checker threshold_checker;
    public float threshold;
    public int y_increment = 1;
    public int[] contour_to_blob_index_map = new int[0]; // nocheckin remove?
    public Contour_Buffer contour_buffer = new Contour_Buffer();
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public void make_roi_in_pixels(AABB roi, int w, int h) {

    float min_x = min(roi.x1, roi.x2);
    float min_y = min(roi.y1, roi.y2);
    float max_x = max(roi.x1, roi.x2);
    float max_y = max(roi.y1, roi.y2);

    if (max_x <= 1) { // roi is normalized
        roi.x1 = (int) min_x * (w-1);
        roi.y1 = (int) min_y * (h-1);
        roi.x2 = (int) max_x * (w-1);
        roi.y2 = (int) max_y * (h-1);
    }
    else { // roi is absolute pixels
        roi.x1 = (int) min_x;
        roi.y1 = (int) min_y;
        roi.x2 = (int) max_x;
        roi.y2 = (int) max_y;
    }
    
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public void prepare_border(int[] pixels, int w, int h, AABB roi_in_pixels, int border_color, boolean do_backup_border, Border_Backup border_backup) {

    int x1 = (int) roi_in_pixels.x1;
    int y1 = (int) roi_in_pixels.y1;
    int x2 = (int) roi_in_pixels.x2;
    int y2 = (int) roi_in_pixels.y2;

    if (do_backup_border) {

        if (border_backup.top.length < (x2 - x1)) {
            border_backup.top    = new int[x2-x1+1];
            border_backup.bottom = new int[x2-x1+1];
        }
        if (border_backup.left.length < (y2 - y1)) {
            border_backup.left   = new int[y2-y1+1];
            border_backup.right  = new int[y2-y1+1];
        }
    }

    int backup_index;
    int pixel_index;
    
    // top edge
    backup_index = 0;
    pixel_index = x1 + (y1 * w);
    for (int x = x1; x <= x2; x++) { 
        if (do_backup_border) {
            border_backup.top[backup_index] = pixels[pixel_index];
            backup_index += 1;
        }
        pixels[pixel_index] = border_color;
        pixel_index += 1;
    }
    // left and right edge
    backup_index = 0;
    int pixel_index_left  = x1 + ((y1+1) * w);
    int pixel_index_right = x2 + ((y1+1) * w);
    for (int y = y1+1; y < y2; y++) { 
        if (do_backup_border) {
            border_backup.left[backup_index]  = pixels[pixel_index_left];
            border_backup.right[backup_index] = pixels[pixel_index_right];
            backup_index += 1;
        }
        pixels[pixel_index_left] = border_color;
        pixels[pixel_index_right] = border_color;
        pixel_index_left  += w;
        pixel_index_right += w;
    }
    // bottom edge
    backup_index = 0; 
    pixel_index = x1 + (y2 * w);
    for (int x = x1; x <= x2; x++) { 
        if (do_backup_border) {
            border_backup.bottom[backup_index] = pixels[pixel_index];
            backup_index += 1;
        }
        pixels[pixel_index] = border_color;
        pixel_index  += 1;
    }   
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public void restore_border_from_backup(int[] pixels, int w, int h, AABB roi_in_pixels, Border_Backup border_backup) {
    
    int x1 = (int) roi_in_pixels.x1;
    int y1 = (int) roi_in_pixels.y1;
    int x2 = (int) roi_in_pixels.x2;
    int y2 = (int) roi_in_pixels.y2;

    int backup_index;
    int pixel_index;
    
    // top edge
    backup_index = 0;
    pixel_index = x1 + (y1 * w);
    for (int x = x1; x <= x2; x++) { 
        pixels[pixel_index] = border_backup.top[backup_index];
        backup_index += 1;
        pixel_index  += 1;
    }
    // left and right edge
    backup_index = 0;
    int pixel_index_left  = x1 + ((y1+1) * w);
    int pixel_index_right = x2 + ((y1+1) * w);
    for (int y = y1+1; y < y2; y++) { 
        pixels[pixel_index_left] = border_backup.left[backup_index];
        pixels[pixel_index_right] = border_backup.right[backup_index];
        backup_index += 1;
        pixel_index_left  += w;
        pixel_index_right += w;
    }
    // bottom edge
    backup_index = 0; 
    pixel_index = x1 + (y2 * w);
    for (int x = x1; x <= x2; x++) { 
        pixels[pixel_index] = border_backup.bottom[backup_index];
        backup_index += 1;
        pixel_index  += 1;
    }   

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public void find_blobs(Blobscanner_Context ctx, int[] pixels, int w, int h, Process_Contour process_contour) {

    make_roi_in_pixels(ctx.roi, w, h);

    boolean do_backup_border = ctx.blobscan_method == Border_Handling.BORDER_REPLACE || ctx.blobscan_method == Border_Handling.BORDER_REPLACE_AND_RESTORE;
    prepare_border(pixels, w, h, ctx.roi, ctx.border_color, do_backup_border, ctx.border_backup);

    if (ctx.contour_to_blob_index_map.length < pixels.length) {
        ctx.contour_to_blob_index_map = new int[pixels.length];
    }

    // TODO, we need fewer then pixels.length
    if (ctx.contour_buffer.contour.length < pixels.length) {
        int old_length = ctx.contour_buffer.contour.length;
        int new_length = pixels.length;
        ctx.contour_buffer.contour = Arrays.copyOf(ctx.contour_buffer.contour, new_length);
        for (int i = old_length; i < new_length; i++) {
            ctx.contour_buffer.contour[i] = new Vec2();
        }
    }


    //
    // Scan in lines untill we hit and edge, then walk the contour
    //

    int x1 = (int) ctx.roi.x1;
    int y1 = (int) ctx.roi.y1;
    int x2 = (int) ctx.roi.x2;
    int y2 = (int) ctx.roi.y2;

    
    outer:
    for (int y = y1; y < y2; y += ctx.y_increment) {

        boolean prev_is_walkable = false;

        for (int x = x1; x < x2; x++) {

            int index = y * w + x;
            boolean current_is_walkable = ctx.threshold_checker.is_walkable(pixels[index], ctx.threshold);

            if (current_is_walkable && !prev_is_walkable) {
                walk_contour(pixels, w, h, index, ctx.contour_buffer, ctx.threshold_checker, ctx.threshold);
                boolean keep_going = process_contour.process_contour(ctx.contour_buffer);
                
                if (!keep_going) {
                    break outer;
                }
            }

            prev_is_walkable = current_is_walkable;
        }
    }


    if (ctx.blobscan_method == Border_Handling.BORDER_REPLACE_AND_RESTORE) {
        restore_border_from_backup(pixels, w, h, ctx.roi, ctx.border_backup);
    }

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Move_Info {
    public int move_dir;
    public int check_dir;
    public int x_move_change;
    public int y_move_change;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

// static public void set_move_dir_to_chekc_dir(Move_Info move_info) {
//     move_info.move_dir = move_info.check_dir;
//     if (move_info.check_dir == 1) {
//         move_info.move_dir == 
//     }
// }

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public void walk_contour(int[] pixels, int w, int h, int start_index, Contour_Buffer contour_buffer, Threshold_Checker threshold_checker, float threshold) {

    final int LEFT  = -1;
    final int RIGHT =  1;
    final int UP    = -w;
    final int DOWN  =  w;

    int current = start_index;
    int x = current % w;
    int y = (current - x) / w;

    int start_x = x;
    int start_y = y;

    int move_dir;
    int check_dir;
    int x_mov_change;
    int y_mov_change;

    //
    // set start direction
    //
    boolean left_walkable  = threshold_checker.is_walkable(pixels[current + LEFT], threshold);
    boolean right_walkable = threshold_checker.is_walkable(pixels[current + RIGHT], threshold);
    boolean up_walkable    = threshold_checker.is_walkable(pixels[current + UP], threshold);
    boolean down_walkable  = threshold_checker.is_walkable(pixels[current + DOWN], threshold);

    if (!left_walkable)       move_dir = up_walkable    ? UP    : right_walkable ? RIGHT : DOWN;
    else if (!up_walkable)    move_dir = right_walkable ? RIGHT : down_walkable  ? DOWN  : LEFT;
    else if (!right_walkable) move_dir = down_walkable  ? DOWN  : left_walkable  ? LEFT  : UP;
    else if (!down_walkable)  move_dir = left_walkable  ? LEFT  : up_walkable    ? UP    : RIGHT;
    else {
        throw new RuntimeException("TODO single pixel"); // nocheckin
    }

    //////
    
    if      (move_dir == RIGHT) { check_dir = UP;    x_mov_change =  1; y_mov_change =  0;}
    else if (move_dir == DOWN)  { check_dir = RIGHT; x_mov_change =  0; y_mov_change =  1;}
    else if (move_dir == LEFT)  { check_dir = DOWN;  x_mov_change = -1; y_mov_change =  0;}
    else  /*             UP */  { check_dir = LEFT;  x_mov_change =  0; y_mov_change = -1;}

    //
    // start walking the contour untill where done
    //
    // int[][] corners = ctx.contour_buffer.corners;
    // int[][] contour = ctx.contour_buffer.contour;
    Vec2[] contour = contour_buffer.contour;


    // corners[0][0] = x;
    // corners[0][1] = y;
    // contour[0][0] = x;
    // contour[0][1] = y;
    contour[0].x = x;
    contour[0].y = y;
    // int corner_index  = 1;
    int contour_index = 1;


    while (true) {     
        
        //
        // move in current move direction if possible
        //
        if (threshold_checker.is_walkable(pixels[current + move_dir], threshold)) {
            
            current += move_dir;
            x += x_mov_change;
            y += y_mov_change;

            contour[contour_index].x = x;
            contour[contour_index].y = y;
            contour_index += 1;
            //
            // move in check direction if possible
            //
            if (threshold_checker.is_walkable(pixels[current + check_dir], threshold)) {
                //
                // change the direction
                //
                if (check_dir == RIGHT) { // move dir was down
                    // corners[corner_index].x= x + 1;
                    // corners[corner_index].y = y + 0;
                    // corner_index += 1;
                    current += check_dir;
                    move_dir  = RIGHT; 
                    check_dir = UP;
                    x_mov_change = 1;
                    y_mov_change = 0;
                    x += 1;
                }
                else if (check_dir == DOWN)  { // move dir was left
                    // corners[corner_index].x = x + 1;
                    // corners[corner_index].y = y + 1;
                    // corner_index += 1;
                    current += check_dir;
                    move_dir  = DOWN;  
                    check_dir = RIGHT;
                    x_mov_change = 0;
                    y_mov_change = 1;
                    y += 1;
                }
                else if (check_dir == LEFT)  { // move dir was up
                    // corners[corner_index].x = x;
                    // corners[corner_index].y = y + 1;
                    // corner_index += 1;
                    current += check_dir;
                    move_dir  = LEFT;  
                    check_dir = DOWN;
                    x_mov_change = -1;
                    y_mov_change = 0;
                    x -= 1;
                }
                else { // if (check_dir == UP)    { // move dir was right
                    // corners[corner_index].x = x;
                    // corners[corner_index].y = y;
                    // corner_index += 1;
                    current += check_dir;
                    move_dir  = UP;    
                    check_dir = LEFT;
                    x_mov_change = 0;
                    y_mov_change = -1;
                    y -= 1;
                }
                
                contour[contour_index].x = x;
                contour[contour_index].y = y;
                contour_index += 1;

                // if (corners[corner_index-1].x == start_x && corners[corner_index-1].y == start_y) {
                //     break;
                // }
                if (contour[contour_index-1].x == start_x && contour[contour_index-1].y == start_y) {
                    break;
                }
            }
        }
        else {
            //
            // we have hit a wall
            // 
            if (move_dir == UP) {    
                // corners[corner_index++] = current;
                move_dir  = RIGHT; 
                check_dir = UP;
                x_mov_change = 1;
                y_mov_change = 0;
            }
            else if (move_dir == RIGHT) { 
                // corners[corner_index++] = current + RIGHT; // half-open fix
                move_dir  = DOWN;  
                check_dir = RIGHT;
                x_mov_change = 0;
                y_mov_change = 1;
            }
            else if (move_dir == DOWN) {  
                // corners[corner_index++] = current + DOWN + RIGHT; // half-open fix
                move_dir  = LEFT;  
                check_dir = DOWN;
                x_mov_change = -1;
                y_mov_change =  0;
            }
            else { //if (move_dir == LEFT) {  
                // corners[corner_index++] = current + DOWN;
                move_dir  = UP;    
                check_dir = LEFT;
                x_mov_change =  0;
                y_mov_change = -1;
            }    
            
            // contour[contour_index++] = current;
            contour[contour_index].x = x;
            contour[contour_index].y = y;
            contour_index += 1;

            // if (corners[corner_index-1] == start_index) {
            //     break;
            // }
            if (contour[contour_index-1].x == start_x && contour[contour_index-1].y == start_y) {
                break;
            }

        }
    }

    // ctx.contour_buffer.corners_length = corner_index;
    contour_buffer.contour_length = contour_index;

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}

