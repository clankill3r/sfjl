/** SFJL_K_D_Tree - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_Octree_Example.java

*/
package sfjl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import static java.lang.Math.*;
import static sfjl.SFJL_Math.*;
import static sfjl.SFJL_Doeke.*;

public class SFJL_K_D_Tree {
     private SFJL_K_D_Tree() {}
//           SFJL_K_D_Tree

static public class K_D_Tree<T> {
    public final int DIMENSIONS;
    public K_D_Tree_Node<T> root;
    public Get_Value<T>[] d_get_value;
    public Comparator<T>[] d_comparators;

    public K_D_Tree(int DIMENSION, Get_Value<T>[] d_get_value) {
        this.DIMENSIONS = DIMENSION;
        this.d_get_value = d_get_value;
        _make_comparotors(this);
    }
}

static public class K_D_Tree_Node<T> {
    public T                point;
    public K_D_Tree_Node<T> left;
    public K_D_Tree_Node<T> right;
}

public interface Get_Value<T> {
    public float value(T t);
}

static public <T> void _make_comparotors(K_D_Tree<T> tree) {
    tree.d_comparators = new Comparator[tree.DIMENSIONS];
    for (int i = 0; i < tree.DIMENSIONS; i++) {
        final int fi = i;
        tree.d_comparators[i] = (a, b)-> {
            float val_a = tree.d_get_value[fi].value(a);
            float val_b = tree.d_get_value[fi].value(b);
            if (val_a < val_b) return -1;
            if (val_a > val_b) return  1;
            return 0;
        };
    }
}


static public <T> void add_all(K_D_Tree<T> tree, List<T> to_add) {
    assert tree.root == null;
    tree.root = add_all(tree, to_add, 0);
}


static public <T> K_D_Tree_Node<T> add_all(K_D_Tree<T> tree, List<T> to_add, int dimension) {

    if (dimension == tree.DIMENSIONS) dimension = 0;

    Collections.sort(to_add, tree.d_comparators[dimension]);

    int median = to_add.size()/2;
    T median_point = to_add.get(median);

    List<T> to_add_left = to_add.subList(0, median);
    List<T> to_add_right = to_add.subList(median + 1, to_add.size());

    K_D_Tree_Node<T> n = new K_D_Tree_Node<T>();
    n.point = median_point;
    if (to_add_left.size() > 0) {
        n.left  = add_all(tree, to_add_left, dimension+1);
    }
    if (to_add_right.size() > 0) {
        n.right = add_all(tree, to_add_right, dimension+1);
    }

    return n;
}


static public <T> T get_closest(K_D_Tree<T> tree, float... values) {
    return get_closest(tree, tree.root, 0, values);
}

static public <T> T get_closest(K_D_Tree<T> tree, K_D_Tree_Node<T> node, int dimension, float... values) {
    if (node == null) return null;
    if (dimension == tree.DIMENSIONS) dimension = 0;

    K_D_Tree_Node<T> next_branch = null;
    K_D_Tree_Node<T> other_branch = null;

    if (values[dimension] < tree.d_get_value[dimension].value(node.point)) {
        next_branch = node.left;
        other_branch = node.right;
    }
    else {
        next_branch = node.right;
        other_branch = node.left;
    }

    T temp = get_closest(tree, next_branch, dimension+1, values);
    T best = _closest(tree, node.point, temp, values);
    
    float radius_sq = _dist_sq(tree, best, values);
    float dist = values[dimension] - tree.d_get_value[dimension].value(node.point);

    if (radius_sq > dist * dist) {
        temp = get_closest(tree, other_branch, dimension+1, values);
        best = _closest(tree, best, temp, values);
    }
    return best;
}


static public <T> T _closest(K_D_Tree<T> tree, T a, T b, float... values) {
    if (a == null) return b;
    if (b == null) return a;
    float dist_a = _dist_sq(tree, a, values);
    float dist_b = _dist_sq(tree, b, values);
    if (dist_a < dist_b) return a;
    return b;
}


static public <T> float _dist_sq(K_D_Tree<T> tree, T t, float... values) {
    float d = 0;
    for (int i = 0; i < tree.DIMENSIONS; i++) {
        float v = values[i]-tree.d_get_value[i].value(t);
        d += v*v;
    }
    return d;
}

}
/**
revision history:
    0.50  (2023-01-04) first numbered version

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