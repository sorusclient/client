#version 430 core

layout(location = 0) in vec2 position;
layout(location = 1) uniform vec4 position1;
layout(location = 2) uniform vec4 colorIn1;
layout(location = 3) uniform vec4 colorIn2;
layout(location = 4) uniform vec4 colorIn3;
layout(location = 5) uniform vec4 colorIn4;
layout(location = 6) uniform vec2 resolutionIn;

out vec4 color;
out vec2 positionFrag;
out vec2 resolutionFrag;
out vec4 dimensionsFrag;

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