uniform sampler2D points_map;
uniform vec2 iResolution;

varying vec4 vertTexCoord;

void main() {
    ivec2 map_size = textureSize(points_map, 0);
    ivec2 screen_pos = ivec2(vertTexCoord.xy * iResolution);
    int index_on_screen = int(screen_pos.y * iResolution.x + screen_pos.x);
    int index_in_map = index_on_screen / 16;

    int x_in_map = index_in_map % map_size.x;
    int y_in_map = (index_in_map-x_in_map) / map_size.x;

    vec2 tex_pos = vec2(x_in_map, y_in_map) / map_size;

    vec4 data_color = texture2D(points_map, tex_pos);
    ivec4 bytes = ivec4(data_color * 255);
    int data = (bytes.a << 24) |(bytes.r << 16) | (bytes.g << 8) | bytes.b;

    int x = int(vertTexCoord.x * iResolution.x);
    int bit_mask = 1 << (x & 0xf);

    if ((data & bit_mask) != 0) {
        int draw_selected_bit_mask = bit_mask << 16;
        if ((data & draw_selected_bit_mask) != 0) {
            gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
        }
        else {
            gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);
        }
    }
    else {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}