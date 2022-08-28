/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Define basic math functions and const data.
 */

#ifndef MATH_H
#define MATH_H

#include <cmath>
#include <cfloat>
#include <cstdlib>
#include <climits>

#include "Core/Types.h"
#include "Core/STDHeaders.h"

NS_CG_BEGIN

namespace Math {
const f32 FLOAT_MIN = 1.17549435e-38F;
const f32 FLOAT_MAX = 3.402823466E+38F;

const f32 EPSILON = numeric_limits<f32>::epsilon();
//const f32 EPSILON = 1e-03;

const s32 ROUNDING_ERROR_S32 = 0;
const s64 ROUNDING_ERROR_S64 = 0;
const f32 ROUNDING_ERROR_F32 = 0.000001f;
const f64 ROUNDING_ERROR_F64 = 0.00000001;

const f32 PI = 3.14159265359f;
const f32 RECIPROCAL_PI = 1.0f / PI;
const f32 HALF_PI = PI / 2.0f;
const f32 TWO_PI = 2.0f * PI;

const f64 PI64 = 3.1415926535897932384626433832795028841971693993751;
const f64 RECIPROCAL_PI64 = 1.0 / PI64;

template <class T>
inline bool Equals(const T& a, const T& b)
{
    return a == b;
}

inline bool Equals(const s32 a, const s32 b, const s32 tolerance = ROUNDING_ERROR_S32)
{
    return (a + tolerance >= b) && (a - tolerance <= b);
}

inline bool Equals(const s64 a, const s64 b, const s64 tolerance = ROUNDING_ERROR_S64)
{
    return (a + tolerance >= b) && (a - tolerance <= b);
}

inline bool Equals(const f32 a, const f32 b, const f32 tolerance = ROUNDING_ERROR_F32)
{
    return (a + tolerance >= b) && (a - tolerance <= b);
}

inline bool Equals(const f64 a, const f64 b, const f64 tolerance = ROUNDING_ERROR_F64)
{
    return (a + tolerance >= b) && (a - tolerance <= b);
}

inline bool Equals(u32 a, u32 b, const s32 tolerance = ROUNDING_ERROR_S32)
{
    return (a + tolerance >= b) && (a - tolerance <= b);
}

inline f32 Sqrt(const f32 f)
{
    if (f < 0) {
        return FLOAT_MAX;
    }
    return sqrtf(f);
}

inline f64 Sqrt(const f64 f)
{
    if (f < 0) {
        return FLOAT_MAX;
    }
    return sqrt(f);
}

inline f32 ReciprocalSqrt(const f32 x)
{
    f32 sqrtResult = sqrtf(x);
    if (sqrtResult == 0) {
        return FLOAT_MAX;
    }

    return 1.0f / sqrtf(x);
}

inline f64 ReciprocalSqrt(const f64 x)
{
    f64 sqrtResult = sqrt(x);
    if (sqrtResult == 0) {
        return FLOAT_MAX;
    }

    return 1.0f / sqrt(x);
}

inline f32 Reciprocal(const f32 f)
{
    if (f == 0) {
        return FLOAT_MAX;
    }
    return 1.0f / f;
}

inline f64 Reciprocal(const f64 f)
{
    if (f == 0) {
        return FLOAT_MAX;
    }
    return 1.0f / f;
}

inline f32 UnitRandom()
{
    return f32(rand()) / RAND_MAX;
}

inline f32 RangeRandom(f32 low, f32 high)
{
    return (high - low) * UnitRandom() + low;
}

}

NS_CG_END

#endif