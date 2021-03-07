#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D CounterSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

float frameCounter;

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

void main() {
	vec4 counterColor = texture2D(CounterSampler, vec2(0.0, 0.0));
	frameCounter = counterColor.r * 256.0 + counterColor.g * 256.0 * 256.0 + counterColor.b * 256.0 * 256.0 * 256.0;
	
	vec4 rngColor = texture2D(CounterSampler, vec2(0.0, (1.5 / InSize.y)));
	float rngNum = fract(sin(rngColor.r + rngColor.g * 256.0 + rngColor.b * 256.0 * 256.0) * 100000.0);
	
	if(rngNum < 0.5) {
		gl_FragColor = mandelbrot();
	}else {
		gl_FragColor = vec4(1.0);//texture2D(DiffuseSampler, vec2(1.0) - texCoord);
	}
	
}
