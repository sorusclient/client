#version 120

varying vec4 color;
varying vec4 dimensionsFrag;
varying vec2 positionFrag;
varying vec2 resolutionFrag;
//varying vec4 fragColor;

void main() {
    //float value = 0.3;

    gl_FragColor = color;
    /*if (positionFrag[0] < dimensionsFrag[0] + dimensionsFrag[2] - cornerRadiusFrag && positionFrag[0] > dimensionsFrag[0] + cornerRadiusFrag) {
        fragColor = vec4(color[0], color[1], color[2], color[3] * (1 - smoothstep(dimensionsFrag[3] / 2 - value, dimensionsFrag[3] / 2, abs(positionFrag[1] - (dimensionsFrag[1] + dimensionsFrag[3] / 2)))));
    } else if (positionFrag[1] < dimensionsFrag[1] + dimensionsFrag[3] - cornerRadiusFrag && positionFrag[1] > dimensionsFrag[1] + cornerRadiusFrag) {
        fragColor = vec4(color[0], color[1], color[2], color[3] * (1 - smoothstep(dimensionsFrag[2] / 2 - value, dimensionsFrag[2] / 2, abs(positionFrag[0] - (dimensionsFrag[0] + dimensionsFrag[2] / 2)))));
    } else {
        float xSide = (step(dimensionsFrag[0] + dimensionsFrag[2] / 2, positionFrag[0]) - 0.5) * 2;
        float ySide = (step(dimensionsFrag[1] + dimensionsFrag[3] / 2, positionFrag[1]) - 0.5) * 2;

        vec2 point = vec2((dimensionsFrag[0] + dimensionsFrag[2] / 2) + (dimensionsFrag[2] / 2 - cornerRadiusFrag) * xSide, (dimensionsFrag[1] + dimensionsFrag[3] / 2) + (dimensionsFrag[3] / 2 - cornerRadiusFrag) * ySide);

        fragColor = vec4(color[0], color[1], color[2], color[3] * (1 - smoothstep(cornerRadiusFrag - value, cornerRadiusFrag, distance(point, positionFrag))));
    }*/

}
