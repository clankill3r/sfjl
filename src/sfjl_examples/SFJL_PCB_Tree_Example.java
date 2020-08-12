/** SFJL_PCB_Tree_Example - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl_examples;

import static sfjl.SFJL_PCB_Tree.*;
import java.util.Iterator;

public class SFJL_PCB_Tree_Example {

    
public static void main(String[] args) {

    // I choose to demonstrate the example as close to the original use as
    // possible. Therefor we extended the PCB_Node and added an extra field to
    // it called "name". Also we wrapped the library function calls cause that
    // would be the place to do more work.

    // Let's start by creating the following tree:

    // root
    //    status_bar
    //          errors
    //          line_number
    


    // We always need a root, the name does not matter. Cause the name was our
    // implementation, not the library implementation.
    Element root = 
    begin_element("root");

        Element status_bar = 
        begin_element("status_bar");

            Element errors =
            begin_element("errors");
            end_element();

            Element line_number =
            begin_element("line_number");
            end_element();

        end_element();

    end_element();

    
    // Lets add a new element "warnings" to the status_bar.

    Element warnings =
    begin_element("warnings", status_bar, APPEND_AS_CHILD);
    end_element();
    
    // Now the status_bar looks like this:

    //    status_bar
    //          errors
    //          line_number
    //          warnings

    // We can change the order with swap:
    swap(line_number, warnings);
    
    // We can also prepend:
    Element menu_bar =
    begin_element("menu_bar", root, PREPEND_AS_CHILD);
        Element file = 
        begin_element("file");
            element("new_file");
            element("open");
            element("save");
        end_element();
    end_element();

    
    System.out.println("tree_is_closed: "+tree_is_closed(pcb_tree_builder));


    // We have a variety of iterators
    Iterator<Element> itr;
    
    // This will go over all nodes, including the root
    System.out.println("-----------------------------------------------------");
    System.out.println("whole tree:\n\n");
    itr = get_iterator(root);

    while (itr.hasNext()) {
        Element e = itr.next();
        System.out.println(indent(depth(e))+e.name);
    }


    // We can also add an acceptor to the iterator, in this example we will only
    // go over the leaf nodes.
    System.out.println("-----------------------------------------------------");
    System.out.println("leafs:\n\n");
    itr = get_iterator(root, (n)->{
        return n.first_child == null;
    });

    while (itr.hasNext()) {
        Element e = itr.next();
        System.out.println(e.name);
    }
    

    // We also have an additional depth that we can use to filter elements.
    // The node passed to `get_iterator` will be seen as depth=0. 
    System.out.println("-----------------------------------------------------");
    System.out.println("nodes with a depth more then 2:\n\n");
    itr = get_iterator(root, (n, depth)->{
        return depth > 2;
    });

    while (itr.hasNext()) {
        Element e = itr.next();
        System.out.println("depth "+depth(e)+": "+e.name);
    }


    System.out.println("-----------------------------------------------------");
    
    // print pretty:
    
    print_tree(root, (e)-> {
        return e.name;
    }, null);

    // print even more pretty with some color:

    String ANSI_RESET  = "\u001b[0m";
    String ANSI_WHITE  = "\u001b[37m";
    String ANSI_GREEN  = "\u001b[32m";

    Print_Tree_Formatter ptf = new Print_Tree_Formatter();
    ptf.print_root_name = false;
    ptf.line_prefix = ANSI_WHITE;

    print_tree(root, (e)-> {
        return ANSI_GREEN + e.name + ANSI_RESET;
    }, ptf);


    System.out.println("-----------------------------------------------------");
    
    // most simple way to loop over the elements:
    for (Element e : root) {
        System.out.println(e.name);
    }



}



static public Add_Type APPEND           = Add_Type.APPEND;
static public Add_Type APPEND_AS_CHILD  = Add_Type.APPEND_AS_CHILD;
static public Add_Type PREPEND          = Add_Type.PREPEND;
static public Add_Type PREPEND_AS_CHILD = Add_Type.PREPEND_AS_CHILD;


static public class Element extends PCB_Node<Element> {
    public String name;
}


static public Element[] element_buffer = new Element[100];
static public int       element_buffer_index = 0;
static {
    for (int i = 0; i < element_buffer.length; i++) {
        element_buffer[i] = new Element();
    }
}

static public PCB_Tree_Builder<Element> pcb_tree_builder = new PCB_Tree_Builder<>();



static public Element begin_element(String name) {
    Element e = element_buffer[element_buffer_index++];
    e.name = name;
    begin_node(pcb_tree_builder, e);
    return e;
}


static public Element begin_element(String name, Element parent, Add_Type add_type) {
    Element e = element_buffer[element_buffer_index++];
    e.name = name;
    begin_node(pcb_tree_builder, e, parent, add_type);
    return e;
}

static public Element end_element() {
    return end_node(pcb_tree_builder);
}

static public Element element(String name) {
    begin_element(name);
    return end_element();
}

static public Element element(String name, Element parent, Add_Type add_type) {
    begin_element(name, parent, add_type);
    return end_element();
}


static public int depth(Element e) {
    int depth = 0;
    while (e.parent != null) {
        depth++;
        e = e.parent;
    }
    return depth;
}

static public String indent(int depth) {
    String r = "";
    while (depth-- > 0) {
        r += "     ";
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