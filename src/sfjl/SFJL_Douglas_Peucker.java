/** SFJL_Douglas_Peucker - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_Douglas_Peucker_Example.java

*/
package sfjl;


public class SFJL_Douglas_Peucker {
     private SFJL_Douglas_Peucker() {}
    

static public class Path_Buffer {
    public float[][] points;
    public int length;
    public Path_Buffer(int MAX_POINTS, int dimensions) {this.points = new float[MAX_POINTS][dimensions];}
}


static public void distance_based_cleanup_2d(float[][] points, int length, float threshold, Path_Buffer result) {

    if (points.length < 3) {
        for (int i = 0; i < length; i++) {
            result.points[i][0] = points[i][0];
            result.points[i][1] = points[i][1];
        }
        result.length = length;
        return;
    }

    float threshold_sq = sq(threshold);

    result.points[0][0] = points[0][0];
    result.points[0][1] = points[0][1];
    result.length = 1;

    float[] prev = points[0];

    for (int i = 1; i < points.length; i++) {
        float[] current = points[i];
        float d = dist_sq(current[0], current[1], prev[0], prev[1]);
        if (d > threshold_sq) {
            result.points[result.length][0] = current[0];
            result.points[result.length][1] = current[1];
            result.length++;
            prev = current;
        }
    }

}


static public final void douglas_peucker_2d(float[][] points, int length, float threshold, boolean fast, Path_Buffer result) {

    // copy input and return
    if (threshold == 0) {

        for (int i = 0; i < length; i++) {
            result.points[i][0] = points[i][0];
            result.points[i][1] = points[i][1];
        }
        result.length = length;
        return;
    }

    if (fast) {
        distance_based_cleanup_2d(points, length, threshold, result);
        points = result.points;
        length = result.length;
    }


    result.length = 0;
    // first point
    result.points[0][0] = points[0][0];
    result.points[0][1] = points[0][1];
    result.length++;
    _douglas_peucker_2d_step(points, 0, length-1, sq(threshold), result);
    // last point
    result.points[result.length][0] = points[length-1][0];
    result.points[result.length][1] = points[length-1][1];
    result.length++;
}


static public final void _douglas_peucker_2d_step(float[][] points, int first, int last, float threshold_sq, Path_Buffer result) {

    float max_dist_sq = 0;
    int index = -1;

    for (int i = first + 1; i < last; i++) {
        float dist_sq = dist_point_to_line_sq(points[i][0],     points[i][1],
                                              points[first][0], points[first][1],
                                              points[last][0],  points[last][1]);

        if (dist_sq > max_dist_sq) {
            index = i;
            max_dist_sq = dist_sq;
        }
    }

    if (index == -1) return;

    if (max_dist_sq > threshold_sq) {
        if (index - first > 1) _douglas_peucker_2d_step(points, first, index, threshold_sq, result);
        result.points[result.length][0] = points[index][0];
        result.points[result.length][1] = points[index][1];
        result.length++;
        if (last - index > 1) _douglas_peucker_2d_step(points, index, last, threshold_sq, result);
    }
    
}


static public void distance_based_cleanup_3d(float[][] points, int length, float threshold, Path_Buffer result) {

    
    if (points.length < 3) {
        for (int i = 0; i < length; i++) {
            result.points[i][0] = points[i][0];
            result.points[i][1] = points[i][1];
            result.points[i][2] = points[i][2];
        }
        result.length = length;
        return;
    }
    
    float threshold_sq = sq(threshold);

    result.points[0][0] = points[0][0];
    result.points[0][1] = points[0][1];
    result.points[0][2] = points[0][2];
    result.length = 1;

    float[] prev = points[0];

    for (int i = 1; i < points.length; i++) {
        float[] current = points[i];
        float d = dist_sq(current[0], current[1], current[2], prev[0], prev[1], prev[2]);
        if (d > threshold_sq) {
            result.points[result.length][0] = current[0];
            result.points[result.length][1] = current[1];
            result.points[result.length][2] = current[2];
            result.length++;
            prev = current;
        }
    }

}


static public final void douglas_peucker_3d(float[][] points, int length, float threshold, boolean fast, Path_Buffer result) {

    if (threshold == 0) {

        for (int i = 0; i < length; i++) {
            result.points[i][0] = points[i][0];
            result.points[i][1] = points[i][1];
            result.points[i][2] = points[i][2];
        }
        result.length = length;
        return;
    }

    if (fast) {
        distance_based_cleanup_3d(points, length, threshold, result);
        points = result.points;
        length = result.length;
    }


    result.length = 0;
    // first point
    result.points[0][0] = points[0][0];
    result.points[0][1] = points[0][1];
    result.points[0][2] = points[0][2];
    result.length++;
    _douglas_peucker_3d_step(points, 0, length-1, sq(threshold), result);
    // last point
    result.points[result.length][0] = points[length-1][0];
    result.points[result.length][1] = points[length-1][1];
    result.points[result.length][2] = points[length-1][2];
    result.length++;

}


static public final void _douglas_peucker_3d_step(float[][] points, int first, int last, float threshold_sq, Path_Buffer result) {

    float max_dist_sq = 0;
    int index = -1;

    for (int i = first + 1; i < last; i++) {
        float dist_sq = dist_point_to_line_sq(points[i][0],     points[i][1],     points[i][2],
                                              points[first][0], points[first][1], points[first][2],  
                                              points[last][0],  points[last][1],  points[last][2]);

        if (dist_sq > max_dist_sq) {
            index = i;
            max_dist_sq = dist_sq;
        }
    }

    if (index == -1) return;

    if (max_dist_sq > threshold_sq) {
        if (index - first > 1) _douglas_peucker_3d_step(points, first, index, threshold_sq, result);
        result.points[result.length][0] = points[index][0];
        result.points[result.length][1] = points[index][1];
        result.points[result.length][2] = points[index][2];
        result.length++;
        if (last - index > 1) _douglas_peucker_3d_step(points, index, last, threshold_sq, result);
    }
    
}



//
// Math
//

static public final float dist_point_to_line_sq(float px, float py, float lx1, float ly1, float lx2, float ly2) {
    float line_dist = dist_sq(lx1, ly1, lx2, ly2);
    if (line_dist == 0) return dist_sq(px, py, lx1, ly1);
    float t = ((px - lx1) * (lx2 - lx1) + (py - ly1) * (ly2 - ly1)) / line_dist;
    if      (t < 0) t = 0;
    else if (t > 1) t = 1;
    return dist_sq(px, py, lx1 + t * (lx2 - lx1), ly1 + t * (ly2 - ly1));
}

static public final float dist_sq(float x1, float y1, float x2, float y2) {
    return sq(x1 - x2) + sq(y1 - y2);
}

static public final float sq(float n) {
    return n*n;
}

// Note(doeke): adapted from http://stackoverflow.com/a/1501725/1022707
static public final float dist_point_to_line_sq(float px, float py, float pz, float lx1, float ly1, float lz1, float lx2, float ly2, float lz2) {
    float line_dist_sq = dist_sq(lx1, ly1, lz1, lx2, ly2, lz2);
    if (line_dist_sq == 0) return dist_sq(px, py, pz, lx1, ly1, lz1);
    float t = ((px - lx1) * (lx2 - lx1) + (py - ly1) * (ly2 - ly1) + (pz - lz1) * (lz2 - lz1)) / line_dist_sq;
    if      (t < 0) t = 0;
    else if (t > 1) t = 1;
    return dist_sq(px, py, pz, lx1 + t * (lx2 - lx1), ly1 + t * (ly2 - ly1), lz1 + t * (lz2 - lz1));
}

static public final float dist_sq(float x1, float y1, float z1, float x2, float y2, float z2) {
    return sq(x1 - x2) + sq(y1 - y2) + sq(z1 - z2);
}

}
/**
revision history:

   0.50  (2020-08-12) first numbered version

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