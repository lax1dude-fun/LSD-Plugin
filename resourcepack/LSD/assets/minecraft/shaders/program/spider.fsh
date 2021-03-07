#version 120


uniform vec2 OutSize;

varying vec2 texCoord;

uniform sampler2D DiffuseSampler;
uniform sampler2D IncrementSampler;

float timeF;

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



//3D KHOLE FRACTAL SHADER
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
#define ITERATIONS 17.0

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
    float time = timeF;
    
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





//SHAMELESSLY STOLEN FROM SHADERTOY: https://www.shadertoy.com/view/4lX3Rj
const int Iterations=14;
const float detail=.00002;
const float Scale=2.;
float iTime;

vec3 lightdir=normalize(vec3(0.,-0.3,-1.));


float ot=0.;
float det=0.;

float hitfloor;
float hitrock;

float smin( float a, float b, float k )
{
    float h = clamp( 0.5+0.5*(b-a)/k, 0.0, 1.0 );
    return mix( b, a, h ) - k*h*(1.0-h);
}

float tt;

float de(vec3 pos) {
	hitfloor=0.;
	hitrock=0.;
	vec3 p=pos;
	p.xz=abs(.5-mod(pos.xz,1.))+.01;
	float DEfactor=1.;
	ot=1000.;
	for (int i=0; i<Iterations; i++) {
		p = abs(p)-vec3(0.,2.,0.);  
		float r2 = dot(p, p);
		float sc=Scale/clamp(r2,0.4,1.);
		p*=sc; 
		DEfactor*=sc;
		p = p - vec3(0.5,1.,0.5);
	}
    float rr=length(pos+vec3(0.,-3.03,1.85-tt))-.017;
    float fl=pos.y-3.013;
    float d=min(fl,length(p)/DEfactor-.0005);
	d=min(d,-pos.y+3.9);
    //d=min(d,rr);
    if (abs(d-fl)<.0001) hitfloor=1.;
    //if (abs(d-rr)<.0001) hitrock=1.;
    return d;
}



vec3 normal(vec3 p) {
	vec3 e = vec3(0.0,det,0.0);
	
	return normalize(vec3(
			de(p+e.yxx)-de(p-e.yxx),
			de(p+e.xyx)-de(p-e.xyx),
			de(p+e.xxy)-de(p-e.xxy)
			)
		);	
}

float shadow(vec3 pos, vec3 sdir) {
		float totalDist =2.0*det, sh=1.;
 		for (int steps=0; steps<30; steps++) {
			if (totalDist<1.) {
				vec3 p = pos - totalDist * sdir;
				float dist = de(p)*1.5;
				if (dist < detail)  sh=0.;
				totalDist += max(0.05,dist);
			}
		}
		return max(0.,sh);	
}

float calcAO( const vec3 pos, const vec3 nor ) {
	float aodet=detail*80.;
	float totao = 0.0;
    float sca = 10.0;
    for( int aoi=0; aoi<5; aoi++ ) {
        float hr = aodet + aodet*float(aoi*aoi);
        vec3 aopos =  nor * hr + pos;
        float dd = de( aopos );
        totao += -(dd-hr)*sca;
        sca *= 0.75;
    }
    return clamp( 1.0 - 5.0*totao, 0.0, 1.0 );
}



float kset(vec3 p) {
	p=abs(.5-fract(p*20.));
	float es, l=es=0.;
	for (int i=0;i<13;i++) {
		float pl=l;
		l=length(p);
		p=abs(p)/dot(p,p)-.5;
		es+=exp(-1./abs(l-pl));
	}
	return es;	
}

mat2 rot;

vec3 light(in vec3 p, in vec3 dir) {
	float hf=hitfloor;
	float hr=0.0;//hitrock;
	vec3 n=normal(p);
	float sh=clamp(shadow(p, lightdir)+hf+hr,.4,1.);
	float ao=calcAO(p,n);
	float diff=max(0.,dot(lightdir,-n))*sh*1.3;
	float amb=max(0.2,dot(dir,-n))*.4;
	vec3 r = reflect(lightdir,n);
	float spec=pow(max(0.,dot(dir,-r))*sh,10.)*(.5+ao*.5);
	float k=kset(p)*.18; 
	vec3 col=mix(vec3(k*1.1,k*k*1.3,k*k*k),vec3(k),.45)*2.;
	vec3 pp=p-vec3(0.,3.03,tt);
    pp.yz*=rot;
    //if (hr>0.) col=vec3(.9,.8,.7)*(1.+kset(pp*2.)*.3);
    col=col*ao*(amb*vec3(.9,.85,1.)+diff*vec3(1.,.9,.9))+spec*vec3(1,.9,.5)*.7;	
	return col;
}


vec3 raymarch(in vec3 from, in vec3 dir) 
{
	float t=iTime;
	float cc=cos(t*.03); float ss=sin(t*.03);
    rot=mat2(cc,ss,-ss,cc);
    vec2 lig=vec2(sin(t*2.)*.6,cos(t)*.25-.25);
	float fog,glow,d=1., totdist=glow=fog=0.;
	vec3 p, col=vec3(0.);
	float ref=0.;
	float steps;
	for (int i=0; i<130; i++) {
		if (d>det && totdist<3.5) {
			p=from+totdist*dir;
			d=de(p);
			det=detail*(1.+totdist*55.);
			totdist+=d; 
			glow+=max(0.,.02-d)*exp(-totdist);
			steps++;
		}
	}
	//glow/=steps;
	float l=pow(max(0.,dot(normalize(-dir),normalize(lightdir))),10.);
	vec3 backg=vec3(.8,.85,1.)*.25*(2.-l)+vec3(1.,.9,.65)*l*.4;
	float hf=hitfloor;
	if (d<det) {
		col=light(p-det*dir*1.5, dir); 
		if (hf>0.5) col*=vec3(1.,.85,.8)*.6;
		col*=min(1.2,.5+totdist*totdist*1.5);
		col = mix(col, backg, 1.0-exp(-1.3*pow(totdist,1.3)));
	} else { 
		col=backg;
	}
	col+=glow*vec3(1.,.9,.8)*.34;
	col+=vec3(1,.8,.6)*pow(l,3.)*.5;
	return col; 
}

vec4 mainImage()
{

    iTime = timeF * 1.0 / 60.0;
	tt= timeF * 1.0 / 60.0 *.05;
    vec2 uv = texCoord.xy*2.-1.;
	uv.y*=OutSize.y/OutSize.x;
	float t=iTime*.15;
	float y=(cos(iTime*.1+3.)+1.);
	uv.y-=.1;
	//uv+=(texture(iChannel1,vec2(iTime*.15)).xy-.5)*max(0.,h)*7.;
	vec3 from=vec3(0.0,3.04+y*.1,-2.+iTime*.05);
	vec3 dir=normalize(vec3(uv*.85,1.));
	vec3 color=raymarch(from,dir); 
	//col*=length(clamp((.6-pow(abs(uv2),vec2(3.))),vec2(0.),vec2(1.)));
	color*=vec3(1.,.94,.87);
	color=pow(color,vec3(1.2));
	color=mix(vec3(length(color)),color,.85)*.95;
	color+=vec3(1,.85,.7)*pow(max(0.,.3-length(uv-vec2(0.,.03)))/.3,1.5)*.65;
	return vec4(color,1.);
}

void main() {
	vec4 countColor = texture2D(IncrementSampler, vec2(0.0, 0.0));
    timeF = countColor.x * 256.0 + countColor.y * 256.0 * 256.0 + countColor.z * 256.0 * 256.0 * 256.0;
	
    vec4 choiceColor = texture2D(IncrementSampler, vec2(1.5 / OutSize.x, 0.0));
	
	vec4 renderColor;
    float choice = rand(choiceColor.x + choiceColor.y * 256.0 + choiceColor.z * 256.0 * 256.0);

    if (choice < 0.5) {
        renderColor = fractal3D();
    } else {
        renderColor = mainImage();
    }
	
	float blendFac = min(timeF / 60.0, 1.0);
	blendFac = blendFac * blendFac * blendFac;
	gl_FragColor = (renderColor * blendFac) + (texture2D(DiffuseSampler, texCoord) * (1.0 - blendFac));
}
