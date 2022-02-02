/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Define Vector3 type for 3D point and vector operations.
 */

#ifndef VECTOR3_H
#define VECTOR3_H

#include "Math/Math.h"

NS_CG_BEGIN

class Vector3 {
public:
    Vector3();
    Vector3(f32 nx, f32 ny, f32 nz);
    ~Vector3();
 
    inline Vector3 operator-() const;
    inline Vector3& operator=(const Vector3& other);

    inline Vector3 operator+(const Vector3& other) const;
    inline Vector3& operator+=(const Vector3& other);

    inline Vector3 operator-(const Vector3& other) const;
    inline Vector3& operator-=(const Vector3& other);

    inline Vector3 operator*(f32 v) const;
    inline Vector3& operator*=(f32 v);

    inline Vector3 operator/(f32 v) const;
    inline Vector3& operator/=(f32 v);

    inline bool operator==(const Vector3& other) const;
    inline bool operator!=(const Vector3& other) const;

    f32 Length() const;
    f32 Dot(const Vector3& other) const;

    Vector3 Cross(const Vector3& p) const;
    Vector3& Normalize();
    Vector3 Normalized() const;

    String ToString() const;

public:
    f32 x;
    f32 y;
    f32 z;

    static const Vector3 ZERO;
    static const Vector3 ONE;
    static const Vector3 UNIT_X;
    static const Vector3 UNIT_Y;
    static const Vector3 UNIT_Z;
    static const Vector3 NEGATIVE_UNIT_X;
    static const Vector3 NEGATIVE_UNIT_Y;
    static const Vector3 NEGATIVE_UNIT_Z;
};

NS_CG_END

#include "Math/Vector3.inl"

#endif