package sfjl;


import static java.lang.Math.*;


//-----------SFJL_Math
public class SFJL_Math {
     private SFJL_Math() {}
    

    
//
// Constants
//
static final public float TWO_PI = (float) (2.0 * PI);


//
// Common
//

static public float map(float value, float istart, float istop, float ostart, float ostop) {
    return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
}

static public double map(double value, double istart, double istop, double ostart, double ostop) {
    return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
}

static public final float norm(float value, float start, float stop) {
    return (value - start) / (stop - start);
}

static public final double norm(double value, double start, double stop) {
    return (value - start) / (stop - start);
}

static public final int constrain(int amt, int low, int high) {
    return (amt < low) ? low : ((amt > high) ? high : amt);
}

static public final double constrain(double amt, double low, double high) {
    return (amt < low) ? low : ((amt > high) ? high : amt);
}

static public final float constrain(float amt, float low, float high) {
    return (amt < low) ? low : ((amt > high) ? high : amt);
}

static public final float atan2(float y, float x) {
    return (float)Math.atan2(y, x);
}

static public final float dist(float x1, float y1, float x2, float y2) {
    return (float) sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
}

static public final double dist(double x1, double y1, double x2, double y2) {
    return (double) sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
}

static public final float dist_sq(float x1, float y1, float x2, float y2) {
    return ((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1));
}

static public final double dist_sq(double x1, double y1, double x2, double y2) {
    return ((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1));
}

static public float nearest(float val, float min, float max, int steps) {
    // we multiply first to get more precise results
    // like 5.8 instead of 5.7999997
    // multiplying first gives more precise results
    val *= steps;
    min *= steps;
    max *= steps;
    float step = (max - min) / steps;
    float diff = val - min;
    float steps_to_diff = round(diff / step);
    float answer = min + step * steps_to_diff;
    return answer / (float) steps;
}

static public double nearest(double val, double min, double max, int steps) {
    // we multiply first to get more precise results
    // like 5.8 instead of 5.7999997
    // multiplying first gives more precise results
    val *= steps;
    min *= steps;
    max *= steps;
    double step = (max - min) / steps;
    double diff = val - min;
    double steps_to_diff = round(diff / step);
    double answer = min + step * steps_to_diff;
    return answer / (double) steps;
}

static public boolean ellipse_hit(float tx, float ty, float cx, float cy, float r) {
    return dist_sq(tx, ty, cx, cy) <= (r*r);
}


//
// Vec2
//

static public class Vec2 {
    public float x;
    public float y;
    public Vec2(){}
    public Vec2(float x, float y) {this.x = x; this.y = y;}
}

static public Vec2 make_vec2(float x, float y) {
    return new Vec2(x, y);
}

static public void mult(Vec2 v, float m) {
    v.x *= m;
    v.y *= m;
}


//
// Mat3
//

static public class Mat3 {
    public float[][] m = new float[3][3];

    public Mat3() {
        set_to_identity(this);
    }
}

static public Mat3 make_mat3(float m00, float m01, float m02,
                             float m10, float m11, float m12,
                             float m20, float m21, float m22) {

    Mat3 m = new Mat3();
    m.m[0][0] = m00; m.m[0][1] = m01; m.m[0][2] = m02;
    m.m[1][0] = m10; m.m[1][1] = m11; m.m[1][2] = m12;
    m.m[2][0] = m20; m.m[2][1] = m21; m.m[2][2] = m22;
    return m;
}


static public Mat3 copy_mat3(Mat3 m) {
    return make_mat3(
        m.m[0][0], m.m[0][1], m.m[0][2], 
        m.m[1][0], m.m[1][1], m.m[1][2], 
        m.m[2][0], m.m[2][1], m.m[2][2]);
}

static public boolean equals(Mat3 a, Mat3 b) {
    // TODO, unroll loop
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            if (a.m[i][j] != b.m[i][j]) return false;
        }
    }
    return true;
}

static public Mat3 make_identity_mat3() {
    return make_mat3(
        1, 0, 0,
        0, 1, 0,
        0, 0, 1);
}

static public void set_to_identity(Mat3 m) {
    set(m, 1, 0, 0,
           0, 1, 0,
           0, 0, 1);
}

static public void set(Mat3 m, float m00, float m01, float m02, 
                               float m10, float m11, float m12, 
                               float m20, float m21, float m22) {
    
    m.m[0][0] = m00;  m.m[0][1] = m01;  m.m[0][2] = m02;
    m.m[1][0] = m10;  m.m[1][1] = m11;  m.m[1][2] = m12;
    m.m[2][0] = m20;  m.m[2][1] = m21;  m.m[2][2] = m22;
}

static public void set(Mat3 m, Mat3 m2) {
    m.m[0][0] = m2.m[0][0];  m.m[0][1] = m2.m[0][1];  m.m[0][2] = m2.m[0][2];
    m.m[1][0] = m2.m[1][0];  m.m[1][1] = m2.m[1][1];  m.m[1][2] = m2.m[1][2];
    m.m[2][0] = m2.m[2][0];  m.m[2][1] = m2.m[2][1];  m.m[2][2] = m2.m[2][2];
}



static public Mat3 make_translation_mat3(float x, float y) {
    return make_mat3(
        1,  0,  x,
        0,  1,  y,
        0,  0,  1
    );
}

static public Mat3 make_scaling_mat3(float x, float y) {
    return make_mat3(
        x, 0, 0,
        0, y, 0,
        0, 0, 1
    );
}

static public Mat3 make_rotation_mat3(float angle) {
    float s = (float) sin(angle);
    float c = (float) cos(angle);
    return make_mat3(
        c, -s,  0,
        s,  c,  0,
        0,  0,  1
    );
}



static public void translate(Mat3 m, float x, float y) {
    mult(m, 1,  0,  x,
            0,  1,  y,
            0,  0,  1);
}


static public void scale(Mat3 m, float x, float y) {
    mult(m, x, 0, 0,
            0, y, 0,
            0, 0, 1);
}


static public void rotate(Mat3 m, float angle) {
    float s = (float) sin(angle);
    float c = (float) cos(angle);
    mult(m, c, -s,  0,
            s,  c,  0,
            0,  0,  1);
}


// ---


static public void transpose(Mat3 source, Mat3 target) {
    Mat3 m = source;
    set(target, m.m[0][0], m.m[1][0], m.m[2][0],
                m.m[0][1], m.m[1][1], m.m[2][1],
                m.m[0][2], m.m[1][2], m.m[2][2]
    );
}


static public void mult(Mat3 m, float m00, float m01, float m02,
                                float m10, float m11, float m12,
                                float m20, float m21, float m22) {
    
    float r00 = m.m[0][0]*m00 + m.m[0][1]*m10 + m.m[0][2]*m20;
    float r01 = m.m[0][0]*m01 + m.m[0][1]*m11 + m.m[0][2]*m21;
    float r02 = m.m[0][0]*m02 + m.m[0][1]*m12 + m.m[0][2]*m22;
  
    float r10 = m.m[1][0]*m00 + m.m[1][1]*m10 + m.m[1][2]*m20;
    float r11 = m.m[1][0]*m01 + m.m[1][1]*m11 + m.m[1][2]*m21;
    float r12 = m.m[1][0]*m02 + m.m[1][1]*m12 + m.m[1][2]*m22;
  
    float r20 = m.m[2][0]*m00 + m.m[2][1]*m10 + m.m[2][2]*m20;
    float r21 = m.m[2][0]*m01 + m.m[2][1]*m11 + m.m[2][2]*m21;
    float r22 = m.m[2][0]*m02 + m.m[2][1]*m12 + m.m[2][2]*m22;

    m.m[0][0] = r00;  m.m[0][1] = r01;  m.m[0][2] = r02;
    m.m[1][0] = r10;  m.m[1][1] = r11;  m.m[1][2] = r12;
    m.m[2][0] = r20;  m.m[2][1] = r21;  m.m[2][2] = r22;
}


static public Mat3 mult(Mat3 a, Mat3 b, Mat3 r) {

    // the for loop version is correct but around 40% slower
    // for(int i = 0; i < 3; i++) {
    //     for(int j = 0; j < 3; j++) {
    //         float sum = 0;
    //         for(int k = 0; k < 3; k++) {
    //             sum += a.m[j][k] * b.m[k][i];
    //         }
    //         r.m[j][i] = sum;
    //     }
    // }
    float r00 = a.m[0][0]*b.m[0][0] + a.m[0][1]*b.m[1][0] + a.m[0][2]*b.m[2][0];
    float r01 = a.m[0][0]*b.m[0][1] + a.m[0][1]*b.m[1][1] + a.m[0][2]*b.m[2][1];
    float r02 = a.m[0][0]*b.m[0][2] + a.m[0][1]*b.m[1][2] + a.m[0][2]*b.m[2][2];
  
    float r10 = a.m[1][0]*b.m[0][0] + a.m[1][1]*b.m[1][0] + a.m[1][2]*b.m[2][0];
    float r11 = a.m[1][0]*b.m[0][1] + a.m[1][1]*b.m[1][1] + a.m[1][2]*b.m[2][1];
    float r12 = a.m[1][0]*b.m[0][2] + a.m[1][1]*b.m[1][2] + a.m[1][2]*b.m[2][2];
  
    float r20 = a.m[2][0]*b.m[0][0] + a.m[2][1]*b.m[1][0] + a.m[2][2]*b.m[2][0];
    float r21 = a.m[2][0]*b.m[0][1] + a.m[2][1]*b.m[1][1] + a.m[2][2]*b.m[2][1];
    float r22 = a.m[2][0]*b.m[0][2] + a.m[2][1]*b.m[1][2] + a.m[2][2]*b.m[2][2];
    
    r.m[0][0] = r00;
    r.m[0][1] = r01;
    r.m[0][2] = r02;
    
    r.m[1][0] = r10;
    r.m[1][1] = r11;
    r.m[1][2] = r12;
    
    r.m[2][0] = r20;
    r.m[2][1] = r21;
    r.m[2][2] = r22;
  
    return r;
}


static public void inverse(Mat3 source, Mat3 target) {

    Mat3 m = source;

    float m00 = m.m[0][0],  m10 = m.m[1][0],  m20 = m.m[2][0];
    float m01 = m.m[0][1],  m11 = m.m[1][1],  m21 = m.m[2][1];
    float m02 = m.m[0][2],  m12 = m.m[1][2],  m22 = m.m[2][2];
    
    float c00 =   m11*m22 - m12*m21,   c10 = -(m01*m22 - m02*m21),  c20 =   m01*m12 - m02*m11;
    float c01 = -(m10*m22 - m12*m20),  c11 =   m00*m22 - m02*m20,   c21 = -(m00*m12 - m02*m10);
    float c02 =   m10*m21 - m11*m20,   c12 = -(m00*m21 - m01*m20),  c22 =   m00*m11 - m01*m10;
    
    float det = m00*c00 + m10*c10 + m20 * c20;
    if (abs(det) < 0.00001f) {

        set(target, 1, 0, 0,
                    0, 1, 0,
                    0, 0, 1);
    }
    else {

        float i00 = c00 / det,  i10 = c01 / det,  i20 = c02 / det;
        float i01 = c10 / det,  i11 = c11 / det,  i21 = c12 / det;
        float i02 = c20 / det,  i12 = c21 / det,  i22 = c22 / det;
        
        set(target, i00, i01, i02,
                    i10, i11, i12,
                    i20, i21, i22
        );

    }   
}


static public Vec2 mult(Mat3 m, Vec2 v, Vec2 result) {
    
    float result_x = m.m[0][0] * v.x + m.m[0][1] * v.y + m.m[0][2];
    float result_y = m.m[1][0] * v.x + m.m[1][1] * v.y + m.m[1][2];
    
    float w = m.m[2][0] * v.x + m.m[2][1] * v.y + m.m[2][2];
    if (w != 0 && w != 1) {
        result_x /= w;
        result_y /= w;
    }
    result.x = result_x;
    result.y = result_y;
    return result;
}



// move to SFJL_Math?
static public float get_rotation(Mat3 m) {
    return atan2(m.m[1][0], m.m[0][0]);  
}


// move to SFJL_Math?
static public void get_scale(Mat3 m, Vec2 result) {
    result.x = (float)Math.sqrt(m.m[0][0] * m.m[0][0] + m.m[1][0] * m.m[1][0]);
    result.y = (float)Math.sqrt(m.m[0][1] * m.m[0][1] + m.m[1][1] * m.m[1][1]);
}



static public float screen_x(Mat3 m, float x, float y) {
    return m.m[0][0] * x + m.m[0][1] * y + m.m[0][2];
}


static public float screen_y(Mat3 m, float x, float y) {
    return m.m[1][0] * x + m.m[1][1] * y + m.m[1][2];
}

static public void screen(Mat3 m, float x, float y, Vec2 result) {
    float result_x = m.m[0][0] * x + m.m[0][1] * y + m.m[0][2];
    float result_y = m.m[1][0] * x + m.m[1][1] * y + m.m[1][2];
    result.x = result_x;
    result.y = result_y;
}

static public void screen(Mat3 m, Vec2 v, Vec2 result) {
    float result_x = m.m[0][0] * v.x + m.m[0][1] * v.y + m.m[0][2];
    float result_y = m.m[1][0] * v.x + m.m[1][1] * v.y + m.m[1][2];
    result.x = result_x;
    result.y = result_y;
}


// TODO check answers https://stackoverflow.com/questions/56645269/check-if-a-matrix-is-axis-aligned
// return 0, 90, 180 or 270 if axis aligned, else returns -1
static public int test_matrix_axis_aligned(Mat3 m) {

    int rot = -1;
    
    // TODO avoid creating those vectors
    Vec2 a = make_vec2(0, 0);
    Vec2 b = make_vec2(1000, 0);
    
    mult(m, a, a);
    mult(m, b, b);

    // TODO rounding with a certain threshold?
    a.x = round(a.x);
    a.y = round(a.y);
    b.x = round(b.x);
    b.y = round(b.y);
    
    boolean axis_aligned = a.x == b.x || a.y == b.y;
    
    if (axis_aligned) {
        
        //float angle = atan2(b.y - a.y, b.x - a.x);
        //println("a: "+angle);
        //if (angle < 0) angle += PI; // wrong for 270
        //rot = (int) round(degrees(angle));
        
        float dx = a.x - b.x;
        float dy = a.y - b.y;
    
        if (dx < 0 && dy == 0) {
            rot = 0;
        } else if (dx == 0 && dy < 0) {
            rot = 90;
        } else if (dx > 0 && dy == 0) {
            rot = 180;
        } else if (dx == 0 && dy > 0) {
            rot = 270;
        }
        
    }
    
    return rot;
}


// -----------------------------------------------------------------------------



    
public boolean rect_rect_intersection_test(float a_x1, float a_y1, float a_x2, float a_y2, float b_x1, float b_y1, float b_x2, float b_y2) {
    return !(a_x2 < b_x1 || a_x1 > b_x2 || a_y2 < b_y1 || a_y1 > b_y2);
}


public static final float sign (float x1, float y1, float x2, float y2, float x3, float y3) {
    return (x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3);
}

// todo, there is a better faster way
public static final boolean point_in_triangle (float tx, float ty, float x1, float y1, float x2, float y2, float x3, float y3) {
    
    boolean b1 = sign(tx, ty, x1, y1, x2, y2) < 0.0f;
    boolean b2 = sign(tx, ty, x2, y2, x3, y3) < 0.0f;
    boolean b3 = sign(tx, ty, x3, y3, x1, y1) < 0.0f;
    
    return ((b1 == b2) && (b2 == b3));
}

public static final boolean point_in_triangle (float tx, float ty, Vec2 a, Vec2 b, Vec2 c) {
    return point_in_triangle(tx, ty, a, b, c);
}


public static final boolean point_inside_quad(float tx, float ty, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
    return point_in_triangle(tx, ty, x1, y1, x2, y2, x3, y3) || point_in_triangle(tx, ty, x1, y1, x3, y3, x4, y4);
}


public static final boolean point_inside_quad(float tx, float ty, Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
    return point_in_triangle(tx, ty, a.x, a.y, b.x, b.y, c.x, c.y) || point_in_triangle(tx, ty, a.x, a.y, c.x, c.y, d.x, d.y);
}


static public final boolean point_inside_rect(float x, float y, Vec2 lt, Vec2 rb) {
    return (!(x < lt.x || x > rb.x ) && !(y < lt.y || y > rb.y ));
}

static public final boolean point_inside_rect(float x, float y, float x1, float y1, float x2, float y2) {
    return (!(x < x1 || x > x2 ) && !(y < y1 || y > y2 ));
}


// ----------------------------------------------------------------------------------------------------------------




}