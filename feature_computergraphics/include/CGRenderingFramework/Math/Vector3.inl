/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Define Vector2 type for point and 2D vector operations.
 */

NS_CG_BEGIN

Vector3 Vector3::operator-() const
{
    return Vector3(-x, -y, -z);
}

Vector3& Vector3::operator=(const Vector3& other)
{
    x = other.x;
    y = other.y;
    z = other.z;
    return *this;
}

Vector3 Vector3::operator+(const Vector3& other) const
{
    return Vector3(x + other.x, y + other.y, z + other.z);
}

Vector3& Vector3::operator+=(const Vector3& other)
{
    x += other.x;
    y += other.y;
    z += other.z;
    return *this;
}

Vector3 Vector3::operator-(const Vector3& other) const
{
    return Vector3(x - other.x, y - other.y, z - other.z);
}

Vector3& Vector3::operator-=(const Vector3& other)
{
    x -= other.x;
    y -= other.y;
    z -= other.z;
    return *this;
}

Vector3 Vector3::operator*(f32 v) const
{
    return Vector3(x * v, y * v, z * v);
}

Vector3& Vector3::operator*=(f32 v)
{
    x *= v;
    y *= v;
    z *= v;
    return *this;
}

Vector3 Vector3::operator/(f32 v) const
{
    if (v == 0) {
        return *this;
    }
    f32 i = 1.f / v;
    return Vector3(x * i, y * i, z * i);
}

Vector3& Vector3::operator/=(f32 v)
{
    if (v == 0) {
        return *this;
    }
    f32 i = 1.f / v;
    x *= i;
    y *= i;
    z *= i;
    return *this;
}

bool Vector3::operator==(const Vector3& other) const
{
    return Math::Equals(x, other.x) && Math::Equals(y, other.y) && Math::Equals(z, other.z);
}

bool Vector3::operator!=(const Vector3& other) const
{
    return !(*this == other);
}

NS_CG_END