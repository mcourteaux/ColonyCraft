#define FOG

#VERTEX

attribute vec2 light;

uniform float sun;
uniform float flicker;
uniform float wave;
#ifdef FOG
uniform vec2 fog;
#endif


varying vec2 vUV;
varying float vLight;
#ifdef FOG
varying float vFog;
#endif

#include "util.glsl"

vec2 wave(vec2 vertex, float wave)
{
	wave *= 0.2;
	return 0.8 * vec2(smoothTriangle(vertex.x + 1.3 * vertex.y + wave + 0.5) - 0.5, smoothTriangle(1.6 * vertex.x + vertex.y + wave) - 0.5);
	//return vec2(0.4 * sin(wave + vertex.x * 1.3), 0.4 * cos(wave + vertex.y * 1.3));
}

void main()
{
	vec4 vertex = gl_Vertex;
	vertex.xz += (1.0 - step(0.6, gl_MultiTexCoord0.y)) * wave(vertex.xz, wave);
	gl_Position = gl_ModelViewProjectionMatrix * vertex;
	
	/* Texture, Light, Fog */
	vUV = gl_MultiTexCoord0.xy;
	vLight = mixLight(vec2(light.x - flicker, sun * light.y));
#ifdef FOG
	vFog = smoothstep(fog.x, fog.y, gl_Position.z);
#endif
}

#FRAGMENT

uniform sampler2D texture;
uniform vec4 fogColor;

varying vec2 vUV;
varying float vLight;
#ifdef FOG
varying float vFog;
#endif

void main()
{
	vec4 color = texture2D(texture, vUV);
	color.xyz *= vLight;
#ifdef FOG
	gl_FragColor = mix(color, fogColor, vFog);
#else
	gl_FragColor = color;
#endif
}
