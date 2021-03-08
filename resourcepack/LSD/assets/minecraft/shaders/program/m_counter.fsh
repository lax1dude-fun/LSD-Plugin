#version 110

uniform sampler2D DiffuseSampler;
uniform sampler2D CounterSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

bool isInPixel(vec2 pixel) {
    if (
            texCoord.x > (pixel.x) / InSize.x &&
            texCoord.x < (pixel.x + 1.0) / InSize.x &&
            texCoord.y > (pixel.y) / InSize.y &&
            texCoord.y < (pixel.y + 1.0) / InSize.y
        ) {
        return true;
    }
    return false;
}

void main() {
	if(isInPixel(vec2(0.0, 1.0))) {
		vec4 inp = texture2D(CounterSampler, texCoord);
		if(inp.r == 0.0 && inp.g == 0.0 && inp.b == 0.0) {
			gl_FragColor = vec4(min(texture2D(DiffuseSampler, vec2(0.25, 0.25)).rgb + (1.0 / 255.0), 1.0), 1.0);
		}else {
			gl_FragColor = vec4(inp.rgb, 1.0);
		}
		return;
	}else if(isInPixel(vec2(0.0, 0.0))) {
		vec4 inp = texture2D(CounterSampler, texCoord);
		inp.r += (1.0 / 255.0);
		if(inp.r > 1.0) {
			inp.r = 0.0;
			inp.g += (1.0 / 255.0);
			if(inp.g > 1.0) {
				inp.g = 0.0;
				inp.b += (1.0 / 255.0);
				if(inp.b > 1.0) {
					inp.b = 0.0;
				}
			}
		}
		gl_FragColor = vec4(inp.rgb, 1.0);
		return;
	}
	discard;
}
