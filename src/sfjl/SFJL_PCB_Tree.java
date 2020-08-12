/** SFJL_PCB_Tree - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_PCB_Tree_Example.java

DOCUMENTATION:
    
  ╔═════════════╗                    ╔═════════════╗                   
. ╢ PCB_Node    ║    <───────parent──╢ PCB_Node    ║                   
  ║             ║                    ║             ║                   
  ║             ╟──first_child──>    ║             ╟──first_child──> null                   
  ╚═╤═══════════╝                    ╚═╤═══════════╝                   
    .                                  │ next_brother
        ^   ^                          │                           
        │   │                          v                           
        │   │                        ╔═════════════╗                   
        │   └──────────parent────────╢ PCB_Node    ║                   
        │                            ║             ║                   
        │                            ║             ╟──first_child──> null                   
        │                            ╚═╤═══════════╝    
        │                              │ next_brother
        │                              │                           
        │                              v     
        │                            ╔═════════════╗                   
        └──────────────parent────────╢ PCB_Node    ║                   
                                     ║             ║   
                                     ║             ╟──first_child──> null                   
                                     ╚═╤═══════════╝    
                                       │ next_brother
                                       │                           
                                       v       
                                      null


--------------------------------------------------------------------------------
The above structure allows to create a tree without the need of having arrays
for the children. Instead you go to the first child if there is any, and you
check all the brothers of the first child. It is a bit more complicated, but it
is really efficient for reusing the data structure without the need of resizing
arrays.

I created this for an IMGUI library, where the tree structure had to be
recreated every frame. Also was the tree subject for big changes in the hierachy
without the pre-knowledge of when that would happen and what the changes would
be.

Doeke Wartena, August 7, 2020

*/
package sfjl;

import java.util.ArrayList;
import java.util.Iterator;

public class SFJL_PCB_Tree {
    private SFJL_PCB_Tree() {}
//          SFJL_PCB_Tree

static public class PCB_Node<T extends PCB_Node<T>> implements Iterable<T> {
    public T parent;
    public T first_child;
    public T next_brother;

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<T> iterator() {
        return get_iterator((T)this);
    }
}


static public class PCB_Tree_Builder_Branch<T extends PCB_Node<T>> {
    public ArrayList<T> last_node_depth_view = new ArrayList<>();
    public int insert_slot = 0;
}


static public class PCB_Tree_Builder<T extends PCB_Node<T>> {
    public ArrayList<PCB_Tree_Builder_Branch<T>> builder_branches = new ArrayList<PCB_Tree_Builder_Branch<T>>();
    public PCB_Tree_Builder_Branch<T> current_branch = new PCB_Tree_Builder_Branch<>();
    public PCB_Tree_Builder() { builder_branches.add(current_branch); }
}


public enum Add_Type {
    APPEND,
    APPEND_AS_CHILD,
    PREPEND,
    PREPEND_AS_CHILD
}


static public <T extends PCB_Node<T>> void begin_node(PCB_Tree_Builder<T> builder, T node) {
    begin_node(builder, node, null, Add_Type.APPEND);
}


static public <T extends PCB_Node<T>> boolean tree_is_closed(PCB_Tree_Builder<T> builder) {
    return builder.builder_branches.size() == 1 && builder.current_branch.insert_slot == 0;
}


static public <T extends PCB_Node<T>> void begin_node(PCB_Tree_Builder<T> builder, T node, T add_to_node, Add_Type add_type) {

    if (add_to_node != null) {
        PCB_Tree_Builder_Branch<T> branch = new PCB_Tree_Builder_Branch<>();
        builder.builder_branches.add(branch);
        builder.current_branch = branch;
        branch.last_node_depth_view.add(add_to_node);
        branch.insert_slot = 1;
    }

    if (add_type == Add_Type.APPEND_AS_CHILD || add_type == Add_Type.PREPEND_AS_CHILD) {
        builder.current_branch.last_node_depth_view.add(get_last_child(add_to_node));
    }

    PCB_Tree_Builder_Branch<T> branch = builder.current_branch;


    if (add_to_node != null && (add_type == Add_Type.APPEND || add_type == Add_Type.PREPEND)) {

        assert add_to_node.parent != null : "Can't use Add_Type "+add_type+" on the root node";

        T original_next_brother = add_to_node.next_brother;
        add_to_node.next_brother = node;
        node.next_brother = original_next_brother;

        node.parent = add_to_node.parent;

        branch.last_node_depth_view.add(node);
        branch.insert_slot += 1;

        if (add_type == Add_Type.PREPEND) {
            swap(node, add_to_node);
        }

    }
    else {

        // ensure size
        if (branch.last_node_depth_view.size() == branch.insert_slot) {
            branch.last_node_depth_view.add(null);
        }
        
        T parent_node = branch.insert_slot > 0 ? branch.last_node_depth_view.get(branch.insert_slot-1) : null;
        T last_child_of_parent = branch.last_node_depth_view.get(branch.insert_slot);

        branch.last_node_depth_view.set(branch.insert_slot, node);
        branch.insert_slot += 1;

        if (parent_node != null) {
            node.parent = parent_node;
            if (last_child_of_parent != null) {
                last_child_of_parent.next_brother = node;
            }
            else {
                parent_node.first_child = node;
            }
        }

        if (add_type == Add_Type.PREPEND_AS_CHILD) {
            swap(node, add_to_node.first_child);
        }
    }
    

   
}


static public <T extends PCB_Node<T>> T end_node(PCB_Tree_Builder<T> builder) {

    PCB_Tree_Builder_Branch<T> branch = builder.current_branch;
    T node = branch.last_node_depth_view.get(branch.insert_slot-1);

    // -------------------
    // - fruits
    //      - apples
    //      - bananas
    //      - citron
    // - snacks
    // -------------------
    // If we closed citron and we close fruits now, then we have to
    // set citron to null in the last_node_depth_view, else citron
    // would become a child of snacks.
    if (branch.insert_slot < branch.last_node_depth_view.size()) {
        branch.last_node_depth_view.set(branch.insert_slot, null);
    }

    branch.insert_slot -= 1;

    // if this is not the main branch but a branch created when we used
    // append_child for example, then we need to destroy the branch when we
    // end that specific node.
    if (branch.insert_slot == 1 && builder.builder_branches.size() > 1) {
        builder.builder_branches.remove(builder.builder_branches.size()-1);
        builder.current_branch = builder.builder_branches.get(builder.builder_branches.size()-1);
    }

    return node;
}


static public <T extends PCB_Node<T>> void swap(T a, T b) {
    
    assert a.parent != null && b.parent != null : "cannot swap with the root node";

    if (a == b) return;

    T parent_of_a = a.parent;
    T parent_of_b = b.parent;
    T next_brother_of_a = a.next_brother;
    T next_brother_of_b = b.next_brother;
    T previous_brother_of_a = get_previous_brother(a);
    T previous_brother_of_b = get_previous_brother(b);

    if (a.next_brother == b) {
        b.next_brother = a;
        a.next_brother = next_brother_of_b;
    }
    else if (b.next_brother == a) {
        a.next_brother = b;
        b.next_brother = next_brother_of_a;
    }
    else {
        a.next_brother = next_brother_of_b;
        b.next_brother = next_brother_of_a;
        a.parent = parent_of_b;
        b.parent = parent_of_a;
    }

    if (previous_brother_of_a != null) {
        if (previous_brother_of_a != b) {
            previous_brother_of_a.next_brother = b;
        }
    }
    else {
        parent_of_a.first_child = b;   
    }
    
    if (previous_brother_of_b != null) {
        if (previous_brother_of_b != a) {
            previous_brother_of_b.next_brother = a;
        }
    }
    else {
        parent_of_b.first_child = a;
    }
}


static public <T extends PCB_Node<T>> T get_previous_brother(T node) {
    if (node.parent == null) return null;
    T t = node.parent.first_child;
    if (t == node) return null;
    while (true && t.next_brother != null) {
        if (t.next_brother == node) {
            return t;
        }
        t = t.next_brother;
    }
    return null;
}


static public <T extends PCB_Node<T>> T get_last_child(T node) {
    T result = node.first_child;
    if (result == null) return null;
    while (result.next_brother != null) {
        result = result.next_brother;
    }
    return result;
}


// ------------------------------------ iterators ------------------------------


static public <T extends PCB_Node<T>> Iterator<T> get_child_iterator(T e) {
    
    return new Iterator<T>() {
        
        T next_child = e.first_child;
        
        @Override
        public boolean hasNext() {
            return next_child != null;
        }
        
        @Override
        public T next() {
            T r = next_child;
            next_child = next_child.next_brother;
            return r;
        }
        
    };
}


// goes threw all including children of children
static public <T extends PCB_Node<T>> Iterator<T> get_iterator(T e) {
    
    return new Iterator<T>() {
        
        T first = e;
        T current = e;

        
        @Override
        public boolean hasNext() {
            return current != null;
        }
        
        @Override
        public T next() {
            T r = current;
            
            if (current.first_child != null) {
                current = current.first_child;
            }
            else if (current.next_brother != null) {
                current = current.next_brother;
            }
            else {
                if (current != first) {
                    current = current.parent;
                    
                    while (current.next_brother == null && current != first) {
                        current = current.parent;
                    }
                    current = current.next_brother;
                }
            }
            return r;
        }
    };
    
}


static public interface PCB_Node_Accept_Checker<T extends PCB_Node<T>> {
    boolean accept(T t);
}


static public <T extends PCB_Node<T>> Iterator<T> get_iterator(T e, PCB_Node_Accept_Checker<T> acceptor) {
    

    return new Iterator<T>() {

        Iterator<T> itr = get_iterator(e);
        T next = null;
        
		@Override
		public boolean hasNext() {
            if (next == null) {
                while (itr.hasNext()) {
                    T node = itr.next();
                    if (acceptor.accept(node)) {
                        next = node;
                        break;
                    }
                }
            }
			return next != null;
		}

		@Override
		public T next() {
            T result = next;
            next = null;
			return result;
		}
    };
}


static public interface PCB_Node_Accept_Checker_With_Depth<T extends PCB_Node<T>> {
    boolean accept(T t, int depth);
}


static public <T extends PCB_Node<T>> Iterator<T> get_iterator(T e, PCB_Node_Accept_Checker_With_Depth<T> acceptor) {
    
    return new Iterator<T>() {
        
        T first = e;
        T current = e;
        int depth_of_current = 0;
        T pending_next;


        void advance_current() {
            if (current.first_child != null) {
                current = current.first_child;
                depth_of_current += 1;
            }
            else if (current.next_brother != null) {
                current = current.next_brother;
            }
            else {
                if (current != first) {
                    current = current.parent;
                    depth_of_current -= 1;
                    
                    while (current.next_brother == null && current != first) {
                        current = current.parent;
                        depth_of_current -= 1;
                    }
                    current = current.next_brother;
                }
            }
        }

     
        @Override
        public boolean hasNext() {

            while (pending_next == null && current != null) {
                if (acceptor.accept(current, depth_of_current)) {
                    pending_next = current;
                    advance_current();
                }
                else {
                    advance_current();
                }
            }
            return pending_next != null;
        }
        
        @Override
        public T next() {
            T r = pending_next;
            pending_next = null;
            return r;
        }
    };
}


//------------------------------------- tree printing


static public class Print_Tree_Formatter {
    public String  h_bar = "─";
    public String  v_bar = "│";
    public String  t     = "├";
    public String  l     = "└";
    public int     h_span = 2;
    public boolean print_root_name = true;
    public String  root_substitute_name = ".";
    public String  line_prefix = "";
}


public interface Print_Tree_Name_Printer<T extends PCB_Node<T>>{
    String name(T t);
}


static public <T extends PCB_Node<T>> void print_tree(T e, Print_Tree_Name_Printer<T> name_printer, Print_Tree_Formatter ptf) {
    if (ptf == null) {
        ptf = new Print_Tree_Formatter();
    }
    print_tree(e, ptf.line_prefix, 0, e.first_child == null, name_printer, ptf);
}


static public <T extends PCB_Node<T>> void print_tree(T e, String prefix, int depth, boolean is_last_child, Print_Tree_Name_Printer<T> name_printer, Print_Tree_Formatter ptf) {

    if (depth == 0) {
        if (ptf.print_root_name) {
            System.out.println(prefix+name_printer.name(e));
        }
        else {
            System.out.println(prefix+ptf.root_substitute_name); // "."
        }
    }
    else {

        // it comes like "   │" or "    ", so we chop off the last char
        // so we can add "└" or "├"
        String line = prefix.substring(0, prefix.length()-1);
        if (is_last_child) {
            line += ptf.l; // "└"
        }
        else {
            line += ptf.t; // "├"
        }
        line += _str_repeat(ptf.h_bar, ptf.h_span)+" "; // "── "
        line += name_printer.name(e);

        System.out.println(line);
    }

    Iterator<T> itr = get_child_iterator(e);

    while (itr.hasNext()) {

        T child = itr.next();
        boolean child_is_last_child = child.next_brother == null;
        String next_prefix = prefix;

        if (depth == 0) {

            if (child_is_last_child) {
                next_prefix += " ";
            }
            else {
                next_prefix += ptf.v_bar; // "│"
            }
        }
        else {
            if (child_is_last_child) {
                next_prefix += _str_repeat(" ", ptf.h_span+2); // "   "
            }
            else {
                next_prefix += _str_repeat(" ", ptf.h_span+1) + ptf.v_bar; // "   │"
            }
        }
        print_tree(child, next_prefix, depth+1, child_is_last_child, name_printer, ptf);
    }
}


static public String _str_repeat(String s, int n) {
    String r = "";
    while (n-- > 0) {
        r += s;
    }
    return r;
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