package sfjl;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import static java.lang.Math.*;
import static sfjl.SFJL_Math.*;

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


static public class Quad_Tree_Manager<T> {
    public int max_items;
    public X<T> x;
    public Y<T> y;
    public int size = 0;
    public int highest_depth_with_items = -1;
    
    public Quad_Tree_Manager(X<T> x, Y<T> y, int max_items) {
        this.x = x;
        this.y = y;
        assert(max_items > 0);
        this.max_items = max_items;
    }
}



static public class Quad_Tree<T> implements Iterable<T> {
    public final Quad_Tree_Manager<T> manager;
    public Quad_Tree<T> parent;
    public Quad_Tree<T>[] children;
    public ArrayList<T> data;
    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public int depth;
    

    public Quad_Tree(Quad_Tree_Manager<T> manager,  Quad_Tree<T> parent, float x1, float y1, float x2, float y2) {
        this(manager.x, manager.y, parent, x1, y1, x2, y2, manager.max_items);
    }
    

    public Quad_Tree(X<T> x, Y<T> y, Quad_Tree<T> parent, float x1, float y1, float x2, float y2, int max_items) {
        if (parent != null) {
            this.manager = parent.manager;
            depth = parent.depth + 1;
        }
        else {
            this.manager = new Quad_Tree_Manager<>(x, y, max_items);
        }
        
        this.parent = parent;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        data = new ArrayList<>(manager.max_items);
    }
    
    
    public float x(T t) { return manager.x.x(t);}
    public float y(T t) { return manager.y.y(t);}
    
    
    public float center_x() {
        return x1 + (x2 - x1) / 2;
    }

    
    public float center_y() {
        return y1 + (y2 - y1) / 2;
    }
    

    public boolean has_children() {
        return children != null;
    }
    

    public boolean is_root() {
        return parent == null;
    }
    

    public int get_index(float x, float y) {
        if (y < center_y()) return x < center_x() ? TL : TR;
        else                return x < center_x() ? BL : BR;
    }
    
    
    
    
    
    
    public Iterator<T> iterator() {
        
        Stack<T> open = new Stack<>();
        Stack<Quad_Tree<T>> open_quad_trees = new Stack<>();
        
        open_quad_trees.add(this);
        
        return new Iterator<T>() {
            
            @Override
            public boolean hasNext() {
                while (open.size() == 0 && open_quad_trees.size() != 0) {
                    Quad_Tree<T> tree = open_quad_trees.pop();
                    if (tree.has_children()) {
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
                return open.pop();
            }
        };
    }
    
    
    public T get_closest(float x, float y) {
        
        T closest = null;
        float closest_dist_sq = Float.POSITIVE_INFINITY;
        
        Stack<Quad_Tree<T>> open = new Stack<>();
        open.add(this);
        
        while (open.size() > 0) {
            Quad_Tree<T> tree =  open.pop();
            float d = dist_sq_point_to_aabb(x, y, tree.x1, tree.y1, tree.x2, tree.y2);
            if (d > closest_dist_sq) {
                continue;
            }
            
            if (tree.has_children()) {
                
                int rl = tree.center_x() < x ? 1 : 0;
                int bt = tree.center_y() < y ? 1 : 0;
                
                // order is reversed so the most optimal get's popped first
                open.add(tree.children[(1-bt)*2+(1-rl)]); // <- worst
                open.add(tree.children[(1-bt)*2+rl]);
                open.add(tree.children[bt*2+(1-rl)]);
                open.add(tree.children[bt*2+rl]); // <- best
            }
            else {
                for (T t : tree.data) {
                    d = dist_sq(x, y, x(t), y(t));
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


    public T get_furthest(float x, float y) {

        T closest = null;
        float max_dist = Float.NEGATIVE_INFINITY;

        ArrayList<Quad_Tree<T>> open = new ArrayList<>();
        open.add(this);

        while (open.size() > 0) {

            Quad_Tree<T> current = open.remove(open.size()-1);

            float max_dist_to_current = max_dist_sq_point_to_corner_aabb(x, y, current.x1, current.y1, current.x2, current.y2);
            if (max_dist_to_current <= max_dist) {
                continue;
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
                
                for (T t : current.data) {
                    float d = dist_sq(x, y, x(t), y(t));
                    if (d > max_dist) {
                        closest = t;
                        max_dist = d;
                    }
                }
            }
        }
        return closest;
    }


    public void get_farrest_n(float x, float y, int n, ArrayList<T> result) {
        
        if (n <= 0) return;

        if (n == 1) {
            T t = get_closest(x, y);
            result.add(t);
            return;
        }

        if (n >= size()) {
            add_all(this, result);
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

        ArrayList<Quad_Tree<T>> leafs = new ArrayList<>();
        int items_in_leafs = 0;

        ArrayList<Quad_Tree<T>> open = new ArrayList<>();
        open.add(this);

        boolean do_add_containing = true;

        Quad_Tree<T> current = null;

        while (open.size() > 0) {

            // TODO IMPORTANT does this recreate the comparator every iteration
            open.sort(new Comparator<Quad_Tree<T>>() {
                @Override
                public int compare(Quad_Tree<T> o1, Quad_Tree<T> o2) {
                    
                    // TODO, I feel like this can be done way more efficient

                    float d1 = 0;
                    if (o1.has_children()) {
                        if (point_outside_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2)) {
                            d1 = max_dist_sq_point_to_corner_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2);
                        }
                    }
                    else {
                        d1 = dist_sq_point_to_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2);
                    }

                    float d2 = 0;
                    if (o2.has_children()) {
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

            if (current.has_children()) {
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
            
                Quad_Tree<T> leaf = leafs.get(i);
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
                float max_dist_to_current = dist_sq_farrest_point_to_aabb_no_zero(x, y, current.x1, current.y1, current.x2, current.y2);
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

    
    
    public void get_all(List<T> result) {
        Stack<Quad_Tree<T>> open = new Stack<>();
        open.add(this);
        
        while (open.size() > 0) {
            Quad_Tree<T> current = open.pop();
            if (current.has_children()) {
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

    
    public List<T> get_within_radius(float x, float y, float radius) {
        ArrayList<T> result = new ArrayList<>();
        get_within_radius_sq(x, y, sq(radius), result);
        return result;   
    }
    

    public void get_within_radius(float x, float y, float radius, List<T> result) {
        get_within_radius_sq(x, y, sq(radius), result);
    }
    

    public List<T> get_within_radius_sq(float x, float y, float radius_sq) {
        ArrayList<T> result = new ArrayList<>();
        get_within_radius_sq(x, y, radius_sq, result);
        return result;
    }
    

    public void get_within_radius_sq(float x, float y, float radius_sq, List<T> result) {
        
        float dist_circle_to_quad_sq = dist_sq_point_to_aabb(x, y, this.x1, this.y1, this.x2, this.y2);
        if (dist_circle_to_quad_sq > radius_sq) return; // quadtree outside circle
        
        float dx = max(x - x1, x2 - x); 
        float dy = max(y - y1, y2 - y);
        
        if (radius_sq >= dx*dx + dy*dy) { // fully contained, add all
            get_all(result);
        }
        else { // intersection
            if (has_children()) {
                children[0].get_within_radius_sq(x, y, radius_sq, result);
                children[1].get_within_radius_sq(x, y, radius_sq, result);
                children[2].get_within_radius_sq(x, y, radius_sq, result);
                children[3].get_within_radius_sq(x, y, radius_sq, result);
            }
            else {
                for (T t : data) {
                    float dist_sq = dist_sq(x, y, x(t), y(t));
                    if (dist_sq < radius_sq) {
                        result.add(t);
                    }
                }
            }
        }
        
    }

    
    public List<T> get_within_aabb(float r_x1, float r_y1, float r_x2, float r_y2) {
        ArrayList<T> result = new ArrayList<>();
        get_within_aabb(r_x1, r_y1, r_x2, r_y2, result);
        return result;  
    }
    
    
    public void get_within_aabb(float _r_x1, float _r_y1, float _r_x2, float _r_y2, List<T> result) {
        
        float r_x1 = min(_r_x1, _r_x2);
        float r_x2 = max(_r_x1, _r_x2);
        float r_y1 = min(_r_y1, _r_y2);
        float r_y2 = max(_r_y1, _r_y2);

        ArrayList<Quad_Tree<T>> open = new ArrayList<>();
        open.add(this);

        while (open.size() > 0) {
            Quad_Tree<T> current = open.remove(open.size()-1);

            if (rect_contains_rect(r_x1, r_y1, r_x2, r_y2, current.x1, current.y1, current.x2, current.y2)) {
                current.get_all(result);
            }
            else if (intersects_rect(r_x1, r_y1, r_x2, r_y2, current)) {
                if (current.has_children()) {
                    open.add(current.children[0]);
                    open.add(current.children[1]);
                    open.add(current.children[2]);
                    open.add(current.children[3]);
                }
                else {
                    for (T t : current.data) {
                        float x = x(t);
                        float y = y(t);
                        if (! (x < r_x1 || x > r_x2 || y < r_y1 || y > r_y2)) { // <= ? IMPORTANT
                            result.add(t);
                        }
                    }
                }
            }

        }
    }


    public List<T> get_outside_aabb(float r_x1, float r_y1, float r_x2, float r_y2) {
        ArrayList<T> result = new ArrayList<>();
        get_outside_aabb(r_x1, r_y1, r_x2, r_y2, result);
        return result;  
    }
    
    
    public void get_outside_aabb(float _r_x1, float _r_y1, float _r_x2, float _r_y2, List<T> result) {
        
        float r_x1 = min(_r_x1, _r_x2);
        float r_x2 = max(_r_x1, _r_x2);
        float r_y1 = min(_r_y1, _r_y2);
        float r_y2 = max(_r_y1, _r_y2);

        ArrayList<Quad_Tree<T>> open = new ArrayList<>();
        open.add(this);


        while (open.size() > 0) {
            Quad_Tree<T> current = open.remove(open.size()-1);

            if (!intersects_rect(r_x1, r_y1, r_x2, r_y2, current)) {
                current.get_all(result);
            }
            else {
                if (current.has_children()) {
                    open.add(current.children[0]);
                    open.add(current.children[1]);
                    open.add(current.children[2]);
                    open.add(current.children[3]);
                }
                else {
                    for (T t : current.data) {
                        float x = x(t);
                        float y = y(t);
                        if ((x < r_x1 || x > r_x2 || y < r_y1 || y > r_y2)) { // <= ? IMPORTANT
                            result.add(t);
                        }
                    }
                }
            }
        }
    }

    
    // TODO assumes result is empty for now!
    public void get_closest_n(float x, float y, int n, List<T> result) {

        if (n <= 0) return; 

        if (n >= manager.size) {
            get_all(result);
            return;
        }

        if (n == 1) {
            T t = get_closest(x, y);
            if (t != null) result.add(t);
            return;
        }

        Stack<Quad_Tree<T>> open = new Stack<>();
        open.add(this);

        ArrayList<Quad_Tree<T>> leafs = new ArrayList<>();
        int items_in_leafs = 0;

        Quad_Tree<T> current = null;

        //
        // find closest leafs till we hit N without checking individual points
        //
        boolean do_add_containing = true;
        

        while (open.size() > 0) {
            // sorts from big to small (cause we pop)
            // TODO is this the same comparator as the other one?
            open.sort(new Comparator<Quad_Tree<T>>() {
                @Override
                public int compare(Quad_Tree<T> o1, Quad_Tree<T> o2) {
                    

                    // TODO, I feel like this can be done way more efficient

                    float d1 = 0;
                    if (o1.has_children()) {
                        if (point_outside_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2)) {
                            d1 = max_dist_sq_point_to_corner_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2);
                        }
                    }
                    else {
                        d1 = dist_sq_point_to_aabb(x, y, o1.x1, o1.y1, o1.x2, o1.y2);
                    }

                    float d2 = 0;
                    if (o2.has_children()) {
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

            current = open.pop();

            if (current.has_children()) {
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
            
                Quad_Tree<T> leaf = leafs.get(i);
                float max_dist_point_to_leaf = max_dist_sq_point_to_corner_aabb(x, y, leaf.x1, leaf.y1, leaf.x2, leaf.y2);

                if (max_dist_point_to_leaf <= dist_point_to_overflow_leaf) {
                    result.addAll(leaf.data);
                    leafs.remove(i);
                }
            }
        }

        ArrayList<T> buffer = new ArrayList<>((n-result.size())*2);

        for (Quad_Tree<T> tree : leafs) {
            buffer.addAll(tree.data);
        }
        
        float add_all_quads_within_this_radius_sq = max_dist_sq_point_to_corner_aabb(x, y, current.x1, current.y1, current.x2, current.y2);

        while (open.size() > 0) {
            Quad_Tree<T> tree = open.pop();
            if (tree.has_children()) {

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
                float d1 = dist_sq(x, y, x(o1), y(o1));
                float d2 = dist_sq(x, y, x(o2), y(o2));
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


    public T min_x() {

        if (manager.size == 0) return null;

        T best = null;
        float min_x = Float.POSITIVE_INFINITY;
        
        Stack<Quad_Tree<T>> open = new Stack<>();
        open.add(this);
        
        while (open.size() > 0) {
            Quad_Tree<T> current = open.pop();
            if (min_x < current.x1) continue;
            if (current.has_children()) {
                // reversed for optimal popping!
                open.add(current.children[BR]);
                open.add(current.children[TR]);
                open.add(current.children[BL]);
                open.add(current.children[TL]);
            }
            else {
                for (T t : current.data) {
                    if (x(t) < min_x) {
                        min_x = x(t);
                        best = t;
                    }
                }
            }
            
        }
        return best;
    }

    
    public T min_y() {

        if (manager.size == 0) return null;

        T best = null;
        float min_y = Float.POSITIVE_INFINITY;
        
        Stack<Quad_Tree<T>> open = new Stack<>();
        open.add(this);
        
        while (open.size() > 0) {
            Quad_Tree<T> current = open.pop();
            if (min_y < current.y1) continue;
            if (current.has_children()) {
                // reversed for optimal popping!
                open.add(current.children[BR]);
                open.add(current.children[BL]);
                open.add(current.children[TR]);
                open.add(current.children[TL]);
            }
            else {
                for (T t : current.data) {
                    if (y(t) < min_y) {
                        min_y = y(t);
                        best = t;
                    }
                }
            }
            
        }
        return best;
    }

    
    public T max_x() {

        if (manager.size == 0) return null;

        T best = null;
        float max_x = Float.NEGATIVE_INFINITY;
        
        Stack<Quad_Tree<T>> open = new Stack<>();
        open.add(this);
        
        while (open.size() > 0) {
            Quad_Tree<T> current = open.pop();
            if (max_x > current.x2) continue;
            if (current.has_children()) {
                // reversed for optimal popping!
                open.add(current.children[BL]);
                open.add(current.children[TL]);
                open.add(current.children[BR]);
                open.add(current.children[TR]);
            }
            else {
                for (T t : current.data) {
                    if (x(t) > max_x) {
                        max_x = x(t);
                        best = t;
                    }
                }
            }
            
        }
        return best;
    }
    

    public T max_y() {

        if (manager.size == 0) return null;

        T best = null;
        float max_y = Float.NEGATIVE_INFINITY;
        
        Stack<Quad_Tree<T>> open = new Stack<>();
        open.add(this);
        
        while (open.size() > 0) {
            Quad_Tree<T> current = open.pop();
            if (max_y > current.y2) continue;
            if (current.has_children()) {
                // reversed for optimal popping!
                open.add(current.children[BL]);
                open.add(current.children[TL]);
                open.add(current.children[BR]);
                open.add(current.children[TR]);
            }
            else {
                for (T t : current.data) {
                    if (y(t) > max_y) {
                        max_y = y(t);
                        best = t;
                    }
                }
            }
            
        }
        return best;
    }
    

    public void clear() {
        
        Stack<Quad_Tree<T>> open = new Stack<>();
        open.add(this);
        
        while (open.size() > 0) {
            Quad_Tree<T> current = open.pop();
            if (current.has_children()) {
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
        if (data == null) {
            data = new ArrayList<>();
        }
        
        manager.size = 0;
        manager.highest_depth_with_items = 0;
        
    }
    
    
    public int size() {
        return manager.size;
    }
    

    /**
    * Does not update lowest_depth_with_items and highest_depth_with_items for speed reasons.
    */
    public boolean remove(T t) {
        
        Quad_Tree<T> current = this;
        
        while (current.has_children()) {
            int where = current.get_index(x(t), y(t));
            current = current.children[where];
        }
        if(current.data.remove(t)) {
            manager.size--;
            return true;
        }
        return false;
    }
    
    
    public void rebuild() {
        // fast if things didn't move much, cause
        // we have the right order for inserting!
        ArrayList<T> items = new ArrayList<>();
        get_all(items);
        clear();
        add_all(this, items);
    }
    

    public void merge_update() {
        
        Stack<Quad_Tree<T>> open = new Stack<>();
        open.add(this);
        
        while (open.size() > 0) {
            Quad_Tree<T> current = open.pop();
            if (current.has_children()) {
                
                if (!current.children[0].has_children() &&
                !current.children[1].has_children() &&
                !current.children[2].has_children() &&
                !current.children[3].has_children()) {
                    
                    int count = current.children[0].data.size();
                    count    += current.children[1].data.size();
                    count    += current.children[2].data.size();
                    count    += current.children[3].data.size();
                    
                    if (count < manager.max_items) {
                        // merge
                        current.data = new ArrayList<>();
                        current.get_all(current.data);
                        current.children[0] = null;
                        current.children[1] = null;
                        current.children[2] = null;
                        current.children[3] = null;
                        current.children = null;
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
    
    
    public int lowest_depth_with_items() {
        
        // breadth-first search
        int index = 0;
        ArrayList<Quad_Tree<T>> open = new ArrayList<>();
        open.add(this);
        
        Quad_Tree<T> found = null;
        
        while (index < open.size()) {
            
            Quad_Tree<T> current = open.get(index);
            index++;
            
            if (current.has_children()) {
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
    

    public int highest_depth_with_items() {
        return manager.highest_depth_with_items;
    }
    
    
    public int hashCode() {
        int bits = Float.floatToIntBits(x1);
        bits += Float.floatToIntBits(y1) * 37;
        bits += Float.floatToIntBits(x2) * 43;
        bits += Float.floatToIntBits(y2) * 47;
        return bits;
    }


    public void get_sorted_by_x(ArrayList<T> result, float max_x) {

        ArrayList<Quad_Tree<T>> open  = new ArrayList<>();
        ArrayList<Quad_Tree<T>> leafs = new ArrayList<>();

        open.add(this);

        while (open.size() > 0) {

            Quad_Tree<T> current = open.remove(open.size()-1);

            if (current.has_children()) {
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

        leafs.sort(new Comparator<Quad_Tree<T>>() {
            @Override
            public int compare(Quad_Tree<T> o1, Quad_Tree<T> o2) {
                float d = o1.x2 - o2.x2;
                if (d > 0) return  1;
                if (d < 0) return -1;
                return 0;
            }
        });

        for (Quad_Tree<T> tree : leafs) {
            if (tree.x2 <= max_x) {
                result.addAll(tree.data);
            }
            else {
                for (T t : tree.data) {
                    if (x(t) <= max_x) {
                        result.add(t);
                    }
                }
            }
        }

        result.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                float x1 = x(o1);
                float x2 = x(o2);
                if (x1 > x2) return  1;
                if (x1 < x2) return -1;
                return 0;
            }
        });
    }
    
    
    
}




static public <T> void add(Quad_Tree<T> qt, T t) {
    add(qt, t, qt.x(t), qt.y(t));
}


static public <T> void add(Quad_Tree<T> qt, T t, float x, float y) {
    
    if (qt.depth == 0) {
        if (x < qt.x1 || x > qt.x2 || y < qt.y1 || y > qt.y2) {
            // TODO return false?
            return;
        }
    }
    
    if (qt.has_children()) {
        int where = qt.get_index(x, y);
        add(qt.children[where], t, x, y);
    }
    else {
        qt.data.add(t);
        qt.manager.size++;
        if (qt.data.size() > qt.manager.max_items) {
            split(qt);
        }
    }
}


static public <T> void add_all(Quad_Tree<T> quad_tree, List<T> items) {
    for (T t : items) {
        add(quad_tree, t);
    }
}



static public <T> void split(Quad_Tree<T> qt) {
    qt.children = new Quad_Tree[4];
    qt.children[TR] = new Quad_Tree<>(qt.manager, qt, qt.center_x(), qt.y1, qt.x2, qt.center_y());
    qt.children[TL] = new Quad_Tree<>(qt.manager, qt, qt.x1, qt.y1, qt.center_x(), qt.center_y());
    qt.children[BL] = new Quad_Tree<>(qt.manager, qt, qt.x1, qt.center_y(), qt.center_x(), qt.y2);
    qt.children[BR] = new Quad_Tree<>(qt.manager, qt, qt.center_x(), qt.center_y(), qt.x2, qt.y2);
    
    for (T t : qt.data) {
        int where = qt.get_index(qt.x(t), qt.y(t));
        add(qt.children[where], t);
    }
    qt.manager.size -= qt.data.size(); // correction
    qt.data.clear();
    qt.data = null;
    
    if (qt.depth + 1 > qt.manager.highest_depth_with_items) {
        qt.manager.highest_depth_with_items = qt.depth + 1;
    }
    
}



// TODO use something like rect_intersects_rect
static public <T> boolean intersects_rect(float x1, float y1, float x2, float y2, Quad_Tree<T> tree) {
    return !(tree.x1 > x2 || 
    tree.x2 < x1 || 
    tree.y1 > y2 ||
    tree.y2 < y1);
}


    
} // EOF