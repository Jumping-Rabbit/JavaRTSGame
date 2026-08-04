#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec3 v_normal;

uniform vec3 u_lightDir;
uniform vec3 u_lightColor;
uniform vec3 u_ambientLight;
uniform vec3 u_teamColor;

void main() {
    vec3 normal = normalize(v_normal);
    float NdotL = max(dot(normal, -u_lightDir), 0.0);
    vec3 diffuseLight = NdotL * u_lightColor;
    vec3 finalLight = u_ambientLight + diffuseLight;

    // EXPLANATION: Blender marks your "TeamColorMaterial" mesh faces
    // by using an alpha flag (e.g., 0.99) to bypass standard color logic.
    vec3 baseColor = v_color.rgb;
    if (v_color.a < 0.995 && v_color.a > 0.985) {
        baseColor = u_teamColor;
    }

    gl_FragColor = vec4(baseColor * finalLight, 1.0);
}
