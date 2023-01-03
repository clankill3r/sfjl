/** SFJL_Octree - v0.51
 
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

public class SFJL_Octree {
     private SFJL_Octree() {}
//           SFJL_Octree

// don't change the order!
public final static int TLF = 0;
public final static int TRF = 1;
public final static int BLF = 2;
public final static int BRF = 3;
public final static int TLB = 4;
public final static int TRB = 5;
public final static int BLB = 6;
public final static int BRB = 7;


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


static public class Octree<T> implements Iterable<T> {
    public int max_items;
    public float merge_threshold = 0.75f;
    public X<T> x;
    public Y<T> y;
    public Z<T> z;
    public int size = 0;
    public Octree_Node<T> root;
    public Node_Allocator<T> allocator = new Simple_Node_Allocator<>();
    public int[] n_quads_at_depth_lookup = new int[_HARD_MAX_DEPTH + 1];
    public int[] n_leafs_at_depth_lookup = new int[_HARD_MAX_DEPTH + 1];
    public boolean max_depth_reached = false;

    public Octree(X<T> x, Y<T> y, Z<T> z, int max_items, float x1, float y1, float z1, float x2, float y2, float z2) {
        this.x = x;
        this.y = y;
        this.z = z;
        assert (max_items > 0);
        this.max_items = max_items;
        root = new Octree_Node<>();
        _set(root, null, x1, y1, z1, x2, y2, z2);
        root.octree = this;
        root.data = new ArrayList<>(max_items);
    }

    @Override
    public Iterator<T> iterator() {
        return SFJL_Octree.iterator(root);
    }
}

static public class Octree_Node<T> implements Iterable<T> {
    public Octree<T> octree;
    public Octree_Node<T> parent;
    @SuppressWarnings("unchecked")
    public Octree_Node<T>[] children = new Octree_Node[8];
    public ArrayList<T> data;
    public float x1;
    public float y1;
    public float z1;
    public float x2;
    public float y2;
    public float z2;
    public int depth;

    @Override
    public Iterator<T> iterator() {
        return SFJL_Octree.iterator(this);
    }
}


static public <T> float x(Octree_Node<T> n, T t) {
    return n.octree.x.x(t);
}


static public <T> float y(Octree_Node<T> n, T t) {
    return n.octree.y.y(t);
}


static public <T> float z(Octree_Node<T> n, T t) {
    return n.octree.z.z(t);
}


static public <T> float center_x(Octree_Node<T> n) {
    return n.x1 + (n.x2 - n.x1) / 2;
}


static public <T> float center_y(Octree_Node<T> n) {
    return n.y1 + (n.y2 - n.y1) / 2;
}


static public <T> float center_z(Octree_Node<T> n) {
    return n.z1 + (n.z2 - n.z1) / 2;
}


static public <T> boolean has_children(Octree_Node<T> n) {
    return n.children[0] != null;
}


static public <T> boolean is_root(Octree_Node<T> n) {
    return n.parent == null;
}


static public <T> int get_optimal_index(Octree_Node<T> n, float x, float y, float z) {

    if (y < center_y(n)) {
        if (x < center_x(n)) {
            return z < center_z(n) ? TLF : TLB;
        }
        else {
            return z < center_z(n) ? TRF : TRB;
        }
    }
    else {
        if (x < center_x(n)) {
            return z < center_z(n) ? BLF : BLB;
        }
        else {
            return z < center_z(n) ? BRF : BRB;
        }
    }
}


static public <T> Octree_Node<T> get_leaf(Octree_Node<T> qtn, float x, float y, float z) {

    if (qtn.depth == 0) {
        if (x < qtn.x1 || x > qtn.x2 || y < qtn.y1 || y > qtn.y2 || z < qtn.z1 || z > qtn.z2) {
            return null;
        }
    }

    Octree_Node<T> current = qtn;
    while (has_children(current)) {
        int where = get_optimal_index(current, x, y, z);
        current = current.children[where];
    }
    return current;
}


static public <T> Octree_Node<T> add(Octree<T> qt, T t) {
    return add(qt.root, t);
}


static public <T> Octree_Node<T> add(Octree_Node<T> qtn, T t) {
    return add(qtn, t, x(qtn, t), y(qtn, t), z(qtn, t));
}


static public <T> Octree_Node<T> add(Octree_Node<T> qtn, T t, float x, float y, float z) {

    qtn = get_leaf(qtn, x, y, z);

    if (qtn != null) {
        qtn.data.add(t);
        qtn.octree.size++;
        if (qtn.data.size() > qtn.octree.max_items) {
            split(qtn);
            qtn = qtn.children[get_optimal_index(qtn, x, y, z)];
        }
    }
    return qtn;
}


static public <T> void add(Octree_Node<T> qtn, List<T> items) {
    for (T t : items) {
        add(qtn, t);
    }
}



// NOCHECKIN changelog
// name constuctor?

// node.data!!
public interface Node_Allocator<T> {
    public Octree_Node<T> allocate_octree_node(Octree<T> octree);
    public void free(Octree_Node<T> node);
}


static public class Simple_Node_Allocator<T> implements Node_Allocator<T> {

    public Octree_Node<T> allocate_octree_node(Octree<T> octree) {
        return new Octree_Node<>();
    }

    public void free(Octree_Node<T> node) {
    }
}


static public class Buffer_Node_Allocator<T> implements Node_Allocator<T> {

    public ArrayList<Octree_Node<T>> node_buffer = new ArrayList<>();

    public Octree_Node<T> allocate_octree_node(Octree<T> octree) {
        Octree_Node<T> t = remove_last(node_buffer);
        if (t == null) {
            t = new Octree_Node<>();
        }
        return t;
    }

    public void free(Octree_Node<T> node) {
        node_buffer.add(node);
    }
}


static public <T> Node_Allocator<T> _allocator(Octree_Node<T> qtn) {
    return qtn.octree.allocator;
}


static public <T> void _set(Octree_Node<T> qtn, Octree_Node<T> parent, float x1, float y1, float z1, float x2, float y2, float z2) {
    qtn.parent = parent;
    qtn.x1 = x1;
    qtn.y1 = y1;
    qtn.z1 = z1;
    qtn.x2 = x2;
    qtn.y2 = y2;
    qtn.z2 = z2;
    qtn.depth = parent == null ? 0 : parent.depth + 1;
}


static public <T> void split(Octree_Node<T> qtn) {

    if (qtn.depth == _HARD_MAX_DEPTH) {
        qtn.octree.max_depth_reached = true;
        return; 
    }

    qtn.children[TRF] = _allocator(qtn).allocate_octree_node(qtn.octree);
    qtn.children[TLF] = _allocator(qtn).allocate_octree_node(qtn.octree);
    qtn.children[BLF] = _allocator(qtn).allocate_octree_node(qtn.octree);
    qtn.children[BRF] = _allocator(qtn).allocate_octree_node(qtn.octree);
    qtn.children[TRB] = _allocator(qtn).allocate_octree_node(qtn.octree);
    qtn.children[TLB] = _allocator(qtn).allocate_octree_node(qtn.octree);
    qtn.children[BLB] = _allocator(qtn).allocate_octree_node(qtn.octree);
    qtn.children[BRB] = _allocator(qtn).allocate_octree_node(qtn.octree);

    if (qtn.children[TRF].data == null) qtn.children[TRF].data = new ArrayList<>(qtn.octree.max_items);
    if (qtn.children[TLF].data == null) qtn.children[TLF].data = new ArrayList<>(qtn.octree.max_items);
    if (qtn.children[BLF].data == null) qtn.children[BLF].data = new ArrayList<>(qtn.octree.max_items);
    if (qtn.children[BRF].data == null) qtn.children[BRF].data = new ArrayList<>(qtn.octree.max_items);
    if (qtn.children[TRB].data == null) qtn.children[TRB].data = new ArrayList<>(qtn.octree.max_items);
    if (qtn.children[TLB].data == null) qtn.children[TLB].data = new ArrayList<>(qtn.octree.max_items);
    if (qtn.children[BLB].data == null) qtn.children[BLB].data = new ArrayList<>(qtn.octree.max_items);
    if (qtn.children[BRB].data == null) qtn.children[BRB].data = new ArrayList<>(qtn.octree.max_items);

    qtn.children[TRF].octree = qtn.octree;
    qtn.children[TLF].octree = qtn.octree;
    qtn.children[BLF].octree = qtn.octree;
    qtn.children[BRF].octree = qtn.octree;
    qtn.children[TRB].octree = qtn.octree;
    qtn.children[TLB].octree = qtn.octree;
    qtn.children[BLB].octree = qtn.octree;
    qtn.children[BRB].octree = qtn.octree;

    float x1 = qtn.x1;
    float y1 = qtn.y1;
    float z1 = qtn.z1;
    float cx = center_x(qtn);
    float cy = center_y(qtn);
    float cz = center_z(qtn);
    float x2 = qtn.x2;
    float y2 = qtn.y2;
    float z2 = qtn.z2;

    _set(qtn.children[TRF], qtn, cx, y1, z1, x2, cy, cz);
    _set(qtn.children[TLF], qtn, x1, y1, z1, cx, cy, cz);
    _set(qtn.children[BLF], qtn, x1, cy, z1, cx, y2, cz);
    _set(qtn.children[BRF], qtn, cx, cy, z1, x2, y2, cz);
    _set(qtn.children[TRB], qtn, cx, y1, cz, x2, cy, z2);
    _set(qtn.children[TLB], qtn, x1, y1, cz, cx, cy, z2);
    _set(qtn.children[BLB], qtn, x1, cy, cz, cx, y2, z2);
    _set(qtn.children[BRB], qtn, cx, cy, cz, x2, y2, z2);


    for (T t : qtn.data) {
        add(qtn, t);
    }

    qtn.octree.size -= qtn.data.size(); // correction
    qtn.data.clear();

    qtn.octree.n_quads_at_depth_lookup[qtn.depth + 1] += 8;
    qtn.octree.n_leafs_at_depth_lookup[qtn.depth + 1] += 8;
}


static public <T> Iterator<T> iterator(Octree_Node<T> qtn) {

    ArrayList<T> open = new ArrayList<>();
    ArrayList<Octree_Node<T>> open_Octrees = new ArrayList<>();

    open_Octrees.add(qtn);

    return new Iterator<T>() {

        @Override
        public boolean hasNext() {
            while (open.size() == 0 && open_Octrees.size() != 0) {
                Octree_Node<T> tree = remove_last(open_Octrees);
                if (has_children(tree)) {
                    open_Octrees.add(tree.children[0]);
                    open_Octrees.add(tree.children[1]);
                    open_Octrees.add(tree.children[2]);
                    open_Octrees.add(tree.children[3]);
                    open_Octrees.add(tree.children[4]);
                    open_Octrees.add(tree.children[5]);
                    open_Octrees.add(tree.children[6]);
                    open_Octrees.add(tree.children[7]);
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


static public <T> T get_closest(Octree<T> qt, float x, float y, float z) {
    return get_closest(qt.root, x, y, z);
}


static public <T> T get_closest(Octree_Node<T> qtn, float x, float y, float z) {

    T closest = null;
    float closest_dist_sq = Float.POSITIVE_INFINITY;

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> tree = remove_last(open);
        float d = dist_sq_point_to_aabb(x, y, z, tree.x1, tree.y1, tree.z1, tree.x2, tree.y2, tree.z2);
        if (d > closest_dist_sq) {
            continue;
        }

        if (has_children(tree)) {

            int rl = center_x(tree) < x ? 1 : 0;
            int bt = center_y(tree) < y ? 1 : 0;
            int fb = center_z(tree) < z ? 1 : 0;

            // order is reversed so the most optimal get's popped first
            // NOCHECKIN double check if fb logic is correct
            open.add(tree.children[(1 - fb) * 4 + (1 - bt) * 2 + (1 - rl)]); // <- worst
            open.add(tree.children[(1 - fb) * 4 + (1 - bt) * 2 + rl]);
            open.add(tree.children[(1 - fb) * 4 + bt * 2 + (1 - rl)]);
            open.add(tree.children[(1 - fb) * 4 + bt * 2 + rl]);
            open.add(tree.children[fb * 4 + (1 - bt) * 2 + (1 - rl)]);
            open.add(tree.children[fb * 4 + (1 - bt) * 2 + rl]);
            open.add(tree.children[fb * 4 + bt * 2 + (1 - rl)]);
            open.add(tree.children[fb * 4 + bt * 2 + rl]); // <- best
        } else {
            for (T t : tree.data) {
                d = dist_sq(x, y, z, x(qtn, t), y(qtn, t), z(qtn, t));
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


static public <T> T get_farthest(Octree_Node<T> qtn, float x, float y, float z) {

    T closest = null;
    float max_dist = Float.NEGATIVE_INFINITY;

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {

        Octree_Node<T> current = open.remove(open.size() - 1);

        float max_dist_to_current = max_dist_sq_point_to_corner_aabb(x, y, z, current.x1, current.y1, current.z1, current.x2,
                current.y2, current.z2);
        if (max_dist_to_current <= max_dist) {
            continue;
        }

        if (has_children(current)) {
            int rl = center_x(current) < x ? 1 : 0;
            int bt = center_y(current) < y ? 1 : 0;
            int fb = center_z(current) < z ? 1 : 0;

            // worst is added as last cause we remove the last one from open
            // NOCHECKIN double check
            open.add(current.children[fb * 4 + bt * 2 + rl]); // <- closest
            open.add(current.children[fb * 4 + bt * 2 + (1 - rl)]);
            open.add(current.children[fb * 4 + (1 - bt) * 2 + rl]);
            open.add(current.children[fb * 4 + (1 - bt) * 2 + (1 - rl)]);
            open.add(current.children[((1 - fb) * 4) + bt * 2 + rl]);
            open.add(current.children[((1 - fb) * 4) + bt * 2 + (1 - rl)]);
            open.add(current.children[((1 - fb) * 4) + (1 - bt) * 2 + rl]);
            open.add(current.children[((1 - fb) * 4) + (1 - bt) * 2 + (1 - rl)]); // <- furthest

        } else {

            for (T t : current.data) {
                float d = dist_sq(x, y, z, x(qtn, t), y(qtn, t), z(qtn, t));
                if (d > max_dist) {
                    closest = t;
                    max_dist = d;
                }
            }
        }
    }
    return closest;
}


static public <T> void get_all(Octree_Node<T> qtn, List<T> result) {
    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = remove_last(open);
        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            open.add(current.children[4]);
            open.add(current.children[5]);
            open.add(current.children[6]);
            open.add(current.children[7]);
        } else {
            result.addAll(current.data);
        }
    }
}


static public <T> List<T> get_within_radius(Octree_Node<T> qtn, float x, float y, float z, float radius) {
    ArrayList<T> result = new ArrayList<>();
    get_within_radius_sq(qtn, x, y, z, sq(radius), result);
    return result;
}


static public <T> void get_within_radius(Octree_Node<T> qtn, float x, float y, float z, float radius, List<T> result) {
    get_within_radius_sq(qtn, x, y, z, sq(radius), result);
}


static public <T> List<T> get_within_radius_sq(Octree_Node<T> qtn, float x, float y, float z, float radius_sq) {
    ArrayList<T> result = new ArrayList<>();
    get_within_radius_sq(qtn, x, y, z, radius_sq, result);
    return result;
}


static public <T> void get_within_radius_sq(Octree<T> qt, float x, float y, float z, float radius_sq, List<T> result) {
    get_within_radius_sq(qt.root, x, y, z, radius_sq, result);
}


static public <T> void get_within_radius_sq(Octree_Node<T> qtn, float x, float y, float z, float radius_sq,
        List<T> result) {

    float dist_circle_to_quad_sq = dist_sq_point_to_aabb(x, y, z, qtn.x1, qtn.y1, qtn.z1, qtn.x2, qtn.y2, qtn.z2);
    if (dist_circle_to_quad_sq > radius_sq)
        return; // octree outside sphere

    float dx = max(x - qtn.x1, qtn.x2 - x);
    float dy = max(y - qtn.y1, qtn.y2 - y);
    float dz = max(z - qtn.z1, qtn.z2 - z);

    if (radius_sq >= dx * dx + dy * dy + dz * dz) { // fully contained, add all
        get_all(qtn, result);
    } else { // intersection
        if (has_children(qtn)) {
            get_within_radius_sq(qtn.children[0], x, y, z, radius_sq, result);
            get_within_radius_sq(qtn.children[1], x, y, z, radius_sq, result);
            get_within_radius_sq(qtn.children[2], x, y, z, radius_sq, result);
            get_within_radius_sq(qtn.children[3], x, y, z, radius_sq, result);
            get_within_radius_sq(qtn.children[4], x, y, z, radius_sq, result);
            get_within_radius_sq(qtn.children[5], x, y, z, radius_sq, result);
            get_within_radius_sq(qtn.children[6], x, y, z, radius_sq, result);
            get_within_radius_sq(qtn.children[7], x, y, z, radius_sq, result);
        } else {
            for (T t : qtn.data) {
                float dist_sq = dist_sq(x, y, z, x(qtn, t), y(qtn, t), z(qtn, t));
                if (dist_sq < radius_sq) {
                    result.add(t);
                }
            }
        }
    }
}


static public <T> void get_within_aabb(Octree<T> qt, float _r_x1, float _r_y1, float _r_z1, float _r_x2, float _r_y2, float _r_z2,
        List<T> result) {
    get_within_aabb(qt.root, _r_x1, _r_y1, _r_z1, _r_x2, _r_y2, _r_z2, result);
}


static public <T> void get_within_aabb(Octree_Node<T> qtn, float _r_x1, float _r_y1, float _r_z1, float _r_x2, float _r_y2, float _r_z2,
        List<T> result) {

    float r_x1 = min(_r_x1, _r_x2);
    float r_x2 = max(_r_x1, _r_x2);
    float r_y1 = min(_r_y1, _r_y2);
    float r_y2 = max(_r_y1, _r_y2);
    float r_z1 = min(_r_z1, _r_z2);
    float r_z2 = max(_r_z1, _r_z2);

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = open.remove(open.size() - 1);

        if (aabb_contains_aabb(r_x1, r_y1, r_z1, r_x2, r_y2, r_z2, current.x1, current.y1, current.z1, current.x2, current.y2, current.z2)) {
            get_all(current, result);
        } else if (intersects_cube(current, r_x1, r_y1, r_z1, r_x2, r_y2, r_z2)) {
            if (has_children(current)) {
                open.add(current.children[0]);
                open.add(current.children[1]);
                open.add(current.children[2]);
                open.add(current.children[3]);
                open.add(current.children[4]);
                open.add(current.children[5]);
                open.add(current.children[6]);
                open.add(current.children[7]);
            } else {
                for (T t : current.data) {
                    float x = x(qtn, t);
                    float y = y(qtn, t);
                    float z = z(qtn, t);
                    if (!(x < r_x1 || x > r_x2 || y < r_y1 || y > r_y2 || z < r_z1 || z > r_z2)) {
                        result.add(t);
                    }
                }
            }
        }

    }
}


static public <T> List<T> get_outside_aabb(Octree_Node<T> qtn, float r_x1, float r_y1, float r_z1, float r_x2, float r_y2, float r_z2) {
    ArrayList<T> result = new ArrayList<>();
    get_outside_aabb(qtn, r_x1, r_y1, r_z1, r_x2, r_y2, r_z2, result);
    return result;
}



static public <T> void get_outside_aabb(Octree_Node<T> qtn, float _r_x1, float _r_y1, float _r_z1, float _r_x2, float _r_y2, float _r_z2,
        List<T> result) {

    float r_x1 = min(_r_x1, _r_x2);
    float r_x2 = max(_r_x1, _r_x2);
    float r_y1 = min(_r_y1, _r_y2);
    float r_y2 = max(_r_y1, _r_y2);
    float r_z1 = min(_r_z1, _r_z2);
    float r_z2 = max(_r_z1, _r_z2);

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = open.remove(open.size() - 1);

        if (!intersects_cube(current, r_x1, r_y1, r_z1, r_x2, r_y2, r_z2)) {
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
            } else {
                for (T t : current.data) {
                    float x = x(qtn, t);
                    float y = y(qtn, t);
                    float z = z(qtn, t);
                    if ((x < r_x1 || x > r_x2 || y < r_y1 || y > r_y2 || z < r_z1 || z > r_z2)) {
                        result.add(t);
                    }
                }
            }
        }
    }
}


static public <T> void get_closest_n(Octree_Node<T> qtn, float x, float y, float z, int n, List<T> result, ArrayList<T> buffer) {

    int incoming_result_size = result.size();

    if (n <= 0)
        return;

    if (n >= qtn.octree.size) {
        get_all(qtn, result);
        return;
    }

    if (n == 1) {
        T t = get_closest(qtn, x, y, z);
        if (t != null)
            result.add(t);
        return;
    }

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    ArrayList<Octree_Node<T>> leafs = new ArrayList<>();
    int items_in_leafs = 0;

    Octree_Node<T> current = null;
    Octree_Node<T> overflow = null;

    // We wan't to find leafs till we hit N,
    // without looking at the individual elements
    // Therefor we keep getting the closest tree
    // till we hit N
    while (open.size() > 0) {

        Collections.sort(open, (a, b)-> {
            float dist_a = dist_sq_point_to_aabb(x, y, z, a.x1, a.y1, a.z1, a.x2, a.y2, a.z2);
            float dist_b = dist_sq_point_to_aabb(x, y, z, b.x1, b.y1, b.z1, b.x2, b.y2, b.z2);
            if (dist_a < dist_b) return 1;
            if (dist_a > dist_b) return -1;
            return 0;
        });

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
        }
        else {

            if (items_in_leafs + current.data.size() >= n) {
                overflow = current;
                open.add(current); // we add it back for later
                break;
            }

            leafs.add(current);
            items_in_leafs += current.data.size();
        }
    }

    // Now we know at which node we overflowed, we can add
    // from every leaf as long as the furtherest point of that
    // leaf is closer then the closest point of the overflow leaf

    float overflow_leaf_dist_sq = dist_sq_point_to_aabb(x, y, z, overflow.x1, overflow.y1, overflow.z1, overflow.x2, overflow.y2, overflow.z2);

    for (Octree_Node<T> node : leafs) {
        float furtherest_point_sq = max_dist_sq_point_to_corner_aabb(x, y, z, node.x1, node.y1, node.z1, node.x2, node.y2, node.z2);
        if (furtherest_point_sq <= overflow_leaf_dist_sq) {
            result.addAll(node.data);
        }
        else {
            open.add(node);
        }
    }

    // Now we need to check points individually, but we also might need more leafs
    // We can ignore all leafs that have the closest point further away then
    // furthest point in the overflow leaf
    float max_dist_sq_to_overflow_point = -1;
    for (T t : overflow) {
        float vx = x(overflow, t);
        float vy = y(overflow, t);
        float vz = z(overflow, t);
        float dist_sq = dist_sq(x, y,z, vx, vy, vz);
        if (dist_sq > max_dist_sq_to_overflow_point) max_dist_sq_to_overflow_point = dist_sq;
    }

    // We will fill a buffer with points that we need to check individually
    buffer.clear();

    while (open.size() > 0) {
        current = remove_last(open);
        float closest_dist_sq = dist_sq_point_to_aabb(x, y, z, current.x1, current.y1, current.z1, current.x2, current.y2, current.z2);
        if (closest_dist_sq > max_dist_sq_to_overflow_point) {
            continue;
        }

        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            open.add(current.children[4]);
            open.add(current.children[5]);
            open.add(current.children[6]);
            open.add(current.children[7]);            
        }
        else {
            buffer.addAll(current.data);
        }
    }

    // Sort the buffer
    Collections.sort(buffer, (a, b)-> {
        float dist_a = dist_sq(x, y, z, x(qtn, a), y(qtn, a), z(qtn, a));
        float dist_b = dist_sq(x, y, z, x(qtn, b), y(qtn, b), z(qtn, b));
        if (dist_a < dist_b) return -1;
        if (dist_a > dist_b) return  1;
        return 0;
    });


    int remaining_n = n - (result.size() - incoming_result_size);

    for (int i = 0; i < remaining_n; i++) {
        result.add(buffer.get(i));
    }
}


static public <T> T min_x(Octree<T> qt) {
    return min_x(qt.root);
}


static public <T> T min_x(Octree_Node<T> qtn) {

    if (qtn.octree.size == 0)
        return null;

    T best = null;
    float min_x = Float.POSITIVE_INFINITY;

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = remove_last(open);
        if (min_x < current.x1)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BRF]);
            open.add(current.children[BRB]);
            open.add(current.children[TRF]);
            open.add(current.children[TRB]);
            open.add(current.children[BLF]);
            open.add(current.children[BLB]);
            open.add(current.children[TLF]);
            open.add(current.children[TLB]);
            
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


static public <T> T min_y(Octree<T> qt) {
    return min_y(qt.root);
}


static public <T> T min_y(Octree_Node<T> qtn) {

    if (qtn.octree.size == 0)
        return null;

    T best = null;
    float min_y = Float.POSITIVE_INFINITY;

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = remove_last(open);
        if (min_y < current.y1)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BRF]);
            open.add(current.children[BRB]);
            open.add(current.children[BLF]);
            open.add(current.children[BLB]);
            open.add(current.children[TRF]);
            open.add(current.children[TRB]);
            open.add(current.children[TLF]);
            open.add(current.children[TLB]);
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


static public <T> T min_z(Octree<T> qt) {
    return min_z(qt.root);
}


static public <T> T min_z(Octree_Node<T> qtn) {

    if (qtn.octree.size == 0)
        return null;

    T best = null;
    float min_z = Float.POSITIVE_INFINITY;

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = remove_last(open);
        if (min_z < current.z1)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BRB]);
            open.add(current.children[BLB]);
            open.add(current.children[TRB]);
            open.add(current.children[TLB]);
            open.add(current.children[BRF]);
            open.add(current.children[BLF]);
            open.add(current.children[TRF]);
            open.add(current.children[TLF]);
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


static public <T> T max_x(Octree<T> qt) {
    return max_x(qt.root);
}


static public <T> T max_x(Octree_Node<T> qtn) {

    if (qtn.octree.size == 0)
        return null;

    T best = null;
    float max_x = Float.NEGATIVE_INFINITY;

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = remove_last(open);
        if (max_x > current.x2)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BLF]);
            open.add(current.children[BLB]);
            open.add(current.children[TLF]);
            open.add(current.children[TLB]);
            open.add(current.children[BRF]);
            open.add(current.children[BRB]);
            open.add(current.children[TRF]);
            open.add(current.children[TRB]);
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


static public <T> T max_y(Octree<T> qt) {
    return max_y(qt.root);
}


static public <T> T max_y(Octree_Node<T> qtn) {

    if (qtn.octree.size == 0)
        return null;

    T best = null;
    float max_y = Float.NEGATIVE_INFINITY;

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = remove_last(open);
        if (max_y > current.y2)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[TLF]);
            open.add(current.children[TLB]);
            open.add(current.children[TRF]);
            open.add(current.children[TRB]);
            open.add(current.children[BLF]);
            open.add(current.children[BLB]);
            open.add(current.children[BRF]);
            open.add(current.children[BRB]);
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


static public <T> T max_z(Octree<T> qt) {
    return max_z(qt.root);
}


static public <T> T max_z(Octree_Node<T> qtn) {

    if (qtn.octree.size == 0)
        return null;

    T best = null;
    float max_z = Float.NEGATIVE_INFINITY;

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = remove_last(open);
        if (max_z > current.y2)
            continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BLF]);
            open.add(current.children[TLF]);
            open.add(current.children[BRF]);
            open.add(current.children[TRF]);
            open.add(current.children[BLB]);
            open.add(current.children[TLB]);
            open.add(current.children[BRB]);
            open.add(current.children[TRB]);
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


static public <T> void clear(Octree<T> qt) {
    clear(qt.root);
}


static public <T> void clear(Octree_Node<T> qtn) {

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = remove_last(open);
        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            open.add(current.children[4]);
            open.add(current.children[5]);
            open.add(current.children[6]);
            open.add(current.children[7]);
            current.children[0] = null;
            current.children[1] = null;
            current.children[2] = null;
            current.children[3] = null;
            current.children[4] = null;
            current.children[5] = null;
            current.children[6] = null;
            current.children[7] = null;
            qtn.octree.allocator.free(current);
        } else {
            current.data.clear();
        }
    }
    qtn.octree.size = 0; // check lookup thing
}


static public <T> int size(Octree_Node<T> qtn) {
    return qtn.octree.size;
}


static public <T> boolean remove(Octree<T> qt, T t) {
    return remove(qt.root, t);
}


static public <T> boolean remove(Octree_Node<T> qtn, T t) {

    float x = x(qtn, t);
    float y = y(qtn, t);
    float z = z(qtn, t);

    qtn = get_leaf(qtn, x, y, z);
    if (swap_remove(qtn.data, t) != null) {
        qtn.octree.size--;
        return true;
    }
    return false;
}


static public <T> boolean merge(Octree_Node<T> qtn) {

    if (!has_children(qtn)) {
        return false;
    }

    if (has_children(qtn.children[0]) || has_children(qtn.children[1]) || has_children(qtn.children[2]) || has_children(qtn.children[3]) ||
        has_children(qtn.children[4]) || has_children(qtn.children[5]) || has_children(qtn.children[6]) || has_children(qtn.children[7])
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
        

    // The merge_threshold should be between 0 and 1. The higher it
    // is the more likely it will do a merge. If the amount of times
    // is less then the max_items * merge_threshold then a merge
    // will happen. For example if max_items is 32, then:
    // 32 * 0.75 = merge when items drop below 24
    // 32 * 0.50 = merge when items drop below 16
    // 32 * 0.25 = merge when items drop below 8

    if (count < (qtn.octree.max_items * qtn.octree.merge_threshold)) {
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

        qtn.children[0].data.clear();
        qtn.children[1].data.clear();
        qtn.children[2].data.clear();
        qtn.children[3].data.clear();
        qtn.children[4].data.clear();
        qtn.children[5].data.clear();
        qtn.children[6].data.clear();
        qtn.children[7].data.clear();

        qtn.octree.allocator.free(qtn.children[0]);
        qtn.octree.allocator.free(qtn.children[1]);
        qtn.octree.allocator.free(qtn.children[2]);
        qtn.octree.allocator.free(qtn.children[3]);
        qtn.octree.allocator.free(qtn.children[4]);
        qtn.octree.allocator.free(qtn.children[5]);
        qtn.octree.allocator.free(qtn.children[6]);
        qtn.octree.allocator.free(qtn.children[7]);

        qtn.children[0] = null;
        qtn.children[1] = null;
        qtn.children[2] = null;
        qtn.children[3] = null;
        qtn.children[4] = null;
        qtn.children[5] = null;
        qtn.children[6] = null;
        qtn.children[7] = null;

        qtn.octree.n_quads_at_depth_lookup[qtn.depth + 1] -= 8;
        qtn.octree.n_leafs_at_depth_lookup[qtn.depth + 1] -= 8;
        qtn.octree.n_leafs_at_depth_lookup[qtn.depth] += 1;

        return true;
    }
    return false;
}


static public <T> void merge_update(Octree<T> qt) {
    merge_update(qt.root);
}


static public <T> void merge_update(Octree_Node<T> qtn) {

    ArrayList<Octree_Node<T>> open = new ArrayList<>();
    open.add(qtn);

    while (open.size() > 0) {
        Octree_Node<T> current = remove_last(open);

        if (!merge(current) && has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            open.add(current.children[4]);
            open.add(current.children[5]);
            open.add(current.children[6]);
            open.add(current.children[7]);
        }
    }
}


static public <T> void update(Octree<T> qt, ArrayList<T> update_helper, ArrayList<T> out_of_bounds) {
    update(qt.root, update_helper, out_of_bounds);
}


static public <T> void update(Octree_Node<T> qtn, ArrayList<T> update_helper, ArrayList<T> out_of_bounds) {

    if (qtn.depth == 0 && !has_children(qtn)) {
        for (int i = qtn.data.size()-1; i >= 0; i--) {
            T t = qtn.data.get(i);
            float x = x(qtn, t);
            float y = y(qtn, t);
            float z = z(qtn, t);
            if (point_outside_aabb(x, y, z, qtn.x1, qtn.y1, qtn.z1, qtn.x2, qtn.y2, qtn.z2)) {
                out_of_bounds.add(swap_remove(qtn.data, i));
            }
        }
        return;
    }

    Iterator<Octree_Node<T>> itr = get_iterator(qtn, Iterator_Type.LEAFS);

    while (itr.hasNext()) {
        Octree_Node<T> n = itr.next();
        
        if (n.data.size() > 0) {
            for (int i = n.data.size()-1; i >= 0; i--) {

                T t = n.data.get(i);
                float x = x(n, t);
                float y = y(n, t);
                float z = z(n, t);

                if (point_outside_aabb(x, y, z, n.x1, n.y1, n.z1, n.x2, n.y2, n.z2)) {
                    swap_remove(n.data, i);
                    update_helper.add(t);
                }
            }
        }
    }


    for (T t : update_helper) {

        Octree_Node<T> n = get_leaf(qtn, x(qtn, t), y(qtn, t), z(qtn, t));

        if (n != null) {
            n.data.add(t);
            if (n.data.size() > n.octree.max_items) {
                split(n);
            }
        }
        else {
            out_of_bounds.add(t);
            qtn.octree.size -= 1;
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


static public <T> Iterator<Octree_Node<T>> get_iterator(Octree_Node<T> qtn, Iterator_Type iterator_type) {

    if (iterator_type == Iterator_Type.DEPTH_FIRST) {

        ArrayList<Octree_Node<T>> _open = new ArrayList<>();
        _open.add(qtn);
        
        return new Iterator<SFJL_Octree.Octree_Node<T>>(){
            
            ArrayList<Octree_Node<T>> open = _open;
            
            @Override
            public boolean hasNext() {
                return open.size() > 0;
            }
            
            @Override
            public Octree_Node<T> next() {
                Octree_Node<T> r = remove_last(open);
                if (has_children(r)) {
                    open.add(r.children[0]);
                    open.add(r.children[1]);
                    open.add(r.children[2]);
                    open.add(r.children[3]);
                    open.add(r.children[4]);
                    open.add(r.children[5]);
                    open.add(r.children[6]);
                    open.add(r.children[7]);
                }
                return r;
            }
        };
    }
    else if (iterator_type == Iterator_Type.BREADTH_FIRST) {

        ArrayList<Octree_Node<T>> _current_depth = new ArrayList<>();
        _current_depth.add(qtn);

        return new Iterator<SFJL_Octree.Octree_Node<T>>(){

            ArrayList<Octree_Node<T>> current_depth = _current_depth;
            ArrayList<Octree_Node<T>> next_depth = new ArrayList<>(16);

            @Override
            public boolean hasNext() {
                return current_depth.size() > 0;
            }

            @Override
            public Octree_Node<T> next() {
                Octree_Node<T> r = swap_remove(current_depth, 0);
                if (has_children(r)) {
                    next_depth.add(r.children[0]);
                    next_depth.add(r.children[1]);
                    next_depth.add(r.children[2]);
                    next_depth.add(r.children[3]);
                    next_depth.add(r.children[4]);
                    next_depth.add(r.children[5]);
                    next_depth.add(r.children[6]);
                    next_depth.add(r.children[7]);
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
    else if (iterator_type == Iterator_Type.LEAFS) {

        ArrayList<Octree_Node<T>> open       = new ArrayList<>();
        ArrayList<Octree_Node<T>> open_leafs = new ArrayList<>();
        open.add(qtn);

        // find the first leaf
        while (true) {
            Octree_Node<T> n = remove_last(open);
            if (has_children(n)) {
                open.add(n.children[0]);
                open.add(n.children[1]);
                open.add(n.children[2]);
                open.add(n.children[3]);
                open.add(n.children[4]);
                open.add(n.children[5]);
                open.add(n.children[6]);
                open.add(n.children[7]);
            }
            else {
                open_leafs.add(n);
                break;
            }
        }
        
        return new Iterator<SFJL_Octree.Octree_Node<T>>(){
            
            @Override
            public boolean hasNext() {
                return open_leafs.size() > 0;
            }
            
            @Override
            public Octree_Node<T> next() {
                Octree_Node<T> r = remove_last(open_leafs);
                while (open_leafs.size() == 0 && open.size() > 0) {
                    Octree_Node<T> n = remove_last(open);
                    if (has_children(n)) {
                        open.add(n.children[0]);
                        open.add(n.children[1]);
                        open.add(n.children[2]);
                        open.add(n.children[3]);
                        open.add(n.children[4]);
                        open.add(n.children[5]);
                        open.add(n.children[6]);
                        open.add(n.children[7]);
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



static public <T> int highest_depth_with_leafs(Octree<T> qt) {
    return highest_depth_with_leafs(qt.root);
}


static public <T> int highest_depth_with_leafs(Octree_Node<T> qtn) {

    int[] n_leafs_at_depth_lookup = qtn.octree.n_leafs_at_depth_lookup;

    for (int i = n_leafs_at_depth_lookup.length-1; i >= 0; i--) {
        if (n_leafs_at_depth_lookup[i] != 0) {
            return i;
        }
    }  
    throw new RuntimeException("unreachable");
}


static public <T> int lowest_depth_with_leafs(Octree<T> qt) {
    return lowest_depth_with_leafs(qt.root);
}


static public <T> int lowest_depth_with_leafs(Octree_Node<T> qtn) {

    int[] n_leafs_at_depth_lookup = qtn.octree.n_leafs_at_depth_lookup;

    for (int i = 0; i < n_leafs_at_depth_lookup.length; i++) {
        if (n_leafs_at_depth_lookup[i] != 0) {
            return i;
        }
    }  
    throw new RuntimeException("unreachable");
}


static public <T> boolean intersects_cube(Octree_Node<T> tree, float x1, float y1, float z1, float x2, float y2, float z2) {
    return !(tree.x1 > x2 || 
    tree.x2 < x1 || 
    tree.y1 > y2 ||
    tree.y2 < y1 ||
    tree.z1 > z2 ||
    tree.z2 < z1);
}
   
}
/**
revision history:
    0.51  (2022-01-30) fixed bug in comparator
    0.50  (2022-01-06) first numbered version

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