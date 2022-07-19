/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Transform utils.
 */

#ifndef TRANSFORM_H
#define TRANSFORM_H

#include "Math/Matrix4.h"
#include "Scene/IComponent.h"

NS_CG_BEGIN

class SceneObject;

class CGKIT_EXPORT Transform : public IComponent {
    friend class SceneObject;

protected:
    Transform(SceneObject* sceneObject = nullptr);
    virtual ~Transform();

public:
    void Translate(const Vector3& translate);
    void Rotate(f32 angle, const Vector3& axis);
    void Scale(const Vector3& scale);

    void SetLocalPosition(const Vector3& position);
    void SetLocalRotation(const Vector3& rotation);
    void SetLocalScale(const Vector3& scale);

    void SetWorldPosition(const Vector3& position);
    void SetWorldRotation(const Vector3& rotation);
    void SetWorldScale(const Vector3& scale);

    const Vector3& GetLocalPosition() const;
    const Vector3& GetLocalRotation() const;
    const Vector3& GetLocalScale() const;

    const Vector3& GetWorldPosition() const;
    const Vector3& GetWorldRotation() const;
    const Vector3& GetWorldScale() const;

    const Matrix4& GetLocalToWorldMatrix() const;

    bool IsTransChanged() const;
    void ClearTransFlags();

private:
    bool IsPositionChanged() const;
    bool IsRotationChanged() const;
    bool IsScaleChanged() const;

    void SetPositionChanged();
    void SetRotationChanged();
    void SetScaleChanged();

    void UpdateAll();
    void UpdateTransform();

    const Transform* GetParentTransform() const;

private:
    Vector3 m_localPosition;
    Vector3 m_localRotation;
    Vector3 m_localScale;

    Vector3 m_worldPosition;
    Vector3 m_worldRotation;
    Vector3 m_worldScale;

    Matrix4 m_localToWorldMatrix;
    u32 m_transChangeFlag;
};

NS_CG_END

#endif