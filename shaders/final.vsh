// 55c3
// November 2020

#version 120

uniform float viewWidth;
uniform float viewHeight;

varying vec2 data[9];

void main() {
	gl_Position = ftransform();
	vec4 texcoord = gl_MultiTexCoord0;

	float texelWidth = 1.0 / viewWidth;
	float texelHeight = 1.0 / viewHeight;

	for (int i = 0; i < 3; i++) {
		for (int j = 0; j < 3; j++) {
			vec2 step = vec2(texelWidth * (j-1), texelHeight * (i-1));
			data[i*3+j] = texcoord.st + step;
		}
	}
}