//Filmic http://filmicworlds.com/blog/filmic-tonemapping-operators/
//"Filmic Mapping 1\n\nBy Jim Hejl and Richard Burgess-Dawson from the \"Filmic Tonemapping for Real-time Rendering\" Siggraph 2010 Course by Haarm-Pieter Duiker."
void tonemap(inout vec3 color,in float exposure){
    color *= exposure;
	color = max(vec3(0.), color - vec3(0.004));
	color = (color * (6.2 * color + .5)) / (color * (6.2 * color + 1.7) + 0.06);
}