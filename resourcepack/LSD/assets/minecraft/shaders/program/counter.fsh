#version 110

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float Time;

void main() {
	if(texCoord.x >= (1.0 / InSize.x) || texCoord.y >= (1.0 / InSize.y)) {
		discard;
	}
	vec4 input = texture2D(DiffuseSampler, texCoord);
	input.r += (1.0 / 255.0);
	if(input.r > 1.0) {
		input.r = 0.0;
		input.g += (1.0 / 255.0);
		if(input.g > 1.0) {
			input.g = 0.0;
			input.b += (1.0 / 255.0);
			if(input.b > 1.0) {
				input.b = 0.0;
			}
		}
	}
	gl_FragColor = vec4(input.rgb, 1.0);
}
