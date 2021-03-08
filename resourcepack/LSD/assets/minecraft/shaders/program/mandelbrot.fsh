#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D CounterSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

float frameCounter;
float rngNum;

//MANDELBROT ====================================
vec4 mandelbrot() {
	vec2 p = vec2(-0.745, 0.186) + 3.0 * ((texCoord * InSize) / (InSize.y) - 0.5) * pow(0.00005, 0.95 - cos(frameCounter * 0.006) * 0.5);
	
	float n = 0.0;
	vec2 z = p * n;
	
	for(; (n < 256.0) && (dot(z,z) < 1e4); n++)
		z = vec2(z.x*z.x - z.y*z.y, 2.*z.x*z.y) + p;
	
	float fact2 = abs(sin(frameCounter * 0.006));
	float factor = fact2 * fact2;
	
	vec2 f = 0.5 + 0.5 * cos(vec2(4.0, 6.0) + 0.05 * (n - log2(log2(dot(z,z)))));
	
	return texture2D(DiffuseSampler, texCoord * (1.0 - factor * 0.4) + min(sqrt(f), 1.0) * factor * 0.3);
}

//voroniblocks ======================================
float s(vec2 p){
    p = fract(p) - .5;
    return (dot(p, p)*2. + .5)*max(abs(p.x)*.866 + p.y*.5, -p.y);
}

float m(vec2 p){
    vec2 o = sin(vec2(1.93, 0) + (frameCounter / 60.0))*.166;
    float a = s(p + vec2(o.x, 0)), b = s(p + vec2(0, .5 + o.y));
    p = -mat2(.5, -.866, .866, .5)*(p + .5);
    float c = s(p + vec2(o.x, 0)), d = s(p + vec2(0, .5 + o.y)); 
    return min(min(a, b), min(c, d))*2.;
}

vec4 voroniblocks() {
	vec2 tc = texCoord;
	tc /= (InSize.y/3000.0);
    vec2 o = vec2(1)*m(tc);
    vec2 b = vec2(.8, .5)*max(o - m(tc + .02), 0.)/.1;
    vec2 offsets = sqrt(pow(vec2(1.0)*o, vec2(1, 3.5))  + b*b*(.5 + b*b));
	
	float blendFac = min(frameCounter / 120.0, 1.0);
	
	return texture2D(DiffuseSampler, texCoord + (offsets * 0.1 - 0.05) * blendFac);
}

//visual snow ======================================

float rand(vec2 uv, float t) {
    return fract(sin(dot(uv, vec2(1225.6548, 321.8942))) * 4251.4865 + t);
}

vec2 rotate(vec2 v, float a) {
	float s = sin(a);
	float c = cos(a);
	mat2 m = mat2(c, -s, s, c);
	return m * v;
}

vec2 hash2(vec2 p ) {
   return fract(sin(vec2(dot(p, vec2(123.4, 748.6)), dot(p, vec2(547.3, 659.3))))*5232.85324);   
}
float hash(vec2 p) {
  return fract(sin(dot(p, vec2(43.232, 75.876)))*4526.3257);   
}

float voronoi(vec2 p) {
    vec2 n = floor(p);
    vec2 f = fract(p);
    float md = 5.0;
    vec2 m = vec2(0.0);
    for (int i = -1;i<=1;i++) {
        for (int j = -1;j<=1;j++) {
            vec2 g = vec2(i, j);
            vec2 o = hash2(n+g);
            o = 0.5+0.5*sin((frameCounter / 60.0)+5.038*o);
            vec2 r = g + o - f;
            float d = dot(r, r);
            if (d<md) {
              md = d;
              m = n+g+o;
            }
        }
    }
    return md;
}

float ov(vec2 p, int itr) {
    float v = 0.0;
    float a = 0.4;
    for (int i = 0;i<itr;i++) {
        v+= voronoi(p)*a;
        p*=2.0;
        a*=0.5;
    }
    return v;
}

vec2 PincushionDistortion(vec2 uv, float strength) 
{
	vec2 st = uv - vec2(0.5);
    float uva = atan(st.x, st.y);
    float uvd = sqrt(dot(st, st));
    float distortAmount = strength * (-1.0);
    uvd = uvd * (1.0 + distortAmount * uvd * uvd);
    vec2 distortedUVs = vec2(0.5) + vec2(sin(uva), cos(uva)) * uvd;
    return distortedUVs;
}

vec3 ChromaticAbberation(sampler2D tex, vec2 uv, float amount) 
{
	float rChannel = texture2D(tex, PincushionDistortion(uv, 0.3 * amount)).r;
    float gChannel = texture2D(tex, PincushionDistortion(uv, 0.15 * amount)).g;
    float bChannel = texture2D(tex, PincushionDistortion(uv, 0.075 * amount)).b;
    vec3 retColor = vec3(rChannel, gChannel, bChannel);
    return retColor;
}

vec4 snow() {
    vec2 ps = vec2(1.0) / InSize.xy;
    vec2 uv = texCoord;
    
    float scale = 30.0 * pow(min(frameCounter / 120.0, 1.0), 2.0);
    
    vec2 offset = rotate((rand(uv, frameCounter / 120.0) - 0.5) * 2.0 * ps * scale, frameCounter);
	
    vec3 noise = texture2D(DiffuseSampler, uv + offset).rgb;
	
	float dx = texCoord.x - 0.5;
	float dz = texCoord.y - 0.5;
	
    float amount = min(max(3.0 * pow(1.0 - ov(texCoord * 1.0, 2) * 2.0, 6.0), 0.0) * (dx*dx + dz*dz) * 5.0, 1.5);
	
    vec3 color = ChromaticAbberation(DiffuseSampler, uv, amount);
    
	//return vec4(amount, amount, amount, 1.0);
    return vec4(mix(color.rgb, noise, amount), 1.0);
}

//water distortion
vec4 water() {
	float amount = ov(texCoord * 1.0, 3) * 0.25;
	float blendFac = min(frameCounter / 120.0, 1.0);
	//return vec4(texture2D(DiffuseSampler, texCoord + (amount - 0.05) * blendFac).rgb, 1.0);
	return vec4(texture2D(DiffuseSampler, texCoord + (rotate(vec2(amount - 0.05, 0.0), frameCounter / 40.0)) * blendFac).rgb, 1.0);
}

//fractal distortion
vec4 fractaledge() {
	vec4 c = vec4(0.0);
    vec2 v = InSize.xy;
	vec2 p = texCoord * v;
	
	float t = (frameCounter / 20.0) + rngNum * 100.0;
	float f = fract(t);
	
    p = (p-v*.5)*.4 / v.y;
    p += p * sin(dot(p, p)*20.-t) * .01;
    
    c *= 0.;
    for (float i = .5 ; i < 8. ; i++)
        p = abs(2.*fract(p-.5)-1.) * mat2(cos(.01*(t*.1)*i*i + .78*vec4(1,7,3,1))),
        c += exp(-abs(p.y)*5.) * (cos(vec4(2,3,1,0)*i)*.5+.5);
        
    c.gb *= .5;
	
	float dx = texCoord.x - 0.5;
	float dz = texCoord.y - 0.5;
	
	return texture2D(DiffuseSampler, texCoord) + c * max(dx*dx + dz*dz - 0.1 - (pow(max(1.0 - frameCounter / 360.0, 0.0), 5.0) * 0.3), 0.0) * 2.0;
}

void main() {
	vec4 counterColor = texture2D(CounterSampler, vec2(0.0, 0.0));
	frameCounter = counterColor.r * 256.0 + counterColor.g * 256.0 * 256.0 + counterColor.b * 256.0 * 256.0 * 256.0;
	
	vec4 rngColor = texture2D(CounterSampler, vec2(0.0, (1.5 / InSize.y)));
	rngNum = fract(sin(rngColor.r + rngColor.g * 256.0 + rngColor.b * 256.0 * 256.0) * 100000.0);
	
	if(rngNum < 0.3) {
		gl_FragColor = mandelbrot();
	}else if(rngNum < 0.5) {
		gl_FragColor = voroniblocks();
	}else if(rngNum < 0.75) {
		gl_FragColor = water();
	}else if(rngNum < 0.90) {
		gl_FragColor = fractaledge();
	}else {
		gl_FragColor = snow();
	}
}
