#version 130

in vec4 color;
in vec4 dimensionsFrag;
in vec2 positionFrag;
in vec2 resolutionFrag;
in float cornerRadiusFrag;
in float thicknessFrag;
out vec4 fragColor;

void main() {
    float value = 0.3;

    if (positionFrag[0] < dimensionsFrag[0] + dimensionsFrag[2] - cornerRadiusFrag && positionFrag[0] > dimensionsFrag[0] + cornerRadiusFrag) {
        float smoothStepLimit = (1 - smoothstep(dimensionsFrag[3] / 2 - value, dimensionsFrag[3] / 2, abs(positionFrag[1] - (dimensionsFrag[1] + dimensionsFrag[3] / 2))));
        float smoothStepRequirement = smoothstep(dimensionsFrag[3] / 2 - thicknessFrag - value, dimensionsFrag[3] / 2 - thicknessFrag, abs(positionFrag[1] - (dimensionsFrag[1] + dimensionsFrag[3] / 2)));
        fragColor = vec4(color[0], color[1], color[2], color[3] * min(smoothStepRequirement, smoothStepLimit));
    } else if (positionFrag[1] < dimensionsFrag[1] + dimensionsFrag[3] - cornerRadiusFrag && positionFrag[1] > dimensionsFrag[1] + cornerRadiusFrag) {
        float smoothStepLimit = (1 - smoothstep(dimensionsFrag[2] / 2 - value, dimensionsFrag[2] / 2, abs(positionFrag[0] - (dimensionsFrag[0] + dimensionsFrag[2] / 2))));
        float smoothStepRequirement = smoothstep(dimensionsFrag[2] / 2 - thicknessFrag - value, dimensionsFrag[2] / 2 - thicknessFrag, abs(positionFrag[0] - (dimensionsFrag[0] + dimensionsFrag[2] / 2)));
        fragColor = vec4(color[0], color[1], color[2], color[3] * min(smoothStepRequirement, smoothStepLimit));
    } else {
        float xSide = (step(dimensionsFrag[0] + dimensionsFrag[2] / 2, positionFrag[0]) - 0.5) * 2;
        float ySide = (step(dimensionsFrag[1] + dimensionsFrag[3] / 2, positionFrag[1]) - 0.5) * 2;

        vec2 point = vec2((dimensionsFrag[0] + dimensionsFrag[2] / 2) + (dimensionsFrag[2] / 2 - cornerRadiusFrag) * xSide, (dimensionsFrag[1] + dimensionsFrag[3] / 2) + (dimensionsFrag[3] / 2 - cornerRadiusFrag) * ySide);

        float smoothStepLimit = (1 - smoothstep(cornerRadiusFrag - value, cornerRadiusFrag, distance(point, positionFrag)));
        float smoothStepRequirement = smoothstep(cornerRadiusFrag - thicknessFrag - value, cornerRadiusFrag - thicknessFrag, distance(point, positionFrag));

        fragColor = vec4(color[0], color[1], color[2], color[3] * min(smoothStepRequirement, smoothStepLimit));
    }

}
