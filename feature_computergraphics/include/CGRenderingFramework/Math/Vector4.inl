/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Define Vector4 type for 3D point and vector operations.
 */

NS_CG_BEGIN

Vector4 Vector4::operator-() const
{
    return Vector4(-x, -y, -z, -w);
}

Vector4& Vector4::operator=(const Vector4& other)
{
    x = other.x;
    y = other.y;
    z = other.z;
    w = other.w;
    return *this;
}

Vector4 Vector4::operator+(const Vector4& other) const
{
    return Vector4(x + other.x, y + other.y, z + other.z, w + other.w);
}

Vector4& Vector4::operator+=(const Vector4& other)
{
    x += other.x;
    y += other.y;
    z += other.z;
    w += other.w;
    return *this;
}

Vector4 Vector4::operator-(const Vector4& other) const
{
    return Vector4(x - other.x, y - other.y, z - other.z, w - other.w);
}

Vector4& Vector4::operator-=(const Vector4& other)
{
    x -= other.x;
    y -= other.y;
    z -= other.z;
    w -= other.w;
    return *this;
}

Vector4 Vector4::operator*(f32 v) const
{
    return Vector4(x * v, y * v, z * v, w * v);
}

Vector4& Vector4::operator*=(f32 v)
{
    x *= v;
    y *= v;
    z *= v;
    w *= v;
    return *this;
}

Vector4 Vector4::operator/(f32 v) const
{
    if (v == 0) {
        return Vector4::ZERO;
    }
    f32 i = 1 / v;
    return Vector4(x * i, y * i, z * i, w * i);
}

Vector4& Vector4::operator/=(f32 v)
{
    if (v == 0) {
        return *this;
    }
    f32 i = (f32)(1) / v;
    x *= i;
    y *= i;
    z *= i;
    w *= i;
    return *this;
}

bool Vector4::operator==(const Vector4& other) const
{
    return Math::Equals(x, other.x) && Math::Equals(y, other.y) &&
        Math::Equals(z, other.z) && Math::Equals(w, other.w);
}

bool Vector4::operator!=(const Vector4& other) const
{
    return !(*this == other);
}

NS_CG_END