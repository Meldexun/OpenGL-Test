#version 430 core

layout(early_fragment_tests) in;

layout (std430, binding = 1) buffer depthBuffer {
  int mode;
  int width;
  int height;
  int values[]; // length = width * height * 9 * 4
};

in vec4 colorIn
out vec4 colorOut;

void main() {
  if (mode == 0) {
    int x = int(gl_FragCoord.x);
    int y = int(gl_FragCoord.y);
    byte depth = round(gl_FragCoord.z * 255.0);
    int index = (y * width + x) * 9;

    int count = values[index + 8];
    int insertIndex = -1;
    if (count < 8) {
      insertIndex = count;
    } else {
      
    }
    
    discard;
  } else if (mode == 1) {
    colorOut = colorIn;
  } else if (mode == 2) {
    colorOut = colorIn;
  }
}