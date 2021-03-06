#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

uniform vec2 OutSize;

varying vec2 texCoord;

float rand(float x) {
    return fract(sin(x) * 100000.0);
}

vec3 rand3D(vec3 pos) {
    return vec3(
        rand(pos.x + 64.0 * pos.y + 4096.0 * pos.z),
        rand(pos.x + 64.0 * pos.y + 4096.0 * pos.z + 0.1),
        rand(pos.x + 64.0 * pos.y + 4096.0 * pos.z + 0.2)
    );
}

float smoothstep2(float a, float b, float x) {
    float fac = x * x * x * (x * (x * 6.0 - 15.0) + 10.0);
    return a + (b - a) * fac;
}

float noise(vec3 inputVec) {

    
    vec3 positions[8];
    positions[0] = vec3(floor(inputVec.x), floor(inputVec.y), floor(inputVec.z));
    positions[1] = vec3(ceil(inputVec.x), floor(inputVec.y), floor(inputVec.z));
    positions[2] = vec3(floor(inputVec.x), ceil(inputVec.y), floor(inputVec.z));
    positions[3] = vec3(ceil(inputVec.x), ceil(inputVec.y), floor(inputVec.z));
    positions[4] = vec3(floor(inputVec.x), floor(inputVec.y), ceil(inputVec.z));
    positions[5] = vec3(ceil(inputVec.x), floor(inputVec.y), ceil(inputVec.z));
    positions[6] = vec3(floor(inputVec.x), ceil(inputVec.y), ceil(inputVec.z));
    positions[7] = vec3(ceil(inputVec.x), ceil(inputVec.y), ceil(inputVec.z));

    vec3 randVecs[8];
    vec3 offsets[8];
    float dots[8];

    for (int i = 0; i < 8; i++) {
        randVecs[i] = normalize(rand3D(positions[i]));
        offsets[i] = inputVec - positions[i];
        dots[i] = dot(randVecs[i], offsets[i]);
    }
    
    return smoothstep2(
        smoothstep2(
            smoothstep2(
                dots[0],
                dots[1],
                offsets[0].x
            ),
            smoothstep2(
                dots[2],
                dots[3],
                offsets[0].x
            ),
            offsets[0].y
        ),
        smoothstep2(
            smoothstep2(
                dots[4],
                dots[5],
                offsets[0].x
            ),
            smoothstep2(
                dots[6],
                dots[7],
                offsets[0].x
            ),
            offsets[0].y
        ),
        offsets[0].z
    );
}

float fnoise(vec3 pos) {
    return noise(pos * 16.0) / 16.0 + noise(pos * 4.0) / 4.0 + noise(pos); 
}

vec4 fractalNoiseFilter() {    
    float aspect = OutSize.y / OutSize.x;
    vec4 prevColor = texture2D(PrevSampler, vec2(0.0, 0.0));

    float time = prevColor.x * 256.0 + prevColor.y * 256.0 * 256.0 + prevColor.z * 256.0 * 256.0 * 256.0;

    vec3 noiseSampleCoords = vec3(time * 0.01, 3.0 * texCoord.xy * vec2(1.0, aspect));

    vec3 noiseValue = 0.15 * vec3(fnoise(noiseSampleCoords));

    vec3 noiseValue2 = 0.15 * vec3(fnoise(noiseSampleCoords + vec3(0.0, 0.0, 10.0)));

    vec4 diffuseColor = texture2D(DiffuseSampler, texCoord + vec2(noiseValue.x, noiseValue2.x));
    vec4 outColor = diffuseColor * 1.0;

    return outColor;
}

vec4 brighten() {
    return 3.0 * texture2D(DiffuseSampler, texCoord);
}

void main() {

    vec4 choiceColor = texture2D(PrevSampler, vec2(1.5 / OutSize.x, 0.0));

    float choice = rand(choiceColor.x + choiceColor.y * 256.0 + choiceColor.z * 256.0 * 256.0);

    vec4 outColor;

    if (choice < 0.5) {
        outColor = fractalNoiseFilter();
    } else if (choice < 1.0) {
        outColor = brighten();
    }

    gl_FragColor = vec4(outColor.xyz, 1.0);
}
