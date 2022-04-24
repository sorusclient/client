#version 120

varying vec4 color;
varying vec4 dimensionsFrag;
varying vec2 positionFrag;
varying vec2 resolutionFrag;
varying float cornerRadiusFrag;
varying vec2 uvCoordinate;
varying vec4 imagePositionFrag;

//varying vec4 fragColor;

uniform sampler2D ourTexture;

void main() {
    float value = 0.3;

    vec2 coordinate = vec2(imagePositionFrag[0] + uvCoordinate[0] * imagePositionFrag[2], imagePositionFrag[1] + uvCoordinate[1] * imagePositionFrag[3]);
    vec4 textureColor = texture2D(ourTexture, coordinate);
    vec4 color2 = vec4(color[0] * textureColor[0], color[1] * textureColor[1], color[2] * textureColor[2], color[3] * textureColor[3]);

    if (positionFrag[0] < dimensionsFrag[0] + dimensionsFrag[2] - cornerRadiusFrag && positionFrag[0] > dimensionsFrag[0] + cornerRadiusFrag) {
        gl_FragColor = vec4(color2[0], color2[1], color2[2], color2[3] * (1 - smoothstep(dimensionsFrag[3] / 2 - value, dimensionsFrag[3] / 2, abs(positionFrag[1] - (dimensionsFrag[1] + dimensionsFrag[3] / 2)))));
    } else if (positionFrag[1] < dimensionsFrag[1] + dimensionsFrag[3] - cornerRadiusFrag && positionFrag[1] > dimensionsFrag[1] + cornerRadiusFrag) {
        gl_FragColor = vec4(color2[0], color2[1], color2[2], color2[3] * (1 - smoothstep(dimensionsFrag[2] / 2 - value, dimensionsFrag[2] / 2, abs(positionFrag[0] - (dimensionsFrag[0] + dimensionsFrag[2] / 2)))));
    } else {
        float xSide = (step(dimensionsFrag[0] + dimensionsFrag[2] / 2, positionFrag[0]) - 0.5) * 2;
        float ySide = (step(dimensionsFrag[1] + dimensionsFrag[3] / 2, positionFrag[1]) - 0.5) * 2;

        vec2 point = vec2((dimensionsFrag[0] + dimensionsFrag[2] / 2) + (dimensionsFrag[2] / 2 - cornerRadiusFrag) * xSide, (dimensionsFrag[1] + dimensionsFrag[3] / 2) + (dimensionsFrag[3] / 2 - cornerRadiusFrag) * ySide);

        gl_FragColor = vec4(color2[0], color2[1], color2[2], color2[3] * (1 - smoothstep(cornerRadiusFrag - value, cornerRadiusFrag, distance(point, positionFrag))));
    }

}
