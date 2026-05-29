layout(std140) uniform m_MatParams {
    vec4 Color;
};

uniform float m_Offset;

in float gradient;
out vec4 outFragColor;

void main() {
    float intensity = clamp(gradient + m_Offset, 0.0, 1.0);
    outFragColor = vec4(Color.rgb * intensity, Color.a);
}
