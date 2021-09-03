/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Define Vector2 type for point and 2D vector operations.
 */

NS_CG_BEGIN

Vector2 Vector2::operator-() const
{
    return Vector2(-x, -y);
}

Vector2& Vector2::operator=(const Vector2& other)
{
    x = other.x;
    y = other.y;
    return *this;
}

Vector2 Vector2::operator+(const Vector2& other) const
{
    return Vector2(x + other.x, y + other.y);
}

Vector2& Vector2::operator+=(const Vector2& other)
{
    x += other.x;
    y += other.y;
    return *this;
}

Vector2 Vector2::operator-(const Vector2& other) const
{
    return Vector2(x - other.x, y - other.y);
}

Vector2& Vector2::operator-=(const Vector2& other)
{
    x -= other.x;
    y -= other.y;
    return *this;
}

Vector2 Vector2::operator*(f32 v) const
{
    return Vector2(x * v, y * v);
}

Vector2& Vector2::operator*=(f32 v)
{
    x *= v;
    y *= v;
    return *this;
}

Vector2 Vector2::operator/(f32 v) const
{
    if (v == 0) {
        return Vector2::ZERO;
    }
    return Vector2(x / v, y / v);
}

Vector2& Vector2::operator/=(f32 v)
{
    if (v == 0) {
        return *this;
    }
    x /= v;
    y /= v;
    return *this;
}

bool Vector2::operator==(const Vector2& other) const
{
    return Math::Equals(x, other.x) && Math::Equals(y, other.y);
}

bool Vector2::operator!=(const Vector2& other) const
{
    return !(*this == other);
}

NS_CG_END