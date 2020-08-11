package sfjl;


import java.util.ArrayList;


public class SFJL {
     private SFJL() {}
//           SFJL     
    

static public <T> T remove_last(ArrayList<T> arr) {
    if (arr.size() == 0) return null;
    return arr.remove(arr.size()-1);
}


public static <T> T swap_remove(ArrayList<T> list, int index_to_remove) {
    list.set(index_to_remove, list.get(list.size()-1));
    return list.remove(list.size()-1);
}


public static <T> T swap_remove(ArrayList<T> list, T object_to_remove) {
    int index_to_remove = list.indexOf(object_to_remove);
    if (index_to_remove == -1) return null;
    list.set(index_to_remove, list.get(list.size()-1));
    return list.remove(list.size()-1);
}


} // EOF