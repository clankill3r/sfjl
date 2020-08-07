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

    // TODO FIX APPEND when the tree is closed!!!!!!!
    // begin_element("warnings", status_bar, APPEND);

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


}


static public Add_Type APPEND           = Add_Type.APPEND;
static public Add_Type APPEND_AS_CHILD  = Add_Type.APPEND_AS_CHILD;
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

static public Builder<Element> builder = new Builder<>();



static public Element begin_element(String name) {
    Element e = element_buffer[element_buffer_index++];
    e.name = name;
    begin_node(builder, e);
    return e;
}


static public Element begin_element(String name, Element parent, Add_Type add_type) {
    Element e = element_buffer[element_buffer_index++];
    e.name = name;
    begin_node(builder, e, parent, add_type);
    return e;
}

static public Element end_element() {
    return end_node(builder);
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


    
} // EOF