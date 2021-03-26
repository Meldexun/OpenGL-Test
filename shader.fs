#version 430 core

layout (std430, binding = 2) buffer visibleBuffer {
  int visibles[];
};

out vec4 FragColor;

flat in int objid;

void main() {
  int index = objid >> 5;
  int i = 1 << (objid & 31);
  atomicOr(visibles[index], i);

  FragColor = vec4(1, 1, 1, 1);
}