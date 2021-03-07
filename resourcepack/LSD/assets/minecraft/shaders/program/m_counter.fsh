#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D CounterSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

void main() {
	
	if(texCoord.x < (1.0 / InSize.x) && texCoord.y < (2.0 / InSize.y)) {
		gl_FragColor = vec4(1.0);
		if(texCoord.y > (1.0 / InSize.y)) {
			vec4 input = texture2D(CounterSampler, vec2(0.0, (1.5 / InSize.y)));
			if(input.r <= 0.0 && input.g <= 0.0 && input.b <= 0.0) {
				gl_FragColor = min(texture2D(DiffuseSampler, vec2(0.25, 0.25)) + (1.0 / 255.0), 1.0);
				return;
			}
		}else {
			vec4 input = texture2D(CounterSampler, vec2(0.0));
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
			return;
		}
	}
	gl_FragColor = texture2D(CounterSampler, texCoord);
}
