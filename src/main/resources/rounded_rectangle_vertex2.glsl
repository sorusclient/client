#version 120

in vec2 position;
uniform vec2 position1;
uniform vec2 position2;
uniform vec2 position3;
uniform vec2 position4;
uniform vec4 colorIn;
uniform vec2 resolutionIn;
uniform float cornerRadiusIn;

varying vec4 color;
varying vec2 positionFrag;
varying vec2 position1Frag;
varying vec2 position2Frag;
varying vec2 position3Frag;
varying vec2 position4Frag;
varying vec2 resolutionFrag;
varying vec4 dimensionsFrag;
//varying float cornerRadiusFrag;

void main()
{
    float x = 0;
    float y = 0;
    if (position[0] == 0 && position[1] == 0) {
        x = position4[0];
        y = position4[1];
    } else if (position[0] == 1 && position[1] == 0) {
        x = position1[0];
        y = position1[1];
    } else if (position[0] == 1 && position[1] == 1) {
        x = position2[0];
        y = position2[1];
    } else if (position[0] == 0 && position[1] == 1) {
        x = position3[0];
        y = position3[1];
    }
    gl_Position = vec4(((x / resolutionIn[0]) - 0.5) * 2, ((y / resolutionIn[1]) - 0.5) * -2, 0.0, 1.0);

    //dimensionsFrag = position1;
    position1Frag = position1;
    position2Frag = position2;
    position3Frag = position3;
    position4Frag = position4;
    positionFrag = vec2(x, y);
    resolutionFrag = resolutionIn;
    //cornerRadiusFrag = cornerRadiusIn;

    color = colorIn;
}
