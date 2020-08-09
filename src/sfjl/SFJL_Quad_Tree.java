package sfjl;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import static java.lang.Math.*;
import static sfjl.SFJL_Math.*;

/*
- make highest_depth_with_items an array?



*/


                                                          // SFJL_Quad_Tree
                                                public class SFJL_Quad_Tree {
                                                    private SFJL_Quad_Tree() {}
  
    

// order is important
public final static int TL = 0;
public final static int TR = 1;
public final static int BL = 2;
public final static int BR = 3;


public interface X<T> {
    public float x(T t);
}

public interface Y<T> {
    public float y(T t);
}


static public class Quad_Tree<T> implements Iterable<T> {
    public int max_items;
    public X<T> x;
    public Y<T> y;
    public int size = 0;
    public int[] n_items_at_depth_lookup = new int[64];

    public Quad_Tree_Node<T> root;
    public ArrayList<Quad_Tree_Node<T>> node_buffer = new ArrayList<>();
    
    public Quad_Tree(X<T> x, Y<T> y, int max_items, float x1, float y1, float x2, float y2) {
        this.x = x;
        this.y = y;
        assert(max_items > 0);
        this.max_items = max_items;
        root = new Quad_Tree_Node<>(this);
        set(root, null, x1, y1, x2, y2);
    }

    @Override
    public Iterator<T> iterator() {
        return SFJL_Quad_Tree.iterator(root);
    }
}



static public class Quad_Tree_Node<T> implements Iterable<T> {
    public Quad_Tree<T> part_of_tree;
    public Quad_Tree_Node<T> parent;
    @SuppressWarnings("unchecked")
    public Quad_Tree_Node<T>[] children = new Quad_Tree_Node[4];
    public ArrayList<T> data;
    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public int depth;

    public Quad_Tree_Node(Quad_Tree<T> part_of_tree) { 
        this.part_of_tree = part_of_tree;
        this.data = new ArrayList<>(part_of_tree.max_items);
    }

    @Override
    public Iterator<T> iterator() {
        return SFJL_Quad_Tree.iterator(this);
    }
    
}

static public <T> float x(Quad_Tree_Node<T> n, T t) { 
    return n.part_of_tree.x.x(t);
}


static public <T> float y(Quad_Tree_Node<T> n, T t) { 
    return n.part_of_tree.y.y(t);
}


static public <T> float center_x(Quad_Tree_Node<T> n) {
    return n.x1 + (n.x2 - n.x1) / 2;
}


static public <T> float center_y(Quad_Tree_Node<T> n) {
    return n.y1 + (n.y2 - n.y1) / 2;
}


static public <T> boolean has_children(Quad_Tree_Node<T> n) {
    return n.children[0] != null;
}


static public <T> boolean is_root(Quad_Tree_Node<T> n) {
    return n.parent == null;
}


static public <T> int get_optimal_index(Quad_Tree_Node<T> n, float x, float y) {
    if (y < center_y(n)) return x < center_x(n) ? TL : TR;
    else                 return x < center_x(n) ? BL : BR;
}




static public <T> void add(Quad_Tree<T> qt, T t) {
    add(qt.root, t);
}


static public <T> void add(Quad_Tree_Node<T> qt, T t) {
    add(qt, t, x(qt, t), y(qt, t));
}


static public <T> void add(Quad_Tree_Node<T> qt, T t, float x, float y) {
    
    if (qt.depth == 0) {
        if (x < qt.x1 || x > qt.x2 || y < qt.y1 || y > qt.y2) {
            // TODO return false?
            return;
        }
    }
    
    if (has_children(qt)) {
        int where = get_optimal_index(qt, x, y);
        add(qt.children[where], t, x, y);
    }
    else {
        qt.data.add(t);
        qt.part_of_tree.n_items_at_depth_lookup[qt.depth] += 1;
        qt.part_of_tree.size++;
        if (qt.data.size() > qt.part_of_tree.max_items) {
            split(qt);
        }
    }
}


static public <T> void add_all(Quad_Tree_Node<T> quad_tree, List<T> items) {
    for (T t : items) {
        add(quad_tree, t);
    }
}



static public <T> Quad_Tree_Node<T> get_or_create_node(Quad_Tree<T> qt) {
    Quad_Tree_Node<T> t = remove_last(qt.node_buffer);
    if (t == null) {
        t = new Quad_Tree_Node<>(qt);
    }
    return t;
}


static public <T> void set(Quad_Tree_Node<T> qtn, Quad_Tree_Node<T> parent, float x1, float y1, float x2, float y2) {
    qtn.parent = parent;
    qtn.x1 = x1;
    qtn.y1 = y1;
    qtn.x2 = x2;
    qtn.y2 = y2;
    qtn.depth = parent == null ? 0 : parent.depth+1;
}


static public <T> void split(Quad_Tree_Node<T> qtn) {

    qtn.children[TR] = get_or_create_node(qtn.part_of_tree);
    qtn.children[TL] = get_or_create_node(qtn.part_of_tree);
    qtn.children[BL] = get_or_create_node(qtn.part_of_tree);
    qtn.children[BR] = get_or_create_node(qtn.part_of_tree);
    
    set(qtn.children[TR], qtn, center_x(qtn), qtn.y1, qtn.x2, center_y(qtn));
    set(qtn.children[TL], qtn, qtn.x1, qtn.y1, center_x(qtn), center_y(qtn));
    set(qtn.children[BL], qtn, qtn.x1, center_y(qtn), center_x(qtn), qtn.y2);
    set(qtn.children[BR], qtn, center_x(qtn), center_y(qtn), qtn.x2, qtn.y2);

    for (T t : qtn.data) {
        int where = get_optimal_index(qtn, x(qtn, t), y(qtn, t));
        add(qtn.children[where], t);
    }

    qtn.part_of_tree.n_items_at_depth_lookup[qtn.depth] -= qtn.data.size();
    qtn.part_of_tree.size -= qtn.data.size(); // correction
    qtn.data.clear();

}



    
static public <T> Iterator<T> iterator(Quad_Tree_Node<T> qt) {
        
    ArrayList<T> open = new ArrayList<>();
    ArrayList<Quad_Tree_Node<T>> open_quad_trees = new ArrayList<>();
    
    open_quad_trees.add(qt);
    
    return new Iterator<T>() {
        
        @Override
        public boolean hasNext() {
            while (open.size() == 0 && open_quad_trees.size() != 0) {
                Quad_Tree_Node<T> tree = remove_last(open_quad_trees);
                if (has_children(tree)) {
                    open_quad_trees.add(tree.children[0]);
                    open_quad_trees.add(tree.children[1]);
                    open_quad_trees.add(tree.children[2]);
                    open_quad_trees.add(tree.children[3]);
                }
                else {
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



static public <T> T get_closest(Quad_Tree<T> qt, float x, float y) {
    return get_closest(qt.root, x, y);
}


static public <T> T get_closest(Quad_Tree_Node<T> qt, float x, float y) {
    
    T closest = null;
    float closest_dist_sq = Float.POSITIVE_INFINITY;
    
    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);
    
    while (open.size() > 0) {
        Quad_Tree_Node<T> tree =  remove_last(open);
        float d = dist_sq_point_to_aabb(x, y, tree.x1, tree.y1, tree.x2, tree.y2);
        if (d > closest_dist_sq) {
            continue;
        }
        
        if (has_children(tree)) {
            
            int rl = center_x(tree) < x ? 1 : 0;
            int bt = center_y(tree) < y ? 1 : 0;
            
            // order is reversed so the most optimal get's popped first
            open.add(tree.children[(1-bt)*2+(1-rl)]); // <- worst
            open.add(tree.children[(1-bt)*2+rl]);
            open.add(tree.children[bt*2+(1-rl)]);
            open.add(tree.children[bt*2+rl]); // <- best
        }
        else {
            for (T t : tree.data) {
                d = dist_sq(x, y, x(qt, t), y(qt, t));
                if (d < closest_dist_sq) {
                    closest_dist_sq = d;
                    closest = t;
                    if (d == 0) return closest; 
                }
            }
        }
        
    }
    return closest;
}


static public <T> T get_farthest(Quad_Tree_Node<T> qt, float x, float y) {

    T closest = null;
    float max_dist = Float.NEGATIVE_INFINITY;

    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);

    while (open.size() > 0) {

        Quad_Tree_Node<T> current = open.remove(open.size()-1);

        float max_dist_to_current = max_dist_sq_point_to_corner_aabb(x, y, current.x1, current.y1, current.x2, current.y2);
        if (max_dist_to_current <= max_dist) {
            continue;
        }

        if (has_children(current)) {
            int rl = center_x(current) < x ? 1 : 0;
            int bt = center_y(current) < y ? 1 : 0;
            
            // wost is added as last cause we remove the last one from open
            open.add(current.children[bt*2+rl]); // <- closest
            open.add(current.children[bt*2+(1-rl)]);
            open.add(current.children[(1-bt)*2+rl]);
            open.add(current.children[(1-bt)*2+(1-rl)]); // <- furthest
            
        }
        else {
            
            for (T t : current.data) {
                float d = dist_sq(x, y, x(qt, t), y(qt, t));
                if (d > max_dist) {
                    closest = t;
                    max_dist = d;
                }
            }
        }
    }
    return closest;
}


static public <T> void get_farthest_n(Quad_Tree_Node<T> qt, float x, float y, int n, ArrayList<T> result) {
    
    if (n <= 0) return;

    if (n == 1) {
        T t = get_closest(qt, x, y);
        result.add(t);
        return;
    }

    if (n >= size(qt)) {
        add_all(qt, result);
        return;
    }
    // do size() -1 and get closest, return all others?

    // TODO
    //float max_dist = Float.NEGATIVE_INFINITY;


    // TODO TODO TODO
    // use closest dist to quad as well?
    // could avoid sorting in certain cases?
    // can also overcomplicate things?
    // TODO TODO TODO
    // TODO TODO TODO
    // TODO TODO TODO
    // FIRST WE WANT TO ADD TILL WE GET THE OVERFLOW QUAD
    // THEN ADD FROM ALL QUADS THAT ARE FURTHER AWAY THEN THE OVERFLOW
    // QUADS
    // After that figure out the remaining

    ArrayList<Quad_Tree_Node<T>> leafs = new ArrayList<>();
    int items_in_leafs = 0;

    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);

    boolean do_add_containing = true;

    Quad_Tree_Node<T> current = null;

    while (open.size() > 0) {

        // TODO IMPORTANT does this recreate the comparator every iteration
        open.sort(new Comparator<Quad_Tree_Node<T>>() {
            @Override
            public int compare(Quad_Tree_Node<T> o1, Quad_Tree_Node<T> o2) {
                
                // TODO, I feel like this can be done way more efficient

                float d1 = 0;
                if (has_children(o1)) {
                    if (point_outside_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2)) {
                        d1 = max_dist_sq_point_to_corner_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2);
                    }
                }
                else {
                    d1 = dist_sq_point_to_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2);
                }

                float d2 = 0;
                if (has_children(o2)) {
                    if (point_outside_aabb(x, y, o2.x1, o2.y1, o2.x2, o2.y2)) {
                        d2 = max_dist_sq_point_to_corner_aabb(x, y, o2.x1, o2.y1, o2.x2, o2.y2);
                    }
                }
                else {
                    d2 = dist_sq_point_to_aabb(x, y, o2.x1, o2.y1, o2.x2, o2.y2);
                }

                
                if (d1 < d2) return -1;
                if (d1 > d2) return  1;
                return 0;
            }
        });

        current = open.remove(open.size()-1);

        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
        }
        else {
            
            leafs.add(current);
            items_in_leafs += current.data.size();

            if (items_in_leafs >= n) {
                
                if (leafs.size() <= 4) {
                    do_add_containing = false;
                }
                break;
            }
        }
    }

    if (do_add_containing) {
        // TODO 
        float dist_point_to_overflow_leaf = max_dist_sq_point_to_corner_aabb(x, y, current.x1, current.y1, current.x2, current.y2);
        
        for (int i = leafs.size()-1; i >= 0; i--) {
        
            Quad_Tree_Node<T> leaf = leafs.get(i);
            float min_dist_point_to_leaf = dist_sq_point_to_aabb(x, y, leaf.x1, leaf.y1, leaf.x2, leaf.y2);

            if (min_dist_point_to_leaf >= dist_point_to_overflow_leaf) {
                result.addAll(leaf.data);
                leafs.remove(i);
            }
        }
    }


    /*

    float closest_dist_so_far = Float.POSITIVE_INFINITY;

    

    ArrayList<T> buffer = new ArrayList<>();

    

        if (buffer.size() >= n) {
            float max_dist_to_current = dist_sq_farthest_point_to_aabb_no_zero(x, y, current.x1, current.y1, current.x2, current.y2);
            if (max_dist_to_current <= closest_dist_so_far) {
                continue; // maybe we can break but there might be edge cases!
            }
        }

        if (current.has_children()) {
            int rl = current.center_x() < x ? 1 : 0;
            int bt = current.center_y() < y ? 1 : 0;
            
            // wost is added as last cause we remove the last one from open
            open.add(current.children[bt*2+rl]); // <- closest
            open.add(current.children[bt*2+(1-rl)]);
            open.add(current.children[(1-bt)*2+rl]);
            open.add(current.children[(1-bt)*2+(1-rl)]); // <- furthest
            
        }
        else {
            
            buffer.addAll(current.data);
            buffer.sort(new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    float d1 = dist_sq(x, y, x(o1), y(o1));
                    float d2 = dist_sq(x, y, x(o2), y(o2));
                    if (d1 < d2) return -1;
                    if (d2 < d1) return 1;
                    return 0;
                }
            });

            // TODO check how distance is sorted

            if (buffer.size() >= n) {
                T closest = buffer.get(max(0, buffer.size()-n));
                float d = dist_sq(x, y, x(closest), y(closest));
                if (d < closest_dist_so_far) {
                    closest_dist_so_far = d;
                }
            }
        }
    }

    for (int i = 0; i < n; i++) {
        T t = buffer.get(i);
        result.add(t);
    }
    */

}


static public <T> void get_all(Quad_Tree_Node<T> qt, List<T> result) {
    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);
    
    while (open.size() > 0) {
        Quad_Tree_Node<T> current = remove_last(open);
        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
        }
        else {
            result.addAll(current.data);
        }
    }
}


static public <T> List<T> get_within_radius(Quad_Tree_Node<T> qt, float x, float y, float radius) {
    ArrayList<T> result = new ArrayList<>();
    get_within_radius_sq(qt, x, y, sq(radius), result);
    return result;   
}


static public <T> void get_within_radius(Quad_Tree_Node<T> qt, float x, float y, float radius, List<T> result) {
    get_within_radius_sq(qt, x, y, sq(radius), result);
}


static public <T> List<T> get_within_radius_sq(Quad_Tree_Node<T> qt, float x, float y, float radius_sq) {
    ArrayList<T> result = new ArrayList<>();
    get_within_radius_sq(qt, x, y, radius_sq, result);
    return result;
}


static public <T> void get_within_radius_sq(Quad_Tree<T> qt, float x, float y, float radius_sq, List<T> result) {
    get_within_radius_sq(qt.root, x, y, radius_sq, result);
}

static public <T> void get_within_radius_sq(Quad_Tree_Node<T> qt, float x, float y, float radius_sq, List<T> result) {
    
    float dist_circle_to_quad_sq = dist_sq_point_to_aabb(x, y, qt.x1, qt.y1, qt.x2, qt.y2);
    if (dist_circle_to_quad_sq > radius_sq) return; // quadtree outside circle
    
    float dx = max(x - qt.x1, qt.x2 - x); 
    float dy = max(y - qt.y1, qt.y2 - y);
    
    if (radius_sq >= dx*dx + dy*dy) { // fully contained, add all
        get_all(qt, result);
    }
    else { // intersection
        if (has_children(qt)) {
            get_within_radius_sq(qt.children[0], x, y, radius_sq, result);
            get_within_radius_sq(qt.children[1], x, y, radius_sq, result);
            get_within_radius_sq(qt.children[2], x, y, radius_sq, result);
            get_within_radius_sq(qt.children[3], x, y, radius_sq, result);
        }
        else {
            for (T t : qt.data) {
                float dist_sq = dist_sq(x, y, x(qt, t), y(qt, t));
                if (dist_sq < radius_sq) {
                    result.add(t);
                }
            }
        }
    }
    
}


static public <T> List<T> get_within_aabb(Quad_Tree_Node<T> qt, float r_x1, float r_y1, float r_x2, float r_y2) {
    ArrayList<T> result = new ArrayList<>();
    get_within_aabb(qt, r_x1, r_y1, r_x2, r_y2, result);
    return result;  
}

static public <T> void get_within_aabb(Quad_Tree<T> qt, float _r_x1, float _r_y1, float _r_x2, float _r_y2, List<T> result) {
    get_within_aabb(qt.root, _r_x1, _r_y1, _r_x2, _r_y2, result);
}


static public <T> void get_within_aabb(Quad_Tree_Node<T> qt, float _r_x1, float _r_y1, float _r_x2, float _r_y2, List<T> result) {
    
    float r_x1 = min(_r_x1, _r_x2);
    float r_x2 = max(_r_x1, _r_x2);
    float r_y1 = min(_r_y1, _r_y2);
    float r_y2 = max(_r_y1, _r_y2);

    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);

    while (open.size() > 0) {
        Quad_Tree_Node<T> current = open.remove(open.size()-1);

        if (rect_contains_rect(r_x1, r_y1, r_x2, r_y2, current.x1, current.y1, current.x2, current.y2)) {
            get_all(current, result);
        }
        else if (intersects_rect(r_x1, r_y1, r_x2, r_y2, current)) {
            if (has_children(current)) {
                open.add(current.children[0]);
                open.add(current.children[1]);
                open.add(current.children[2]);
                open.add(current.children[3]);
            }
            else {
                for (T t : current.data) {
                    float x = x(qt, t);
                    float y = y(qt, t);
                    if (! (x < r_x1 || x > r_x2 || y < r_y1 || y > r_y2)) { // <= ? IMPORTANT
                        result.add(t);
                    }
                }
            }
        }

    }
}


static public <T> List<T> get_outside_aabb(Quad_Tree_Node<T> qt, float r_x1, float r_y1, float r_x2, float r_y2) {
    ArrayList<T> result = new ArrayList<>();
    get_outside_aabb(qt, r_x1, r_y1, r_x2, r_y2, result);
    return result;  
}


static public <T> void get_outside_aabb(Quad_Tree_Node<T> qt, float _r_x1, float _r_y1, float _r_x2, float _r_y2, List<T> result) {
    
    float r_x1 = min(_r_x1, _r_x2);
    float r_x2 = max(_r_x1, _r_x2);
    float r_y1 = min(_r_y1, _r_y2);
    float r_y2 = max(_r_y1, _r_y2);

    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);


    while (open.size() > 0) {
        Quad_Tree_Node<T> current = open.remove(open.size()-1);

        if (!intersects_rect(r_x1, r_y1, r_x2, r_y2, current)) {
            get_all(current, result);
        }
        else {
            if (has_children(current)) {
                open.add(current.children[0]);
                open.add(current.children[1]);
                open.add(current.children[2]);
                open.add(current.children[3]);
            }
            else {
                for (T t : current.data) {
                    float x = x(qt, t);
                    float y = y(qt, t);
                    if ((x < r_x1 || x > r_x2 || y < r_y1 || y > r_y2)) { // <= ? IMPORTANT
                        result.add(t);
                    }
                }
            }
        }
    }
}


// TODO assumes result is empty for now!
static public <T> void get_closest_n(Quad_Tree_Node<T> qt, float x, float y, int n, List<T> result) {

    if (n <= 0) return; 

    if (n >= qt.part_of_tree.size) {
        get_all(qt, result);
        return;
    }

    if (n == 1) {
        T t = get_closest(qt, x, y);
        if (t != null) result.add(t);
        return;
    }

    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);

    ArrayList<Quad_Tree_Node<T>> leafs = new ArrayList<>();
    int items_in_leafs = 0;

    Quad_Tree_Node<T> current = null;

    //
    // find closest leafs till we hit N without checking individual points
    //
    boolean do_add_containing = true;
    

    while (open.size() > 0) {
        // sorts from big to small (cause we pop)
        // TODO is this the same comparator as the other one?
        open.sort(new Comparator<Quad_Tree_Node<T>>() {
            @Override
            public int compare(Quad_Tree_Node<T> o1, Quad_Tree_Node<T> o2) {
                

                // TODO, I feel like this can be done way more efficient

                float d1 = 0;
                if (has_children(o1)) {
                    if (point_outside_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2)) {
                        d1 = max_dist_sq_point_to_corner_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2);
                    }
                }
                else {
                    d1 = dist_sq_point_to_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2);
                }

                float d2 = 0;
                if (has_children(o2)) {
                    if (point_outside_aabb(x, y, o2.x1, o2.y1, o2.x2, o2.y2)) {
                        d2 = max_dist_sq_point_to_corner_aabb(x, y, o2.x1, o2.y1, o2.x2, o2.y2);
                    }
                }
                else {
                    d2 = dist_sq_point_to_aabb(x, y, o2.x1, o2.y1, o2.x2, o2.y2);
                }
                
                if (d1 < d2) return 1;
                if (d1 > d2) return -1;
                return 0;
            }
        });

        current = remove_last(open);

        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
        }
        else {
            
            leafs.add(current);
            items_in_leafs += current.data.size();

            if (items_in_leafs >= n) {
                
                if (leafs.size() <= 4) {
                    do_add_containing = false;
                }
                break;
            }
        }
    }

    // we now know a minimum radius before we overflow N, now we can  add quads if they are fully contained in this radius
    if (do_add_containing) {
        float dist_point_to_overflow_leaf = dist_sq_point_to_aabb(x, y, current.x1, current.y1, current.x2, current.y2);
        
        for (int i = leafs.size()-1; i >= 0; i--) {
        
            Quad_Tree_Node<T> leaf = leafs.get(i);
            float max_dist_point_to_leaf = max_dist_sq_point_to_corner_aabb(x, y, leaf.x1, leaf.y1, leaf.x2, leaf.y2);

            if (max_dist_point_to_leaf <= dist_point_to_overflow_leaf) {
                result.addAll(leaf.data);
                leafs.remove(i);
            }
        }
    }

    ArrayList<T> buffer = new ArrayList<>((n-result.size())*2);

    for (Quad_Tree_Node<T> tree : leafs) {
        buffer.addAll(tree.data);
    }
    
    float add_all_quads_within_this_radius_sq = max_dist_sq_point_to_corner_aabb(x, y, current.x1, current.y1, current.x2, current.y2);

    while (open.size() > 0) {
        Quad_Tree_Node<T> tree = remove_last(open);
        if (has_children(tree)) {

            float d = dist_sq_point_to_aabb(x, y, tree.x1, tree.y1, tree.x2, tree.y2);
            if (d <= add_all_quads_within_this_radius_sq) {
                open.add(tree.children[0]);
                open.add(tree.children[1]);
                open.add(tree.children[2]);
                open.add(tree.children[3]);
            }
        }
        else {
            float d = dist_sq_point_to_aabb(x, y, tree.x1, tree.y1, tree.x2, tree.y2);
            if (d <= add_all_quads_within_this_radius_sq) {
                buffer.addAll(tree.data);
            }
        }
    }

    buffer.sort(new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            float d1 = dist_sq(x, y, x(qt, o1), y(qt, o1));
            float d2 = dist_sq(x, y, x(qt, o2), y(qt, o2));
            if (d1 < d2) return -1;
            if (d1 > d2) return 1;
            return 0;
        }
    });

    int max = n - result.size();
    for (int i = 0; i < max; i++) {
        T t = buffer.get(i);
        result.add(t);
    }

}


static public <T> T min_x(Quad_Tree<T> qt) {
    return min_x(qt.root);
}

static public <T> T min_x(Quad_Tree_Node<T> qt) {

    if (qt.part_of_tree.size == 0) return null;

    T best = null;
    float min_x = Float.POSITIVE_INFINITY;
    
    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);
    
    while (open.size() > 0) {
        Quad_Tree_Node<T> current = remove_last(open);
        if (min_x < current.x1) continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BR]);
            open.add(current.children[TR]);
            open.add(current.children[BL]);
            open.add(current.children[TL]);
        }
        else {
            for (T t : current.data) {
                if (x(qt, t) < min_x) {
                    min_x = x(qt, t);
                    best = t;
                }
            }
        }
        
    }
    return best;
}


static public <T> T min_y(Quad_Tree<T> qt) {
    return min_y(qt.root);
}


static public <T> T min_y(Quad_Tree_Node<T> qt) {

    if (qt.part_of_tree.size == 0) return null;

    T best = null;
    float min_y = Float.POSITIVE_INFINITY;
    
    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);
    
    while (open.size() > 0) {
        Quad_Tree_Node<T> current = remove_last(open);
        if (min_y < current.y1) continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BR]);
            open.add(current.children[BL]);
            open.add(current.children[TR]);
            open.add(current.children[TL]);
        }
        else {
            for (T t : current.data) {
                if (y(qt, t) < min_y) {
                    min_y = y(qt, t);
                    best = t;
                }
            }
        }
        
    }
    return best;
}


static public <T> T max_x(Quad_Tree<T> qt) {
    return max_x(qt.root);
}


static public <T> T max_x(Quad_Tree_Node<T> qt) {

    if (qt.part_of_tree.size == 0) return null;

    T best = null;
    float max_x = Float.NEGATIVE_INFINITY;
    
    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);
    
    while (open.size() > 0) {
        Quad_Tree_Node<T> current = remove_last(open);
        if (max_x > current.x2) continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BL]);
            open.add(current.children[TL]);
            open.add(current.children[BR]);
            open.add(current.children[TR]);
        }
        else {
            for (T t : current.data) {
                if (x(qt, t) > max_x) {
                    max_x = x(qt, t);
                    best = t;
                }
            }
        }
        
    }
    return best;
}


static public <T> T max_y(Quad_Tree<T> qt) {
    return max_y(qt.root);
}


static public <T> T max_y(Quad_Tree_Node<T> qt) {

    if (qt.part_of_tree.size == 0) return null;

    T best = null;
    float max_y = Float.NEGATIVE_INFINITY;
    
    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);
    
    while (open.size() > 0) {
        Quad_Tree_Node<T> current = remove_last(open);
        if (max_y > current.y2) continue;
        if (has_children(current)) {
            // reversed for optimal popping!
            open.add(current.children[BL]);
            open.add(current.children[TL]);
            open.add(current.children[BR]);
            open.add(current.children[TR]);
        }
        else {
            for (T t : current.data) {
                if (y(qt, t) > max_y) {
                    max_y = y(qt, t);
                    best = t;
                }
            }
        }
        
    }
    return best;
}


static public <T> void clear(Quad_Tree<T> qt) {
    clear(qt.root);
}

static public <T> void clear(Quad_Tree_Node<T> qt) {
    
    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);
    
    while (open.size() > 0) {
        Quad_Tree_Node<T> current = remove_last(open);
        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
            current.children = null;
        }
        else {
            current.data.clear();
        }
    }
    
    qt.part_of_tree.size = 0; // check lookup thing
    
}


static public <T> int size(Quad_Tree_Node<T> qt) {
    return qt.part_of_tree.size;
}


static public <T> boolean remove(Quad_Tree<T> qt, T t) {
    return remove(qt.root, t);
}


static public <T> boolean remove(Quad_Tree_Node<T> qt, T t) {
    
    Quad_Tree_Node<T> current = qt;

    float x = x(qt, t);
    float y = y(qt, t);
    
    while (has_children(current)) {
        int where = get_optimal_index(current, x, y);
        current = current.children[where];
    }
    if(current.data.remove(t)) {
        qt.part_of_tree.size--;
        qt.part_of_tree.n_items_at_depth_lookup[qt.depth] -= 1;
        return true;
    }
    return false;
}


static public <T> void rebuild(Quad_Tree<T> qt) {
    rebuild(qt.root);
}

static public <T> void rebuild(Quad_Tree_Node<T> qt) {
    // fast if things didn't move much, cause
    // we have the right order for inserting!
    ArrayList<T> items = new ArrayList<>();
    get_all(qt, items);
    clear(qt);
    add_all(qt, items);
}


static public <T> void merge_update(Quad_Tree<T> qt) {
    merge_update(qt.root);
}

static public <T> void merge_update(Quad_Tree_Node<T> qt) {
    
    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>(); 
    open.add(qt);
    
    while (open.size() > 0) {
        Quad_Tree_Node<T> current = remove_last(open);
      
        if (has_children(current)) {
            
            if (!has_children(current.children[0]) &&
                !has_children(current.children[1]) &&
                !has_children(current.children[2]) &&
                !has_children(current.children[3])) {
                
                int count =  current.children[0].data.size();
                    count += current.children[1].data.size();
                    count += current.children[2].data.size();
                    count += current.children[3].data.size();
                
                if (count < qt.part_of_tree.max_items) {
                    // merge
                    get_all(current, current.data);

                    qt.part_of_tree.node_buffer.add(current.children[0]);
                    qt.part_of_tree.node_buffer.add(current.children[1]);
                    qt.part_of_tree.node_buffer.add(current.children[2]);
                    qt.part_of_tree.node_buffer.add(current.children[3]);

                    current.children[0].data.clear();
                    current.children[1].data.clear();
                    current.children[2].data.clear();
                    current.children[3].data.clear();

                    current.children[0] = null;
                    current.children[1] = null;
                    current.children[2] = null;
                    current.children[3] = null;
                }
            }
            else {
                open.add(current.children[0]);
                open.add(current.children[1]);
                open.add(current.children[2]);
                open.add(current.children[3]);
            }
        }
        
    }
    
    
}


static public <T> int lowest_depth_with_items(Quad_Tree<T> qt) {
    return lowest_depth_with_items(qt.root);
}



static public <T> int lowest_depth_with_items(Quad_Tree_Node<T> qt) {
    
    // breadth-first search
    int index = 0;
    ArrayList<Quad_Tree_Node<T>> open = new ArrayList<>();
    open.add(qt);
    
    Quad_Tree_Node<T> found = null;
    
    while (index < open.size()) {
        
        Quad_Tree_Node<T> current = open.get(index);
        index++;
        
        if (has_children(current)) {
            open.add(current.children[0]);
            open.add(current.children[1]);
            open.add(current.children[2]);
            open.add(current.children[3]);
        }
        else {
            if (current.data.size() > 0) {
                found = current;
                break;
            }
        }
    }
    
    if (found == null) return -1;
    return found.depth;
}


static public <T> int highest_depth_with_items(Quad_Tree<T> qt) {
    return highest_depth_with_items(qt.root);
}

static public <T> int highest_depth_with_items(Quad_Tree_Node<T> qt) {

    if (qt.part_of_tree.size == 0) {
        return -1;
    }

    int[] n_items_at_depth_lookup = qt.part_of_tree.n_items_at_depth_lookup;

    for (int i = n_items_at_depth_lookup.length-1; i >= 0; i--) {
        if (n_items_at_depth_lookup[i] != 0) {
            return i;
        }
    }  
    return _unreachable_int("bug");
}


static public int _unreachable_int(String message) {
    throw new RuntimeException(message);
}



static public <T> void get_sorted_by_x(Quad_Tree_Node<T> qt, ArrayList<T> result, float max_x) {

    ArrayList<Quad_Tree_Node<T>> open  = new ArrayList<>();
    ArrayList<Quad_Tree_Node<T>> leafs = new ArrayList<>();

    open.add(qt);

    while (open.size() > 0) {

        Quad_Tree_Node<T> current = open.remove(open.size()-1);

        if (has_children(current)) {
            if (!(current.x1 >= max_x)) {
                open.add(current.children[TR]);
                open.add(current.children[BR]);
                open.add(current.children[TL]);
                open.add(current.children[BL]);
            }
        }
        else {
            leafs.add(current);
        }
    }

    leafs.sort(new Comparator<Quad_Tree_Node<T>>() {
        @Override
        public int compare(Quad_Tree_Node<T> o1, Quad_Tree_Node<T> o2) {
            float d = o1.x2 - o2.x2;
            if (d > 0) return  1;
            if (d < 0) return -1;
            return 0;
        }
    });

    for (Quad_Tree_Node<T> tree : leafs) {
        if (tree.x2 <= max_x) {
            result.addAll(tree.data);
        }
        else {
            for (T t : tree.data) {
                if (x(qt, t) <= max_x) {
                    result.add(t);
                }
            }
        }
    }

    result.sort(new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            float x1 = x(qt, o1);
            float x2 = x(qt, o2);
            if (x1 > x2) return  1;
            if (x1 < x2) return -1;
            return 0;
        }
    });
}


// TODO use something like rect_intersects_rect
static public <T> boolean intersects_rect(float x1, float y1, float x2, float y2, Quad_Tree_Node<T> tree) {
    return !(tree.x1 > x2 || 
    tree.x2 < x1 || 
    tree.y1 > y2 ||
    tree.y2 < y1);
}

static public <T> T remove_last(ArrayList<T> arr) {
    if (arr.size() == 0) return null;
    return arr.remove(arr.size()-1);
}


    
} // EOF