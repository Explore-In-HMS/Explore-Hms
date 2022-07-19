/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Define quaternion for rotation operations.
 */

#ifndef QUATERNION_H
#define QUATERNION_H

#include "Math/Vector3.h"

NS_CG_BEGIN

class CGKIT_EXPORT Quaternion {
public:
    Quaternion();
    ~Quaternion();
    Quaternion(f32 nx, f32 ny, f32 nz, f32 nw);

    Quaternion& operator=(const Quaternion& other);
    Quaternion operator+(const Quaternion& other) const;
    Quaternion operator-(const Quaternion& other) const;

    Quaternion operator*(const Quaternion& other) const;
    Quaternion& operator*=(const Quaternion& other);
    Vector3 operator*(const Vector3& value) const;
    Quaternion operator*(f32 s) const;
    Quaternion& operator*=(f32 s);

    bool operator==(const Quaternion& other) const;
    bool operator!=(const Quaternion& other) const;

    Quaternion& Set(f32 nx, f32 ny, f32 nz, f32 nw);
    Quaternion& Set(const Vector3& euler);

    Quaternion& Inverse();
    Quaternion Inversed() const;
    Quaternion& Normalize();
    Quaternion Normalized() const;
    f32 Dot(const Quaternion& other) const;
    Quaternion& FromAngleAxisToQuat(f32 radianAngle, const Vector3& axis);
    void FromQuatToAngleAxis(f32& angradianAnglele, Vector3& axis) const;

    Vector3 ToEuler() const;
    String ToString() const;

public:
    f32 x;
    f32 y;
    f32 z;
    f32 w;

    static const Quaternion ZERO;
    static const Quaternion IDENTITY;
};

NS_CG_END

#endif