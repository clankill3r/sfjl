package sfjl;

/*
sfjl_hash

http://www.isthe.com/chongo/tech/comp/fnv/



DEPENDENCIES:
- none

*/
public class SFJL_FNV_Hash {
    private SFJL_FNV_Hash() {
    }


// "In the general case, almost any offset_basis will serve so long as it is non-zero."
//      quote by: - http://www.isthe.com/chongo/tech/comp/fnv/
// I can't remember how I got to 1083068131, neither I care
static public final int FNV_START_HASH_32 = 1083068131;
static public final int FNV_START_HASH_64 = FNV_START_HASH_32;




static public long fnv_hash_64(long... p) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < p.length; i++ ) {
        h = ( h ^ p[i] ) * 1099511628211L;
    }
    return h;
}

static public long fnv_hash_56(long... p) {
    long h = fnv_hash_64(p);
    long MASK_56 = (1L<<56)-1;
    return (h>>>56) ^ (h & MASK_56);
}

static public long fnv_hash_48(long... p) {
    long h = fnv_hash_64(p);
    long MASK_48 = (1L<<48)-1;
    return (h>>>48) ^ (h & MASK_48);
}

static public long fnv_hash_40(long... p) {
    long h = fnv_hash_40(p);
    long MASK_40 = (1L<<40)-1;
    return (h>>>40) ^ (h & MASK_40);
}

static public int fnv_hash_32(int... p) {
    int h = FNV_START_HASH_32;
    for (int i = 0; i < p.length; i++ ) {
        h = ( h ^ p[i] ) * 16777619;
    }
    return h;
}

static public int fnv_hash_24(int... p) {
    int h = fnv_hash_32(p);
    int MASK_24 = (1<<24)-1;
    return (h>>>24) ^ (h & MASK_24);
}

static public int fnv_hash_16(int... p) {
    int h = fnv_hash_32(p);
    int MASK_16 = (1<<16)-1;
    return (h>>>16) ^ (h & MASK_16);
}

static public int fnv_hash_8(int... p) {
    int h = fnv_hash_32(p);
    int MASK_8 = (1<<8)-1;
    return (h>>>8) ^ (h & MASK_8);
}

static public long fnv_hash_64(String s) {
    long h = FNV_START_HASH_64;
    for (int i = 0; i < s.length(); i++ ) {
        int c = s.charAt(i);
        h = ( h ^ c ) * 1099511628211L;
    }
    return h;
}

static public int fnv_hash_32(String s) {
    int h = FNV_START_HASH_32;
    for (int i = 0; i < s.length(); i++) {
        int c = s.charAt(i);
        h = ( h ^ c ) * 16777619;
    }
    return h;
}


// start with START_HASH_64
static public long fnv_hash_64_builder(long h, long i) {
    assert h != 0;
    h = ( h ^ i ) * 1099511628211L;
    return h;
}

// start with START_HASH_32
static public int fnv_hash_32_builder(int h, int i) {
    assert h != 0;
    h = ( h ^ i ) * 16777619;
    return h;
}

static public long fnv_hash_64b_to_56b(long hash_64) {
    long MASK_56 = (1L<<56)-1;
    return (hash_64>>>56) ^ (hash_64 & MASK_56);
}

static public long fnv_hash_64b_to_48b(long hash_64) {
    long MASK_48 = (1L<<48)-1;
    return (hash_64>>>48) ^ (hash_64 & MASK_48);
}

static public long fnv_hash_64b_to_40b(long hash_64) {
    long MASK_40 = (1L<<40)-1;
    return (hash_64>>>40) ^ (hash_64 & MASK_40);
}

static public int fnv_hash_32b_to_24b(int hash_32) {
    int MASK_24 = (1<<24)-1;
    return (hash_32>>>24) ^ (hash_32 & MASK_24);
}

static public int fnv_hash_32b_to_16b(int hash_32) {
    int MASK_16 = (1<<16)-1;
    return (hash_32>>>16) ^ (hash_32 & MASK_16);
}

static public int fnv_hash_32b_to_8b(int hash_32) {
    int MASK_8 = (1<<8)-1;
    return (hash_32>>>8) ^ (hash_32 & MASK_8);
}


 
}