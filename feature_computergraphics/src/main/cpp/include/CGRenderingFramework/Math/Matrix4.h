/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Define Matrix4 type for 3D space translations, scale and rotation operations.
 */

#ifndef MATRIX4_H
#define MATRIX4_H

#include "Math/Vector3.h"
#include "Math/Vector4.h"
#include "Math/Quaternion.h"

NS_CG_BEGIN

// 4x4 matrix. Mostly used as transformation matrix for 3d calculations.
// The matrix is a left hand matrix, row major with translations in the 4th row.
// v * M1 * M2 * M3

static const u32 MATRIX4_ROW_SIZE = 4;
static const u32 MATRIX4_COLUMN_SIZE = 4;
static const u32 MATRIX4_SIZE = MATRIX4_ROW_SIZE * MATRIX4_COLUMN_SIZE;

class Matrix4 {
public:
    Matrix4();
    ~Matrix4();

    Matrix4(f32 m00, f32 m01, f32 m02, f32 m03,
        f32 m10, f32 m11, f32 m12, f32 m13,
        f32 m20, f32 m21, f32 m22, f32 m23,
        f32 m30, f32 m31, f32 m32, f32 m33);

    f32& operator()(u32 row, u32 col);
    const f32& operator()(u32 row, u32 col) const;
    f32& operator[](u32 index);
    const f32& operator[](u32 index) const;

    Matrix4& operator=(const Matrix4& other);
    Matrix4& operator=(const f32& scalar);

    Matrix4 operator+(const Matrix4& other) const;
    Matrix4& operator+=(const Matrix4& other);

    Matrix4 operator-(const Matrix4& other) const;
    Matrix4& operator-=(const Matrix4& other);

    Matrix4 operator*(const Matrix4& other) const;
    Matrix4& operator*=(const Matrix4& other);

    Vector3 operator*(const Vector3& v) const;
    Vector4 operator*(const Vector4& v) const;

    Matrix4 operator*(const f32& scalar) const;
    Matrix4& operator*=(const f32& scalar);

    bool operator==(const Matrix4& other) const;
    bool operator!=(const Matrix4& other) const;

    Matrix4& MakeIdentity();
    f32 Determinant() const;

    Matrix4 Inversed() const;
    Matrix4& Inverse();
    Matrix4 Transposed() const;
    Matrix4& Transpose();

    Matrix4& SetTrans(const Vector3& v);
    Vector3 GetTrans() const;
    Matrix4& SetScale(const Vector3& scale);
    Vector3 GetScale() const;

    Vector3 GetUpVector() const;
    Vector3 GetRightVector() const;
    Vector3 GetForwardVector() const;

    Matrix4 GetRotationMatrix(const Quaternion& rot);
    Vector3 GetRotation() const;
    Matrix4& SetRotation(const Vector3& rotation);
    Matrix4& MakeRotationAxisAngle(f32 radianAngle, const Vector3& axis);
    Matrix4& Rotate(const Vector3& axis, f32 radianAngle);

    void MakeTransform(const Vector3& position, const Vector3& scale, const Vector3& rotate);
    void MakeTransform(const Vector3& position, const Vector3& scale, const Quaternion& orientation);
    void Decomposition(Vector3* position, Vector3* scale, Vector3* rotate) const;
    void Decomposition(Vector3* position, Vector3* scale, Quaternion* rotation) const;

    void Rotate(Vector3& vect) const;
    void Translate(Vector3& vect) const;
    void Transform(Vector3& vect) const;

    Vector3 Transform(const Vector3& in) const;
    Vector4 Transform(const Vector4& in) const;

    String ToString() const;

public:
    union {
        f32 m[MATRIX4_ROW_SIZE * MATRIX4_COLUMN_SIZE];
        f32 M[MATRIX4_ROW_SIZE][MATRIX4_COLUMN_SIZE];
    };

    static const Matrix4 ZERO;
    static const Matrix4 IDENTITY;
};

NS_CG_END

#endif