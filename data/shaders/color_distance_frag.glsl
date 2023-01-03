uniform sampler2D texture;
varying vec4 vertTexCoord;
uniform vec4 target_color;

float color_distance(vec3 c1, vec3 c2) {

  int r1 = int(c1.r * 255);
  int g1 = int(c1.g * 255);
  int b1 = int(c1.b * 255);

  int r2 = int(c2.r * 255);
  int g2 = int(c2.g * 255);
  int b2 = int(c2.b * 255);

  int rmean = (r1 + r2) >> 2;
  int r = r2 - r1;
  int g = g2 - g1;
  int b = b2 - b1;

  int squared_color_distance =  (((512 + rmean) * r * r) >> 8) +
    ((g * g) << 2) +
    (((767 - rmean) * b * b) >> 8);

  float max_dist = 767; // not 100% sure

  return sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8)) / max_dist;
}

void main() {
  vec4 texColor = texture2D(texture, vertTexCoord.st).rgba;
  float d = color_distance(target_color.rgb, texColor.rgb);
  gl_FragColor = vec4(d, d, d, 1.0);
}
