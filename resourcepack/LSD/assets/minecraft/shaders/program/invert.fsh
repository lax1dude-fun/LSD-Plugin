#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D CounterSampler;
uniform sampler2D PrevSampler;

uniform vec2 OutSize;

varying vec2 texCoord;


//COMMON
float getTime() {
    vec4 countColor = texture2D(CounterSampler, vec2(0.0, 0.0));
    return countColor.x * 256.0 + countColor.y * 256.0 * 256.0 + countColor.z * 256.0 * 256.0 * 256.0;
}

float smoothstep2(float a, float b, float x) {
    float fac = x * x * x * (x * (x * 6.0 - 15.0) + 10.0);
    return a + (b - a) * fac;
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

    float time = getTime();

    vec3 noiseSampleCoords = vec3(time * 0.01, 3.0 * texCoord.xy * vec2(1.0, aspect));

    vec3 noiseValue = 0.15 * vec3(fnoise(noiseSampleCoords));

    vec3 noiseValue2 = 0.15 * vec3(fnoise(noiseSampleCoords + vec3(0.0, 0.0, 10.0)));

    vec4 diffuseColor = texture2D(DiffuseSampler, texCoord + vec2(noiseValue.x, noiseValue2.x));
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

    float time = getTime();
    //if (mod(int(time), 10) == 0) {
        return outColor + contrastEdge() * 0.5;
    // } else {
    //     return outColor;
    // }

}





//MELTING SHADER
vec4 melting() {
    float time = getTime();

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
        noiseMap
    );
}





//3D FRACTAL SHADER
vec3 uPosition = vec3(0.0, 0.0, 0.0);
vec3 uRotation;
float scaleFactor = 0.5;
vec3 fractalRotationParams;
vec3 lambertLightLocation = vec3(0.1, 0.1, 0.1);
mat3 uRotationMatrix = mat3(
    vec3(1.0, 0.0, 0.0),
    vec3(0.0, 1.0, 0.0),
    vec3(0.0, 0.0, 1.0)
);
float fov = 3.0;
vec3 uFractalColor = vec3(0.85, 0.85, 0.85);
float uShadowBrightness = 0.5;
float uHitThreshold = 0.0001;

//rotate around z axis
vec3 rz(vec3 coords, float angle) {
	mat3 rotationMatrix = mat3(
		cos(angle), -sin(angle), 0,
		sin(angle), cos(angle), 0,
		0, 0, 1);
	return rotationMatrix * coords;
}

//rotate around x axis
vec3 rx(vec3 coords, float angle) {
	mat3 rotationMatrix = mat3(
		1, 0, 0,
		0, cos(angle), -sin(angle),
		0, sin(angle), cos(angle));
	return rotationMatrix * coords;
}

//rotate around y axis
vec3 ry(vec3 coords, float angle) {
	mat3 rotationMatrix = mat3(
		cos(angle), 0, sin(angle),
		0, 1, 0,
		-sin(angle), 0, cos(angle));
	return rotationMatrix * coords;
}

//reflect across all three axes
vec3 reflectAxes(vec3 a) {
	return vec3(abs(a.z), abs(a.x), abs(a.y));
}

//ray reflection iteration (EDIT THIS TO CHANGE THE FRACTAL, MORE SPECIFICALLY THE "ANGLE" PARAMETER FOR RX, RY, AND RZ)
vec3 rayReflectIteration(vec3 a, vec3 offset, float iteration) {
	return rx(rz(ry(reflectAxes(a), 
	fractalRotationParams.x), 
	fractalRotationParams.y), 
	fractalRotationParams.z) + offset;
}

//iteraetion count
#define ITERATIONS 17

#define STEPS 32

//cube signed distance function (SDF)
float cubeSDF(vec3 rayPosition, vec3 cubePosition, float cubeSize) {
	vec3 dist = abs(rayPosition) - cubePosition;
	return length(max(max(max(dist.x, dist.y), dist.z), 0.0)) + min(max(dist.x, max(dist.y, dist.z)), 0.0);
}

//fractal SDF
float fractalSDF(vec3 rayPosition, vec3 spherePosition, float sphereRadius) {
	vec3 rayPos2 = rayPosition;
	for (float i = 0.0; i < ITERATIONS; i++) {
		rayPos2 = rayReflectIteration(rayPos2 / scaleFactor, vec3(-2.0), i);
	}
	return cubeSDF(rayPos2, spherePosition, sphereRadius) * pow(scaleFactor, ITERATIONS);
}

//scene SDF
float globalSDF(vec3 rayPosition) {
	return fractalSDF(rayPosition, vec3(2.0, 2.0, 2.0), 2.0);
}

//march a single ray
vec3 marchRay(vec3 origin, vec3 direction, out float finalMinDist, out int stepsBeforeThreshold) {
	vec3 directionNormalized = normalize(direction);
	vec3 position = origin;
	float minDist = 0.0;
	for (int i = 0; i < STEPS; i++) {
		minDist = globalSDF(position);
		position += directionNormalized * minDist;
		if (minDist > uHitThreshold) {
			stepsBeforeThreshold = i;
		}
	}
	finalMinDist = minDist;
	return position;
}


//light sources (currently unused)
vec3 lightSource = vec3(-1.0, -1.4, 0.0) * 2.5;
vec3 lightSource2 = vec3(1.0, 0.6, 0.5) * 2.5;

//lambertian diffuse shading
vec3 lambertShading(vec3 color, vec3 normal, vec3 light) {
	vec3 lightNormalized = normalize(light);
	float lightIntensity = max(0.0, dot(normal, lightNormalized));
	return color * lightIntensity;
}

//random function I found on stackoverflow
float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

//marches the rays, calculates normals, determines and returns color, etc.
vec4 fractal3D() {
    float time = getTime();
    
    fractalRotationParams = noise3(time * 0.001) * 2.0;
    uFractalColor = noise3(time * 0.003 + 5.0) * 1.5 + vec3(1.0);

    vec3 angle = noise3(time * 0.0005 + 10.0) * 13.0;

    uRotationMatrix = mat3(
		cos(angle.x), -sin(angle.x), 0,
		sin(angle.x), cos(angle.x), 0,
		0, 0, 1) * mat3(
		1, 0, 0,
		0, cos(angle.y), -sin(angle.y),
		0, sin(angle.y), cos(angle.y)) * mat3(
		cos(angle.z), 0, sin(angle.z),
		0, 1, 0,
		-sin(angle.z), 0, cos(angle.z)) * uRotationMatrix;

    vec2 uViewportSize = OutSize;

	vec3 coords = gl_FragCoord.xyz / (uViewportSize.y) - vec3(uViewportSize.x / uViewportSize.y * 0.5, 0.5, 0.0);
	coords.x *= 1.5 * fov;
	coords.y *= 1.5 * fov;
	float distToSurface = 0.0;
	float distToSurfaceX = 0.0;
	float distToSurfaceY = 0.0;
	int steps1 = 0;
	int steps2 = 0;
	int steps3 = 0;
	float dx = 0.0001;
	vec3 dist = marchRay(uPosition, uRotationMatrix * vec3(coords.x, 1.0, coords.y), distToSurface, steps1);
	
	float shadowDistToSurface = 0.0;
	int shadowSteps = -1;
	vec3 shadowRay = marchRay(dist + (lambertLightLocation - dist) * uHitThreshold, (lambertLightLocation - dist), shadowDistToSurface, shadowSteps);

	if (shadowDistToSurface > uHitThreshold * 0.5 || sign(shadowRay.x - lambertLightLocation.x) != sign(dist.x - lambertLightLocation.x)) {
		return vec4(uFractalColor * (1.0 - float(steps1) / 32.0), 1.0);
	} else {
		return vec4(vec3(uFractalColor - float(steps1) / 32.0) * uShadowBrightness, 1.0);
	}
}





void main() {

    vec4 choiceColor = texture2D(CounterSampler, vec2(1.5 / OutSize.x, 0.0));

    float choice = rand(choiceColor.x + choiceColor.y * 256.0 + choiceColor.z * 256.0 * 256.0);

    vec4 outColor;

    if (choice < 0.25) {
        outColor = fractalNoiseFilter();
    } else if (choice < 0.5) {
        outColor = conwayGameOfLife();
    } else if (choice < 0.75) {
        outColor = melting();
    } else {
        outColor = fractal3D();
    }

    gl_FragColor = vec4(outColor.xyz, 1.0);
}
