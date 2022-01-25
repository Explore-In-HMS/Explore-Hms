/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Define Axis aligned bounding box.
 */

#ifndef AABB_H
#define AABB_H

#include "Math/Vector3.h"

NS_CG_BEGIN

enum CornerType {
    CORNER_TYPE_BACK_LEFT_BOTTOM = 0,
    CORNER_TYPE_BACK_LEFT_TOP = 1,
    CORNER_TYPE_FRONT_LEFT_BOTTOM = 2,
    CORNER_TYPE_FRONT_LEFT_TOP = 3,
    CORNER_TYPE_BACK_RIGHT_BOTTOM = 4,
    CORNER_TYPE_BACK_RIGHT_TOP = 5,
    CORNER_TYPE_FRONT_RIGHT_BOTTOM = 6,
    CORNER_TYPE_FRONT_RIGHT_TOP = 7,

    CORNER_TYPE_MAX,
};

class CGKIT_EXPORT AABB {
public:
    AABB();
    AABB(const AABB& other);
    ~AABB();

    AABB& operator=(const AABB& other);
    bool operator==(const AABB& other) const;
    bool operator!=(const AABB& other) const;

    Vector3 GetPositiveVertex(const Vector3& normal) const;
    Vector3 GetNegativeVertex(const Vector3& normal) const;

    const Vector3& GetMinimum() const;
    const Vector3& GetMaximum() const;

    void SetMinimum(const Vector3& vec);
    void SetMaximum(const Vector3& vec);
 
    const Vector3* GetCorners() const;

    Vector3 GetCenter() const;
    Vector3 GetSize() const;

    void AddInternalPoint(const Vector3& p);
    void AddInternalBox(const AABB& b);

    String ToString() const;

private:
    Vector3 m_minimum;
    Vector3 m_maximum;
    mutable Vector3* m_corners;
};

NS_CG_END

#endif