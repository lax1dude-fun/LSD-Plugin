#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D CounterSampler;
uniform sampler2D PrevSampler;

uniform vec2 OutSize;

varying vec2 texCoord;


float time;

//COMMON
float getTime() {
    vec4 countColor = texture2D(CounterSampler, vec2(0.0, 0.0));
    return countColor.x * 256.0 + countColor.y * 256.0 * 256.0 + countColor.z * 256.0 * 256.0 * 256.0;
}

float smoothstep2(float a, float b, float x) {
    float fac = x * x * x * (x * (x * 6.0 - 15.0) + 10.0);
    return a + (b - a) * fac;
}

vec3 smootherstepVec(vec3 v) {
    return vec3(
        smoothstep2(0.0, 1.0, v.x),
        smoothstep2(0.0, 1.0, v.y),
        smoothstep2(0.0, 1.0, v.z)
    );
}

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

vec3 noise3(float input) {
    return vec3(
        noise(vec3(input)),
        noise(vec3(input + 10.0)),
        noise(vec3(input + 20.0))
    );
}






//NOISE OFFSET SHADER

float fnoise(vec3 pos) {
    return noise(pos * 16.0) / 16.0 + noise(pos * 4.0) / 4.0 + noise(pos); 
}

vec4 fractalNoiseFilter() {    
    float aspect = OutSize.y / OutSize.x;

    vec3 noiseSampleCoords = vec3(time * 0.01, 3.0 * texCoord.xy * vec2(1.0, aspect));

    vec3 noiseValue = 0.15 * vec3(fnoise(noiseSampleCoords));

    vec3 noiseValue2 = 0.15 * vec3(fnoise(noiseSampleCoords + vec3(0.0, 0.0, 10.0)));

    vec4 diffuseColor = texture2D(DiffuseSampler, texCoord + vec2(noiseValue.x, noiseValue2.x) * clamp(time / 90.0, 0.0, 1.5));
    vec4 outColor = diffuseColor * 1.0;

    return outColor;
}





//CONTRAST EDGE SHADER
vec4 contrastEdge() {
    vec4 sample1 = texture2D(DiffuseSampler, texCoord);
    vec4 sample2 = texture2D(DiffuseSampler, texCoord + vec2(0.0, 1.0 / OutSize.y));
    vec4 sample3 = texture2D(DiffuseSampler, texCoord + vec2(1.0 / OutSize.x, 0.0));
    float diff1 = length(sample1 - sample2);
    float diff2 = length(sample1 - sample3);
    if (diff1 > 0.15 || diff2 > 0.15) {
        return sample1 * 1.5;
    } else if (diff1 > 0.03 || diff2 > 0.03) {
        return sample1 * 0.8;
    }
    return vec4(0.0, 0.0, 0.0, 1.0);
}


//CGoL SHADER

float randFromColor(vec4 col) {
    return rand(col.x + 256.0 * col.y + 256.0 * 256.0 * col.z);
}

vec4 conwayGameOfLife() {
    vec4 cell = texture2D(PrevSampler, texCoord);
    float dx = 1.0 / OutSize.x;
    float dy = 1.0 / OutSize.y;
    vec4 neighbors[8] = vec4[8](
        texture2D(PrevSampler, texCoord + vec2(-dx, -dy)),
        texture2D(PrevSampler, texCoord + vec2(0.0, -dy)),
        texture2D(PrevSampler, texCoord + vec2(dx, -dy)),
        texture2D(PrevSampler, texCoord + vec2(-dx, 0.0)),
        texture2D(PrevSampler, texCoord + vec2(dx, 0.0)),
        texture2D(PrevSampler, texCoord + vec2(-dx, dy)),
        texture2D(PrevSampler, texCoord + vec2(0.0, dy)),
        texture2D(PrevSampler, texCoord + vec2(dx, dy))
    );

    int aliveCount = 0;

    vec4 leastBrightest = vec4(1.0);

    for (int i = 0; i < 8; i++) {
        if (length(neighbors[i].xyz) > 0.5) {
            aliveCount++;
            if (neighbors[i].x < leastBrightest.x) {
                leastBrightest = neighbors[i];
            }
        }
    }

    vec4 outColor = cell;

    if (aliveCount > 3 || aliveCount < 2) {
        if (length(cell.xyz) > 0.5) {
            outColor = vec4(vec3(0.28), 1.0);
        }
        outColor = vec4(outColor.xyz - vec3(0.1), 1.0);
        //}
    } else if (aliveCount == 3) {
        outColor = leastBrightest;
    } else {
        outColor = vec4(outColor.xyz - vec3(0.01), 1.0);
    }
    //outColor = vec4(float(aliveCount) / 8.0, 0.0, 0.0, 1.0);

    //if (mod(int(time), 10) == 0) {
        return outColor + contrastEdge() * 0.5;
    // } else {
    //     return outColor;
    // }

}





//MELTING SHADER
vec4 melting() {

    float timeNoise = noise(vec3(time * 0.0005));
    float xDriftNoise = noise(vec3(time * 0.0005 + 1.0));

    vec2 noiseSampleCoords = vec2(texCoord.x * 5.0 + xDriftNoise * 6.0, pow(texCoord.y, 2) * 1.5 + time * 0.0015);

    float noiseMap = clamp(pow(fnoise(vec3(noiseSampleCoords, time * 0.001)) + 0.85 + timeNoise * 0.1, 2.0), 0.0, 1.002);

    //if (noiseMap > 0.4) {
    //    noiseMap = 1.0;
    //}

    return mix(
        texture2D(DiffuseSampler, texCoord),
        texture2D(PrevSampler, texCoord + vec2(xDriftNoise * 0.03 / OutSize.x, 6.0 * (1.0 - texCoord.y) / OutSize.y)),
        noiseMap * clamp(time / 90.0, 0.0, 1.0)
    );
}



float shift = 0.0;

float rand2(float x) {
    return rand(x + shift);
    //shift += 123.123;
}



//GLASS VORONOI SHADER
vec4 glassVoronoi() {


    float closestDist = 2.0;
    float closestIndex = 0.0;
    float secondClosestDist = 2.0;
    float secondClosestIndex = 0.0;
    float boundaryDistance = 0.0;

    vec2 positions[64];
    for (int i = 0; i < 64; i++) {
        float fi = float(i);
        
        positions[i] = vec2(
            rand2(fi) + sin(time * 0.001 * (rand2(fi + 100.0)) + 6.28 * (rand2(fi + 600.0))) * rand2(fi + 200.0) * 0.5,
            rand2(fi + 300.0) + sin(time * 0.001 * (rand2(fi + 400.0)) + 6.28 * (rand2(fi + 700.0))) * rand2(fi + 500.0) * 0.5
        );

        float dist = length(positions[i] - texCoord);
        if (dist < closestDist) {
            secondClosestDist = closestDist;
            closestDist = dist;
            closestIndex = fi;
        } else if (dist < secondClosestDist) {
            secondClosestDist = dist;
            secondClosestIndex = fi;
        }

    }

    boundaryDistance = smoothstep2(0.0, 12.0 * clamp(0.0, 1.0, time / 90.0), secondClosestDist - closestDist);

    // if (boundaryDistance < 0.03) {
    //     boundaryDistance = 0.0;
    // }


    return texture2D(DiffuseSampler, texCoord + boundaryDistance * vec2(rand2(closestIndex), rand2(closestIndex + 128.0)));
}




//DAZED SHADER
vec4 dazed() {
    vec2 prevOffset = vec2(
        fnoise(vec3(time * 0.01)),
        fnoise(vec3(time * 0.01 + 3.0))
    ) * 0.01;

    float rotationOffset = fnoise(vec3(time * 0.002 + 6.0)) * 0.05;

    mat2 rotMatrix = mat2(
        cos(rotationOffset), -sin(rotationOffset),
        sin(rotationOffset), cos(rotationOffset)
    );

    return mix(
        texture2D(DiffuseSampler, texCoord),
        texture2D(PrevSampler, (texCoord - vec2(0.5)) * rotMatrix * 0.98 + vec2(0.5) + prevOffset),
        clamp(time / 90.0, 0.0, 0.95)
    );
}





//2D FRACTAL SHADER
vec4 fractal2D() {

    float rotationOffset = noise(vec3(time * 0.003, 0.1, 0.1)) * 3.00;

    mat2 rotMatrix = mat2(
        cos(rotationOffset), -sin(rotationOffset),
        sin(rotationOffset), cos(rotationOffset)
    );

    float rotationOffset2 = noise(vec3(time * 0.003, 0.1, 0.1)) * 13.00;

    mat2 rotMatrix2 = mat2(
        cos(rotationOffset2), -sin(rotationOffset2),
        sin(rotationOffset2), cos(rotationOffset2)
    );

    vec2 moveOffset = vec2(
        fnoise(vec3(time * 0.001)) * 1.0,
        fnoise(vec3(time * 0.001 + 23.0)) * 1.0
    ) * 0.8;
    vec2 coord = texCoord + moveOffset;

    for (int i = 0; i < 14; i++) {

        float fi = float(i);
        vec2 offset = vec2(0.5);

        coord *= 2.0;
        coord -= offset;

        coord = vec2(coord.x, 0.5 - abs(0.5 - coord.y));
        coord = vec2(0.5 - abs(0.5 - coord.x), coord.y);

        coord += offset;
        
        coord *= rotMatrix;

    }

    // if (length(coord - vec2(0.5)) < 0.7) {
    //     return texture2D(DiffuseSampler, texCoord) - vec4(0.2);
    // }
    return texture2D(DiffuseSampler, texCoord + clamp(time / 90.0, 0.0, 1.0) * vec2(log(length(coord - vec2(0.5)))) * 0.02 * rotMatrix2);
}





//GRAVITY SHADER
vec4 gravity() {
    vec2 positions[32];
    
    vec2 coord = texCoord;
    for (int i = 0; i < 32; i++) {
        float fi = float(i);
        
        positions[i] = vec2(
            rand2(fi) + sin(time * 0.001 * (rand2(fi + 100.0)) + 6.28 * (rand2(fi + 600.0))) * rand2(fi + 200.0) * 0.5,
            rand2(fi + 300.0) + sin(time * 0.001 * (rand2(fi + 400.0)) + 6.28 * (rand2(fi + 700.0))) * rand2(fi + 500.0) * 0.5
        );

        vec2 displacement = positions[i] - texCoord;

        coord += normalize(displacement) / length(displacement) * 0.01 * clamp(time / 90.0, 0.0, 1.0); 

    }

    return texture2D(DiffuseSampler, coord);
}






//SMEAR SHADER
vec4 smear() {
    vec2 positions[32];

    float closestDist = 2.0;
    float closestIndex = 0.0;
    
    vec2 coord = texCoord;
    for (int i = 0; i < 32; i++) {
        float fi = float(i);
        
        positions[i] = vec2(
            rand2(fi) + sin(time * 0.01 * (rand2(fi + 100.0)) + 6.28 * (rand2(fi + 600.0))) * rand2(fi + 200.0) * 0.5,
            rand2(fi + 300.0) + sin(time * 0.01 * (rand2(fi + 400.0)) + 6.28 * (rand2(fi + 700.0))) * rand2(fi + 500.0) * 0.5
        );

        vec2 displacement = positions[i] - texCoord;

        float dist = length(positions[i] - texCoord);
        if (dist < closestDist) {
            closestDist = dist;
            closestIndex = fi;
        } 
        coord += normalize(displacement) / length(displacement) * 0.01; 

    }

    vec2 moveOffset = vec2(
        fnoise(vec3(closestIndex, time * 0.00025 + 35.0, -62.2)) * 1.0,
        fnoise(vec3(closestIndex, time * 0.00025 + 23.0, -3.4)) * 1.0
    ) * 0.005;

    return mix(
        texture2D(DiffuseSampler, texCoord),
        texture2D(PrevSampler, texCoord + moveOffset),
        smoothstep2(0.0, 1.0, 1.00 - closestDist * 1.5) * clamp(time / 90.0, 0.0, 1.0)
    );
}



#define choices 7.0;

bool choose(float choice, int index) {
    return choice < float(index) / choices; 
}

void main() {
    time = getTime();

    vec4 choiceColor = texture2D(CounterSampler, vec2(1.5 / OutSize.x, 0.0));

    float choice = rand(choiceColor.x + choiceColor.y * 256.0 + choiceColor.z * 256.0 * 256.0);

    vec4 outColor;

    if (choose(choice, 1)) {
        outColor = fractalNoiseFilter();
    } else if (choose(choice, 2)) {
        outColor = melting();
    } else if (choose(choice, 3)) {
        outColor = glassVoronoi();
    } else if (choose(choice, 4)) {
        outColor = dazed();
    } else if (choose(choice, 5)) {
        outColor = fractal2D();
    } else if (choose(choice, 6)) {
        outColor = gravity();
    } else {
        outColor = smear();
    }
    //outColor = smear();


    gl_FragColor = vec4(outColor.xyz, 1.0);
}
