#version 120

attribute vec2 position;
uniform vec4 position1;
uniform vec4 colorIn1;
uniform vec4 colorIn2;
uniform vec4 colorIn3;
uniform vec4 colorIn4;
uniform vec2 resolutionIn;

varying vec4 color;
varying vec2 positionFrag;
varying vec2 resolutionFrag;
varying vec4 dimensionsFrag;

void main()
{
    float x = (((position1[0] + position1[2] * position[0]) / resolutionIn[0]) - 0.5) * 2;
    float y = (((position1[1] + position1[3] * position[1]) / resolutionIn[1]) - 0.5) * -2;
    gl_Position = vec4(x, y, 0.0, 1.0);

    dimensionsFrag = position1;
    positionFrag = vec2(position1[0] + position1[2] * position[0], position1[1] + position1[3] * position[1]);
    resolutionFrag = resolutionIn;

    if (position[0] == 0 && position[1] == 0) {
        color = colorIn1;
    } else if (position[0] == 1 && position[1] == 0) {
        color = colorIn2;
    } else if (position[0] == 1 && position[1] == 1) {
        color = colorIn3;
    } else if (position[0] == 0 && position[1] == 1) {
        color = colorIn4;
    } else {
        color = vec4(0.5, 0.5, 0.5, 0.5);
    }
}
