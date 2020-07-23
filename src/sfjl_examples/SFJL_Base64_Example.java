package sfjl_examples;

import static sfjl.SFJL_Base64.*;
import processing.core.PApplet;
import java.nio.charset.StandardCharsets;


public class SFJL_Base64_Example extends PApplet {

    public static void main(String[] args) {
        PApplet.main(SFJL_Base64_Example.class, args);
    }


    @Override
    public void setup() {

        // I used quotes from https://en.wikipedia.org/wiki/Base64
        // so I could compare against the wikipedia results

        String thomas_hobbes_quote = String.join("",
            "Man is distinguished, not only by his reason, but by this singular passion from other animals, ", 
            "which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable ",
            "generation of knowledge, exceeds the short vehemence of any carnal pleasure.");


        // java.nio.charset.CharsetEncoder
        // why is the encoded not a String?
        // byte[] encoded     = base64_encode(BASE64, thomas_hobbes_quote.getBytes());
        String encoded = new String(base64_encode(BASE64, thomas_hobbes_quote.getBytes()));
        String decoded = new String(base64_decode(encoded));


        System.out.println(decoded);
        System.out.println(thomas_hobbes_quote.equals(decoded));

        // System.out.println(encoded_str);


        // String decoded = new String(base64_decode(encoded), StandardCharsets.UTF_8);
        
        // wiki: 
        // moi:  TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=
    }
    
}