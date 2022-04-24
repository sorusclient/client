#version 120

in vec2 position;
uniform vec4 position1;
uniform vec4 colorIn;
uniform vec2 resolutionIn;
uniform float cornerRadiusIn;

varying vec4 color;
varying vec2 positionFrag;
varying vec2 resolutionFrag;
varying vec4 dimensionsFrag;
varying float cornerRadiusFrag;

void main()
{
    float x = position1[0] + position1[2] * position[0];
    float y = position1[1] + position1[3] * position[1];
    gl_Position = vec4(((x / resolutionIn[0]) - 0.5) * 2, ((y / resolutionIn[1]) - 0.5) * -2, 0.0, 1.0);

    dimensionsFrag = position1;
    positionFrag = vec2(x, y);
    resolutionFrag = resolutionIn;
    cornerRadiusFrag = cornerRadiusIn;

    color = colorIn;
}
