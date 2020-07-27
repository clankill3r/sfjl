package sfjl;

/*

Fowler–Noll–Vo Hash

http://www.isthe.com/chongo/tech/comp/fnv/



DEPENDENCIES:
- none

Note(Doeke): I don't trust the JITC to inline that well, that's why the usage of fnv_hash_64_builder is very litle.

*/
public class SFJL_FNV_Hash {
    private SFJL_FNV_Hash() {
    }


// "In the general case, almost any offset_basis will serve so long as it is non-zero."
//      quote by: - http://www.isthe.com/chongo/tech/comp/fnv/
// Note(Doeke): I can't remember how I got to 1083068131, neither I care (I sure did some effort)
static public final int FNV_START_HASH_32 = 1083068131;
static public final int FNV_START_HASH_64 = FNV_START_HASH_32;

static public final long _FNV_64_MULTIPLIER = 1099511628211L;
static public final int  _FNV_32_MULTIPLIER = 16777619;


static public long fnv_hash_64b_to_n_bits(long hash_64, int n_bits) {
    long MASK_X = (1L<<n_bits)-1;
    return (hash_64>>>n_bits) ^ (hash_64 & MASK_X);
}

static public int fnv_hash_32b_to_n_bits(int hash_32, int n_bits) {
    int MASK_X = (1<<n_bits)-1;
    return (hash_32>>>n_bits) ^ (hash_32 & MASK_X);
}

// -----------------------------------------------------------------

// start with anything but 0, START_HASH_64 is a good start
static public long fnv_hash_64_builder(long h, long i) {
    assert h != 0;
    h = ( h ^ i ) * _FNV_64_MULTIPLIER;
    return h;
}

// start with anything but 0, START_HASH_32 is a good start
static public int fnv_hash_32_builder(int h, int i) {
    assert h != 0;
    h = ( h ^ i ) * _FNV_32_MULTIPLIER;
    return h;
}

// -----------------------------------------------------------------

//
// 64
//

static public long fnv_hash_64(long[] p, int length) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < length; i++ ) {
        h = ( h ^ p[i] ) * _FNV_64_MULTIPLIER;
    }
    return h;
}

static public long fnv_hash_64(double[] p, int length) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < length; i++ ) {
        h = ( h ^ Double.doubleToLongBits(p[i])) * _FNV_64_MULTIPLIER;
    }
    return h;
}

static public long fnv_hash_64(int[] p, int length) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < length; i++ ) {
        h = ( h ^ p[i] ) * _FNV_64_MULTIPLIER;
    }
    return h;
}

static public long fnv_hash_64(float[] p, int length) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < length; i++ ) {
        h = ( h ^ Float.floatToIntBits(p[i])) * _FNV_64_MULTIPLIER;
    }
    return h;
}

static public long fnv_hash_64(byte[] p, int length) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < length; i++ ) {
        h = ( h ^ p[i]) * _FNV_64_MULTIPLIER;
    }
    return h;
}

static public long fnv_hash_64(short[] p, int length) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < length; i++ ) {
        h = ( h ^ p[i]) * _FNV_64_MULTIPLIER;
    }
    return h;
}

static public long fnv_hash_64(char[] p, int length) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < length; i++ ) {
        h = ( h ^ p[i]) * _FNV_64_MULTIPLIER;
    }
    return h;
}

static public long fnv_hash_64(String s) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < s.length(); i++ ) {
        int c = s.charAt(i);
        h = ( h ^ c ) * _FNV_64_MULTIPLIER;
    }
    return h;
}

static public long fnv_hash_64(long   []p) { return fnv_hash_64(p, p.length); }
static public long fnv_hash_64(double []p) { return fnv_hash_64(p, p.length); }
static public long fnv_hash_64(int    []p) { return fnv_hash_64(p, p.length); }
static public long fnv_hash_64(float  []p) { return fnv_hash_64(p, p.length); }
static public long fnv_hash_64(byte   []p) { return fnv_hash_64(p, p.length); }
static public long fnv_hash_64(short  []p) { return fnv_hash_64(p, p.length); }
static public long fnv_hash_64(char   []p) { return fnv_hash_64(p, p.length); }
static public long fnv_hash_64(String []p) { 
    long h = FNV_START_HASH_64;
    for (String s : p) {
        for (int i = 0; i < s.length(); i++) {
            h = fnv_hash_64_builder(h, s.charAt(i));
        }
    }
    return h;
}

// -----------------------------------------------------------------

//
// 32
//

static public int fnv_hash_32(long[] p, int length) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(p, length), 32);
}

static public int fnv_hash_32(double[] p, int length) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(p, length), 32);
}

static public int fnv_hash_32(int[] p, int length) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(p, length), 32);
}

static public int fnv_hash_32(float[] p, int length) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(p, length), 32);
}

static public int fnv_hash_32(byte[] p, int length) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(p, length), 32);
}

static public int fnv_hash_32(short[] p, int length) {
    return (int) fnv_hash_64b_to_n_bits(fnv_hash_64(p, length), 32);
}

static public int fnv_hash_32(char[] p, int length) {
    // Note(Doeke): hash to match String, therefor we don't reduce
    int h = FNV_START_HASH_32;
    for (int i = 0; i < length; i++) {
        h = fnv_hash_32_builder(h, p[i]);
    }
    return h;
}

static public int fnv_hash_32(String s) {
    int h = FNV_START_HASH_32;
    for (int i = 0; i < s.length(); i++) {
        int c = s.charAt(i);
        h = ( h ^ c ) * _FNV_32_MULTIPLIER;
    }
    return h;
}


static public long fnv_hash_32(long   []p) { return fnv_hash_32(p, p.length); }
static public long fnv_hash_32(double []p) { return fnv_hash_32(p, p.length); }
static public long fnv_hash_32(int    []p) { return fnv_hash_32(p, p.length); }
static public long fnv_hash_32(float  []p) { return fnv_hash_32(p, p.length); }
static public long fnv_hash_32(byte   []p) { return fnv_hash_32(p, p.length); }
static public long fnv_hash_32(short  []p) { return fnv_hash_32(p, p.length); }
static public long fnv_hash_32(char   []p) { return fnv_hash_32(p, p.length); }
static public long fnv_hash_32(String []p) { 
    
    int h = FNV_START_HASH_32;
    for (String s : p) {
        for (int i = 0; i < s.length(); i++) {
            h = fnv_hash_32_builder(h, s.charAt(i));
        }
    }
    return h;
}

 
}