#version 430 core

layout(points) in;
layout(triangle_strip, max_vertices = 3) out;

in VertexData {
  flat int objid;
} IN[1];

flat out int objid;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

void main() {
  objid = IN[0].objid;
  gl_Position = vec4(-0.5, -0.5, 0, 1);
  EmitVertex();

  objid = IN[0].objid;
  gl_Position = vec4(0.5, -0.4, 0, 1);
  EmitVertex();

  objid = IN[0].objid;
  gl_Position = vec4(0.2, 0.5, 0, 1);
  EmitVertex();
}