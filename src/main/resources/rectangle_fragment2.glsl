#version 120

in vec4 color;
in vec2 positionFrag;
in vec2 position1Frag;
in vec2 position2Frag;
in vec2 position3Frag;
in vec2 position4Frag;
in vec2 resolutionFrag;
in float cornerRadiusFrag;

float distToLine(vec2 pt1, vec2 pt2, vec2 testPt){
    vec2 lineDir = pt2 - pt1;
    vec2 perpDir = vec2(lineDir.y, -lineDir.x);
    vec2 dirToPt1 = pt1 - testPt;
    return abs(dot(normalize(perpDir), dirToPt1));
}

void main() {
    float value = 0.3;

    gl_FragColor = vec4(color[0], color[1], color[2], color[3] * min(min(smoothstep(0, value, distToLine(position2Frag, position3Frag, positionFrag)), smoothstep(0, value, distToLine(position4Frag, position1Frag, positionFrag))), min(smoothstep(0, value, distToLine(position1Frag, position2Frag, positionFrag)), smoothstep(0, value, distToLine(position3Frag, position4Frag, positionFrag)))));

}