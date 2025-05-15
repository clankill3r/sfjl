/** SFJL_Blobscanner - v0.51
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_Blobscanner_Example.java
        SFJL_Blobscanner_Example_Heightmap.java

*/
package sfjl;
import static java.lang.Math.*;
import java.util.Arrays;

import sfjl.SFJL_Math.AABB;
import sfjl.SFJL_Math.Vec2;
public class SFJL_Blobscanner {
     private SFJL_Blobscanner() {}
//---------- SFJL_Blobscanner
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public enum Border_Handling {
    DONT_BORDER,
    REPLACE_BORDER,
    REPLACE_BORDER_AND_RESTORE_BORDER
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
static public class Contour_Exist_Map {
    public byte[] pixels = new byte[0];
    public byte write_index;
    public int width;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Contour_Buffer<T> {
    public T   contour;
    public int contour_length;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public interface Add_To_Contour_Buffer<T> {
    void exe(Contour_Buffer<T> contour_buffer, int index, int x, int y);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public interface Process_Contour<T> {
    void exe(Contour_Buffer<T> contour_buffer);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public interface Reset_Contour_Buffer<T> {
    void exe(Contour_Buffer<T> contour_buffer);
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
public enum Contour_Store_Settings {
    ALL_PIXELS,
    ONLY_CORNERS,
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Contour_Context<T> {
    public Contour_Store_Settings store_settings = Contour_Store_Settings.ONLY_CORNERS;
    public Contour_Exist_Map contour_exist_map = new Contour_Exist_Map();
    public Contour_Buffer<T> buffer = new Contour_Buffer<>();
    public Reset_Contour_Buffer<T> reset_contour_buffer;
    public Add_To_Contour_Buffer<T> add_to_contour_buffer;
}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public class Blobscanner_Context<T> {
    public Border_Handling border_handling = Border_Handling.REPLACE_BORDER_AND_RESTORE_BORDER;
    public Border_Backup border_backup = new Border_Backup();
    public int border_color;
    public AABB roi = new AABB(0, 0, 1, 1);
    public Threshold_Checker threshold_checker;
    public float threshold;
    public int y_increment = 1;
    public Contour_Context<T> contour_ctx;
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
static public void find_blobs_vec2(Blobscanner_Context<Vec2[]> ctx, int[] pixels, int w, int h, Process_Contour<Vec2[]> process_contour) {

    Contour_Buffer<Vec2[]> contour_buffer = ctx.contour_ctx.buffer;

    if (contour_buffer.contour == null) {
        contour_buffer.contour = new Vec2[pixels.length/2];
        for (int i = 0; i < pixels.length/2; i++) {
            contour_buffer.contour[i] = new Vec2();
        }
    }
    else if (contour_buffer.contour.length < pixels.length/2) {
        int old_length = contour_buffer.contour.length;
        int new_length = pixels.length/2;
        contour_buffer.contour = Arrays.copyOf(contour_buffer.contour, new_length);
        for (int i = old_length; i < new_length; i++) {
            contour_buffer.contour[i] = new Vec2();
        }
    }

    _find_blobs(ctx, pixels, w, h, process_contour);

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public void find_blobs_index(Blobscanner_Context<int[]> ctx, int[] pixels, int w, int h,Process_Contour<int[]> process_contour) {

    Contour_Buffer<int[]> contour_buffer = ctx.contour_ctx.buffer;

    if (contour_buffer.contour == null) {
        contour_buffer.contour = new int[pixels.length/2];
    }
    else if (contour_buffer.contour.length < pixels.length/2) {
        contour_buffer.contour = Arrays.copyOf(contour_buffer.contour, pixels.length/2);
    }

    _find_blobs(ctx, pixels, w, h, process_contour);

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public <T> void _find_blobs(Blobscanner_Context<T> ctx, int[] pixels, int w, int h, Process_Contour<T> process_contour) {
    
    make_roi_in_pixels(ctx.roi, w, h);

    if (ctx.border_handling == Border_Handling.REPLACE_BORDER || ctx.border_handling == Border_Handling.REPLACE_BORDER_AND_RESTORE_BORDER) {
        prepare_border(pixels, w, h, ctx.roi, ctx.border_color, ctx.border_handling, ctx.border_backup);
    }

    if (ctx.contour_ctx.contour_exist_map.pixels.length < pixels.length) {
        ctx.contour_ctx.contour_exist_map.pixels = new byte[pixels.length];
        ctx.contour_ctx.contour_exist_map.write_index = 1;
    }
    ctx.contour_ctx.contour_exist_map.width = w;

    //
    // Scan in lines untill we hit and edge, then walk the contour
    //
    int x1 = (int) ctx.roi.x1;
    int y1 = (int) ctx.roi.y1;
    int x2 = (int) ctx.roi.x2;
    int y2 = (int) ctx.roi.y2;

    if (y1 == 0) y1 = 1; // nothing is walkable on the edge

    for (int y = y1; y < y2; y += ctx.y_increment) {
        boolean prev_is_walkable = false;

        for (int x = x1; x < x2; x++) {
            int index = y * w + x;
            boolean current_is_walkable = ctx.threshold_checker.is_walkable(pixels[index], ctx.threshold);

            if (current_is_walkable && !prev_is_walkable) {
                boolean has_not_been_walked = ctx.contour_ctx.contour_exist_map.pixels[index] != ctx.contour_ctx.contour_exist_map.write_index;
                if (has_not_been_walked) {
                    walk_contour(ctx, pixels, w, h, x, y, ctx.threshold_checker, ctx.threshold, false);
                    process_contour.exe(ctx.contour_ctx.buffer);
                }
            }
            prev_is_walkable = current_is_walkable;
        }
    }

    if (ctx.border_handling == Border_Handling.REPLACE_BORDER_AND_RESTORE_BORDER) {
        restore_border_from_backup(pixels, w, h, ctx.roi, ctx.border_backup);
    }
    ctx.contour_ctx.contour_exist_map.write_index += 1;
    // reset on overflow
    if (ctx.contour_ctx.contour_exist_map.write_index == 0)  {
        for (int i = 0; i < ctx.contour_ctx.contour_exist_map.pixels.length; i++) {
            ctx.contour_ctx.contour_exist_map.pixels[i] = 0;
        }
        ctx.contour_ctx.contour_exist_map.write_index = 1;
    }

}
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public <T> void walk_contour(Blobscanner_Context<T> ctx, int[] pixels, int w, int h, int x, int y, Threshold_Checker threshold_checker, float threshold, boolean ccw) {

    var contour_ctx = ctx.contour_ctx;
    var contour_exist_map = contour_ctx.contour_exist_map;

    contour_ctx.reset_contour_buffer.exe(contour_ctx.buffer);

    final int LEFT  = -1;
    final int RIGHT =  1;
    final int UP    = -w;
    final int DOWN  =  w;

    int current = y * w + x;

    boolean left_walkable  = threshold_checker.is_walkable(pixels[current + LEFT], threshold);
    boolean right_walkable = threshold_checker.is_walkable(pixels[current + RIGHT], threshold);
    boolean up_walkable    = threshold_checker.is_walkable(pixels[current + UP], threshold);
    boolean down_walkable  = threshold_checker.is_walkable(pixels[current + DOWN], threshold);

    if (!(left_walkable || right_walkable || up_walkable || down_walkable)) {
        return; // single pixel
    }

    int[] move_dirs;
    int[] move_change_x;
    int[] move_change_y;
    int[] check_dirs;

    int dir_index; 

    if (!ccw) {
        move_dirs     = new int[] {RIGHT, UP,  LEFT, DOWN};
        move_change_x = new int[] {1,      0,    -1,    0};
        move_change_y = new int[] {0,     -1,     0,    1};
        check_dirs    = new int[] {UP,   LEFT, DOWN, RIGHT};

        if (!left_walkable)       dir_index = up_walkable    ? 1 : right_walkable ? 0 : 3;
        else if (!up_walkable)    dir_index = right_walkable ? 0 : down_walkable  ? 3 : 2;
        else if (!right_walkable) dir_index = down_walkable  ? 3 : left_walkable  ? 2 : 1;
        else                      dir_index = left_walkable  ? 2 : up_walkable    ? 1 : 0;

    }
    else {
        move_dirs =     new int[]{RIGHT, DOWN,  LEFT, UP};
        move_change_x = new int[]{1,     0,    -1,    0};
        move_change_y = new int[]{0,     1,     0,   -1};
        check_dirs =    new int[]{DOWN,  LEFT,  UP,   RIGHT};

        if (!left_walkable)       dir_index = down_walkable  ? 1 : right_walkable ? 0 : 3;
        else if (!down_walkable)  dir_index = right_walkable ? 0 : up_walkable    ? 3 : 2;
        else if (!right_walkable) dir_index = up_walkable    ? 3 : left_walkable  ? 2 : 1;
        else                      dir_index = left_walkable  ? 2 : down_walkable  ? 1 : 0;
    }

    int move_dir_at_start = move_dirs[dir_index];
    int start_x = x;
    int start_y = y;
    int n_wall_hits_in_a_row = 0;
    contour_exist_map.pixels[current] = contour_exist_map.write_index;

    final boolean ALL_PIXELS   = contour_ctx.store_settings == Contour_Store_Settings.ALL_PIXELS;
    final boolean ONLY_CORNERS = contour_ctx.store_settings == Contour_Store_Settings.ONLY_CORNERS;

    while (true) {     
        
        if (threshold_checker.is_walkable(pixels[current + check_dirs[dir_index]], threshold)) {
            if (ONLY_CORNERS) contour_ctx.add_to_contour_buffer.exe(contour_ctx.buffer, current, x, y);

            dir_index += 1;
            dir_index &= 0x3;
            current += move_dirs[dir_index];
            x += move_change_x[dir_index];
            y += move_change_y[dir_index];
            contour_exist_map.pixels[current] = contour_exist_map.write_index;

            if (ALL_PIXELS) contour_ctx.add_to_contour_buffer.exe(contour_ctx.buffer, current, x, y);
        }
        else if (threshold_checker.is_walkable(pixels[current + move_dirs[dir_index]], threshold)) {
            n_wall_hits_in_a_row = 0;
            current += move_dirs[dir_index];
            x += move_change_x[dir_index];
            y += move_change_y[dir_index];
            contour_exist_map.pixels[current] = contour_exist_map.write_index;

            if (ALL_PIXELS) contour_ctx.add_to_contour_buffer.exe(contour_ctx.buffer, current, x, y);
        }
        else {
            n_wall_hits_in_a_row += 1;

            if (ONLY_CORNERS && n_wall_hits_in_a_row != 2) {
                contour_ctx.add_to_contour_buffer.exe(contour_ctx.buffer, current, x, y);
            }
            dir_index -= 1;
            dir_index &= 0x3;
        }
        if (x == start_x && y == start_y && move_dirs[dir_index] == move_dir_at_start) {
            break;
        }
    }
}
//
// Default implementations
//
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public Add_To_Contour_Buffer<Vec2[]> add_to_contour_vec2 = (Contour_Buffer<Vec2[]> contour_buffer, int index, int x, int y)-> {
    contour_buffer.contour[contour_buffer.contour_length].x = x;
    contour_buffer.contour[contour_buffer.contour_length].y = y;
    contour_buffer.contour_length += 1;
};
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public Reset_Contour_Buffer<Vec2[]> reset_contour_buffer_vec2 = (Contour_Buffer<Vec2[]> contour_buffer)-> {
    contour_buffer.contour_length = 0;
};
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public Add_To_Contour_Buffer<int[]> add_to_contour_index = (Contour_Buffer<int[]> contour_buffer, int index, int x, int y)-> {
    contour_buffer.contour[contour_buffer.contour_length] = index;
    contour_buffer.contour_length += 1;
};
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
static public Reset_Contour_Buffer<int[]> reset_contour_buffer_index = (Contour_Buffer<int[]> contour_buffer)-> {
    contour_buffer.contour_length = 0;
};
// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

}
/**
revision history:

    0.51  (2025-05-12) cw/ccw option for walk_contour
                       + added Contour_Context
                       + minor improvements
    0.50  (2022-02-10) first numbered version

*/

/**
------------------------------------------------------------------------------
This software is available under 2 licenses -- choose whichever you prefer.
------------------------------------------------------------------------------
ALTERNATIVE A - MIT License
Copyright (c) 2020 Doeke Wartena
Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
------------------------------------------------------------------------------
ALTERNATIVE B - Public Domain (www.unlicense.org)
This is free and unencumbered software released into the public domain.
Anyone is free to copy, modify, publish, use, compile, sell, or distribute this
software, either in source code form or as a compiled binary, for any purpose,
commercial or non-commercial, and by any means.
In jurisdictions that recognize copyright laws, the author or authors of this
software dedicate any and all copyright interest in the software to the public
domain. We make this dedication for the benefit of the public at large and to
the detriment of our heirs and successors. We intend this dedication to be an
overt act of relinquishment in perpetuity of all present and future rights to
this software under copyright law.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
------------------------------------------------------------------------------
*/