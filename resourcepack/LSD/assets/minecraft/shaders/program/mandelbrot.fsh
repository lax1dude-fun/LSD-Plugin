#version 110

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float Time;

void main() {
	vec2 p = vec2(-0.745, 0.186) + 3.0 * ((texCoord * InSize) / (InSize.y) - 0.5) * pow(0.0005, 0.97 - cos(3.14159 * Time * 2.0) * 0.2);
	
	float n = 0.0;
	vec2 z = p * n;
	
	for(; (n < 96.0) && (dot(z,z) < 1e4); n++)
		z = vec2(z.x*z.x - z.y*z.y, 2.*z.x*z.y) + p;
	
	vec2 f = 0.5 + 0.5 * cos(vec2(3.0, 4.0) + 0.05 * (n - log2(log2(dot(z,z)))));
	gl_FragColor = texture2D(DiffuseSampler, texCoord * 0.9 + min(f, 1.0) * 0.1);
}
