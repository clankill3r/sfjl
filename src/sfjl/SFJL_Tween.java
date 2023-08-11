/** SFJL_Tween - v0.51
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

EXAMPLE:
    See SFJL_Tween_Example.java

*/
package sfjl;

import static java.lang.Math.*;

public class SFJL_Tween {
     private SFJL_Tween() {}
//           SFJL_Tween

static public enum In_Out_Type {
    IN,
    OUT,
    IN_OUT
}

static public enum Ease_Type {
    SINE,
    BOUNCE,
    CIRC,
    CUBIC,
    EXPO,
    QUAD,
    QUART,
    QUINT,
    LINEAR
}


static public enum Tween {
    SINE_IN(Ease_Type.SINE, In_Out_Type.IN, Sine::in),
    SINE_OUT(Ease_Type.SINE, In_Out_Type.OUT, Sine::out),
    SINE_IN_OUT(Ease_Type.SINE, In_Out_Type.IN_OUT, Sine::in_out),
    
    BOUNCE_IN(Ease_Type.BOUNCE, In_Out_Type.IN, Bounce::in),
    BOUNCE_OUT(Ease_Type.BOUNCE, In_Out_Type.OUT, Bounce::out),
    BOUNCE_IN_OUT(Ease_Type.BOUNCE, In_Out_Type.IN_OUT, Bounce::in_out),
    
    CIRC_IN(Ease_Type.CIRC, In_Out_Type.IN, Circ::in),
    CIRC_OUT(Ease_Type.CIRC, In_Out_Type.OUT, Circ::out),
    CIRC_IN_OUT(Ease_Type.CIRC, In_Out_Type.IN_OUT, Circ::in_out),
    
    CUBIC_IN(Ease_Type.CUBIC, In_Out_Type.IN, Cubic::in),
    CUBIC_OUT(Ease_Type.CUBIC, In_Out_Type.OUT, Cubic::out),
    CUBIC_IN_OUT(Ease_Type.CUBIC, In_Out_Type.IN_OUT, Cubic::in_out),
    
    EXPO_IN(Ease_Type.EXPO, In_Out_Type.IN, Expo::in),
    EXPO_OUT(Ease_Type.EXPO, In_Out_Type.OUT, Expo::out),
    EXPO_IN_OUT(Ease_Type.EXPO, In_Out_Type.IN_OUT, Expo::in_out),
    
    QUAD_IN(Ease_Type.QUAD, In_Out_Type.IN, Quad::in),
    QUAD_OUT(Ease_Type.QUAD, In_Out_Type.OUT, Quad::out),
    QUAD_IN_OUT(Ease_Type.QUAD, In_Out_Type.IN_OUT, Quad::in_out),
    
    QUART_IN(Ease_Type.QUART, In_Out_Type.IN, Quart::in),
    QUART_OUT(Ease_Type.QUART, In_Out_Type.OUT, Quart::out),
    QUART_IN_OUT(Ease_Type.QUART, In_Out_Type.IN_OUT, Quart::in_out),
    
    QUINT_IN(Ease_Type.QUINT, In_Out_Type.IN, Quint::in),
    QUINT_OUT(Ease_Type.QUINT, In_Out_Type.OUT, Quint::out),
    QUINT_IN_OUT(Ease_Type.QUINT, In_Out_Type.IN_OUT, Quint::in_out),
    
    LINEAR(Ease_Type.LINEAR, In_Out_Type.IN, Linear::in);
    
    public Ease_Type ease_type;
    public In_Out_Type in_out_type;
    public Ease ref;

    Tween(Ease_Type ease_type, In_Out_Type in_out_type, Ease ease) {
        this.ease_type = ease_type;
        this.in_out_type = in_out_type;
        this.ref = ease;
    }
}



static public final float ease(In_Out_Type in_out_type, Ease_Type type, float begin, float target, float t) {
    switch (in_out_type) {
        case IN: return ease_in(type, begin, target, t);
        case OUT: return ease_out(type, begin, target, t);
        case IN_OUT: return ease_in_out(type, begin, target, t);
    }
    return -1;
}

static public final float ease_in(Ease_Type type, float begin, float target, float t) {
    switch (type) {
        case SINE:   return Sine.in  (begin, target-begin, t);
        case LINEAR: return Linear.in(begin, target-begin, t);
        case BOUNCE: return Bounce.in(begin, target-begin, t);
        case CIRC:   return Circ.in  (begin, target-begin, t);
        case CUBIC:  return Cubic.in (begin, target-begin, t);
        case EXPO:   return Expo.in  (begin, target-begin, t);
        case QUAD:   return Quad.in  (begin, target-begin, t);
        case QUART:  return Quart.in (begin, target-begin, t);
        case QUINT:  return Quint.in (begin, target-begin, t);
    }
    return -1;
}

static public final float ease_out(Ease_Type type, float begin, float target, float t) {
    switch (type) {
        case SINE:   return Sine.out  (begin, target-begin, t);
        case LINEAR: return Linear.out(begin, target-begin, t);
        case BOUNCE: return Bounce.out(begin, target-begin, t);
        case CIRC:   return Circ.out  (begin, target-begin, t);
        case CUBIC:  return Cubic.out (begin, target-begin, t);
        case EXPO:   return Expo.out  (begin, target-begin, t);
        case QUAD:   return Quad.out  (begin, target-begin, t);
        case QUART:  return Quart.out (begin, target-begin, t);
        case QUINT:  return Quint.out (begin, target-begin, t);
    }
    return -1;
}

static public final float ease_in_out(Ease_Type type, float begin, float target, float t) {
    switch (type) {
        case SINE:   return Sine.in_out  (begin, target-begin, t);
        case LINEAR: return Linear.in_out(begin, target-begin, t);
        case BOUNCE: return Bounce.in_out(begin, target-begin, t);
        case CIRC:   return Circ.in_out  (begin, target-begin, t);
        case CUBIC:  return Cubic.in_out (begin, target-begin, t);
        case EXPO:   return Expo.in_out  (begin, target-begin, t);
        case QUAD:   return Quad.in_out  (begin, target-begin, t);
        case QUART:  return Quart.in_out (begin, target-begin, t);
        case QUINT:  return Quint.in_out (begin, target-begin, t);
    }
    return -1;
}

public interface Ease {
    float ease(float b, float c, float t);
}


static public final class Sine {

    static public final float in(float b, float c, float t) {   
        return -c * (float)cos(t * (PI/2)) + c + b;
    }

    static public final float out(float b, float c, float t) {
        return c * (float)sin(t * (PI/2)) + b;	
    }

    static public final float in_out(float b, float c, float t) {
        return -c/2 * ((float)cos(PI*t) - 1) + b;
    }
}

// Linear

static public final class Linear {

    static public final float in(float b, float c, float t) {   
        return c*t + b;
    }

    static public final float out(float b, float c, float t) {
        return c*t + b;
    }

    static public final float in_out(float b, float c, float t) {
        return c*t + b;
    }
}

// Bounce
static public final class Bounce {

    static public final float in(float b, float c, float t) {   
        return c - out(0, c, 1f-t) + b;
    }

    static public final float out(float b, float c, float t) {
        if ((t) < (1/2.75f)) {
            return c*(7.5625f*t*t) + b;
        } else if (t < (2/2.75f)) {
            return c*(7.5625f*(t-=(1.5f/2.75f))*t + .75f) + b;
        } else if (t < (2.5/2.75)) {
            return c*(7.5625f*(t-=(2.25f/2.75f))*t + .9375f) + b;
        } else {
            return c*(7.5625f*(t-=(2.625f/2.75f))*t + .984375f) + b;
        }
    }

    static public final float in_out(float b, float c, float t) {
        if (t < 0.5) return in (0, c, t*2) * .5f + b;
        else return out (0, c, t*2-1) * .5f + c*.5f + b;
    }
}

// Circ

static public final class Circ {

    static public final float in(float b, float c, float t) {   
        return -c * ((float)Math.sqrt(1 - t*t) - 1) + b;
    }

    static public final float out(float b, float c, float t) {
        return c * (float)Math.sqrt(1 - (t=t-1)*t) + b;
    }

    static public final float in_out(float b, float c, float t) {
        if ((t/=0.5f) < 1)
            return -c/2 * ((float) Math.sqrt(1 - t*t) - 1) + b;
        return c/2 * ((float) Math.sqrt(1 - (t-=2)*t) + 1) + b;
    }
}


// Cubic

static public final class Cubic {

    static public final float in(float b, float c, float t) {   
        return c*t*t*t + b;
    }

    static public final float out(float b, float c, float t) {
        return c*((t=t-1)*t*t + 1) + b;
    }

    static public final float in_out(float b, float c, float t) {
        if ((t/=0.5f) < 1) return c/2*t*t*t + b;
        return c/2*((t-=2)*t*t + 2) + b;
    }
}

// Expo

static public final class Expo {

    static public final float in(float b, float c, float t) {   
        return (t==0) ? b : c * (float)Math.pow(2, 10 * (t - 1)) + b;
    }

    static public final float out(float b, float c, float t) {
        return (t==1.0) ? b+c : c * (-(float)Math.pow(2, -10 * t) + 1) + b;	
    }

    static public final float in_out(float b, float c, float t) {
        if (t==0) return b;
        if (t==1) return b+c;
        if ((t /= 0.5f) < 1) return c/2 * (float)Math.pow(2, 10 * (t - 1)) + b;
        return c/2 * (-(float)Math.pow(2, -10 * --t) + 2) + b;
    }
}

// Quad

static public final class Quad {

    static public final float in(float b, float c, float t) {   
        return c*t*t + b;
    }

    static public final float out(float b, float c, float t) {
        return -c *t*(t-2) + b;
    }

    static public final float in_out(float b, float c, float t) {
        if ((t/=0.5f) < 1) return c/2*t*t + b;
        return -c/2 * ((--t)*(t-2) - 1) + b;
    }
}

// Quart

static public final class Quart {

    static public final float in(float b, float c, float t) {   
        return c*t*t*t*t + b;
    }

    static public final float out(float b, float c, float t) {
        return -c * ((t=t-1)*t*t*t - 1) + b;
    }

    static public final float in_out(float b, float c, float t) {
        if ((t/=0.5f) < 1) return c/2*t*t*t*t + b;
        return -c/2 * ((t-=2)*t*t*t - 2) + b;
    }
}

// Quint

static public final class Quint {

    static public final float in(float b, float c, float t) {   
        return c*t*t*t*t*t + b;
    }

    static public final float out(float b, float c, float t) {
        return c*((t=t-1)*t*t*t*t + 1) + b;
    }

    static public final float in_out(float b, float c, float t) {
        if ((t/=0.5f) < 1) return c/2*t*t*t*t*t + b;
        return c/2*((t-=2)*t*t*t*t + 2) + b;
    }
}
   
}
/**
revision history:

   0.51  (2023-07-04) - replaced time and duration parameter with a single t (0..1)
                      - renamed Ease_In_Out_Type to In_Out_Type
                      - added Tween enum that has all options + method reference 
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