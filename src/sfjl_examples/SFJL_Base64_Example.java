package sfjl_examples;

import static sfjl.SFJL_Base64.*;


public class SFJL_Base64_Example {


    public static void main(String[] args) {
        String quote = "the quick brown fox jumps over the lazy dog";
        byte[] encoded = base64_encode(BASE64, quote.getBytes());
        byte[] decoded = base64_decode(encoded);
        System.out.println(new String(decoded));
    }
    
}