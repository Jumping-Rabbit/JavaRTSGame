#version 460

layout(location = 0) in vec4 inA;      // RECT/TEXT: x,y,w,h, CIRCLE: cx,cy,r,_, LINE: x1,y1,x2,y2   (1920x1080 units)
layout(location = 1) in vec4 inB;      // TEXT: u0,v0,u1,v1 , LINE: width,_,_,_, else unused
layout(location = 2) in vec4 inColor;  // R8G8B8A8_UNORM, straight (non-premultiplied) RGBA
layout(location = 3) in uint inType;   // bits 0-7: type (0 rect, 1 circle, 2 line, 3 text glyph), bits 8-15: font index (atlas layer)

layout(push_constant) uniform PC {
    vec2  viewport;
    vec2  offset;
    float scale;
    float uiScale;
} pc;

layout(location = 0) out vec4 vColor;
layout(location = 1) out vec2 vLocal;        // pixel-space position relative to shape center (rect/circle/line)
layout(location = 2) out vec2 vUV;
layout(location = 3) flat out vec2 vParam;   // rect: (halfW, halfH) px, circle: (radiusPx, _), line: (halfLengthPx, halfWidthPx)
layout(location = 4) flat out uint vType;    // passed through unchanged (type + font index)

const uint T_RECT = 0u;
const uint T_CIRCLE = 1u;
const uint T_LINE = 2u;
const uint T_TEXT = 3u;

void main() {
    vec2 corner = vec2(float(gl_VertexIndex & 1), float(gl_VertexIndex >> 1));
    float s = pc.scale * pc.uiScale;
    uint type = inType & 0xFFu;

    vec2 pos;
    vLocal = vec2(0.0);
    vUV = vec2(0.0);
    vParam = vec2(0.0);

    if (type == T_TEXT) {
        pos = pc.offset + (inA.xy + corner * inA.zw) * s;
        vUV = mix(inB.xy, inB.zw, corner);
    } else if (type == T_RECT) {
        vec2 half_ = inA.zw * s * 0.5;
        vec2 c = pc.offset + inA.xy * s + half_;
        vec2 l = (corner * 2.0 - 1.0) * (half_ + 1.0);   // +1px pad for antialiasing
        pos = c + l;
        vLocal = l;
        vParam = half_;
    } else if (type == T_CIRCLE) {
        vec2 c = pc.offset + inA.xy * s;
        float r = inA.z * s;
        vec2 l = (corner * 2.0 - 1.0) * (r + 1.0);
        pos = c + l;
        vLocal = l;
        vParam = vec2(r, 0.0);
    } else { // T_LINE (flat caps)
        vec2 p1 = pc.offset + inA.xy * s;
        vec2 p2 = pc.offset + inA.zw * s;
        vec2 d = p2 - p1;
        float len = length(d);
        vec2 dir = len > 1e-5 ? d / len : vec2(1.0, 0.0);
        vec2 nrm = vec2(-dir.y, dir.x);
        float hw = inB.x * s * 0.5;
        float along = mix(-1.0, len + 1.0, corner.x);
        float across = mix(-(hw + 1.0), hw + 1.0, corner.y);
        pos = p1 + dir * along + nrm * across;
        vLocal = vec2(along - len * 0.5, across);
        vParam = vec2(len * 0.5, hw);
    }

    vColor = inColor;
    vType = inType;
    gl_Position = vec4(pos / pc.viewport * 2.0 - 1.0, 0.0, 1.0);
}
