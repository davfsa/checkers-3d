#version 330

out vec4 fragColour;

uniform vec3 modelColour;

void main()
{
    fragColour = vec4(modelColour, 1.0);
}
