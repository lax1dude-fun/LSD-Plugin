#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D GameSampler;

varying vec2 texCoord;
uniform vec2 InSize;
uniform vec2 OutSize;

bool isInPixel(vec2 pixel) {
    if (
            texCoord.x > (pixel.x) / OutSize.x &&
            texCoord.x < (pixel.x + 1.0) / OutSize.x &&
            texCoord.y > (pixel.y) / OutSize.y &&
            texCoord.y < (pixel.y + 1.0) / OutSize.y
        ) {
        return true;
    }
    return false;
}

void main() {
    if (isInPixel(vec2(0.0, 0.0))) {
        vec4 diffuseColor = texture2D(DiffuseSampler, texCoord);
        vec4 outColor = diffuseColor;
        if (outColor.y == 1.0) {
            outColor.y = 0.0;
            outColor.z += 1.0/256.0;
        } else if (outColor.x == 1.0) {
            outColor.x = 0.0;
            outColor.y += 1.0/256.0;
        } else {
            outColor.x += 1.0/256.0;
        }
        gl_FragColor = vec4(outColor.xyz, 1.0);
    } else if (isInPixel(vec2(1.0, 0.0))) {
        vec4 diffuseColor = texture2D(DiffuseSampler, texCoord);
        vec4 timeColor = texture2D(DiffuseSampler, vec2(0.0, 0.0));
        if (timeColor.x <= 1.0/32.0 && timeColor.y <= 1.0/256.0 && timeColor.z <= 1.0/256.0) {
            gl_FragColor = texture2D(GameSampler, vec2(0.234, 0.345));
        } else {
            gl_FragColor = diffuseColor;
        }
    } else {
        discard;
    }

}
