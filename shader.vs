#version 430 core

layout(location = 0) in int objid;

out VertexData {
  flat int objid;
} OUT;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

void main() {
  OUT.objid = objid;
}