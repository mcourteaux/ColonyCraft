
float mixLight(vec2 l)
{
    /*
     * optimized bilerp function
	 *        x,     y,   q11, q12, q21, q22, x1,  x2,  y1,  y2
	 * biLerp(block, sun, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 0.0, 1.0);
	 */
	
	l = -l * l * (2.0 * l - vec2(3.0, 3.0));
	
	return l.x * (1.0 - l.y) + (1.0 - l.x) * l.y + l.x * l.y;
}