/** SFJL_N_Tree - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_N_Tree_Example.java

*/
package sfjl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import static java.lang.Math.*;
import static sfjl.SFJL_Math.*;
import static sfjl.SFJL_Doeke.*;

public class SFJL_N_Tree {
     private SFJL_N_Tree() {}
//           SFJL_N_Tree

// don't change the order!
public final static int TLF_A = 0;
public final static int TRF_A = 1;
public final static int BLF_A = 2;
public final static int BRF_A = 3;
public final static int TLB_A = 4;
public final static int TRB_A = 5;
public final static int BLB_A = 6;
public final static int BRB_A = 7;
public final static int TLF_B = 8;
public final static int TRF_B = 9;
public final static int BLF_B = 10;
public final static int BRF_B = 11;
public final static int TLB_B = 12;
public final static int TRB_B = 13;
public final static int BLB_B = 14;
public final static int BRB_B = 15;


public final static int _HARD_MAX_DEPTH = 32; // We could make this a variable, but this seems reasonable. 

public interface X<T> {
    public float x(T t);
}

public interface Y<T> {
    public float y(T t);
}

public interface Z<T> {
    public float z(T t);
}

public interface W<T> {
    public float w(T t);
}


static public class N_Tree<T> implements Iterable<T> {
    public int max_items;
    public float merge_threshold = 0.75f;
    public X<T> x;
    public Y<T> y;
    public Z<T> z;
    public W<T> w;
    public int size = 0;
    public N_Tree_Node<T> root;
    public Node_Allocator<T> allocator = new Simple_Node_Allocator<>();
    public int[] n_quads_at_depth_lookup = new int[_HARD_MAX_DEPTH + 1];
    public int[] n_leafs_at_depth_lookup = new int[_HARD_MAX_DEPTH + 1];
    public boolean max_depth_reached = false;

    public N_Tree(X<T> x, Y<T> y, Z<T> z, W<T> w, int max_items, float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        assert (max_items > 0);
        this.max_items = max_items;
        root = new N_Tree_Node<>();
        _set(root, null, x1, y1, z1, w1, x2, y2, z2, w2);
        root.n_tree = this;
        root.data = new ArrayList<>(max_items);
    }

    @Override
    public Iterator<T> iterator() {
        return SFJL_N_Tree.iterator(root);
    }
}

static public class N_Tree_Node<T> implements Iterable<T> {
    public N_Tree<T> n_tree;
    public N_Tree_Node<T> parent;
    @SuppressWarnings("unchecked")
    public N_Tree_Node<T>[] children = new N_Tree_Node[16];
    public ArrayList<T> data;
    public float x1;
    public float y1;
    public float z1;
    public float w1;
    public float x2;
    public float y2;
    public float z2;
    public float w2;
    public int depth;

    @Override
    public Iterator<T> iterator() {
        return SFJL_N_Tree.iterator(this);
    }
}


static public <T> float x(N_Tree_Node<T> n, T t) {
    return n.n_tree.x.x(t);
}


static public <T> float y(N_Tree_Node<T> n, T t) {
    return n.n_tree.y.y(t);
}


static public <T> float z(N_Tree_Node<T> n, T t) {
    return n.n_tree.z.z(t);
}

static public <T> float w(N_Tree_Node<T> n, T t) {
    return n.n_tree.w.w(t);
}


static public <T> float center_x(N_Tree_Node<T> n) {
    return n.x1 + (n.x2 - n.x1) / 2;
}


static public <T> float center_y(N_Tree_Node<T> n) {
    return n.y1 + (n.y2 - n.y1) / 2;
}


static public <T> float center_z(N_Tree_Node<T> n) {
    return n.z1 + (n.z2 - n.z1) / 2;
}


static public <T> float center_w(N_Tree_Node<T> n) {
    return n.w1 + (n.w2 - n.w1) / 2;
}


static public <T> boolean has_children(N_Tree_Node<T> n) {
    return n.children[0] != null;
}


static public <T> boolean is_root(N_Tree_Node<T> n) {
    return n.parent == null;
}


static public <T> int get_optimal_index(N_Tree_Node<T> n, float x, float y, float z, float w) {

    if (y < center_y(n)) {
        if (x < center_x(n)) {
            if (z < center_z(n)) {
                return w < center_w(n) ? TLF_A : TLF_B; 
            }
            else {
                return w < center_w(n) ? TLB_A : TLB_B; 
            }
        }
        else {
            if (z < center_z(n)) {
                return w < center_w(n) ? TRF_A : TRF_B;
            }
            else {
                return w < center_w(n) ? TRB_A : TRB_B;
            }
        }
    }
    else {
        if (x < center_x(n)) {
            if (z < center_z(n)) {
                return w < center_w(n) ? BLF_A : BLF_B;
            }
            else {
                return w < center_w(n) ? BLB_A : BLB_B;
            }
        }
        else {
            if (z < center_z(n)) {
                return w < center_w(n) ? BRF_A : BRF_B;
            }
            else {
                return w < center_w(n) ? BRB_A : BRB_B;
            }
        }
    }
}


static public <T> N_Tree_Node<T> get_leaf(N_Tree_Node<T> qtn, float x, float y, float z, float w) {

    if (qtn.depth == 0) {
        if (x < qtn.x1 || x > qtn.x2 || y < qtn.y1 || y > qtn.y2 || z < qtn.z1 || z > qtn.z2 || w < qtn.w1 || w > qtn.w2) {
            return null;
        }
    }

    N_Tree_Node<T> current = qtn;
    while (has_children(current)) {
        int where = get_optimal_index(current, x, y, z, w);
        current = current.children[where];
    }
    return current;
}


static public <T> N_Tree_Node<T> add(N_Tree<T> qt, T t) {
    return add(qt.root, t);
}


static public <T> N_Tree_Node<T> add(N_Tree_Node<T> qtn, T t) {
    return add(qtn, t, x(qtn, t), y(qtn, t), z(qtn, t), w(qtn, t));
}


static public <T> N_Tree_Node<T> add(N_Tree_Node<T> qtn, T t, float x, float y, float z, float w) {

    qtn = get_leaf(qtn, x, y, z, w);

    if (qtn != null) {
        qtn.data.add(t);
        qtn.n_tree.size++;
        if (qtn.data.size() > qtn.n_tree.max_items) {
            split(qtn);
            qtn = qtn.children[get_optimal_index(qtn, x, y, z, w)];
        }
    }
    return qtn;
}


static public <T> void add(N_Tree_Node<T> qtn, List<T> items) {
    for (T t : items) {
        add(qtn, t);
    }
}



// NOCHECKIN changelog
// name constuctor?

// node.data!!
public interface Node_Allocator<T> {
    public N_Tree_Node<T> allocate_n_tree_node(N_Tree<T> octree);
    public void free(N_Tree_Node<T> node);
}


static public class Simple_Node_Allocator<T> implements Node_Allocator<T> {

    public N_Tree_Node<T> allocate_n_tree_node(N_Tree<T> octree) {
        return new N_Tree_Node<>();
    }

    public void free(N_Tree_Node<T> node) {
    }
}


static public class Buffer_Node_Allocator<T> implements Node_Allocator<T> {

    public ArrayList<N_Tree_Node<T>> node_buffer = new ArrayList<>();

    public N_Tree_Node<T> allocate_n_tree_node(N_Tree<T> octree) {
        N_Tree_Node<T> t = remove_last(node_buffer);
        if (t == null) {
            t = new N_Tree_Node<>();
        }
        return t;
    }

    public void free(N_Tree_Node<T> node) {
        node_buffer.add(node);
    }
}


static public <T> Node_Allocator<T> _allocator(N_Tree_Node<T> qtn) {
    return qtn.n_tree.allocator;
}


static public <T> void _set(N_Tree_Node<T> qtn, N_Tree_Node<T> parent, float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
    qtn.parent = parent;
    qtn.x1 = x1;
    qtn.y1 = y1;
    qtn.z1 = z1;
    qtn.w1 = w1;
    qtn.x2 = x2;
    qtn.y2 = y2;
    qtn.z2 = z2;
    qtn.w2 = w2;
    qtn.depth = parent == null ? 0 : parent.depth + 1;
}


static public <T> void split(N_Tree_Node<T> qtn) {

    if (qtn.depth == _HARD_MAX_DEPTH) {
        qtn.n_tree.max_depth_reached = true;
        return; 
    }

    qtn.children[TRF_A] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[TLF_A] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[BLF_A] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[BRF_A] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[TRB_A] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[TLB_A] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[BLB_A] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[BRB_A] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[TRF_B] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[TLF_B] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[BLF_B] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[BRF_B] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[TRB_B] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[TLB_B] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[BLB_B] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);
    qtn.children[BRB_B] = _allocator(qtn).allocate_n_tree_node(qtn.n_tree);

    if (qtn.children[TRF_A].data == null) qtn.children[TRF_A].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[TLF_A].data == null) qtn.children[TLF_A].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[BLF_A].data == null) qtn.children[BLF_A].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[BRF_A].data == null) qtn.children[BRF_A].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[TRB_A].data == null) qtn.children[TRB_A].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[TLB_A].data == null) qtn.children[TLB_A].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[BLB_A].data == null) qtn.children[BLB_A].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[BRB_A].data == null) qtn.children[BRB_A].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[TRF_B].data == null) qtn.children[TRF_B].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[TLF_B].data == null) qtn.children[TLF_B].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[BLF_B].data == null) qtn.children[BLF_B].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[BRF_B].data == null) qtn.children[BRF_B].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[TRB_B].data == null) qtn.children[TRB_B].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[TLB_B].data == null) qtn.children[TLB_B].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[BLB_B].data == null) qtn.children[BLB_B].data = new ArrayList<>(qtn.n_tree.max_items);
    if (qtn.children[BRB_B].data == null) qtn.children[BRB_B].data = new ArrayList<>(qtn.n_tree.max_items);

    qtn.children[TRF_A].n_tree = qtn.n_tree;
    qtn.children[TLF_A].n_tree = qtn.n_tree;
    qtn.children[BLF_A].n_tree = qtn.n_tree;
    qtn.children[BRF_A].n_tree = qtn.n_tree;
    qtn.children[TRB_A].n_tree = qtn.n_tree;
    qtn.children[TLB_A].n_tree = qtn.n_tree;
    qtn.children[BLB_A].n_tree = qtn.n_tree;
    qtn.children[BRB_A].n_tree = qtn.n_tree;
    qtn.children[TRF_B].n_tree = qtn.n_tree;
    qtn.children[TLF_B].n_tree = qtn.n_tree;
    qtn.children[BLF_B].n_tree = qtn.n_tree;
    qtn.children[BRF_B].n_tree = qtn.n_tree;
    qtn.children[TRB_B].n_tree = qtn.n_tree;
    qtn.children[TLB_B].n_tree = qtn.n_tree;
    qtn.children[BLB_B].n_tree = qtn.n_tree;
    qtn.children[BRB_B].n_tree = qtn.n_tree;

    float x1 = qtn.x1;
    float y1 = qtn.y1;
    float z1 = qtn.z1;
    float w1 = qtn.w1;
    float cx = center_x(qtn);
    float cy = center_y(qtn);
    float cz = center_z(qtn);
    float cw = center_w(qtn);
    float x2 = qtn.x2;
    float y2 = qtn.y2;
    float z2 = qtn.z2;
    float w2 = qtn.w2;

    _set(qtn.children[TRF_A], qtn, cx, y1, z1, w1, x2, cy, cz, cw);
    _set(qtn.children[TLF_A], qtn, x1, y1, z1, w1, cx, cy, cz, cw);
    _set(qtn.children[BLF_A], qtn, x1, cy, z1, w1, cx, y2, cz, cw);
    _set(qtn.children[BRF_A], qtn, cx, cy, z1, w1, x2, y2, cz, cw);
    _set(qtn.children[TRB_A], qtn, cx, y1, cz, w1, x2, cy, z2, cw);
    _set(qtn.children[TLB_A], qtn, x1, y1, cz, w1, cx, cy, z2, cw);
    _set(qtn.children[BLB_A], qtn, x1, cy, cz, w1, cx, y2, z2, cw);
    _set(qtn.children[BRB_A], qtn, cx, cy, cz, w1, x2, y2, z2, cw);
    _set(qtn.children[TRF_B], qtn, cx, y1, z1, cw, x2, cy, cz, w2);
    _set(qtn.children[TLF_B], qtn, x1, y1, z1, cw, cx, cy, cz, w2);
    _set(qtn.children[BLF_B], qtn, x1, cy, z1, cw, cx, y2, cz, w2);
    _set(qtn.children[BRF_B], qtn, cx, cy, z1, cw, x2, y2, cz, w2);
    _set(qtn.children[TRB_B], qtn, cx, y1, cz, cw, x2, cy, z2, w2);
    _set(qtn.children[TLB_B], qtn, x1, y1, cz, cw, cx, cy, z2, w2);
    _set(qtn.children[BLB_B], qtn, x1, cy, cz, cw, cx, y2, z2, w2);
    _set(qtn.children[BRB_B], qtn, cx, cy, cz, cw, x2, y2, z2, w2);


    for (T t : qtn.data) {
        add(qtn, t);
    }

    qtn.n_tree.size -= qtn.data.size(); // correction
    qtn.data.clear();

    qtn.n_tree.n_quads_at_depth_lookup[qtn.depth + 1] += 16;
    qtn.n_tree.n_leafs_at_depth_lookup[qtn.depth + 1] += 16;
}


static public <T> Iterator<T> iterator(N_Tree_Node<T> qtn) {

    ArrayList<T> open = new ArrayList<>();
    ArrayList<N_Tree_Node<T>> open_N_Trees = new ArrayList<>();

    open_N_Trees.add(qtn);

    return new Iterator<T>() {

        @Override
        public boolean hasNext() {
            while (open.size() == 0 && open_N_Trees.size() != 0) {
                N_Tree_Node<T> tree = remove_last(open_N_Trees);
                if (has_children(tree)) {
                    open_N_Trees.add(tree.children[0]);
                    open_N_Trees.add(tree.children[1]);
                    open_N_Trees.add(tree.children[2]);
                    open_N_Trees.add(tree.children[3]);
                    open_N_Trees.add(tree.children[4]);
                    open_N_Trees.add(tree.children[5]);
                    open_N_Trees.add(tree.children[6]);
                    open_N_Trees.add(tree.children[7]);
                    open_N_Trees.add(tree.children[8]);
                    open_N_Trees.add(tree.children[9]);
                    open_N_Trees.add(tree.children[10]);
                    open_N_Trees.add(tree.children[11]);
                    open_N_Trees.add(tree.children[12]);
                    open_N_Trees.add(tree.children[13]);
                    open_N_Trees.add(tree.children[14]);
                    open_N_Trees.add(tree.children[15]);
                } else {
                    open.addAll(tree.data);
                }
            }
            return open.size() > 0;
        }

        @Override
        public T next() {
            return remove_last(open);
        }
    };
}


static public <T> T get_closest(N_Tree<T> qt, float x, float y, float z, float w) {
    return get_closest(qt.root, x, y, z, w);
}


static public <T> T get_closest(N_Tree_Node<T> qtn, float x, float y, float z, float w) {

    T closest = null;
    float closest_dist_sq = Float.POSITIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> tree = remove_last(open);
        float d = dist_sq_point_to_4d_aabb(x, y, z, w, tree.x1, tree.y1, tree.z1, tree.w1, tree.x2, tree.y2, tree.z2, tree.w2);
        if (d > closest_dist_sq) {
            continue;
        }

        if (has_children(tree)) {

            int rl = center_x(tree) < x ? 1 : 0;
            int bt = center_y(tree) < y ? 1 : 0;
            int fb = center_z(tree) < z ? 1 : 0;
            int ab = center_w(tree) < w ? 1 : 0;

            // order is reversed so the most optimal get's popped first
            // NOCHECKIN double check if fb logic is correct
            
            open.add(tree.children[((1-ab) * 8) + (1 - fb) * 4 + (1 - bt) * 2 + (1 - rl)]); 
            open.add(tree.children[((1-ab) * 8) + (1 - fb) * 4 + (1 - bt) * 2 + rl]);
            open.add(tree.children[((1-ab) * 8) + (1 - fb) * 4 + bt * 2 + (1 - rl)]);
            open.add(tree.children[((1-ab) * 8) + (1 - fb) * 4 + bt * 2 + rl]);
            open.add(tree.children[((1-ab) * 8) + fb * 4 + (1 - bt) * 2 + (1 - rl)]);
            open.add(tree.children[((1-ab) * 8) + fb * 4 + (1 - bt) * 2 + rl]);
            open.add(tree.children[((1-ab) * 8) + fb * 4 + bt * 2 + (1 - rl)]);
            open.add(tree.children[((1-ab) * 8) + fb * 4 + bt * 2 + rl]);
            open.add(tree.children[ab * 8 + (1 - fb) * 4 + (1 - bt) * 2 + (1 - rl)]); 
            open.add(tree.children[ab * 8 + (1 - fb) * 4 + (1 - bt) * 2 + rl]);
            open.add(tree.children[ab * 8 + (1 - fb) * 4 + bt * 2 + (1 - rl)]);
            open.add(tree.children[ab * 8 + (1 - fb) * 4 + bt * 2 + rl]);
            open.add(tree.children[ab * 8 + fb * 4 + (1 - bt) * 2 + (1 - rl)]);
            open.add(tree.children[ab * 8 + fb * 4 + (1 - bt) * 2 + rl]);
            open.add(tree.children[ab * 8 + fb * 4 + bt * 2 + (1 - rl)]);
            open.add(tree.children[ab * 8 + fb * 4 + bt * 2 + rl]);
            
            // <- best
        } else {
            for (T t : tree.data) {
                d = dist_sq(x, y, z, w, x(qtn, t), y(qtn, t), z(qtn, t), w(qtn, t));
                if (d < closest_dist_sq) {
                    closest_dist_sq = d;
                    closest = t;
                    if (d == 0)
                        return closest;
                }
            }
        }

    }
    return closest;
}


static public <T> T get_farthest(N_Tree_Node<T> qtn, float x, float y, float z, float w) {

    T closest = null;
    float max_dist = Float.NEGATIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {

        N_Tree_Node<T> current = open.remove(open.size() - 1);

        float max_dist_to_current = max_dist_sq_point_to_corner_4d_aabb(x, y, z, w, current.x1, current.y1, current.z1, current.w1, current.x2,
                current.y2, current.z2, current.w2);
        if (max_dist_to_current <= max_dist) {
            continue;
        }

        if (has_children(current)) {
            int rl = center_x(current) < x ? 1 : 0;
            int bt = center_y(current) < y ? 1 : 0;
            int fb = center_z(current) < z ? 1 : 0;
            int ab = center_w(current) < w ? 1 : 0;

            // worst is added as last cause we remove the last one from open
            // NOCHECKIN tripple check (check octree first)
            open.add(current.children[ab * 8 + fb * 4 + bt * 2 + rl]); // <- closest
            open.add(current.children[ab * 8 + fb * 4 + bt * 2 + (1 - rl)]);
            open.add(current.children[ab * 8 + fb * 4 + (1 - bt) * 2 + rl]);
            open.add(current.children[ab * 8 + fb * 4 + (1 - bt) * 2 + (1 - rl)]);
            open.add(current.children[ab * 8 + ((1 - fb) * 4) + bt * 2 + rl]);
            open.add(current.children[ab * 8 + ((1 - fb) * 4) + bt * 2 + (1 - rl)]);
            open.add(current.children[ab * 8 + ((1 - fb) * 4) + (1 - bt) * 2 + rl]);
            open.add(current.children[ab * 8 + ((1 - fb) * 4) + (1 - bt) * 2 + (1 - rl)]);
            open.add(current.children[((1 - ab) * 8) + fb * 4 + bt * 2 + rl]);
            open.add(current.children[((1 - ab) * 8) + fb * 4 + bt * 2 + (1 - rl)]);
            open.add(current.children[((1 - ab) * 8) + fb * 4 + (1 - bt) * 2 + rl]);
            open.add(current.children[((1 - ab) * 8) + fb * 4 + (1 - bt) * 2 + (1 - rl)]);
            open.add(current.children[((1 - ab) * 8) + ((1 - fb) * 4) + bt * 2 + rl]);
            open.add(current.children[((1 - ab) * 8) + ((1 - fb) * 4) + bt * 2 + (1 - rl)]);
            open.add(current.children[((1 - ab) * 8) + ((1 - fb) * 4) + (1 - bt) * 2 + rl]);
            open.add(current.children[((1 - ab) * 8) + ((1 - fb) * 4) + (1 - bt) * 2 + (1 - rl)]); // <- furthest

        } else {

            for (T t : current.data) {
                float d = dist_sq(x, y, z, w, x(qtn, t), y(qtn, t), z(qtn, t), w(qtn, t));
                if (d > max_dist) {
                    closest = t;
                    max_dist = d;
                }
            }
        }
    }
    return closest;
}


static public <T> void get_all(N_Tree_Node<T> qtn, List<T> result) {
    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            open.add(current.children[4]);
            open.add(current.children[5]);
            open.add(current.children[6]);
            open.add(current.children[7]);
            open.add(current.children[8]);
            open.add(current.children[9]);
            open.add(current.children[10]);
            open.add(current.children[11]);
            open.add(current.children[12]);
            open.add(current.children[13]);
            open.add(current.children[14]);
            open.add(current.children[15]);
        } else {
            result.addAll(current.data);
        }
    }
}


static public <T> List<T> get_within_radius(N_Tree_Node<T> qtn, float x, float y, float z, float w, float radius) {
    ArrayList<T> result = new ArrayList<>();
    get_within_radius_sq(qtn, x, y, z, w, sq(radius), result);
    return result;
}


static public <T> void get_within_radius(N_Tree_Node<T> qtn, float x, float y, float z, float w, float radius, List<T> result) {
    get_within_radius_sq(qtn, x, y, z, w, sq(radius), result);
}


static public <T> List<T> get_within_radius_sq(N_Tree_Node<T> qtn, float x, float y, float z, float w, float radius_sq) {
    ArrayList<T> result = new ArrayList<>();
    get_within_radius_sq(qtn, x, y, z, w, radius_sq, result);
    return result;
}


static public <T> void get_within_radius_sq(N_Tree<T> qt, float x, float y, float z, float w, float radius_sq, List<T> result) {
    get_within_radius_sq(qt.root, x, y, z, w, radius_sq, result);
}


static public <T> void get_within_radius_sq(N_Tree_Node<T> qtn, float x, float y, float z, float w, float radius_sq,
        List<T> result) {

    float dist_circle_to_quad_sq = dist_sq_point_to_4d_aabb(x, y, z, w, qtn.x1, qtn.y1, qtn.z1, qtn.w1, qtn.x2, qtn.y2, qtn.z2, qtn.w2);
    if (dist_circle_to_quad_sq > radius_sq)
        return; // octree outside sphere

    
    float dx = max(x - qtn.x1, qtn.x2 - x);
    float dy = max(y - qtn.y1, qtn.y2 - y);
    float dz = max(z - qtn.z1, qtn.z2 - z);
    float dw = max(w - qtn.w1, qtn.w2 - w);

    assert (dx >= 0 && dy >= 0 && dz >= 0 && dw >= 0); // NOCHECKIN, delete assert

    if (radius_sq >= dx * dx + dy * dy + dz * dz + dw * dw) { // fully contained, add all
        get_all(qtn, result);
    } else { // intersection
        if (has_children(qtn)) {
            get_within_radius_sq(qtn.children[0], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[1], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[2], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[3], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[4], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[5], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[6], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[7], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[8], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[9], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[10], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[11], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[12], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[13], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[14], x, y, z, w, radius_sq, result);
            get_within_radius_sq(qtn.children[15], x, y, z, w, radius_sq, result);
        } else {
            for (T t : qtn.data) {
                float dist_sq = dist_sq(x, y, z, w, x(qtn, t), y(qtn, t), z(qtn, t), w(qtn, t));
                if (dist_sq < radius_sq) {
                    result.add(t);
                }
            }
        }
    }
}


static public <T> void get_within_aabb(N_Tree<T> qt, float _r_x1, float _r_y1, float _r_z1, float _r_w1, float _r_x2, float _r_y2, float _r_z2, float _r_w2, 
        List<T> result) {
    get_within_aabb(qt.root, _r_x1, _r_y1, _r_z1, _r_w1, _r_x2, _r_y2, _r_z2, _r_w2, result);
}


static public <T> void get_within_aabb(N_Tree_Node<T> qtn, float _r_x1, float _r_y1, float _r_z1, float _r_w1, float _r_x2, float _r_y2, float _r_z2, float _r_w2,
        List<T> result) {

    float r_x1 = min(_r_x1, _r_x2);
    float r_x2 = max(_r_x1, _r_x2);
    float r_y1 = min(_r_y1, _r_y2);
    float r_y2 = max(_r_y1, _r_y2);
    float r_z1 = min(_r_z1, _r_z2);
    float r_z2 = max(_r_z1, _r_z2);
    float r_w1 = min(_r_w1, _r_w2);
    float r_w2 = max(_r_w1, _r_w2);
    

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = open.remove(open.size() - 1);

        if (_4d_aabb_contains_4d_aabb(r_x1, r_y1, r_z1, r_w1, r_x2, r_y2, r_z2, r_w2, current.x1, current.y1, current.z1, current.w1, current.x2, current.y2, current.z2, current.w2)) {
            get_all(current, result);
        } else if (intersects_cube(current, r_x1, r_y1, r_z1, r_w1, r_x2, r_y2, r_z2, r_w2)) {
            if (has_children(current)) {
                open.add(current.children[0]);
                open.add(current.children[1]);
                open.add(current.children[2]);
                open.add(current.children[3]);
                open.add(current.children[4]);
                open.add(current.children[5]);
                open.add(current.children[6]);
                open.add(current.children[7]);
                open.add(current.children[8]);
                open.add(current.children[9]);
                open.add(current.children[10]);
                open.add(current.children[11]);
                open.add(current.children[12]);
                open.add(current.children[13]);
                open.add(current.children[14]);
                open.add(current.children[15]);
            } else {
                for (T t : current.data) {
                    float x = x(qtn, t);
                    float y = y(qtn, t);
                    float z = z(qtn, t);
                    float w = w(qtn, t);
                    if (!(x < r_x1 || x > r_x2 || y < r_y1 || y > r_y2 || z < r_z1 || z > r_z2 || w < r_w1 || w > r_w2)) {
                        result.add(t);
                    }
                }
            }
        }

    }
}


static public <T> List<T> get_outside_aabb(N_Tree_Node<T> qtn, float r_x1, float r_y1, float r_z1, float r_w1, float r_x2, float r_y2, float r_z2, float r_w2) {
    ArrayList<T> result = new ArrayList<>();
    get_outside_aabb(qtn, r_x1, r_y1, r_z1, r_w1, r_x2, r_y2, r_z2, r_w2, result);
    return result;
}



static public <T> void get_outside_aabb(N_Tree_Node<T> qtn, float _r_x1, float _r_y1, float _r_z1, float _r_w1, float _r_x2, float _r_y2, float _r_z2, float _r_w2,
        List<T> result) {

    float r_x1 = min(_r_x1, _r_x2);
    float r_x2 = max(_r_x1, _r_x2);
    float r_y1 = min(_r_y1, _r_y2);
    float r_w1 = min(_r_w1, _r_w2);
    float r_y2 = max(_r_y1, _r_y2);
    float r_z1 = min(_r_z1, _r_z2);
    float r_z2 = max(_r_z1, _r_z2);
    float r_w2 = max(_r_w1, _r_w2);

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = open.remove(open.size() - 1);

        if (!intersects_cube(current, r_x1, r_y1, r_z1, r_w1, r_x2, r_y2, r_z2, r_w2)) {
            get_all(current, result);
        } else {
            if (has_children(current)) {
                open.add(current.children[0]);
                open.add(current.children[1]);
                open.add(current.children[2]);
                open.add(current.children[3]);
                open.add(current.children[4]);
                open.add(current.children[5]);
                open.add(current.children[6]);
                open.add(current.children[7]);
                open.add(current.children[8]);
                open.add(current.children[9]);
                open.add(current.children[10]);
                open.add(current.children[11]);
                open.add(current.children[12]);
                open.add(current.children[13]);
                open.add(current.children[14]);
                open.add(current.children[15]);
            } else {
                for (T t : current.data) {
                    float x = x(qtn, t);
                    float y = y(qtn, t);
                    float z = z(qtn, t);
                    float w = z(qtn, t);
                    if ((x < r_x1 || x > r_x2 || y < r_y1 || y > r_y2 || z < r_z1 || z > r_z2 || w < r_w1 || w > r_w2)) {
                        result.add(t);
                    }
                }
            }
        }
    }
}


static public ArrayList<?> debug;

static public <T> void get_closest_n(N_Tree_Node<T> qtn, float x, float y, float z, float w, int n, List<T> result) {

    if (n <= 0)
        return;

    if (n >= qtn.n_tree.size) {
        get_all(qtn, result);
        return;
    }

    if (n == 1) {
        T t = get_closest(qtn, x, y, z, w);
        if (t != null)
            result.add(t);
        return;
    }

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    ArrayList<N_Tree_Node<T>> leafs = new ArrayList<>();
    int items_in_leafs = 0;

    N_Tree_Node<T> current = null;

    //
    // find closest leafs till we hit N without checking individual points
    //
    boolean do_add_containing = true;

    // TODO check if correct and comment why we do certain things
    Comparator<N_Tree_Node<T>> comparator_desc = new Comparator<N_Tree_Node<T>>() {
        @Override
        public int compare(N_Tree_Node<T> o1, N_Tree_Node<T> o2) {

            float d1 = Float.MAX_VALUE;
            if (has_children(o1)) {
                if (point_outside_4d_aabb(x, y, z, w, o1.x1, o1.y1, o1.z1, o1.w1, o1.x2, o1.y2, o1.z2, o1.w2)) {
                    d1 = max_dist_sq_point_to_corner_4d_aabb(x, y, z, w, o1.x1, o1.y1, o1.z1, o1.w1, o1.x2, o1.y2, o1.z2, o1.w2);
                }
            } else {
                d1 = dist_sq_point_to_4d_aabb(x, y, z, w, o1.x1, o1.y1, o1.z1, o1.w1, o1.x2, o1.y2, o1.z2, o1.w2);
            }

            float d2 = Float.MAX_VALUE;
            if (has_children(o2)) {
                if (point_outside_aabb(x, y, z, o2.x1, o2.y1, o2.z1, o2.x2, o2.y2, o2.z2)) {
                    d2 = max_dist_sq_point_to_corner_4d_aabb(x, y, z, w, o2.x1, o2.y1, o2.z1, o2.w1, o2.x2, o2.y2, o2.z2, o2.w2);
                }
            } else {
                d2 = dist_sq_point_to_4d_aabb(x, y, z, w, o2.x1, o2.y1, o2.z1, o2.w1, o2.x2, o2.y2, o2.z2, o2.w2);
            }

            if (d1 < d2)
                return 1;
            if (d1 > d2)
                return -1;
            return 0;
        }
    };

    while (open.size() > 0) {

        open.sort(comparator_desc);

        current = remove_last(open);

        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            open.add(current.children[4]);
            open.add(current.children[5]);
            open.add(current.children[6]);
            open.add(current.children[7]);
            open.add(current.children[8]);
            open.add(current.children[9]);
            open.add(current.children[10]);
            open.add(current.children[11]);
            open.add(current.children[12]);
            open.add(current.children[13]);
            open.add(current.children[14]);
            open.add(current.children[15]);
        } else {

            leafs.add(current);
            items_in_leafs += current.data.size();

            if (items_in_leafs >= n) {
                // what?
                if (leafs.size() <= 8) {
                    do_add_containing = false;
                }
                break;
            }
        }
    }

    // we now know a minimum radius before we overflow N, now we can add quads if
    // they are fully contained in this radius
    if (do_add_containing) {
        float dist_point_to_overflow_leaf = dist_sq_point_to_4d_aabb(x, y, z, w, current.x1, current.y1, current.z1, current.w1, current.x2,
                current.y2, current.z2, current.w2);

        for (int i = leafs.size() - 1; i >= 0; i--) {

            N_Tree_Node<T> leaf = leafs.get(i);
            float max_dist_point_to_leaf = max_dist_sq_point_to_corner_4d_aabb(x, y, z, w, leaf.x1, leaf.y1, leaf.z1, leaf.w1, leaf.x2,
                    leaf.y2, leaf.z2, leaf.w2);

            if (max_dist_point_to_leaf <= dist_point_to_overflow_leaf) {
                result.addAll(leaf.data);
                leafs.remove(i);
            }
        }
    }

    ArrayList<T> buffer = new ArrayList<>((n - result.size()) * 2);

    for (N_Tree_Node<T> tree : leafs) {
        buffer.addAll(tree.data);
    }

    float add_all_quads_within_this_radius_sq = max_dist_sq_point_to_corner_4d_aabb(x, y, z, w, current.x1, current.y1, current.z1, current.w1,
            current.x2, current.y2, current.z2, current.w2);

    while (open.size() > 0) {

        open.sort(comparator_desc); // NOCHECKIN, I added this 7 jan 2022

        N_Tree_Node<T> tree = remove_last(open);
        if (has_children(tree)) {

            float d = dist_sq_point_to_4d_aabb(x, y, z, w, tree.x1, tree.y1, tree.z1, tree.w1, tree.x2, tree.y2, tree.z2, tree.w2);
            if (d <= add_all_quads_within_this_radius_sq) {
                open.add(tree.children[0]);
                open.add(tree.children[1]);
                open.add(tree.children[2]);
                open.add(tree.children[3]);
                open.add(tree.children[4]);
                open.add(tree.children[5]);
                open.add(tree.children[6]);
                open.add(tree.children[7]);
                open.add(tree.children[8]);
                open.add(tree.children[9]);
                open.add(tree.children[10]);
                open.add(tree.children[11]);
                open.add(tree.children[12]);
                open.add(tree.children[13]);
                open.add(tree.children[14]);
                open.add(tree.children[15]);
            }
        } else {
            float d = dist_sq_point_to_4d_aabb(x, y, z, w, tree.x1, tree.y1, tree.z1, tree.w1, tree.x2, tree.y2, tree.z2, tree.w2);
            if (d <= add_all_quads_within_this_radius_sq) {
                buffer.addAll(tree.data);
            }
        }
    }

    buffer.sort(new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            float d1 = dist_sq(x, y, z, w, x(qtn, o1), y(qtn, o1), z(qtn, o1), w(qtn, o1));
            float d2 = dist_sq(x, y, z, w, x(qtn, o2), y(qtn, o2), z(qtn, o2), w(qtn, o2));
            if (d1 < d2)
                return -1;
            if (d1 > d2)
                return 1;
            return 0;
        }
    });

    int max = n - result.size();
    for (int i = 0; i < max; i++) {
        T t = buffer.get(i);
        result.add(t);
    }

    debug = buffer;

}


static public <T> void get_closest_n2(N_Tree_Node<T> qtn, float x, float y, float z, float w, int n, List<T> result) {

    if (n <= 0)
        return;

    if (n >= qtn.n_tree.size) {
        get_all(qtn, result);
        return;
    }

    if (n == 1) {
        T t = get_closest(qtn, x, y, z, w);
        if (t != null)
            result.add(t);
        return;
    }

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    ArrayList<N_Tree_Node<T>> leafs = new ArrayList<>();
    int items_in_leafs = 0;

    N_Tree_Node<T> current = null;

    //
    // find closest leafs till we hit N without checking individual points
    //
    boolean do_add_containing = true;

    // we should be able to store max_dist_sq_point_to_corner_aabb / dist_sq_point_to_aabb
    // instead of recalculating

    Comparator<N_Tree_Node<T>> comparator_desc = new Comparator<N_Tree_Node<T>>() {
        @Override
        public int compare(N_Tree_Node<T> o1, N_Tree_Node<T> o2) {

            float d1 = -1;
            if (has_children(o1)) {
                if (point_outside_4d_aabb(x, y, z, w, o1.x1, o1.y1, o1.z1, o1.w1, o1.x2, o1.y2, o1.z2, o1.w2)) {
                    d1 = max_dist_sq_point_to_corner_4d_aabb(x, y, z, w, o1.x1, o1.y1, o1.z1, o1.w2, o1.x2, o1.y2, o1.z2, o1.w2);
                }
            } else {
                d1 = dist_sq_point_to_4d_aabb(x, y, z, w, o1.x1, o1.y1, o1.z1, o1.w1, o1.x2, o1.y2, o1.z2, o1.w2);
            }

            float d2 = -1;
            if (has_children(o2)) {
                if (point_outside_4d_aabb(x, y, z, w, o2.x1, o2.y1, o2.z1, o2.w1, o2.x2, o2.y2, o2.z2, o2.w2)) {
                    d2 = max_dist_sq_point_to_corner_4d_aabb(x, y, z, w, o2.x1, o2.y1, o2.z1, o2.w1, o2.x2, o2.y2, o2.z2, o2.w2);
                }
            } else {
                d2 = dist_sq_point_to_4d_aabb(x, y, z, w, o2.x1, o2.y1, o2.z1, o2.w1, o2.x2, o2.y2, o2.z2, o2.w2);
            }

            if (d1 < d2)
                return 1;
            if (d1 > d2)
                return -1;
            return 0;
        }
    };

    while (open.size() > 0) {

        open.sort(comparator_desc);

        current = remove_last(open);

        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            open.add(current.children[4]);
            open.add(current.children[5]);
            open.add(current.children[6]);
            open.add(current.children[7]);
            open.add(current.children[8]);
            open.add(current.children[9]);
            open.add(current.children[10]);
            open.add(current.children[11]);
            open.add(current.children[12]);
            open.add(current.children[13]);
            open.add(current.children[14]);
            open.add(current.children[15]);
        } else {

            leafs.add(current);
            items_in_leafs += current.data.size();

            if (items_in_leafs >= n) {
                // what?
                if (leafs.size() <= 16) {
                    do_add_containing = false;
                }
                break;
            }
        }
    }

    // we now know a minimum radius before we overflow N, now we can add quads if
    // they are fully contained in this radius
    if (do_add_containing) {
        float dist_point_to_overflow_leaf = dist_sq_point_to_4d_aabb(x, y, z, w, current.x1, current.y1, current.z1, current.w2, current.x2,
                current.y2, current.z2, current.w2);

        for (int i = leafs.size() - 1; i >= 0; i--) {

            N_Tree_Node<T> leaf = leafs.get(i);
            float max_dist_point_to_leaf = max_dist_sq_point_to_corner_4d_aabb(x, y, z, w, leaf.x1, leaf.y1, leaf.z1, leaf.w1, leaf.x2,
                    leaf.y2, leaf.z2, leaf.w2);

            if (max_dist_point_to_leaf <= dist_point_to_overflow_leaf) {
                result.addAll(leaf.data);
                leafs.remove(i);
            }
        }
    }

    ArrayList<T> buffer = new ArrayList<>((n - result.size()) * 2);

    for (N_Tree_Node<T> tree : leafs) {
        buffer.addAll(tree.data);
    }

    float add_all_quads_within_this_radius_sq = max_dist_sq_point_to_corner_4d_aabb(x, y, z, w, current.x1, current.y1, current.z1, current.w1, 
            current.x2, current.y2, current.z2, current.w2);

    while (open.size() > 0) {

        open.sort(comparator_desc); // NOCHECKIN, I added this 7 jan 2022

        N_Tree_Node<T> tree = remove_last(open);
        if (has_children(tree)) {

            float d = dist_sq_point_to_4d_aabb(x, y, z, w, tree.x1, tree.y1, tree.z1, tree.w2, tree.x2, tree.y2, tree.z2, tree.w2);
            if (d <= add_all_quads_within_this_radius_sq) {
                open.add(tree.children[0]);
                open.add(tree.children[1]);
                open.add(tree.children[2]);
                open.add(tree.children[3]);
                open.add(tree.children[4]);
                open.add(tree.children[5]);
                open.add(tree.children[6]);
                open.add(tree.children[7]);
                open.add(tree.children[8]);
                open.add(tree.children[9]);
                open.add(tree.children[10]);
                open.add(tree.children[11]);
                open.add(tree.children[12]);
                open.add(tree.children[13]);
                open.add(tree.children[14]);
                open.add(tree.children[15]);
            }
        } else {
            float d = dist_sq_point_to_4d_aabb(x, y, z, w, tree.x1, tree.y1, tree.z1, tree.w1, tree.x2, tree.y2, tree.z2, tree.w2);
            if (d <= add_all_quads_within_this_radius_sq) {
                buffer.addAll(tree.data);
            }
        }
    }

    buffer.sort(new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            float d1 = dist_sq(x, y, z, w, x(qtn, o1), y(qtn, o1), z(qtn, o1), w(qtn, o1));
            float d2 = dist_sq(x, y, z, w, x(qtn, o2), y(qtn, o2), z(qtn, o2), w(qtn, o2));
            if (d1 < d2)
                return -1;
            if (d1 > d2)
                return 1;
            return 0;
        }
    });

    int max = n - result.size();
    for (int i = 0; i < max; i++) {
        T t = buffer.get(i);
        result.add(t);
    }

    debug = buffer;

}


static public <T> T min_x(N_Tree<T> qt) {
    return min_x(qt.root);
}


static public <T> T min_x(N_Tree_Node<T> qtn) {

    if (qtn.n_tree.size == 0)
        return null;

    T best = null;
    float min_x = Float.POSITIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (min_x < current.x1)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BRF_A]);
            open.add(current.children[BRF_B]);
            open.add(current.children[BRB_A]);
            open.add(current.children[BRB_B]);
            open.add(current.children[TRF_A]);
            open.add(current.children[TRF_B]);
            open.add(current.children[TRB_A]);
            open.add(current.children[TRB_B]);
            open.add(current.children[BLF_A]);
            open.add(current.children[BLF_B]);
            open.add(current.children[BLB_A]);
            open.add(current.children[BLB_B]);
            open.add(current.children[TLF_A]);
            open.add(current.children[TLF_B]);
            open.add(current.children[TLB_A]);
            open.add(current.children[TLB_B]);
            
        } else {
            for (T t : current.data) {
                if (x(qtn, t) < min_x) {
                    min_x = x(qtn, t);
                    best = t;
                }
            }
        }
    }
    return best;
}


static public <T> T min_y(N_Tree<T> qt) {
    return min_y(qt.root);
}


static public <T> T min_y(N_Tree_Node<T> qtn) {

    if (qtn.n_tree.size == 0)
        return null;

    T best = null;
    float min_y = Float.POSITIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (min_y < current.y1)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BRF_A]);
            open.add(current.children[BRF_B]);
            open.add(current.children[BRB_A]);
            open.add(current.children[BRB_B]);
            open.add(current.children[BLF_A]);
            open.add(current.children[BLF_B]);
            open.add(current.children[BLB_A]);
            open.add(current.children[BLB_B]);
            open.add(current.children[TRF_A]);
            open.add(current.children[TRF_B]);
            open.add(current.children[TRB_A]);
            open.add(current.children[TRB_B]);
            open.add(current.children[TLF_A]);
            open.add(current.children[TLF_B]);
            open.add(current.children[TLB_A]);
            open.add(current.children[TLB_B]);
        } else {
            for (T t : current.data) {
                if (y(qtn, t) < min_y) {
                    min_y = y(qtn, t);
                    best = t;
                }
            }
        }
    }
    return best;
}


static public <T> T min_z(N_Tree<T> qt) {
    return min_z(qt.root);
}


static public <T> T min_z(N_Tree_Node<T> qtn) {

    if (qtn.n_tree.size == 0)
        return null;

    T best = null;
    float min_z = Float.POSITIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (min_z < current.z1)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BRB_A]);
            open.add(current.children[BRB_B]);
            open.add(current.children[BLB_A]);
            open.add(current.children[BLB_B]);
            open.add(current.children[TRB_A]);
            open.add(current.children[TRB_B]);
            open.add(current.children[TLB_A]);
            open.add(current.children[TLB_B]);
            open.add(current.children[BRF_A]);
            open.add(current.children[BRF_B]);
            open.add(current.children[BLF_A]);
            open.add(current.children[BLF_B]);
            open.add(current.children[TRF_A]);
            open.add(current.children[TRF_B]);
            open.add(current.children[TLF_A]);
            open.add(current.children[TLF_B]);
        } else {
            for (T t : current.data) {
                if (z(qtn, t) < min_z) {
                    min_z = z(qtn, t);
                    best = t;
                }
            }
        }
    }
    return best;
}


static public <T> T min_w(N_Tree<T> qt) {
    return min_w(qt.root);
}


static public <T> T min_w(N_Tree_Node<T> qtn) {

    if (qtn.n_tree.size == 0)
        return null;

    T best = null;
    float min_w = Float.POSITIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (min_w < current.w1)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BRB_B]);
            open.add(current.children[BLB_B]);
            open.add(current.children[TRB_B]);
            open.add(current.children[TLB_B]);
            open.add(current.children[BRF_B]);
            open.add(current.children[BLF_B]);
            open.add(current.children[TRF_B]);
            open.add(current.children[TLF_B]);
            open.add(current.children[BRB_A]);
            open.add(current.children[BLB_A]);
            open.add(current.children[TRB_A]);
            open.add(current.children[TLB_A]);
            open.add(current.children[BRF_A]);
            open.add(current.children[BLF_A]);
            open.add(current.children[TRF_A]);
            open.add(current.children[TLF_A]);
        } else {
            for (T t : current.data) {
                if (w(qtn, t) < min_w) {
                    min_w = w(qtn, t);
                    best = t;
                }
            }
        }
    }
    return best;
}


static public <T> T max_x(N_Tree<T> qt) {
    return max_x(qt.root);
}


static public <T> T max_x(N_Tree_Node<T> qtn) {

    if (qtn.n_tree.size == 0)
        return null;

    T best = null;
    float max_x = Float.NEGATIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (max_x > current.x2)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BLF_A]);
            open.add(current.children[BLF_B]);
            open.add(current.children[BLB_A]);
            open.add(current.children[BLB_B]);
            open.add(current.children[TLF_A]);
            open.add(current.children[TLF_B]);
            open.add(current.children[TLB_A]);
            open.add(current.children[TLB_B]);
            open.add(current.children[BRF_A]);
            open.add(current.children[BRF_B]);
            open.add(current.children[BRB_A]);
            open.add(current.children[BRB_B]);
            open.add(current.children[TRF_A]);
            open.add(current.children[TRF_B]);
            open.add(current.children[TRB_A]);
            open.add(current.children[TRB_B]);
        } else {
            for (T t : current.data) {
                if (x(qtn, t) > max_x) {
                    max_x = x(qtn, t);
                    best = t;
                }
            }
        }
    }
    return best;
}


static public <T> T max_y(N_Tree<T> qt) {
    return max_y(qt.root);
}


static public <T> T max_y(N_Tree_Node<T> qtn) {

    if (qtn.n_tree.size == 0)
        return null;

    T best = null;
    float max_y = Float.NEGATIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (max_y > current.y2)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[TLF_A]);
            open.add(current.children[TLF_B]);
            open.add(current.children[TLB_A]);
            open.add(current.children[TLB_B]);
            open.add(current.children[TRF_A]);
            open.add(current.children[TRF_B]);
            open.add(current.children[TRB_A]);
            open.add(current.children[TRB_B]);
            open.add(current.children[BLF_A]);
            open.add(current.children[BLF_B]);
            open.add(current.children[BLB_A]);
            open.add(current.children[BLB_B]);
            open.add(current.children[BRF_A]);
            open.add(current.children[BRF_B]);
            open.add(current.children[BRB_A]);
            open.add(current.children[BRB_B]);
        } else {
            for (T t : current.data) {
                if (y(qtn, t) > max_y) {
                    max_y = y(qtn, t);
                    best = t;
                }
            }
        }
    }
    return best;
}


static public <T> T max_z(N_Tree<T> qt) {
    return max_z(qt.root);
}


static public <T> T max_z(N_Tree_Node<T> qtn) {

    if (qtn.n_tree.size == 0)
        return null;

    T best = null;
    float max_z = Float.NEGATIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (max_z > current.y2)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BLF_A]);
            open.add(current.children[BLF_B]);
            open.add(current.children[TLF_A]);
            open.add(current.children[TLF_B]);
            open.add(current.children[BRF_A]);
            open.add(current.children[BRF_B]);
            open.add(current.children[TRF_A]);
            open.add(current.children[TRF_B]);
            open.add(current.children[BLB_A]);
            open.add(current.children[BLB_B]);
            open.add(current.children[TLB_A]);
            open.add(current.children[TLB_B]);
            open.add(current.children[BRB_A]);
            open.add(current.children[BRB_B]);
            open.add(current.children[TRB_A]);
            open.add(current.children[TRB_B]);
        } else {
            for (T t : current.data) {
                if (z(qtn, t) > max_z) {
                    max_z = z(qtn, t);
                    best = t;
                }
            }
        }
    }
    return best;
}


static public <T> T max_w(N_Tree<T> qt) {
    return max_w(qt.root);
}


static public <T> T max_w(N_Tree_Node<T> qtn) {

    if (qtn.n_tree.size == 0)
        return null;

    T best = null;
    float max_w = Float.NEGATIVE_INFINITY;

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (max_w > current.w2)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BLF_A]);
            open.add(current.children[TLF_A]);
            open.add(current.children[BRF_A]);
            open.add(current.children[TRF_A]);
            open.add(current.children[BLB_A]);
            open.add(current.children[TLB_A]);
            open.add(current.children[BRB_A]);
            open.add(current.children[TRB_A]);
            open.add(current.children[BLF_B]);
            open.add(current.children[TLF_B]);
            open.add(current.children[BRF_B]);
            open.add(current.children[TRF_B]);
            open.add(current.children[BLB_B]);
            open.add(current.children[TLB_B]);
            open.add(current.children[BRB_B]);
            open.add(current.children[TRB_B]);
        } else {
            for (T t : current.data) {
                if (w(qtn, t) > max_w) {
                    max_w = w(qtn, t);
                    best = t;
                }
            }
        }
    }
    return best;
}




static public <T> void clear(N_Tree<T> qt) {
    clear(qt.root);
}


static public <T> void clear(N_Tree_Node<T> qtn) {

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);
        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            open.add(current.children[4]);
            open.add(current.children[5]);
            open.add(current.children[6]);
            open.add(current.children[7]);
            open.add(current.children[8]);
            open.add(current.children[9]);
            open.add(current.children[10]);
            open.add(current.children[11]);
            open.add(current.children[12]);
            open.add(current.children[13]);
            open.add(current.children[14]);
            open.add(current.children[15]);
            current.children[0] = null;
            current.children[1] = null;
            current.children[2] = null;
            current.children[3] = null;
            current.children[4] = null;
            current.children[5] = null;
            current.children[6] = null;
            current.children[7] = null;
            current.children[8] = null;
            current.children[9] = null;
            current.children[10] = null;
            current.children[11] = null;
            current.children[12] = null;
            current.children[13] = null;
            current.children[14] = null;
            current.children[15] = null;
            qtn.n_tree.allocator.free(current);
        } else {
            current.data.clear();
        }
    }
    qtn.n_tree.size = 0; // check lookup thing
}


static public <T> int size(N_Tree_Node<T> qtn) {
    return qtn.n_tree.size;
}


static public <T> boolean remove(N_Tree<T> qt, T t) {
    return remove(qt.root, t);
}


static public <T> boolean remove(N_Tree_Node<T> qtn, T t) {

    float x = x(qtn, t);
    float y = y(qtn, t);
    float z = z(qtn, t);
    float w = w(qtn, t);

    qtn = get_leaf(qtn, x, y, z, w);
    if (swap_remove(qtn.data, t) != null) {
        qtn.n_tree.size--;
        return true;
    }
    return false;
}


static public <T> boolean merge(N_Tree_Node<T> qtn) {

    if (!has_children(qtn)) {
        return false;
    }

    if (has_children(qtn.children[0]) || has_children(qtn.children[1]) || has_children(qtn.children[2]) || has_children(qtn.children[3]) ||
        has_children(qtn.children[4]) || has_children(qtn.children[5]) || has_children(qtn.children[6]) || has_children(qtn.children[7]) ||
        has_children(qtn.children[8]) || has_children(qtn.children[9]) || has_children(qtn.children[10]) || has_children(qtn.children[11]) ||
        has_children(qtn.children[12]) || has_children(qtn.children[13]) || has_children(qtn.children[14]) || has_children(qtn.children[15])
    ) {
        return false;
    }

    int count  = qtn.children[0].data.size();
        count += qtn.children[1].data.size();
        count += qtn.children[2].data.size();
        count += qtn.children[3].data.size();
        count += qtn.children[4].data.size();
        count += qtn.children[5].data.size();
        count += qtn.children[6].data.size();
        count += qtn.children[7].data.size();
        count += qtn.children[8].data.size();
        count += qtn.children[9].data.size();
        count += qtn.children[10].data.size();
        count += qtn.children[11].data.size();
        count += qtn.children[12].data.size();
        count += qtn.children[13].data.size();
        count += qtn.children[14].data.size();
        count += qtn.children[15].data.size();
        

    // The merge_threshold should be between 0 and 1. The higher it
    // is the more likely it will do a merge. If the amount of times
    // is less then the max_items * merge_threshold then a merge
    // will happen. For example if max_items is 32, then:
    // 32 * 0.75 = merge when items drop below 24
    // 32 * 0.50 = merge when items drop below 16
    // 32 * 0.25 = merge when items drop below 8

    if (count < (qtn.n_tree.max_items * qtn.n_tree.merge_threshold)) {
        //
        // merge
        //
        qtn.data.addAll(qtn.children[0].data);
        qtn.data.addAll(qtn.children[1].data);
        qtn.data.addAll(qtn.children[2].data);
        qtn.data.addAll(qtn.children[3].data);
        qtn.data.addAll(qtn.children[4].data);
        qtn.data.addAll(qtn.children[5].data);
        qtn.data.addAll(qtn.children[6].data);
        qtn.data.addAll(qtn.children[7].data);
        qtn.data.addAll(qtn.children[8].data);
        qtn.data.addAll(qtn.children[9].data);
        qtn.data.addAll(qtn.children[10].data);
        qtn.data.addAll(qtn.children[11].data);
        qtn.data.addAll(qtn.children[12].data);
        qtn.data.addAll(qtn.children[13].data);
        qtn.data.addAll(qtn.children[14].data);
        qtn.data.addAll(qtn.children[15].data);

        qtn.children[0].data.clear();
        qtn.children[1].data.clear();
        qtn.children[2].data.clear();
        qtn.children[3].data.clear();
        qtn.children[4].data.clear();
        qtn.children[5].data.clear();
        qtn.children[6].data.clear();
        qtn.children[7].data.clear();
        qtn.children[8].data.clear();
        qtn.children[9].data.clear();
        qtn.children[10].data.clear();
        qtn.children[11].data.clear();
        qtn.children[12].data.clear();
        qtn.children[13].data.clear();
        qtn.children[14].data.clear();
        qtn.children[15].data.clear();

        qtn.n_tree.allocator.free(qtn.children[0]);
        qtn.n_tree.allocator.free(qtn.children[1]);
        qtn.n_tree.allocator.free(qtn.children[2]);
        qtn.n_tree.allocator.free(qtn.children[3]);
        qtn.n_tree.allocator.free(qtn.children[4]);
        qtn.n_tree.allocator.free(qtn.children[5]);
        qtn.n_tree.allocator.free(qtn.children[6]);
        qtn.n_tree.allocator.free(qtn.children[7]);
        qtn.n_tree.allocator.free(qtn.children[8]);
        qtn.n_tree.allocator.free(qtn.children[9]);
        qtn.n_tree.allocator.free(qtn.children[10]);
        qtn.n_tree.allocator.free(qtn.children[11]);
        qtn.n_tree.allocator.free(qtn.children[12]);
        qtn.n_tree.allocator.free(qtn.children[13]);
        qtn.n_tree.allocator.free(qtn.children[14]);
        qtn.n_tree.allocator.free(qtn.children[15]);

        qtn.children[0] = null;
        qtn.children[1] = null;
        qtn.children[2] = null;
        qtn.children[3] = null;
        qtn.children[4] = null;
        qtn.children[5] = null;
        qtn.children[6] = null;
        qtn.children[7] = null;
        qtn.children[8] = null;
        qtn.children[9] = null;
        qtn.children[10] = null;
        qtn.children[11] = null;
        qtn.children[12] = null;
        qtn.children[13] = null;
        qtn.children[14] = null;
        qtn.children[15] = null;

        qtn.n_tree.n_quads_at_depth_lookup[qtn.depth + 1] -= 16;
        qtn.n_tree.n_leafs_at_depth_lookup[qtn.depth + 1] -= 16;
        qtn.n_tree.n_leafs_at_depth_lookup[qtn.depth] += 1;

        return true;
    }
    return false;
}


static public <T> void merge_update(N_Tree<T> qt) {
    merge_update(qt.root);
}


static public <T> void merge_update(N_Tree_Node<T> qtn) {

    ArrayList<N_Tree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        N_Tree_Node<T> current = remove_last(open);

        if (!merge(current) && has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            open.add(current.children[4]);
            open.add(current.children[5]);
            open.add(current.children[6]);
            open.add(current.children[7]);
            open.add(current.children[8]);
            open.add(current.children[9]);
            open.add(current.children[10]);
            open.add(current.children[11]);
            open.add(current.children[12]);
            open.add(current.children[13]);
            open.add(current.children[14]);
            open.add(current.children[15]);
        }
    }
}


static public <T> void update(N_Tree<T> qt, ArrayList<T> update_helper, ArrayList<T> out_of_bounds) {
    update(qt.root, update_helper, out_of_bounds);
}


static public <T> void update(N_Tree_Node<T> qtn, ArrayList<T> update_helper, ArrayList<T> out_of_bounds) {

    if (qtn.depth == 0 && !has_children(qtn)) {
        for (int i = qtn.data.size()-1; i >= 0; i--) {
            T t = qtn.data.get(i);
            float x = x(qtn, t);
            float y = y(qtn, t);
            float z = z(qtn, t);
            float w = w(qtn, t);
            if (point_outside_4d_aabb(x, y, z, w, qtn.x1, qtn.y1, qtn.z1, qtn.w1, qtn.x2, qtn.y2, qtn.z2, qtn.w2)) {
                out_of_bounds.add(swap_remove(qtn.data, i));
            }
        }
        return;
    }

    Iterator<N_Tree_Node<T>> itr = get_iterator(qtn, Iterator_Type.LEAFS);

    while (itr.hasNext()) {
        N_Tree_Node<T> n = itr.next();
        
        if (n.data.size() > 0) {
            for (int i = n.data.size()-1; i >= 0; i--) {

                T t = n.data.get(i);
                float x = x(n, t);
                float y = y(n, t);
                float z = z(n, t);
                float w = w(n, t);

                if (point_outside_4d_aabb(x, y, z, w, n.x1, n.y1, n.z1, n.w1, n.x2, n.y2, n.z2, n.w2)) {
                    swap_remove(n.data, i);
                    update_helper.add(t);
                }
            }
        }
    }


    for (T t : update_helper) {

        N_Tree_Node<T> n = get_leaf(qtn, x(qtn, t), y(qtn, t), z(qtn, t), w(qtn, t));

        if (n != null) {
            n.data.add(t);
            if (n.data.size() > n.n_tree.max_items) {
                split(n);
            }
        }
        else {
            out_of_bounds.add(t);
            qtn.n_tree.size -= 1;
        }
    }

    update_helper.clear();

    merge_update(qtn);

}


public enum Iterator_Type {
    BREADTH_FIRST,
    DEPTH_FIRST,
    LEAFS
}


static public <T> Iterator<N_Tree_Node<T>> get_iterator(N_Tree_Node<T> qtn, Iterator_Type iterator_type) {

    if (iterator_type == Iterator_Type.DEPTH_FIRST) {

        ArrayList<N_Tree_Node<T>> _open = new ArrayList<>();
        _open.add(qtn);
        
        return new Iterator<SFJL_N_Tree.N_Tree_Node<T>>(){
            
            ArrayList<N_Tree_Node<T>> open = _open;
            
            @Override
            public boolean hasNext() {
                return open.size() > 0;
            }
            
            @Override
            public N_Tree_Node<T> next() {
                N_Tree_Node<T> r = remove_last(open);
                if (has_children(r)) {
                    open.add(r.children[0]);
                    open.add(r.children[1]);
                    open.add(r.children[2]);
                    open.add(r.children[3]);
                    open.add(r.children[4]);
                    open.add(r.children[5]);
                    open.add(r.children[6]);
                    open.add(r.children[7]);
                    open.add(r.children[8]);
                    open.add(r.children[9]);
                    open.add(r.children[10]);
                    open.add(r.children[11]);
                    open.add(r.children[12]);
                    open.add(r.children[13]);
                    open.add(r.children[14]);
                    open.add(r.children[15]);
                }
                return r;
            }
        };
    }
    if (iterator_type == Iterator_Type.BREADTH_FIRST) {

        ArrayList<N_Tree_Node<T>> _current_depth = new ArrayList<>();
        _current_depth.add(qtn);

        return new Iterator<SFJL_N_Tree.N_Tree_Node<T>>(){

            ArrayList<N_Tree_Node<T>> current_depth = _current_depth;
            ArrayList<N_Tree_Node<T>> next_depth = new ArrayList<>(16);

            @Override
            public boolean hasNext() {
                return current_depth.size() > 0;
            }

            @Override
            public N_Tree_Node<T> next() {
                N_Tree_Node<T> r = swap_remove(current_depth, 0);
                if (has_children(r)) {
                    next_depth.add(r.children[0]);
                    next_depth.add(r.children[1]);
                    next_depth.add(r.children[2]);
                    next_depth.add(r.children[3]);
                    next_depth.add(r.children[4]);
                    next_depth.add(r.children[5]);
                    next_depth.add(r.children[6]);
                    next_depth.add(r.children[7]);
                    next_depth.add(r.children[8]);
                    next_depth.add(r.children[9]);
                    next_depth.add(r.children[10]);
                    next_depth.add(r.children[11]);
                    next_depth.add(r.children[12]);
                    next_depth.add(r.children[13]);
                    next_depth.add(r.children[14]);
                    next_depth.add(r.children[15]);
                }
                if (current_depth.size() == 0) {
                    var temp = current_depth;
                    current_depth = next_depth;
                    next_depth = temp;
                    next_depth.clear();
                }
                return r;
            }
            
        };

    }
    if (iterator_type == Iterator_Type.LEAFS) {

        ArrayList<N_Tree_Node<T>> open       = new ArrayList<>();
        ArrayList<N_Tree_Node<T>> open_leafs = new ArrayList<>();
        open.add(qtn);

        // find the first leaf
        while (true) {
            N_Tree_Node<T> n = remove_last(open);
            if (has_children(n)) {
                open.add(n.children[0]);
                open.add(n.children[1]);
                open.add(n.children[2]);
                open.add(n.children[3]);
                open.add(n.children[4]);
                open.add(n.children[5]);
                open.add(n.children[6]);
                open.add(n.children[7]);
                open.add(n.children[8]);
                open.add(n.children[9]);
                open.add(n.children[10]);
                open.add(n.children[11]);
                open.add(n.children[12]);
                open.add(n.children[13]);
                open.add(n.children[14]);
                open.add(n.children[15]);
            }
            else {
                open_leafs.add(n);
                break;
            }
        }
        
        return new Iterator<SFJL_N_Tree.N_Tree_Node<T>>(){
            
            @Override
            public boolean hasNext() {
                return open_leafs.size() > 0;
            }
            
            @Override
            public N_Tree_Node<T> next() {
                N_Tree_Node<T> r = remove_last(open_leafs);
                while (open_leafs.size() == 0 && open.size() > 0) {
                    N_Tree_Node<T> n = remove_last(open);
                    if (has_children(n)) {
                        open.add(n.children[0]);
                        open.add(n.children[1]);
                        open.add(n.children[2]);
                        open.add(n.children[3]);
                        open.add(n.children[4]);
                        open.add(n.children[5]);
                        open.add(n.children[6]);
                        open.add(n.children[7]);
                        open.add(n.children[8]);
                        open.add(n.children[9]);
                        open.add(n.children[10]);
                        open.add(n.children[11]);
                        open.add(n.children[12]);
                        open.add(n.children[13]);
                        open.add(n.children[14]);
                        open.add(n.children[15]);
                    }
                    else {
                        open_leafs.add(n);
                    }
                }
                return r;
            }
        };
    }

    return null;
}



static public <T> int highest_depth_with_leafs(N_Tree<T> qt) {
    return highest_depth_with_leafs(qt.root);
}


static public <T> int highest_depth_with_leafs(N_Tree_Node<T> qtn) {

    int[] n_leafs_at_depth_lookup = qtn.n_tree.n_leafs_at_depth_lookup;

    for (int i = n_leafs_at_depth_lookup.length-1; i >= 0; i--) {
        if (n_leafs_at_depth_lookup[i] != 0) {
            return i;
        }
    }  
    throw new RuntimeException("unreachable");
}


static public <T> int lowest_depth_with_leafs(N_Tree<T> qt) {
    return lowest_depth_with_leafs(qt.root);
}


static public <T> int lowest_depth_with_leafs(N_Tree_Node<T> qtn) {

    int[] n_leafs_at_depth_lookup = qtn.n_tree.n_leafs_at_depth_lookup;

    for (int i = 0; i < n_leafs_at_depth_lookup.length; i++) {
        if (n_leafs_at_depth_lookup[i] != 0) {
            return i;
        }
    }  
    throw new RuntimeException("unreachable");
}


static public <T> boolean intersects_cube(N_Tree_Node<T> tree, float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
    return !(tree.x1 > x2 || 
    tree.x2 < x1 || 
    tree.y1 > y2 ||
    tree.y2 < y1 ||
    tree.z1 > z2 ||
    tree.z2 < z1 ||
    tree.w1 > w2 ||
    tree.w2 < w1);
}



// return 0 if the point is inside the aabb
static public final float dist_sq_point_to_4d_aabb(float px, float py, float pz, float pw, float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
        
    if (px < x1) { 
        if (py < y1) { 
            if (pz < z1) {
                if (pw < w1) {
                    return dist_sq(px, py, pz, pw, x1, y1, z1, w1);
                }
                else if (pw > w2 ) {
                    return dist_sq(px, py, pz, pw, x1, y1, z1, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, pz, x1, y1, z1);
                }
            }
            else if (pz > z2) {
                if (pw < w1) {
                    return dist_sq(px, py, pz, pw, x1, y1, z2, w1);
                }
                else if (pw > w2) {
                    return dist_sq(px, py, pz, pw, x1, y1, z2, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, pz, x1, y1, z2);
                }
            }
            else {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, py, pw, x1, y1, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, py, pw, x1, y1, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, x1, y1);
                }
            }
        }
        else if (py > y2) {
            if (pz < z1) {
                if (pw < w1) {
                    return dist_sq(px, py, pz, pw, x1, y2, z1, w1); 
                }
                else if (pw > w2) {
                    return dist_sq(px, py, pz, pw, x1, y2, z1, w2); 
                }
                else {
                    return SFJL_Math.dist_sq(px, py, pz, x1, y2, z1); 
                }
            }
            else if (pz > z2) {
                if (pw < w1) {
                    return dist_sq(px, py, pz, pw, x1, y2, z2, w1); 
                }
                else if (pw > w2) {
                    return dist_sq(px, py, pz, pw, x1, y2, z2, w2); 
                }
                else {
                    return SFJL_Math.dist_sq(px, py, pz, x1, y2, z2); 
                }
            }
            else {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, py, pw, x1, y2, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, py, pw, x1, y2, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, x1, y2); 
                }
            }
        }
        else {
            if (pz < z1) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, pz, pw, x1, z1, w1); 
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, pz, pw, x1, z1, w2); 
                }
                else {
                    return SFJL_Math.dist_sq(px, pz, x1, z1); 
                }
            }
            else if (pz > z2) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, pz, pw, x1, z2, w1); 
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, pz, pw, x1, z2, w2); 
                }
                else {
                    return SFJL_Math.dist_sq(px, pz, x1, z2);                     
                }
            }
            else {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, pw, x1, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, pw, x1, w2);
                }
                else {
                    return sq(x1 - px);
                }
            }
        }
    }
    else if (px > x2) {
        if (py < y1) {
            if (pz < z1) {
                if (pw < w1) {
                    return dist_sq(px, py, pz, pw, x2, y1, z1, w1);
                }
                else if (pw > w2) {
                    return dist_sq(px, py, pz, pw, x2, y1, z1, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, pz, x2, y1, z1);
                }
            }
            else if (pz > z2) {
                if (pw < w1) {
                    return dist_sq(px, py, pz, pw, x2, y1, z2, w1);
                }
                else if (pw > w2) {
                    return dist_sq(px, py, pz, pw, x2, y1, z2, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, pz, x2, y1, z2);
                }
            }
            else {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, py, pw, x2, y1, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, py, pw, x2, y1, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, x2, y1);
                }
            }
        }
        else if (py > y2) { 
            if (pz < z1) {
                if (pw < w1) {
                    return dist_sq(px, py, pz, pw, x2, y2, z1, w1);
                }
                else if (pw > w2) {
                    return dist_sq(px, py, pz, pw, x2, y2, z1, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, pz, x2, y2, z1);
                }
            }
            else if (pz > z2) {
                if (pw < w1) {
                    return dist_sq(px, py, pz, pw, x2, y2, z2, w1);
                }
                else if (pw > w2) {
                    return dist_sq(px, py, pz, pw, x2, y2, z2, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, pz, x2, y2, z2);
                }
            }
            else {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, py, pw, x2, y2, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, py, pw, x2, y2, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, py, x2, y2);
                }
            }
        }
        else {
            if (pz < z1) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, pz, pw, x2,  z1, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, pz, pw, x2,  z1, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, pz, x2,  z1);
                }
            }
            else if (pz > z2) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, pz, pw, x2, z2, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, pz, pw, x2, z2, w2);
                }
                else {
                    return SFJL_Math.dist_sq(px, pz, x2, z2);
                }
            }
            else {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(px, pw, x2, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(px, pw, x2, w2);
                }
                else {
                    return sq(px - x2);
                }
            }
        }
    }
    else {
        if (py < y1) { 
            if (pz < z1) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(py, pz, pw, y1, z1, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(py, pz, pw, y1, z1, w2);
                }
                else {
                    return SFJL_Math.dist_sq(py, pz, y1, z1);
                }
            }
            else if (pz > z2) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(py, pz, pw, y1, z2, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(py, pz, pw, y1, z2, w2);
                }
                else {
                    return SFJL_Math.dist_sq(py, pz, y1, z2);
                }
            }
            else {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(py, pw, y1, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(py, pw, y1, w2);
                }
                else {
                    return sq(y1 - py);
                }
            }
        }
        else if (py > y2) { 
            if (pz < z1) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(py, pz, pw, y2, z1, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(py, pz, pw, y2, z1, w2);
                }
                else {
                    return SFJL_Math.dist_sq(py, pz, y2, z1);
                }
            }
            else if (pz > z2) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(py, pz, pw, y2, z2, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(py, pz, pw, y2, z2, w2);
                }
                else {
                    return SFJL_Math.dist_sq(py, pz, y2, z2);
                }
            }
            else {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(py, pw, y2, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(py, pw, y2, w2);
                }
                else {
                    return sq(py - y2);
                }
            }
        }
        else {
            if (pz < z1) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(pz, pw, z1, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(pz, pw, z1, w2);
                }
                else {
                    return sq(z1 - pz);
                }
            }
            else if (pz > z2) {
                if (pw < w1) {
                    return SFJL_Math.dist_sq(pz, pw, z2, w1);
                }
                else if (pw > w2) {
                    return SFJL_Math.dist_sq(pz, pw, z2, w2);
                }
                else {
                    return sq(pz - z2);
                }
                
            }
            else {
                if (pw < w1) {
                    return sq(w1 - pw);
                }
                else if (pw > w2) {
                    return sq(pw - w2);
                }
                else {
                    return 0f;
                }
            }
        }
    }
}

static public final float max_dist_sq_point_to_corner_4d_aabb(float x, float y, float z, float w, float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
    float far_x = abs(x-x1) > abs(x-x2) ? x1 : x2;
    float far_y = abs(y-y1) > abs(y-y2) ? y1 : y2;
    float far_z = abs(z-z1) > abs(z-z2) ? z1 : z2;
    float far_w = abs(w-w1) > abs(w-w2) ? w1 : w2;
    return dist_sq(x, y, z, w, far_x, far_y, far_z, far_w);
}


static public final float dist_sq(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
    return ((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1) + ((z2-z1)*(z2-z1)) + ((w2-w1)*(w2-w1)));
}

static public boolean _4d_aabb_contains_4d_aabb(float r_x1, float r_y1, float r_z1, float r_w1, 
                                                float r_x2, float r_y2, float r_z2, float r_w2,
                                                float r2_x1, float r2_y1, float r2_z1, float r2_w1,
                                                float r2_x2, float r2_y2, float r2_z2, float r2_w2) {
    return (r2_x1 >= r_x1 && r2_x2 <= r_x2 && r2_y1 >= r_y1 &&  r2_y2 <= r_y2 && r2_z1 >= r_z1 &&  r2_z2 <= r_z2 && r2_w1 >= r_w1 &&  r2_w2 <= r_w2);
}


static public final boolean point_outside_4d_aabb(float x, float y, float z, float w, float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
    return x < x1 || x > x2  || y < y1 || y > y2 || z < z1 || z > z2 || w < w1 || w > w2;
}
   
}
/**
revision history:

   0.50  (2022-01-09) first numbered version

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