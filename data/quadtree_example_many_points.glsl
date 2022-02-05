uniform sampler2D points_map;
uniform vec2 iResolution;

varying vec4 vertTexCoord;

void main() {
    ivec2 map_size = textureSize(points_map, 0);
    vec3 data_color = texture2D(points_map, vertTexCoord.xy).rgb;
    ivec3 bytes = ivec3(data_color * 255);
    int data = (bytes.r << 16) | (bytes.g << 8) | bytes.b;

    int x = int(vertTexCoord.x * iResolution.x);
    int bit_mask = 1 << (x & 0xf);

    if ((data & bit_mask) != 0) {
        gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);
    }
    else {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}