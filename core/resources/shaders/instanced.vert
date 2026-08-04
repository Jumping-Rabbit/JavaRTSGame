attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;

// 4-Float instance injection attribute layout
attribute vec3 i_position;  // x, y, z
attribute float i_angle;    // Rotation tracking angle

uniform mat4 u_projViewTrans;
varying vec4 v_color;
varying vec3 v_normal;

void main() {
    float c = cos(i_angle);
    float s = sin(i_angle);

    // Fast Y-axis orientation rotation calculation
    mat3 rotationMatrix = mat3(
        c,   0.0,  s,
        0.0, 1.0,  0.0,
       -s,   0.0,  c
    );

    vec3 rotatedPosition = rotationMatrix * a_position.xyz;
    v_normal = normalize(rotationMatrix * a_normal);

    v_color = a_color;
    gl_Position = u_projViewTrans * vec4(rotatedPosition + i_position, 1.0);
}
