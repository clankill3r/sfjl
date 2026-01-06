/** SFJL_Keystone - v0.50
 
LICENSE:
    See end of file for license information.

REVISION HISTORY:
    See end of file for revision information.

*/
package sfjl;

import java.util.ArrayList;
import processing.core.*;
import processing.event.MouseEvent;
import processing.opengl.PGraphics2D;
import processing.opengl.PGraphics3D;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;
import sfjl.SFJL_Math.Mat3;
import sfjl.SFJL_Math.Vec2;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static sfjl.SFJL_Math.*;

public class SFJL_Keystone {
    private SFJL_Keystone() {}

static public PShader s_keystone;
static public PShader s_keystone_in_out;


static public class Keystone_Context {
    public Vec2 dragged_corner;
    public Keystone_Surface dragged_cornerpin_surface;    
    public ArrayList<Event_Context> event_context_queue_buffer = new ArrayList<>();
    public int event_context_queue_size;
    public int drag_start_x;
    public int drag_start_y;
    public Vec2[] corners_at_start_drag = new Vec2[4]; // if we drag the surface we store all 4, else only the specific corner in [0]
}
static public Keystone_Context keystone_context = new Keystone_Context();
static {
    keystone_context.corners_at_start_drag[0] = new Vec2();
    keystone_context.corners_at_start_drag[1] = new Vec2();
    keystone_context.corners_at_start_drag[2] = new Vec2();
    keystone_context.corners_at_start_drag[3] = new Vec2();
}


static public class Keystone_Surface {
    public Vec2 TL = new Vec2(0, 0); // top left
    public Vec2 TR = new Vec2(1, 0); // top right
    public Vec2 BL = new Vec2(0, 1); // bottom left
    public Vec2 BR = new Vec2(1, 1); // bottom right
    public int color = 0xffff0000;
    public float control_point_size = 30;
    public boolean calibrate = true;
    public Mat3 transform;
    public Mat3 inv_transform;
    public boolean is_clean = false;
}

private static final float PERSPECTIVE_DIVIDE_EPSILON = 1.0E-10f;

// a Keystone_Surface could be drawn more then once,
// hence the separation
static public class Event_Context {
    public Keystone_Surface surface;
    public PMatrix3D mat = new PMatrix3D();
    public int x;
    public int y;
    public int width;
    public int height;
    public PVector mouse = new PVector();
}

static public class MouseEvent_Handler {
    public void mouseEvent(MouseEvent e) {
        handle_mouse_event(e);
    }
}
static public MouseEvent_Handler mouse_event_handler = new MouseEvent_Handler();

static public class Pre_Handler {
    public void pre() {
        keystone_context.event_context_queue_size = 0;
    }
}
static public Pre_Handler pre_handler = new Pre_Handler();

static public PApplet p5;

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

static public void init(PApplet p5) {
    s_keystone = p5.loadShader("shaders/sfjl_keystone_frag.glsl", "shaders/sfjl_keystone_vert.glsl");
    s_keystone_in_out = p5.loadShader("shaders/sfjl_keystone_in_out_frag.glsl", "shaders/sfjl_keystone_vert.glsl");
    p5.registerMethod("mouseEvent", mouse_event_handler);
    p5.registerMethod("pre", pre_handler);
    SFJL_Keystone.p5 = p5;
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

static public void update_transform(Keystone_Surface s) {

    if (s.is_clean) return;

    Mat3 transform = get_quad_to_quad(0, 0, 1f, 0, 1f, 1f, 0, 1f, // source to
            s.TL.x, s.TL.y, s.TR.x, s.TR.y, s.BR.x, s.BR.y, s.BL.x, s.BL.y); // dest

    s.transform = transform;
    s.inv_transform = create_inverse(s.transform);
    s.is_clean = true;
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

static public void render(Keystone_Surface s, PGraphics g, PImage texture, int x, int y, int region_width, int region_height) {

    update_transform(s);

    g.textureMode = PConstants.NORMAL;

    PMatrix3D inv_transform_p5 = new PMatrix3D(
        s.inv_transform.m[0][0], s.inv_transform.m[0][1], s.inv_transform.m[0][2], 1,
        s.inv_transform.m[1][0], s.inv_transform.m[1][1], s.inv_transform.m[1][2], 1,
        s.inv_transform.m[2][0], s.inv_transform.m[2][1], s.inv_transform.m[2][2], 1,
        1, 1, 1, 1
    );

    inv_transform_p5.transpose();

    float min_x = min(s.TL.x, min(s.TR.x, min(s.BR.x, s.BL.x)));
    float min_y = min(s.TL.y, min(s.TR.y, min(s.BR.y, s.BL.y)));
    float max_x = max(s.TL.x, max(s.TR.x, max(s.BR.x, s.BL.x)));
    float max_y = max(s.TL.y, max(s.TR.y, max(s.BR.y, s.BL.y)));

    float x1 = min_x * region_width;
    float y1 = min_y * region_height;    
    float x2 = max_x * region_width;
    float y2 = max_y * region_height;

    // https://github.com/processing/processing4/issues/1134
    // IMPORTANT
    // If processing would fix the issue then it swings back at us.
    // Cause we send a matrix and min max values to the shader based
    // on calculations for 0, 0 being top left.
    // The right way would probably be then to flip the inv_matrix in y
    // for the right fields and also adjust min_y and max_y.
    //
    boolean flip_y_because_of_processing_issue_1134 = texture instanceof PGraphics2D || texture instanceof PGraphics3D;

    s_keystone.set("inv_transform", inv_transform_p5, true);
    s_keystone.set("min_x", min_x);
    s_keystone.set("min_y", min_y);
    s_keystone.set("max_x", max_x);
    s_keystone.set("max_y", max_y);
    s_keystone.set("the_texture", texture);
    s_keystone.set("flip_y_because_of_processing_issue_1134", flip_y_because_of_processing_issue_1134);

    g.shader(s_keystone);
    g.rectMode(PConstants.CORNERS);
    g.noStroke();
    g.fill(255);
    g.rect(x + x1, y + y1, x + x2, y + y2);
    g.resetShader();

    if (s.calibrate) {
        render_frame(s, g, x, y, region_width, region_height);
    }
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

static public void render(Keystone_Surface input_surface, Keystone_Surface output_surface, PGraphics g, PImage texture, int x, int y, int region_width, int region_height) {

    update_transform(input_surface);
    update_transform(output_surface);

    g.textureMode = PConstants.NORMAL;

    var out = output_surface;
   
    PMatrix3D inv_transform_p5 = new PMatrix3D(
        out.inv_transform.m[0][0], out.inv_transform.m[0][1], out.inv_transform.m[0][2], 1,
        out.inv_transform.m[1][0], out.inv_transform.m[1][1], out.inv_transform.m[1][2], 1,
        out.inv_transform.m[2][0], out.inv_transform.m[2][1], out.inv_transform.m[2][2], 1,
        1, 1, 1, 1
    );

    inv_transform_p5.transpose();

    
    float min_x = min(out.TL.x, min(out.TR.x, min(out.BR.x, out.BL.x)));
    float min_y = min(out.TL.y, min(out.TR.y, min(out.BR.y, out.BL.y)));
    float max_x = max(out.TL.x, max(out.TR.x, max(out.BR.x, out.BL.x)));
    float max_y = max(out.TL.y, max(out.TR.y, max(out.BR.y, out.BL.y)));
    
    float x1 = min_x * region_width;
    float y1 = min_y * region_height;    
    float x2 = max_x * region_width;
    float y2 = max_y * region_height;

    Mat3 square_to_quad = SFJL_Keystone.get_square_to_quad(
        input_surface.TL.x,
        input_surface.TL.y,
        input_surface.TR.x,
        input_surface.TR.y,
        input_surface.BR.x,
        input_surface.BR.y,
        input_surface.BL.x,
        input_surface.BL.y
    );

    PMatrix3D square_to_quad_p5 = new PMatrix3D(
        square_to_quad.m[0][0], square_to_quad.m[0][1], square_to_quad.m[0][2], 1,
        square_to_quad.m[1][0], square_to_quad.m[1][1], square_to_quad.m[1][2], 1,
        square_to_quad.m[2][0], square_to_quad.m[2][1], square_to_quad.m[2][2], 1,
        1, 1, 1, 1
    );

    square_to_quad_p5.transpose();

    // Info in other render function
    boolean flip_y_because_of_processing_issue_1134 = texture instanceof PGraphics2D || texture instanceof PGraphics3D;
    
    s_keystone_in_out.set("inv_transform", inv_transform_p5, true);
    s_keystone_in_out.set("min_x", min_x);
    s_keystone_in_out.set("min_y", min_y);
    s_keystone_in_out.set("max_x", max_x);
    s_keystone_in_out.set("max_y", max_y);
    s_keystone_in_out.set("the_texture", texture);
    s_keystone_in_out.set("square_to_quad", square_to_quad_p5, true);
    s_keystone_in_out.set("flip_y_because_of_processing_issue_1134", flip_y_because_of_processing_issue_1134);

    g.shader(s_keystone_in_out);
    g.rectMode(PConstants.CORNERS);
    g.noStroke();
    g.fill(255);
    g.rect(x + x1, y + y1, x + x2, y + y2);
    g.resetShader();

    if (output_surface.calibrate) {
        render_frame(output_surface, g, x, y, region_width, region_height);
    }
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

static public void render_frame(Keystone_Surface s, PGraphics g, int x, int y, int region_width, int region_height) {

    g.noFill();
    g.stroke(s.color);
    g.beginShape(PConstants.QUADS);
    g.vertex(x + s.TL.x * region_width, y + s.TL.y * region_height);
    g.vertex(x + s.TR.x * region_width, y + s.TR.y * region_height);
    g.vertex(x + s.BR.x * region_width, y + s.BR.y * region_height);
    g.vertex(x + s.BL.x * region_width, y + s.BL.y * region_height);
    g.endShape(PConstants.CLOSE);

    if (s.calibrate) {

        //
        // We do some of maybe not required work here.
        // We could be more smart about this but it's not worth the complexity.
        // One of the problems is that a DRAG event can happen more then once
        // in a single frame. And we don't want to invert the matrix more the once...
        //

        if (keystone_context.event_context_queue_buffer.size() == keystone_context.event_context_queue_size) {
            keystone_context.event_context_queue_buffer.add(new Event_Context());
        }
        
        Event_Context event_ctx = keystone_context.event_context_queue_buffer.get(keystone_context.event_context_queue_size);
        keystone_context.event_context_queue_size += 1;

        // getMatrix() will complain about not being implemented,
        // so we do a manual copy...
        PMatrix3D m1 = event_ctx.mat;
        PMatrix3D m2 = ((PGraphicsOpenGL)g).modelview;
        m1.m00 = m2.m00; m1.m01 = m2.m01; m1.m02 = m2.m02; m1.m03 = m2.m03;
        m1.m10 = m2.m10; m1.m11 = m2.m11; m1.m12 = m2.m12; m1.m13 = m2.m13;
        m1.m20 = m2.m20; m1.m21 = m2.m21; m1.m22 = m2.m22; m1.m23 = m2.m23;
        m1.m30 = m2.m30; m1.m31 = m2.m31; m1.m32 = m2.m32; m1.m33 = m2.m33;

        event_ctx.surface = s;
        event_ctx.x = x;
        event_ctx.y = y;
        event_ctx.width = region_width;
        event_ctx.height = region_height;

        event_ctx.mat.invert();

        PVector mouse = event_ctx.mouse;
        mouse.x = p5.mouseX;
        mouse.y = p5.mouseY;
        event_ctx.mat.mult(mouse, mouse);
        mouse.x -= event_ctx.x;
        mouse.y -= event_ctx.y;
        mouse.x /= event_ctx.width;
        mouse.y /= event_ctx.height;

        render_control_points(s, g, x, y, region_width, region_height);
    }

}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

static public void render_control_points(Keystone_Surface s, PGraphics g, int x, int y, int region_width, int region_height) {

    g.stroke(s.color);
    g.strokeWeight(2f);
    g.noFill();
    g.ellipse(x + s.TL.x * region_width, y + s.TL.y * region_height, s.control_point_size, s.control_point_size);
    g.ellipse(x + s.TR.x * region_width, y + s.TR.y * region_height, s.control_point_size, s.control_point_size);
    g.ellipse(x + s.BR.x * region_width, y + s.BR.y * region_height, s.control_point_size, s.control_point_size);
    g.ellipse(x + s.BL.x * region_width, y + s.BL.y * region_height, s.control_point_size, s.control_point_size);
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

static public void move_relative(Keystone_Surface s, float x, float y) {
    move_relative(s, s.TL, x, y);
    move_relative(s, s.TR, x, y);
    move_relative(s, s.BR, x, y);
    move_relative(s, s.BL, x, y);
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

static public Vec2 get_control_point(Keystone_Surface s, float x, float y, int region_width, int region_height) {
    x *= region_width;
    y *= region_height;
    if (PApplet.dist(s.TL.x * region_width, s.TL.y * region_height, x, y) < s.control_point_size / 2f) return s.TL;
    if (PApplet.dist(s.TR.x * region_width, s.TR.y * region_height, x, y) < s.control_point_size / 2f) return s.TR;
    if (PApplet.dist(s.BR.x * region_width, s.BR.y * region_height, x, y) < s.control_point_size / 2f) return s.BR;
    if (PApplet.dist(s.BL.x * region_width, s.BL.y * region_height, x, y) < s.control_point_size / 2f) return s.BL;
    return null;
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

/**
 * This function will give you the position of the mouse in the surface's
 * coordinate system.
 *
 * @return The transformed mouse position
 */

static public Vec2 get_transformed_point(Keystone_Surface s, float cx, float cy, int region_width, int region_height) {
    
    if (!s.is_clean) {
        update_transform(s);
    }
    if (s.inv_transform == null) return null;

    cx /= (float) region_width;
    cy /= (float) region_height;

    Vec2 point = new Vec2(cx, cy);
    transform(s.inv_transform, point, point);
    return new Vec2(point.x * region_width, point.y * region_height);
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
//
// MeshPoint
//

static public void move_relative(Keystone_Surface surface, Vec2 corner, float x, float y) {
    corner.x += x;
    corner.y += y;
    surface.is_clean = false;
}


// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
//
// PerspectiveTransform
//


/**
* Transforms the specified ptSrc and stores the result in ptDst.
* If ptDst is null, a new Point2D object will be allocated before
* storing. In either case, ptDst containing the transformed point
* is returned for convenience.
* Note that ptSrc and ptDst can be the same. In this case, the input
* point will be overwritten with the transformed point.
*
* @param src The array containing the source point objects.
* @param dest The array where the transform point objects are returned.
*/
static public Vec2 transform(Mat3 t, Vec2 src, Vec2 dest) {
    
    if (dest == null) {
       dest = new Vec2();
    }
    
    float x = src.x;
    float y = src.y;
    float w = t.m[2][0] * x + t.m[2][1] * y + t.m[2][2];
    dest.x = (t.m[0][0] * x + t.m[0][1] * y + t.m[0][2]) / w;
    dest.y = (t.m[1][0] * x + t.m[1][1] * y + t.m[1][2]) / w;
    
    return dest;
}



/**
* Scales the matrix elements so m22 is equal to 1.0.
* m22 must not be equal to 0.
*/
static public void _normalize(Mat3 t) {
    float invscale = 1.0f/t.m[2][2];
    t.m[0][0] *= invscale;
    t.m[0][1] *= invscale;
    t.m[0][2] *= invscale;
    t.m[1][0] *= invscale;
    t.m[1][1] *= invscale;
    t.m[1][2] *= invscale;
    t.m[2][0] *= invscale;
    t.m[2][1] *= invscale;
    t.m[2][2] = 1.0f;
}


/**
* Returns a new PerpectiveTransform that is the inverse
* of the current transform.
*/
static public Mat3 create_inverse(Mat3 t) {
    Mat3 tx = make_mat3(t);
    _make_adjoint(tx);
    if (Math.abs(tx.m[2][2]) <  PERSPECTIVE_DIVIDE_EPSILON) {
        // non invertible
        return null;
    }
    _normalize(tx);
    return tx;
}



/**
* Replaces the matrix with its adjoint.
*/
static public void _make_adjoint(Mat3 t) {
    float m00p = t.m[1][1] * t.m[2][2] - t.m[1][2] * t.m[2][1];
    float m01p = t.m[1][2] * t.m[2][0] - t.m[1][0] * t.m[2][2]; // flipped sign
    float m02p = t.m[1][0] * t.m[2][1] - t.m[1][1] * t.m[2][0];
    float m10p = t.m[0][2] * t.m[2][1] - t.m[0][1] * t.m[2][2]; // flipped sign
    float m11p = t.m[0][0] * t.m[2][2] - t.m[0][2] * t.m[2][0];
    float m12p = t.m[0][1] * t.m[2][0] - t.m[0][0] * t.m[2][1]; // flipped sign
    float m20p = t.m[0][1] * t.m[1][2] - t.m[0][2] * t.m[1][1];
    float m21p = t.m[0][2] * t.m[1][0] - t.m[0][0] * t.m[1][2]; // flipped sign
    float m22p = t.m[0][0] * t.m[1][1] - t.m[0][1] * t.m[1][0];
    
    // Transpose and copy sub-determinants
    t.m[0][0] = m00p;
    t.m[0][1] = m10p;
    t.m[0][2] = m20p;
    t.m[1][0] = m01p;
    t.m[1][1] = m11p;
    t.m[1][2] = m21p;
    t.m[2][0] = m02p;
    t.m[2][1] = m12p;
    t.m[2][2] = m22p;
}




/**
* Post-concatenates a given PerspectiveTransform to this transform.
*/
static public void concatenate(Mat3 t, Mat3 tx) {
    
    float m00p = t.m[0][0] * tx.m[0][0] + t.m[1][0] * tx.m[0][1] + t.m[2][0] * tx.m[0][2];
    float m10p = t.m[0][0] * tx.m[1][0] + t.m[1][0] * tx.m[1][1] + t.m[2][0] * tx.m[1][2];
    float m20p = t.m[0][0] * tx.m[2][0] + t.m[1][0] * tx.m[2][1] + t.m[2][0] * tx.m[2][2];
    float m01p = t.m[0][1] * tx.m[0][0] + t.m[1][1] * tx.m[0][1] + t.m[2][1] * tx.m[0][2];
    float m11p = t.m[0][1] * tx.m[1][0] + t.m[1][1] * tx.m[1][1] + t.m[2][1] * tx.m[1][2];
    float m21p = t.m[0][1] * tx.m[2][0] + t.m[1][1] * tx.m[2][1] + t.m[2][1] * tx.m[2][2];
    float m02p = t.m[0][2] * tx.m[0][0] + t.m[1][2] * tx.m[0][1] + t.m[2][2] * tx.m[0][2];
    float m12p = t.m[0][2] * tx.m[1][0] + t.m[1][2] * tx.m[1][1] + t.m[2][2] * tx.m[1][2];
    float m22p = t.m[0][2] * tx.m[2][0] + t.m[1][2] * tx.m[2][1] + t.m[2][2] * tx.m[2][2];
    
    t.m[0][0] = m00p;
    t.m[1][0] = m10p;
    t.m[2][0] = m20p;
    t.m[0][1] = m01p;
    t.m[1][1] = m11p;
    t.m[2][1] = m21p;
    t.m[0][2] = m02p;
    t.m[1][2] = m12p;
    t.m[2][2] = m22p;
}



/**
* Creates a PerspectiveTransform that maps an arbitrary
* quadrilateral onto the unit square.
*
* (x0, y0) -> (0, 0)
* (x1, y1) -> (1, 0)
* (x2, y2) -> (1, 1)
* (x3, y3) -> (0, 1)
*/
static public Mat3 get_quad_to_square(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
    Mat3 tx = new Mat3();
    get_square_to_quad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
    _make_adjoint(tx);
    return tx;
}


/**
* Creates a PerspectiveTransform that maps the unit square
* onto an arbitrary quadrilateral.
*
* (0, 0) -> (x0, y0)
* (1, 0) -> (x1, y1)
* (1, 1) -> (x2, y2)
* (0, 1) -> (x3, y3)
*/
static public Mat3 get_square_to_quad(float x0, float y0,
    float x1, float y1,
    float x2, float y2,
    float x3, float y3) {
    Mat3 tx = new Mat3();
    get_square_to_quad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
    return tx;
}



static public void get_square_to_quad(float x0, float y0,
    float x1, float y1,
    float x2, float y2,
    float x3, float y3,
    Mat3 tx) {
        
    float dx3 = x0 - x1 + x2 - x3;
    float dy3 = y0 - y1 + y2 - y3;
    
    tx.m[2][2] = 1.0F;
    
    if ((dx3 == 0.0F) && (dy3 == 0.0F)) { // to do: use tolerance
        tx.m[0][0] = x1 - x0;
        tx.m[0][1] = x2 - x1;
        tx.m[0][2] = x0;
        tx.m[1][0] = y1 - y0;
        tx.m[1][1] = y2 - y1;
        tx.m[1][2] = y0;
        tx.m[2][0] = 0.0F;
        tx.m[2][1] = 0.0F;
    } else {
        float dx1 = x1 - x2;
        float dy1 = y1 - y2;
        float dx2 = x3 - x2;
        float dy2 = y3 - y2;
        
        float invdet = 1.0F/(dx1*dy2 - dx2*dy1);
        tx.m[2][0] = (dx3*dy2 - dx2*dy3)*invdet;
        tx.m[2][1] = (dx1*dy3 - dx3*dy1)*invdet;
        tx.m[0][0] = x1 - x0 + tx.m[2][0]*x1;
        tx.m[0][1] = x3 - x0 + tx.m[2][1]*x3;
        tx.m[0][2] = x0;
        tx.m[1][0] = y1 - y0 + tx.m[2][0]*y1;
        tx.m[1][1] = y3 - y0 + tx.m[2][1]*y3;
        tx.m[1][2] = y0;
    }
}



/**
* Creates a PerspectiveTransform that maps an arbitrary
* quadrilateral onto another arbitrary quadrilateral.
*
* (x0, y0) -> (x0p, y0p)
* (x1, y1) -> (x1p, y1p)
* (x2, y2) -> (x2p, y2p)
* (x3, y3) -> (x3p, y3p)
*/
static public Mat3 get_quad_to_quad(float x0, float y0,
    float x1, float y1,
    float x2, float y2,
    float x3, float y3,
    float x0p, float y0p,
    float x1p, float y1p,
    float x2p, float y2p,
    float x3p, float y3p) {

    Mat3 tx1 = get_quad_to_square(x0, y0, x1, y1, x2, y2, x3, y3);
    
    Mat3 tx2 = get_square_to_quad(x0p, y0p, x1p, y1p, x2p, y2p, x3p, y3p);
    
    concatenate(tx1, tx2);
    return tx1;
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
//
// Mouse Stuff
//

static public boolean is_point_in_surface(Keystone_Surface s, float x, float y) {
    return (SFJL_Math.point_in_triangle(x , y, s.TL, s.TR, s.BL) || 
    SFJL_Math.point_in_triangle(x, y, s.BL, s.TR, s.BR));
}


// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

static public void handle_mouse_event(MouseEvent e) {
    
    switch (e.getAction()) {

        case MouseEvent.PRESS:

            // inside a surface has higher priority
            for (int i = 0; i < keystone_context.event_context_queue_size; i += 1) {

                Event_Context event_ctx = keystone_context.event_context_queue_buffer.get(i);
                Keystone_Surface s = event_ctx.surface;

                if (is_point_in_surface(s, event_ctx.mouse.x, event_ctx.mouse.y)) {
                    keystone_context.dragged_cornerpin_surface = s;
                    keystone_context.dragged_corner = null;
                    keystone_context.drag_start_x = p5.mouseX;
                    keystone_context.drag_start_y = p5.mouseY;
                    Vec2 cp = get_control_point(s, event_ctx.mouse.x, event_ctx.mouse.y, event_ctx.width, event_ctx.height);
                    
                    if (cp != null) {
                        keystone_context.dragged_corner = cp;
                        keystone_context.corners_at_start_drag[0].x = cp.x;
                        keystone_context.corners_at_start_drag[0].y = cp.y;
                        break;
                    }
                    else {
                        keystone_context.corners_at_start_drag[0].x = s.TL.x;
                        keystone_context.corners_at_start_drag[0].y = s.TL.y;
                        keystone_context.corners_at_start_drag[1].x = s.TR.x;
                        keystone_context.corners_at_start_drag[1].y = s.TR.y;
                        keystone_context.corners_at_start_drag[2].x = s.BL.x;
                        keystone_context.corners_at_start_drag[2].y = s.BL.y;
                        keystone_context.corners_at_start_drag[3].x = s.BR.x;
                        keystone_context.corners_at_start_drag[3].y = s.BR.y;
                    }
                }
            }

            // did we find anything?
            if (keystone_context.dragged_cornerpin_surface == null) {
                for (int i = 0; i < keystone_context.event_context_queue_size; i += 1) {

                    Event_Context event_ctx = keystone_context.event_context_queue_buffer.get(i);
                    Keystone_Surface s = event_ctx.surface;
                    // we are not over the surface but we could
                    // still be over a control point
                    Vec2 cp = get_control_point(s, event_ctx.mouse.x, event_ctx.mouse.y, event_ctx.width, event_ctx.height);
                    if (cp != null) {
                        keystone_context.dragged_corner = cp;
                        // we still store the surface so we know what the corner belongs too
                        keystone_context.dragged_cornerpin_surface = s;
                        keystone_context.drag_start_x = p5.mouseX;
                        keystone_context.drag_start_y = p5.mouseY;
                        keystone_context.corners_at_start_drag[0].x = cp.x;
                        keystone_context.corners_at_start_drag[0].y = cp.y;
                        break;
                    }
                }
            }
            break;

        case MouseEvent.DRAG:

            // we need to find a event_ctx that has a surface equal to the
            // dragged_cornerpin_surface. Which event_ctx this is does not matter
            // cause it's about the amount we dragged
            Event_Context event_ctx = null;

            for (int i = 0; i < keystone_context.event_context_queue_size; i += 1) {

                Event_Context _event_ctx = keystone_context.event_context_queue_buffer.get(i);

                if (_event_ctx.surface == keystone_context.dragged_cornerpin_surface) {
                    event_ctx = _event_ctx;
                    break;
                }
            }

            if (keystone_context.dragged_corner != null) {
               
                PVector drag = new PVector(
                    p5.mouseX - keystone_context.drag_start_x,
                    p5.mouseY - keystone_context.drag_start_y
                );

                // remove translation
                PMatrix3D linear = event_ctx.mat.get();
                linear.m03 = linear.m13 = linear.m23 = 0;

                linear.mult(drag, drag);

                drag.x /= event_ctx.width;
                drag.y /= event_ctx.height;

                keystone_context.dragged_corner.x = keystone_context.corners_at_start_drag[0].x;
                keystone_context.dragged_corner.y = keystone_context.corners_at_start_drag[0].y;
                move_relative(keystone_context.dragged_cornerpin_surface, keystone_context.dragged_corner, drag.x, drag.y);
            }
            else if (keystone_context.dragged_cornerpin_surface != null) {
                // float move_x = event_ctx.mouse.x - event_ctx.pmouse.x;
                // float move_y = event_ctx.mouse.y - event_ctx.pmouse.y;

                PVector drag = new PVector(
                    p5.mouseX - keystone_context.drag_start_x,
                    p5.mouseY - keystone_context.drag_start_y
                );

                // remove translation
                PMatrix3D linear = event_ctx.mat.get();
                linear.m03 = linear.m13 = linear.m23 = 0;

                linear.mult(drag, drag);

                drag.x /= event_ctx.width;
                drag.y /= event_ctx.height;

                Vec2[] start = keystone_context.corners_at_start_drag;
                Keystone_Surface s = keystone_context.dragged_cornerpin_surface;
                s.TL.x = start[0].x;
                s.TL.y = start[0].y;
                s.TR.x = start[1].x;
                s.TR.y = start[1].y;
                s.BL.x = start[2].x;
                s.BL.y = start[2].y;
                s.BR.x = start[3].x;
                s.BR.y = start[3].y;

                move_relative(s, drag.x, drag.y);
            }
        
            break;

        case MouseEvent.RELEASE:
            keystone_context.dragged_corner = null;
            keystone_context.dragged_cornerpin_surface = null;
            break;
    }
    
}

// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

}
/**
revision history:
    0.50  (2026-01-05) first numbered version

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