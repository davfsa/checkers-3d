#version 330

in vec2 frgTextCoords;
in vec4 frgColor;

uniform sampler2D txtSampler;

out vec4 outColour;

void main()
{
    outColour = frgColor * texture(txtSampler, frgTextCoords);
}
