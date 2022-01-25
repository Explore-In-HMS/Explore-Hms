/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Define Vector4 type for 3D point and vector operations.
 */

#ifndef VECTOR4_H
#define VECTOR4_H

#include "Math/Math.h"

NS_CG_BEGIN

class Vector4 {
public:
    Vector4();
    Vector4(f32 nx, f32 ny, f32 nz, f32 nw);
    ~Vector4();

    inline Vector4 operator-() const;
    inline Vector4& operator=(const Vector4& other);

    inline Vector4 operator+(const Vector4& other) const;
    inline Vector4& operator+=(const Vector4& other);

    inline Vector4 operator-(const Vector4& other) const;
    inline Vector4& operator-=(const Vector4& other);

    inline Vector4 operator*(f32 v) const;
    inline Vector4& operator*=(f32 v);

    inline Vector4 operator/(f32 v) const;
    inline Vector4& operator/=(f32 v);

    inline bool operator==(const Vector4& other) const;
    inline bool operator!=(const Vector4& other) const;

    f32 Length() const;
    f32 Dot(const Vector4& v) const;

    Vector4& Normalize();
    Vector4 Normalized() const;
    String ToString() const;

public:
    f32 x;
    f32 y;
    f32 z;
    f32 w;

    static const Vector4 ZERO;
    static const Vector4 ONE;
};

NS_CG_END

#include "Math/Vector4.inl"

#endif