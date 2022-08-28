/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Plane utils.
 */

#ifndef PLANE_H
#define PLANE_H

#include "Math/Vector3.h"

NS_CG_BEGIN

class Plane {
public:
    Plane() = default;
    ~Plane() = default;

    Plane& operator=(const Plane& other);
    bool operator==(const Plane& plane) const;
    bool operator!=(const Plane& plane) const;

    void Set(const Vector3& normal, f32 distance);
    void Set(const Vector3& normal, const Vector3& point);
    void Set(const Vector3& point1, const Vector3& point2, const Vector3& point3);
 
    String ToString() const;

public:
    Vector3 m_normal = Vector3::ZERO;
    f32 m_distance = 0.0f;
};

NS_CG_END

#endif