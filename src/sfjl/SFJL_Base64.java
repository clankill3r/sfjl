package sfjl;

import java.util.Arrays;


public class SFJL_Base64 {
     private SFJL_Base64() {}


public enum Base64_Type {
    BASE64,
    BASE64_URL
}


static public final Base64_Type BASE64     = Base64_Type.BASE64;
static public final Base64_Type BASE64_URL = Base64_Type.BASE64_URL;


static public final byte[] base64_index_table =     {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
static public final byte[] base64_index_table_url = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};


static public final byte[] base64_encode(Base64_Type base64_type, byte[] to_encode) {

    byte[] base64_index_table = base64_type == BASE64 ? SFJL_Base64.base64_index_table : base64_index_table_url;

    int remainder = to_encode.length % 3;
    int max = to_encode.length - remainder;

    int length = to_encode.length / 3 * 4;
    if (remainder != 0) length += 4;

    byte[] result = new byte[length]; 

    int write_index = 0;

    
    for (int i = 0; i < max; i += 3) {

        int r = (to_encode[i+0] & 0xff) << 16 |
                (to_encode[i+1] & 0xff) << 8 |
                (to_encode[i+2] & 0xff);

        result[write_index+0] = base64_index_table[(r >>> 18) & 0x3f];
        result[write_index+1] = base64_index_table[(r >>> 12) & 0x3f];
        result[write_index+2] = base64_index_table[(r >>> 6)  & 0x3f];
        result[write_index+3] = base64_index_table[(r)  & 0x3f];
        write_index += 4;

    }
    
    if (remainder == 2) {
       
        int r = (to_encode[max+0] & 0xff) << 16 |
                (to_encode[max+1] & 0xff) << 8;

        result[write_index+0] = base64_index_table[(r >>> 18) & 0x3f];
        result[write_index+1] = base64_index_table[(r >>> 12) & 0x3f];
        result[write_index+2] = base64_index_table[(r >>> 6)  & 0x3f];
        result[write_index+3] = '=';
        write_index += 4;
        
    } 
    else if (remainder == 1) {
        
        int r = (to_encode[max+0] & 0xff) << 16;

        result[write_index+0] = base64_index_table[(r >>> 18) & 0x3f];
        result[write_index+1] = base64_index_table[(r >>> 12) & 0x3f];
        result[write_index+2] = '=';
        result[write_index+3] = '=';
        write_index += 4;
    }

    return result;
}


public static final int[] char_to_base64_lookup = new int[128];
static {
    Arrays.fill(char_to_base64_lookup, -1);

    for (int i = 0; i < base64_index_table.length; i++) {

        byte c = base64_index_table[i];
        int b64_index = -1;

        if (c >= 64 && c <= 90) {
            b64_index = c - 65;
        }
        else if (c >= 97 && c <= 122) {
            b64_index = c - 97 + 26;
        }
        else if (c >= 48 && c <= 57) {
            b64_index = c - 48 + 26 + 26;
        }

        char_to_base64_lookup[base64_index_table[i]] = b64_index;
    }
    char_to_base64_lookup['+'] = 62;
    char_to_base64_lookup['/'] = 63;

    char_to_base64_lookup['-'] = 62;
    char_to_base64_lookup['_'] = 63;
}


static public final byte[] base64_decode(byte[] base64) {
    
    //
    // check how much padding we have, regardless of if it was given or not
    //
    int padding = -1;
    int length_with_steps_of_4 = base64.length - (base64.length % 4);

    
    if (base64[base64.length-2] == '=') {
        padding = 2;
        length_with_steps_of_4 -= 4;
    }
    else if (base64[base64.length-1] == '=') {
        padding = 1;
        length_with_steps_of_4 -= 4;
    }

    if (padding == -1) {
        int remainder = base64.length % 4;
        if (remainder == 3) {
            padding = 1;
        }
        else if (remainder == 2) {
            padding = 2;
        }
        else {
            assert remainder != -1; // not a valid base64 string
            padding = 0;
        }
    }
    
    // ---
    
    int length = (length_with_steps_of_4 / 4) * 3;
    length += padding == 2 ? 1 : padding == 1 ? 2 : 0;
    
    byte[] result = new byte[length];
    int index = 0;

    //
    // decode
    //
    int i = 0;
    for (; i < length_with_steps_of_4; i += 4) {
        byte c1 = (byte) base64[i+0];
        byte c2 = (byte) base64[i+1];
        byte c3 = (byte) base64[i+2];
        byte c4 = (byte) base64[i+3];
        
        int r = 0;
        r |= char_to_base64_lookup[c1] << 18;
        r |= char_to_base64_lookup[c2] << 12;
        r |= char_to_base64_lookup[c3] << 6;
        r |= char_to_base64_lookup[c4] << 0;

        result[index++] = (byte) (r >> 16);
        result[index++] = (byte) (r >> 8 & 0xff);
        result[index++] = (byte) (r >> 0 & 0xff);
    }
    if (padding == 1) {
        byte c1 = (byte) base64[i+0];
        byte c2 = (byte) base64[i+1];
        byte c3 = (byte) base64[i+2];

        int r = 0;
        r |= char_to_base64_lookup[c1] << 18;
        r |= char_to_base64_lookup[c2] << 12;
        r |= char_to_base64_lookup[c3] << 6;

        result[index++] = (byte) (r >> 16);
        result[index++] = (byte) (r >> 8 & 0xff);
    }
    else if (padding == 2) {
        byte c1 = (byte) base64[i+0];
        byte c2 = (byte) base64[i+1];

        int r = 0;
        r |= char_to_base64_lookup[c1] << 18;
        r |= char_to_base64_lookup[c2] << 12;

        result[index++] = (byte) (r >> 16);
    }
    
    return result;
}

    
}