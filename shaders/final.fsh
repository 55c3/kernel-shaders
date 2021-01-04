// 55c3
// November 2020

#version 120

#define BRIGHTNESS 0 // brighteness level [0 25 51 76 102 127 153 178 204 229 255]
#define CONTRAST 0 // contrast level [0 25 51 76 102 127 153 178 204 229 255]

#define NONE 0
#define EMBOSS 1
#define OUTLINE 2
#define SOBEL 3
#define SHARPEN 4

#define KERNEL SOBEL // type of kernel [EMBOSS OUTLINE SOBEL SHARPEN NONE]

#define NE 1
#define NW 2
#define SE 3
#define SW 4
#define UP 5
#define DOWN 6
#define LEFT 7
#define RIGHT 8

#define DIRECTION NE // direction to apply filters [UP DOWN LEFT RIGHT NE NW SE SW]

uniform sampler2D gcolor;

varying vec2 data[9];

const float kernel[9] = float[](
#if KERNEL == OUTLINE
	-1,-1,-1,
	-1, 8,-1,
	-1,-1,-1
#elif KERNEL == EMBOSS
	#if DIRECTION == UP
		 1, 2, 1,
		 0, 1, 0,
		-1,-2,-1
	#elif DIRECTION == NE
		 0, 1, 2,
		-1, 1, 1,
		-2,-1, 0
	#elif DIRECTION == RIGHT
		-1, 0, 1,
		-2, 1, 2,
		-1, 0, 1
	#elif DIRECTION == SE
		-2,-1, 0,
		-1, 1, 1,
		 0, 1, 2
	#elif DIRECTION == DOWN
		-1,-2,-1,
		 0, 1, 0,
		 1, 2, 1
	#elif DIRECTION == SW
		 0,-1,-2,
		 1, 1,-1,
		 2, 1, 0
	#elif DIRECTION == LEFT
		1, 0,-1,
		2, 1,-2,
		1, 0,-1
	#elif DIRECTION == NW
		2, 1, 0,
		1, 1,-1,
		0,-1,-2
	#endif
#elif KERNEL == SOBEL
	#if DIRECTION == UP
		 1, 2, 1,
		 0, 0, 0,
		-1,-2,-1
	#elif DIRECTION == NE
		 0, 1, 2,
		-1, 0, 1,
		-2,-1, 0
	#elif DIRECTION == RIGHT
		-1, 0, 1,
		-2, 0, 2,
		-1, 0, 1
	#elif DIRECTION == SE
		-2,-1, 0,
		-1, 0, 1,
		 0, 1, 2
	#elif DIRECTION == DOWN
		-1,-2,-1,
		 0, 0, 0,
		 1, 2, 1
	#elif DIRECTION == SW
		 0,-1,-2,
		 1, 0,-1,
		 2, 1, 0
	#elif DIRECTION == LEFT
		1, 0,-1,
		2, 0,-2,
		1, 0,-1
	#elif DIRECTION == NW
		2, 1, 0,
		1, 0,-1,
		0,-1,-2
	#endif
#elif KERNEL == SHARPEN
	 0,-1, 0,
	-1, 5,-1,
	 0,-1, 0
#else // identity matrix
	0,0,0,
	0,1,0,
	0,0,0
#endif
);

void main() {
	#if KERNEL == NONE
		gl_FragColor = texture2D(gcolor, data[5]);
	#else
		vec4 resultColor = vec4(0.0);
		for (int i = 0; i < 9; i++) {
			vec4 color = texture2D(gcolor, data[i]);
			resultColor += color * kernel[i];
		}

		#if BRIGHTNESS != 0
			resultColor += BRIGHTNESS / 255.0;
		#endif

		#if CONTRAST != 0
			float factor = (259.0 * (CONTRAST + 255)) / (255.0 * (259 - CONTRAST));
			resultColor += factor * (resultColor - 0.5) + 0.5;
		#endif

		gl_FragColor = resultColor;
	#endif
}
