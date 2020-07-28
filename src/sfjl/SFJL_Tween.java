package sfjl;

import static java.lang.Math.*;


public class SFJL_Tween {
     private SFJL_Tween() {}


static public enum Ease_In_Out_Type {
    EASE_IN,
    EASE_OUT,
    EASE_IN_OUT
}

static public enum Ease_Type {
    SINE,
    LINEAR,
    BOUNCE,
    CIRC,
    CUBIC,
    EXPO,
    QUAD,
    QUART,
    QUINT
}

static public final float ease(Ease_In_Out_Type in_out_type, Ease_Type type, float t, float b, float target, float d) {
    switch (in_out_type) {
        case EASE_IN: return ease_in(type, t, b, target, d);
        case EASE_OUT: return ease_out(type, t, b, target, d);
        case EASE_IN_OUT: return ease_in_out(type, t, b, target, d);
    }
    return -1;
}

static public final float ease_in(Ease_Type type, float t, float b, float target, float d) {
    switch (type) {
        case SINE:   return Sine.ease_in  (t, b, target-b, d);
        case LINEAR: return Linear.ease_in(t, b, target-b, d);
        case BOUNCE: return Bounce.ease_in(t, b, target-b, d);
        case CIRC:   return Circ.ease_in  (t, b, target-b, d);
        case CUBIC:  return Cubic.ease_in (t, b, target-b, d);
        case EXPO:   return Expo.ease_in  (t, b, target-b, d);
        case QUAD:   return Quad.ease_in  (t, b, target-b, d);
        case QUART:  return Quart.ease_in (t, b, target-b, d);
        case QUINT:  return Quint.ease_in (t, b, target-b, d);
    }
    return -1;
}

static public final float ease_out(Ease_Type type, float t, float b, float target, float d) {
    switch (type) {
        case SINE:   return Sine.ease_out  (t, b, target-b, d);
        case LINEAR: return Linear.ease_out(t, b, target-b, d);
        case BOUNCE: return Bounce.ease_out(t, b, target-b, d);
        case CIRC:   return Circ.ease_out  (t, b, target-b, d);
        case CUBIC:  return Cubic.ease_out (t, b, target-b, d);
        case EXPO:   return Expo.ease_out  (t, b, target-b, d);
        case QUAD:   return Quad.ease_out  (t, b, target-b, d);
        case QUART:  return Quart.ease_out (t, b, target-b, d);
        case QUINT:  return Quint.ease_out (t, b, target-b, d);
    }
    return -1;
}

static public final float ease_in_out(Ease_Type type, float t, float b, float target, float d) {
    switch (type) {
        case SINE:   return Sine.ease_in_out  (t, b, target-b, d);
        case LINEAR: return Linear.ease_in_out(t, b, target-b, d);
        case BOUNCE: return Bounce.ease_in_out(t, b, target-b, d);
        case CIRC:   return Circ.ease_in_out  (t, b, target-b, d);
        case CUBIC:  return Cubic.ease_in_out (t, b, target-b, d);
        case EXPO:   return Expo.ease_in_out  (t, b, target-b, d);
        case QUAD:   return Quad.ease_in_out  (t, b, target-b, d);
        case QUART:  return Quart.ease_in_out (t, b, target-b, d);
        case QUINT:  return Quint.ease_in_out (t, b, target-b, d);
    }
    return -1;
}

// Sine

static public final class Sine {

    static public final float ease_in(float t, float b, float c, float d) {   
        return -c * (float)cos(t/d * (PI/2)) + c + b;
    }

    static public final float ease_out(float t, float b, float c, float d) {
        return c * (float)sin(t/d * (PI/2)) + b;	
    }

    static public final float ease_in_out(float t, float b, float c, float d) {
        return -c/2 * ((float)cos(PI*t/d) - 1) + b;
    }
}

// Linear

static public final class Linear {

    static public final float ease_in(float t, float b, float c, float d) {   
        return c*t/d + b;
    }

    static public final float ease_out(float t, float b, float c, float d) {
        return c*t/d + b;
    }

    static public final float ease_in_out(float t, float b, float c, float d) {
        return c*t/d + b;
    }
}

// Bounce

static public final class Bounce {

    static public final float ease_in(float t, float b, float c, float d) {   
        return c - ease_out(d-t, 0, c, d) + b;
    }

    static public final float ease_out(float t, float b, float c, float d) {
        if ((t/=d) < (1/2.75f)) {
            return c*(7.5625f*t*t) + b;
        } else if (t < (2/2.75f)) {
            return c*(7.5625f*(t-=(1.5f/2.75f))*t + .75f) + b;
        } else if (t < (2.5/2.75)) {
            return c*(7.5625f*(t-=(2.25f/2.75f))*t + .9375f) + b;
        } else {
            return c*(7.5625f*(t-=(2.625f/2.75f))*t + .984375f) + b;
        }
    }

    static public final float ease_in_out(float t, float b, float c, float d) {
        if (t < d/2) return ease_in (t*2, 0, c, d) * .5f + b;
        else return ease_out (t*2-d, 0, c, d) * .5f + c*.5f + b;
    }
}

// Circ

static public final class Circ {

    static public final float ease_in(float t, float b, float c, float d) {   
        return -c * ((float)Math.sqrt(1 - (t/=d)*t) - 1) + b;
    }

    static public final float ease_out(float t, float b, float c, float d) {
        return c * (float)Math.sqrt(1 - (t=t/d-1)*t) + b;
    }

    static public final float ease_in_out(float t, float b, float c, float d) {
        if ((t/=d/2) < 1) return -c/2 * ((float)Math.sqrt(1 - t*t) - 1) + b;
        return c/2 * ((float)Math.sqrt(1 - (t-=2)*t) + 1) + b;
    }
}

// Cubic

static public final class Cubic {

    static public final float ease_in(float t, float b, float c, float d) {   
        return c*(t/=d)*t*t + b;
    }

    static public final float ease_out(float t, float b, float c, float d) {
        return c*((t=t/d-1)*t*t + 1) + b;
    }

    static public final float ease_in_out(float t, float b, float c, float d) {
        if ((t/=d/2) < 1) return c/2*t*t*t + b;
        return c/2*((t-=2)*t*t + 2) + b;
    }
}

// Expo

static public final class Expo {

    static public final float ease_in(float t, float b, float c, float d) {   
        return (t==0) ? b : c * (float)Math.pow(2, 10 * (t/d - 1)) + b;
    }

    static public final float ease_out(float t, float b, float c, float d) {
        return (t==d) ? b+c : c * (-(float)Math.pow(2, -10 * t/d) + 1) + b;	
    }

    static public final float ease_in_out(float t, float b, float c, float d) {
        if (t==0) return b;
        if (t==d) return b+c;
        if ((t/=d/2) < 1) return c/2 * (float)Math.pow(2, 10 * (t - 1)) + b;
        return c/2 * (-(float)Math.pow(2, -10 * --t) + 2) + b;
    }
}

// Quad

static public final class Quad {

    static public final float ease_in(float t, float b, float c, float d) {   
        return c*(t/=d)*t + b;
    }

    static public final float ease_out(float t, float b, float c, float d) {
        return -c *(t/=d)*(t-2) + b;
    }

    static public final float ease_in_out(float t, float b, float c, float d) {
        if ((t/=d/2) < 1) return c/2*t*t + b;
        return -c/2 * ((--t)*(t-2) - 1) + b;
    }
}

// Quart

static public final class Quart {

    static public final float ease_in(float t, float b, float c, float d) {   
        return c*(t/=d)*t*t*t + b;
    }

    static public final float ease_out(float t, float b, float c, float d) {
        return -c * ((t=t/d-1)*t*t*t - 1) + b;
    }

    static public final float ease_in_out(float t, float b, float c, float d) {
        if ((t/=d/2) < 1) return c/2*t*t*t*t + b;
        return -c/2 * ((t-=2)*t*t*t - 2) + b;
    }
}

// Quint

static public final class Quint {

    static public final float ease_in(float t, float b, float c, float d) {   
        return c*(t/=d)*t*t*t*t + b;
    }

    static public final float ease_out(float t, float b, float c, float d) {
        return c*((t=t/d-1)*t*t*t*t + 1) + b;
    }

    static public final float ease_in_out(float t, float b, float c, float d) {
        if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
        return c/2*((t-=2)*t*t*t*t + 2) + b;
    }
}


    
}