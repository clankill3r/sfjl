package sfjl;
import static java.lang.Math.*;
import static sfjl.SFJL_Math.*;
import java.util.Arrays;
import sfjl.SFJL_Math.Vec2;
public class SFJL_Blobscanner {
     private SFJL_Blobscanner() {}
//---------- SFJL_Blobscanner
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public enum Border_Handling {
    DONT_BORDER,
    BORDER_CHECK_BOUNDS,
    REPLACE_BORDER,
    REPLACE_BORDER_AND_RESTORE_BORDER
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public enum Contour_Settings {
    ALL_PIXELS,
    ONLY_CORNERS,
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Contour_Buffer<T> {
    public T[] contour;
    public int contour_length;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Contour_Exist_Map {
    byte[] pixels = new byte[0];
    byte write_index;
    int width;
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
public interface Process_Contour<T> {
    boolean process_contour(Contour_Buffer<T> contour_buffer);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Blobscanner_Settings {
    public Border_Handling border_handling = Border_Handling.REPLACE_BORDER_AND_RESTORE_BORDER;
    public Border_Backup border_backup = new Border_Backup();
    public int border_color;
    public AABB roi = new AABB(0, 0, 1, 1);
    public Threshold_Checker threshold_checker;
    public float threshold;
    public int y_increment = 1;
    public Contour_Settings contour_settings = Contour_Settings.ONLY_CORNERS;
    public Contour_Buffer<Vec2> contour_buffer_vec2   = new Contour_Buffer<>();
    public Contour_Buffer<int[]> contour_buffer_index = new Contour_Buffer<>();
    // public byte[] contour_already_scanned_lookup = new byte[0];
    // public byte   contour_already_exists_number;
    public Contour_Exist_Map contour_exist_map = new Contour_Exist_Map();
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
static public void prepare_border(int[] pixels, int w, int h, AABB roi_in_pixels, int border_color, Border_Handling border_handling, Border_Backup border_backup) {

    int x1 = (int) roi_in_pixels.x1;
    int y1 = (int) roi_in_pixels.y1;
    int x2 = (int) roi_in_pixels.x2;
    int y2 = (int) roi_in_pixels.y2;

    boolean do_backup_border = border_handling == Border_Handling.REPLACE_BORDER_AND_RESTORE_BORDER;

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
static public void find_blobs_vec2(Blobscanner_Settings ctx, int[] pixels, int w, int h, Process_Contour<Vec2> process_contour) {

    Contour_Buffer<Vec2> contour_buffer = ctx.contour_buffer_vec2;

    // TODO, we need fewer then pixels.length
    if (contour_buffer.contour == null) {
        contour_buffer.contour = new Vec2[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            contour_buffer.contour[i] = new Vec2();
        }
    }
    else if (contour_buffer.contour.length < pixels.length) {
        int old_length = contour_buffer.contour.length;
        int new_length = pixels.length;
        contour_buffer.contour = Arrays.copyOf(contour_buffer.contour, new_length);
        for (int i = old_length; i < new_length; i++) {
            contour_buffer.contour[i] = new Vec2();
        }
    }

    _find_blobs(ctx, pixels, w, h, contour_helper_vec2, contour_buffer, process_contour);

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public <T> void _find_blobs(Blobscanner_Settings ctx, int[] pixels, int w, int h, Contour_Helper<T> contour_helper, Contour_Buffer<T> contour_buffer, Process_Contour<T> process_contour) {
    
    make_roi_in_pixels(ctx.roi, w, h);

    if (ctx.border_handling == Border_Handling.REPLACE_BORDER || ctx.border_handling == Border_Handling.REPLACE_BORDER_AND_RESTORE_BORDER) {
        prepare_border(pixels, w, h, ctx.roi, ctx.border_color, ctx.border_handling, ctx.border_backup);
    }

    if (ctx.contour_exist_map.pixels.length < pixels.length) {
        ctx.contour_exist_map.pixels = new byte[pixels.length];
        ctx.contour_exist_map.write_index = 1;
    }
    ctx.contour_exist_map.width = w;

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
                
                if (ctx.contour_exist_map.pixels[index] != ctx.contour_exist_map.write_index) {
                    
                    // glitches   47fps - 60fps
                    // walk_contour(ctx.contour_exist_map, pixels, w, h, x, y, ctx.contour_settings, contour_buffer, ctx.threshold_checker, ctx.threshold, contour_helper);
                    
                    // glitches   47fps - 60fps
                    // walk_contour_func_calls_where_removed(ctx.contour_exist_map, pixels, w, h, x, y, ctx.contour_settings, contour_buffer, ctx.threshold_checker, ctx.threshold, contour_helper);
                    
                    // glitches   47fps - 60fps
                    // walk_contour_arrays(ctx.contour_exist_map, pixels, w, h, x, y, ctx.contour_settings, contour_buffer, ctx.threshold_checker, ctx.threshold, contour_helper);
                    
                    // !!!!!!!   
                    // walk_contour_faster(ctx.contour_exist_map, pixels, w, h, x, y, ctx.contour_settings, contour_buffer, ctx.threshold_checker, ctx.threshold, contour_helper);
                    
                    walk_contour_slower(ctx.contour_exist_map, pixels, w, h, x, y, ctx.contour_settings, contour_buffer, ctx.threshold_checker, ctx.threshold, contour_helper);
                    boolean keep_going = process_contour.process_contour(contour_buffer);
                    
                    if (!keep_going) {
                        break outer;
                    }
                }
            }
            prev_is_walkable = current_is_walkable;
        }
    }

    if (ctx.border_handling == Border_Handling.REPLACE_BORDER_AND_RESTORE_BORDER) {
        restore_border_from_backup(pixels, w, h, ctx.roi, ctx.border_backup);
    }

    ctx.contour_exist_map.write_index += 1;
    if (ctx.contour_exist_map.write_index == 0)  {
        for (int i = 0; i < ctx.contour_exist_map.pixels.length; i++) {
            ctx.contour_exist_map.pixels[i] = 0;
        }
        ctx.contour_exist_map.write_index = 1;
    }

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
interface Contour_Helper<T> {
    void reset(Contour_Buffer<T> contour_buffer);
    void add_to_contour(Contour_Buffer<T> contour_buffer, int index, int x, int y);
}

static public Contour_Helper<Vec2> contour_helper_vec2 = new Contour_Helper<Vec2>() {

    @Override
    public void add_to_contour(Contour_Buffer<Vec2> contour_buffer, int index, int x, int y) {
        contour_buffer.contour[contour_buffer.contour_length].x = x;
        contour_buffer.contour[contour_buffer.contour_length].y = y;
        contour_buffer.contour_length += 1;
    }

    @Override
    public void reset(Contour_Buffer<Vec2> contour_buffer) {
        contour_buffer.contour_length = 0;
    }
    
};


// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

// returns true if the blob is new
// ctx can be null
static public <T> void walk_contour_slower(Contour_Exist_Map contour_exist_map, int[] pixels, int w, int h, int x, int y, Contour_Settings contour_settings, Contour_Buffer<T> contour_buffer, Threshold_Checker threshold_checker, float threshold, Contour_Helper<T> contour_helper) {

    final int LEFT  = -1;
    final int RIGHT =  1;
    final int UP    = -w;
    final int DOWN  =  w;

    int current = y * w + x;

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

    if (!(left_walkable || right_walkable || up_walkable || down_walkable)) {
        return; // single pixel
    }

    if (!left_walkable)       move_dir = up_walkable    ? UP    : right_walkable ? RIGHT : DOWN;
    else if (!up_walkable)    move_dir = right_walkable ? RIGHT : down_walkable  ? DOWN  : LEFT;
    else if (!right_walkable) move_dir = down_walkable  ? DOWN  : left_walkable  ? LEFT  : UP;
    else                      move_dir = left_walkable  ? LEFT  : up_walkable    ? UP    : RIGHT;

    //////
    
    if      (move_dir == RIGHT) { check_dir = UP;    x_mov_change =  1; y_mov_change =  0;}
    else if (move_dir == DOWN)  { check_dir = RIGHT; x_mov_change =  0; y_mov_change =  1;}
    else if (move_dir == LEFT)  { check_dir = DOWN;  x_mov_change = -1; y_mov_change =  0;}
    else  /*             UP */  { check_dir = LEFT;  x_mov_change =  0; y_mov_change = -1;}

    contour_helper.reset(contour_buffer);

    int move_dir_at_start = move_dir;
    int start_x = x;
    int start_y = y;

    int n_wall_hits_in_a_row = 0;

    contour_exist_map.pixels[current] = contour_exist_map.write_index;
    
    if (contour_settings == Contour_Settings.ONLY_CORNERS) {

        while (true) {     
            
            //
            // move in check direction if possible
            //
            if (check_dir == RIGHT && threshold_checker.is_walkable(pixels[current + RIGHT], threshold)) {
                contour_helper.add_to_contour(contour_buffer, current, x, y); // corner
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
                move_dir  = RIGHT;
                check_dir = UP;
                current += move_dir;
                x += 1;
                if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                    break;
                }
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else if (check_dir == DOWN && threshold_checker.is_walkable(pixels[current + DOWN], threshold)) {
                contour_helper.add_to_contour(contour_buffer, current, x, y); // corner
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
                move_dir  = DOWN;
                check_dir = RIGHT;
                current += move_dir;
                y += 1;
                if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                    break;
                }
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else if (check_dir == LEFT && threshold_checker.is_walkable(pixels[current + LEFT], threshold)) {
                contour_helper.add_to_contour(contour_buffer, current, x, y); // corner
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
                move_dir  = LEFT;
                check_dir = DOWN;
                current += move_dir;
                x -= 1;
                if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                    break;
                }
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else if (check_dir == UP && threshold_checker.is_walkable(pixels[current + UP], threshold)) {
                contour_helper.add_to_contour(contour_buffer, current, x, y); // corner
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
                move_dir  = UP;  
                check_dir = LEFT;
                current += move_dir;
                y -= 1;
                if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                    break;
                }
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else if (move_dir == RIGHT && threshold_checker.is_walkable(pixels[current + RIGHT], threshold)) {
                x += 1;
                current += RIGHT;
                if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                    break;
                }
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else if (move_dir == DOWN && threshold_checker.is_walkable(pixels[current + DOWN], threshold)) {
                y += 1;
                current += DOWN;
                if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                    break;
                }
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else if (move_dir == LEFT && threshold_checker.is_walkable(pixels[current + LEFT], threshold)) {
                x -= 1;
                current += LEFT;
                if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                    break;
                }
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else if (move_dir == UP && threshold_checker.is_walkable(pixels[current + UP], threshold)) {
                y -= 1;
                current += UP;
                if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                    break;
                }
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else {

                if (move_dir == RIGHT) { // wall
                    move_dir = DOWN;
                    check_dir = RIGHT;
                }
                else if (move_dir == DOWN) {
                    move_dir = LEFT;
                    check_dir = DOWN;
                }
                else if (move_dir == LEFT) {
                    move_dir = UP;
                    check_dir = LEFT;
                }
                else if (move_dir == UP) {
                    move_dir = RIGHT;
                    check_dir = UP;
                }

                if (threshold_checker.is_walkable(pixels[current + move_dir], threshold)) {
                    // we will make this move next iteration but we store it now
                    contour_helper.add_to_contour(contour_buffer, current, x, y); // corner

                    if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                        break;
                    }
                }

            }



            // else if (threshold_checker.is_walkable(pixels[current + move_dir], threshold)) {
            //     //
            //     // move in current move direction if possible
            //     //
            //     n_wall_hits_in_a_row = 0;

            //     current += move_dir;
            //     x += x_mov_change;
            //     y += y_mov_change;
            // }
            // else {
            //     //
            //     // we have hit a wall
            //     //
            //     n_wall_hits_in_a_row += 1;

            //     if (n_wall_hits_in_a_row != 2) {
            //         contour_helper.add_to_contour(contour_buffer, current, x, y);
            //         contour_exist_map.pixels[current] = contour_exist_map.write_index;
            //     }
                 
            // }
            
        }
    }
}



static public <T> void walk_contour(Contour_Exist_Map contour_exist_map, int[] pixels, int w, int h, int x, int y, Contour_Settings contour_settings, Contour_Buffer<T> contour_buffer, Threshold_Checker threshold_checker, float threshold, Contour_Helper<T> contour_helper) {

    final int LEFT  = -1;
    final int RIGHT =  1;
    final int UP    = -w;
    final int DOWN  =  w;

    int current = y * w + x;

    int[] move_dirs =     {RIGHT, UP,  LEFT, DOWN};
    int[] check_dirs =    {UP,    LEFT, DOWN, RIGHT};
    int[] move_change_x = {1,     0,    -1,     0};
    int[] move_change_y = {0,     -1,     0,    1};

    int xx;

    //
    // set start direction
    //
    boolean left_walkable  = threshold_checker.is_walkable(pixels[current + LEFT], threshold);
    boolean right_walkable = threshold_checker.is_walkable(pixels[current + RIGHT], threshold);
    boolean up_walkable    = threshold_checker.is_walkable(pixels[current + UP], threshold);
    boolean down_walkable  = threshold_checker.is_walkable(pixels[current + DOWN], threshold);

    if (!(left_walkable || right_walkable || up_walkable || down_walkable)) {
        return; // single pixel
    }

    if (!left_walkable)       xx = up_walkable    ? 1 : right_walkable ? 0 : 3;
    else if (!up_walkable)    xx = right_walkable ? 0 : down_walkable  ? 3 : 2;
    else if (!right_walkable) xx = down_walkable  ? 3 : left_walkable  ? 2 : 1;
    else                      xx = left_walkable  ? 2 : up_walkable    ? 1 : 0;

    //////
    

    contour_helper.reset(contour_buffer);

    int move_dir_at_start = move_dirs[xx];
    int start_x = x;
    int start_y = y;

    int n_wall_hits_in_a_row = 0;

    contour_exist_map.pixels[current] = contour_exist_map.write_index;

    
    if (contour_settings == Contour_Settings.ONLY_CORNERS) {

        while (true) {     
          
            if (threshold_checker.is_walkable(pixels[current + check_dirs[xx]], threshold)) {

                // contour_buffer.contour[contour_buffer.contour_length].x = x;
                // contour_buffer.contour[contour_buffer.contour_length].y = y;
                // contour_buffer.contour_length += 1;
                contour_helper.add_to_contour(contour_buffer, current, x, y);
                
                xx += 1;
                xx &= 0x3;

                current += move_dirs[xx];
                x += move_change_x[xx];
                y += move_change_y[xx];

                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else if (threshold_checker.is_walkable(pixels[current + move_dirs[xx]], threshold)) {
                n_wall_hits_in_a_row = 0;

                current += move_dirs[xx];
                x += move_change_x[xx];
                y += move_change_y[xx];

                contour_exist_map.pixels[current] = contour_exist_map.write_index;

            }
            else {
                n_wall_hits_in_a_row += 1;

                if (n_wall_hits_in_a_row != 2) {
                    // contour_buffer.contour[contour_buffer.contour_length].x = x;
                    // contour_buffer.contour[contour_buffer.contour_length].y = y;
                    // contour_buffer.contour_length += 1;
                    contour_helper.add_to_contour(contour_buffer, current, x, y);
                }
                
                xx -= 1;
                xx &= 0x3;
            }
            if (x == start_x && y == start_y && move_dirs[xx] == move_dir_at_start) {
                break;
            }
        }
    }
}

// function calls removed
static public <T> void walk_contour_func_calls_where_removed(Contour_Exist_Map contour_exist_map, int[] pixels, int w, int h, int x, int y, Contour_Settings contour_settings, Contour_Buffer<T> contour_buffer, Threshold_Checker threshold_checker, float threshold, Contour_Helper<T> contour_helper) {

    final int LEFT  = -1;
    final int RIGHT =  1;
    final int UP    = -w;
    final int DOWN  =  w;

    int current = y * w + x;

    int[] move_dirs =     {RIGHT, UP,  LEFT, DOWN};
    int[] check_dirs =    {UP,    LEFT, DOWN, RIGHT};
    int[] move_change_x = {1,     0,    -1,     0};
    int[] move_change_y = {0,     -1,     0,    1};

    int xx;

    //
    // set start direction
    //
    boolean left_walkable  = threshold_checker.is_walkable(pixels[current + LEFT], threshold);
    boolean right_walkable = threshold_checker.is_walkable(pixels[current + RIGHT], threshold);
    boolean up_walkable    = threshold_checker.is_walkable(pixels[current + UP], threshold);
    boolean down_walkable  = threshold_checker.is_walkable(pixels[current + DOWN], threshold);

    if (!(left_walkable || right_walkable || up_walkable || down_walkable)) {
        return; // single pixel
    }

    if (!left_walkable)       xx = up_walkable    ? 1 : right_walkable ? 0 : 3;
    else if (!up_walkable)    xx = right_walkable ? 0 : down_walkable  ? 3 : 2;
    else if (!right_walkable) xx = down_walkable  ? 3 : left_walkable  ? 2 : 1;
    else                      xx = left_walkable  ? 2 : up_walkable    ? 1 : 0;

    //////
    

    contour_helper.reset(contour_buffer);

    int move_dir_at_start = move_dirs[xx];
    int start_x = x;
    int start_y = y;

    int n_wall_hits_in_a_row = 0;

    contour_exist_map.pixels[current] = contour_exist_map.write_index;
    
    if (contour_settings == Contour_Settings.ONLY_CORNERS) {

        while (true) {     
          
            if (threshold_checker.is_walkable(pixels[current + check_dirs[xx]], threshold)) {

                contour_helper.add_to_contour(contour_buffer, current, x, y);
                

                xx += 1;
                xx &= 0x3;

                current += move_dirs[xx];
                x += move_change_x[xx];
                y += move_change_y[xx];
                contour_exist_map.pixels[current] = contour_exist_map.write_index;

            }
            else if (threshold_checker.is_walkable(pixels[current + move_dirs[xx]], threshold)) {
                n_wall_hits_in_a_row = 0;

                current += move_dirs[xx];
                x += move_change_x[xx];
                y += move_change_y[xx];
                contour_exist_map.pixels[current] = contour_exist_map.write_index;

            }
            else {
                n_wall_hits_in_a_row += 1;

                if (n_wall_hits_in_a_row != 2) {
                    contour_helper.add_to_contour(contour_buffer, current, x, y);
                    contour_exist_map.pixels[current] = contour_exist_map.write_index;
                }
                
                xx -= 1;
                xx &= 0x3;
            }
            if (move_dirs[xx] == move_dir_at_start && x == start_x && y == start_y) {
                break;
            }
        }
    }
}



static public <T> void walk_contour_arrays(Contour_Exist_Map contour_exist_map, int[] pixels, int w, int h, int x, int y, Contour_Settings contour_settings, Contour_Buffer<T> contour_buffer, Threshold_Checker threshold_checker, float threshold, Contour_Helper<T> contour_helper) {

    final int LEFT  = -1;
    final int RIGHT =  1;
    final int UP    = -w;
    final int DOWN  =  w;

    int current = y * w + x;

    int[] move_dirs =     {RIGHT, UP,  LEFT, DOWN};
    int[] check_dirs =    {UP,    LEFT, DOWN, RIGHT};
    int[] move_change_x = {1,     0,    -1,     0};
    int[] move_change_y = {0,     -1,     0,    1};

    int xx;

    //
    // set start direction
    //
    boolean left_walkable  = threshold_checker.is_walkable(pixels[current + LEFT], threshold);
    boolean right_walkable = threshold_checker.is_walkable(pixels[current + RIGHT], threshold);
    boolean up_walkable    = threshold_checker.is_walkable(pixels[current + UP], threshold);
    boolean down_walkable  = threshold_checker.is_walkable(pixels[current + DOWN], threshold);

    if (!(left_walkable || right_walkable || up_walkable || down_walkable)) {
        return; // single pixel
    }

    if (!left_walkable)       xx = up_walkable    ? 1 : right_walkable ? 0 : 3;
    else if (!up_walkable)    xx = right_walkable ? 0 : down_walkable  ? 3 : 2;
    else if (!right_walkable) xx = down_walkable  ? 3 : left_walkable  ? 2 : 1;
    else                      xx = left_walkable  ? 2 : up_walkable    ? 1 : 0;

    //////
    

    contour_helper.reset(contour_buffer);

    int move_dir_at_start = move_dirs[xx];
    int start_x = x;
    int start_y = y;

    int n_wall_hits_in_a_row = 0;

    contour_exist_map.pixels[current] = contour_exist_map.write_index;
    
    if (contour_settings == Contour_Settings.ONLY_CORNERS) {

        while (true) {     
          
            if (threshold_checker.is_walkable(pixels[current + check_dirs[xx]], threshold)) {

                contour_helper.add_to_contour(contour_buffer, current, x, y);

                xx += 1;
                xx &= 0x3;

                current += move_dirs[xx];
                x += move_change_x[xx];
                y += move_change_y[xx];

                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else if (threshold_checker.is_walkable(pixels[current + move_dirs[xx]], threshold)) {
                n_wall_hits_in_a_row = 0;

                current += move_dirs[xx];
                x += move_change_x[xx];
                y += move_change_y[xx];

                contour_exist_map.pixels[current] = contour_exist_map.write_index;

            }
            else {
                n_wall_hits_in_a_row += 1;

                if (n_wall_hits_in_a_row != 2) {
                    contour_helper.add_to_contour(contour_buffer, current, x, y);
                }
                
                xx -= 1;
                xx &= 0x3;
            }
            if (move_dirs[xx] == move_dir_at_start && x == start_x && y == start_y) {
                break;
            }
        }
    }
}


// returns true if the blob is new
// ctx can be null
static public <T> void walk_contour_faster(Contour_Exist_Map contour_exist_map, int[] pixels, int w, int h, int x, int y, Contour_Settings contour_settings, Contour_Buffer<T> contour_buffer, Threshold_Checker threshold_checker, float threshold, Contour_Helper<T> contour_helper) {

    final int LEFT  = -1;
    final int RIGHT =  1;
    final int UP    = -w;
    final int DOWN  =  w;

    int current = y * w + x;

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
    else                      move_dir = left_walkable  ? LEFT  : up_walkable    ? UP    : RIGHT;

    //////
    
    if      (move_dir == RIGHT) { check_dir = UP;    x_mov_change =  1; y_mov_change =  0;}
    else if (move_dir == DOWN)  { check_dir = RIGHT; x_mov_change =  0; y_mov_change =  1;}
    else if (move_dir == LEFT)  { check_dir = DOWN;  x_mov_change = -1; y_mov_change =  0;}
    else  /*             UP */  { check_dir = LEFT;  x_mov_change =  0; y_mov_change = -1;}

    contour_helper.reset(contour_buffer);

    int move_dir_at_start = move_dir;
    int start_x = x;
    int start_y = y;

    int n_wall_hits_in_a_row = 0;

    contour_exist_map.pixels[current] = contour_exist_map.write_index;
    
    if (contour_settings == Contour_Settings.ONLY_CORNERS) {

        while (true) {     
          
            //
            // move in check direction if possible
            //
            if (threshold_checker.is_walkable(pixels[current + check_dir], threshold)) {

                contour_helper.add_to_contour(contour_buffer, current, x, y);

                //
                // change the direction
                //
                if (check_dir == RIGHT) { // move dir was down
                    current += check_dir;
                    move_dir  = RIGHT; 
                    check_dir = UP;
                    x_mov_change = 1;
                    y_mov_change = 0;
                    x += 1;
                    contour_exist_map.pixels[current] = contour_exist_map.write_index;
                }
                else if (check_dir == DOWN)  { // move dir was left
                    current += check_dir;
                    move_dir  = DOWN;  
                    check_dir = RIGHT;
                    x_mov_change = 0;
                    y_mov_change = 1;
                    y += 1;
                    contour_exist_map.pixels[current] = contour_exist_map.write_index;
                }
                else if (check_dir == LEFT)  { // move dir was up
                    current += check_dir;
                    move_dir  = LEFT;  
                    check_dir = DOWN;
                    x_mov_change = -1;
                    y_mov_change = 0;
                    x -= 1;
                    contour_exist_map.pixels[current] = contour_exist_map.write_index;
                }
                else { // if (check_dir == UP)    { // move dir was right
                    current += check_dir;
                    move_dir  = UP;    
                    check_dir = LEFT;
                    x_mov_change = 0;
                    y_mov_change = -1;
                    y -= 1;
                    contour_exist_map.pixels[current] = contour_exist_map.write_index;
                }
            }
            else if (threshold_checker.is_walkable(pixels[current + move_dir], threshold)) {
                //
                // move in current move direction if possible
                //
                n_wall_hits_in_a_row = 0;

                current += move_dir;
                x += x_mov_change;
                y += y_mov_change;
                contour_exist_map.pixels[current] = contour_exist_map.write_index;
            }
            else {
                //
                // we have hit a wall
                //
                n_wall_hits_in_a_row += 1;

                if (n_wall_hits_in_a_row != 2) {
                    contour_helper.add_to_contour(contour_buffer, current, x, y);
                }
                 
                if (move_dir == UP) {    
                    move_dir  = RIGHT; 
                    check_dir = UP;
                    x_mov_change = 1;
                    y_mov_change = 0;
                }
                else if (move_dir == RIGHT) { 
                    move_dir  = DOWN;  
                    check_dir = RIGHT;
                    x_mov_change = 0;
                    y_mov_change = 1;
                }
                else if (move_dir == DOWN) {  
                    move_dir  = LEFT;  
                    check_dir = DOWN;
                    x_mov_change = -1;
                    y_mov_change =  0;
                }
                else { //if (move_dir == LEFT) {  
                    move_dir  = UP;    
                    check_dir = LEFT;
                    x_mov_change =  0;
                    y_mov_change = -1;
                }    
            }
            if (move_dir == move_dir_at_start && x == start_x && y == start_y) {
                break;
            }
        }
    }
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}

