uniform sampler2D the_texture;

uniform mat3x3 inv_transform;

uniform float min_x;
uniform float min_y;
uniform float max_x;
uniform float max_y;

uniform mat3x3 square_to_quad;

varying vec4 vertTexCoord;

uniform bool flip_y_because_of_processing_issue_1134; // https://github.com/processing/processing4/issues/1134

vec2 transform(mat3 t, vec2 src) {
    vec3 hom = vec3(src, 1.0);             // Convert to homogeneous coordinates
    vec3 result = t * hom;                 // Matrix multiplication
    return result.xy / result.z;           // Perspective divide
}

float map(float value, float inMin, float inMax, float outMin, float outMax) {
    return outMin + (outMax - outMin) * ((value - inMin) / (inMax - inMin));
}

void main() {

    float x = map(vertTexCoord.s, 0, 1, min_x, max_x);
    float y = map(vertTexCoord.t, 0, 1, min_y, max_y);

    vec2 v = transform(inv_transform, vec2(x, y));

    // Compute distance to edge in UV space
    vec2 edge_dist = min(v, 1.0 - v); // distance to each axis' closest edge

    // Compute per-pixel change rate in v
    vec2 grad = fwidth(v); // screen-space delta of v

    // Feather alpha using smoothstep from screen-space-scaled gradient
    float alpha_x = smoothstep(0.0, grad.x, edge_dist.x);
    float alpha_y = smoothstep(0.0, grad.y, edge_dist.y);
    float alpha = alpha_x * alpha_y;

    v = transform(square_to_quad, v);

    if (flip_y_because_of_processing_issue_1134) {
        v.y = 1.0 - v.y;
    }

    vec4 texColor = texture2D(the_texture, v);
    gl_FragColor = vec4(texColor.rgb, texColor.a * alpha);
}
