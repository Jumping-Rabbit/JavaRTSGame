#define CELL_SHIFT 22
#define NORMALIZED_BIT_COUNT 15u    // the larger component is scaled to exactly this many bits
#define MAXIMUM_RADIUS_SUM (1u << 22)

struct Entity {
    int positionX;
    int positionY;
    int radius;
    int unused; // keeps the struct 16 bytes
};

[[vk::binding(0, 0)]] cbuffer Parameters {
    uint EntityCount;
    int GridWidth;
    int GridHeight;
    uint Padding0;
};

[[vk::binding(1, 0)]] StructuredBuffer<Entity> Entities; // sorted by cell
[[vk::binding(2, 0)]] StructuredBuffer<uint> CellStart; // GridWidth*GridHeight + 1 prefix sums
[[vk::binding(3, 0)]] RWStructuredBuffer<int2> OutputMove; // indexed by sorted entity index

uint AbsoluteValueAsUnsigned(int value) {
    return value < 0 ? (0u - (uint)value) : (uint)value;
}

// floor(multiplicand * multiplier / divisor)
// Requires multiplicand < 2^16, multiplier < 2^22, 0 < divisor < 2^16.
uint MultiplyThenDivide(uint multiplicand, uint multiplier, uint divisor) {
    uint multiplierHighBits = multiplier >> 11; // < 2^11
    uint multiplierLowBits = multiplier & 2047u;
    uint partialProduct = multiplicand * multiplierHighBits; // < 2^27
    uint quotient = partialProduct / divisor;
    uint remainder = partialProduct - quotient * divisor; // < 2^16
    partialProduct = (remainder << 11) + multiplicand * multiplierLowBits; // < 2^27 + 2^27
    return (quotient << 11) + partialProduct / divisor;
}

[numthreads(64, 1, 1)]
void main(uint3 dispatchThreadId : SV_DispatchThreadID) {
    uint entityIndex = dispatchThreadId.x;
    [branch]
    if (entityIndex >= EntityCount) return;

    Entity thisEntity = Entities[entityIndex];
    int cellX = thisEntity.positionX >> CELL_SHIFT;
    int cellY = thisEntity.positionY >> CELL_SHIFT;

    int firstColumn = max(cellX - 1, 0);
    int lastColumn = min(cellX + 1, GridWidth - 1);
    int firstRow = max(cellY - 1, 0);
    int lastRow = min(cellY + 1, GridHeight - 1);

    int2 totalPush = int2(0, 0);
    [branch]
    if (firstColumn <= lastColumn) {
        for (int rowIndex = firstRow; rowIndex <= lastRow; rowIndex++) {
            uint firstEntityIndex = CellStart[rowIndex * GridWidth + firstColumn];
            uint endEntityIndex = CellStart[rowIndex * GridWidth + lastColumn + 1];

            for (uint otherEntityIndex = firstEntityIndex; otherEntityIndex < endEntityIndex; otherEntityIndex++) {
                if (otherEntityIndex == entityIndex) continue;

                Entity otherEntity = Entities[otherEntityIndex];
                int deltaX = otherEntity.positionX - thisEntity.positionX;
                int deltaY = otherEntity.positionY - thisEntity.positionY;
                uint absoluteDeltaX = AbsoluteValueAsUnsigned(deltaX);
                uint absoluteDeltaY = AbsoluteValueAsUnsigned(deltaY);
                uint radiusSum = min((uint)(thisEntity.radius + otherEntity.radius), MAXIMUM_RADIUS_SUM);

                if (absoluteDeltaX >= radiusSum || absoluteDeltaY >= radiusSum) continue;
                if ((((absoluteDeltaX + absoluteDeltaY) * 181u) >> 8) >= radiusSum) continue;

                [branch]
                if ((absoluteDeltaX | absoluteDeltaY) == 0u) {
                    int stackedPushDistance = (int)((radiusSum + 1u) >> 1);
                    totalPush.x += (entityIndex < otherEntityIndex) ? -stackedPushDistance : stackedPushDistance;
                    continue;
                }

                uint largerComponent = max(absoluteDeltaX, absoluteDeltaY);
                uint bitLength = firstbithigh(largerComponent) + 1u;
                uint normalizedX, normalizedY, shiftAmount;
                [flatten]
                if (bitLength > NORMALIZED_BIT_COUNT) {
                    shiftAmount = bitLength - NORMALIZED_BIT_COUNT;
                    normalizedX = absoluteDeltaX >> shiftAmount;
                    normalizedY = absoluteDeltaY >> shiftAmount;
                } else {
                    shiftAmount = NORMALIZED_BIT_COUNT - bitLength;
                    normalizedX = absoluteDeltaX << shiftAmount;
                    normalizedY = absoluteDeltaY << shiftAmount;
                }

                uint normalizedLarger = max(normalizedX, normalizedY);
                uint normalizedSmaller = min(normalizedX, normalizedY);
                uint normalizedDistanceSquared = normalizedX * normalizedX + normalizedY * normalizedY;

                // alpha-max-beta-min starting guess (123/128 and 51/128) always >= 15744.
                uint initialGuess = (123u * normalizedLarger + 51u * normalizedSmaller) >> 7;
                uint normalizedDistance = (initialGuess + normalizedDistanceSquared / initialGuess) >> 1;
                uint distance = (bitLength > NORMALIZED_BIT_COUNT)
                    ? (normalizedDistance << shiftAmount)
                    : ((normalizedDistance + ((1u << shiftAmount) >> 1)) >> shiftAmount);

                if (distance >= radiusSum) continue;

                uint overlap = radiusSum - distance;
                uint pushDistance = (overlap + 1u) >> 1;

                int moveX = (int)MultiplyThenDivide(normalizedX, pushDistance, normalizedDistance);
                int moveY = (int)MultiplyThenDivide(normalizedY, pushDistance, normalizedDistance);
                totalPush.x += (deltaX < 0) ? moveX : -moveX;
                totalPush.y += (deltaY < 0) ? moveY : -moveY;
            }
        }
    }

    OutputMove[entityIndex] = totalPush;
}
