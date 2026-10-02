#version 460

// One R8_UNORM SDF atlas per font, stored as layers of a 2D array texture (all atlases same size).
layout(set = 0, binding = 0) uniform sampler2DArray uAtlas;

layout(location = 0) in vec4 vColor;
layout(location = 1) in vec2 vLocal;
layout(location = 2) in vec2 vUV;
layout(location = 3) flat in vec2 vParam;
layout(location = 4) flat in uint vType;

layout(location = 0) out vec4 outColor;   // premultiplied alpha

const uint T_RECT = 0u;
const uint T_CIRCLE = 1u;
const uint T_LINE = 2u;
const uint T_TEXT = 3u;

// Must match the onedge_value you pass to stbtt_GetCodepointSDF (128 -> 128/255).
const float SDF_EDGE = 128.0 / 255.0;

void main() {
    uint type = vType & 0xFFu;
    float cov;

    if (type == T_TEXT) {
        float font = float((vType >> 8) & 0xFFu);
        float s = texture(uAtlas, vec3(vUV, font)).r;
        float w = max(fwidth(s) * 0.5, 1e-4);
        cov = smoothstep(SDF_EDGE - w, SDF_EDGE + w, s);
    } else if (type == T_CIRCLE) {
        cov = clamp(0.5 - (length(vLocal) - vParam.x), 0.0, 1.0);
    } else { // rect and line are both boxes in local pixel space
        vec2 d = abs(vLocal) - vParam;
        float sd = length(max(d, 0.0)) + min(max(d.x, d.y), 0.0);
        cov = clamp(0.5 - sd, 0.0, 1.0);
    }

    float a = vColor.a * cov;
    outColor = vec4(vColor.rgb * a, a);
}
