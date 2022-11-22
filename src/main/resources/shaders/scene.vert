#version 330

layout (location = 0) in vec3 position;

out vec2 pos;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;


void main() {
    pos = position.xy;
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
}
