#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D CounterSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

float frameCounter;

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

void main() {
	vec4 counterColor = texture2D(CounterSampler, vec2(0.0, 0.0));
	frameCounter = counterColor.r * 256.0 + counterColor.g * 256.0 * 256.0 + counterColor.b * 256.0 * 256.0 * 256.0;
	
	vec4 rngColor = texture2D(CounterSampler, vec2(0.0, (1.5 / InSize.y)));
	float rngNum = fract(sin(rngColor.r + rngColor.g * 256.0 + rngColor.b * 256.0 * 256.0) * 100000.0);
	
	if(rngNum < 0.5) {
		gl_FragColor = mandelbrot();
	}else {
		gl_FragColor = voroniblocks();//texture2D(DiffuseSampler, vec2(1.0) - texCoord);
	}
	
}
