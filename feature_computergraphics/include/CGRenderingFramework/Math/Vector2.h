/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Define Vector2 type for point and 2D vector operations.
 */

#ifndef VECTOR2_H
#define VECTOR2_H

#include "Math/Math.h"

NS_CG_BEGIN

class Vector2 {
public:
    Vector2();
    Vector2(f32 nx, f32 ny);
    ~Vector2();

    inline Vector2 operator-() const;
    inline Vector2& operator=(const Vector2& other);

    inline Vector2 operator+(const Vector2& other) const;
    inline Vector2& operator+=(const Vector2& other);

    inline Vector2 operator-(const Vector2& other) const;
    inline Vector2& operator-=(const Vector2& other);

    inline Vector2 operator*(f32 v) const;
    inline Vector2& operator*=(f32 v);

    inline Vector2 operator/(f32 v) const;
    inline Vector2& operator/=(f32 v);

    inline bool operator==(const Vector2& other) const;
    inline bool operator!=(const Vector2& other) const;

    f32 Length() const;
    f32 Dot(const Vector2& other) const;
    Vector2& Normalize();

    String ToString() const;

 public:
    f32 x;
    f32 y;

    static const Vector2 ZERO;
    static const Vector2 ONE;

    static const Vector2 UP;
    static const Vector2 DOWN;
    static const Vector2 LEFT;
    static const Vector2 RIGHT;

    static const Vector2 UNIT_X;
    static const Vector2 UNIT_Y;
    static const Vector2 NEGATIVE_UNIT_X;
    static const Vector2 NEGATIVE_UNIT_Y;
};

NS_CG_END

#include "Math/Vector2.inl"

#endif