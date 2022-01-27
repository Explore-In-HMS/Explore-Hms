/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Camera utils.
 */

#ifndef CAMERA_H
#define CAMERA_H

#include "Math/Vector2.h"
#include "Math/Vector3.h"
#include "Math/Matrix4.h"
#include "Scene/IComponent.h"
#include "Rendering/RenderCommon.h"

NS_CG_BEGIN

class Frustum;

class CGKIT_EXPORT Camera : public IComponent {
    friend class SceneObject;

protected:
    Camera(SceneObject* sceneObject = nullptr);
    virtual ~Camera();
    Camera(const Camera& camera) = delete;
    Camera& operator=(const Camera& camera) = delete;

public:
    virtual void Start() override;
    virtual void Update(f32 deltaTime) override;

    void SetProjectionType(ProjectionType projectionType);
    void SetProjection(f32 fov, f32 aspectRatio, f32 zNear, f32 zFar);
    void SetFOV(f32 fov);
    void SetViewport(f32 x, f32 y, f32 width, f32 height, f32 minDepth = 0.0f, f32 maxDepth = 1.0f);
    void SetZNear(f32 zNear);
    void SetZFar(f32 zFar);

    void SetOrthogonal(f32 left, f32 right, f32 bottom, f32 top, f32 near, f32 far);

    void SetEyePos(const Vector3& eyePos);
    void SetTarget(const Vector3& targetPos);

    void SetPreRotation(const Matrix4& preRotation);
 
    const Frustum* GetFrustum() const;
    const Matrix4& GetProjectionMatrix() const;
    const Matrix4& GetViewMatrix() const;

    ProjectionType GetProjectionType() const
    {
        return m_projectionType;
    }
    f32 GetAspectRatio() const;
    f32 GetFOV() const
    {
        return m_fov;
    }
    f32 GetZNear() const
    {
        return m_zNear;
    }
    f32 GetZFar() const
    {
        return m_zFar;
    }

    const Vector3& GetEyePos() const;
    const Vector3& GetTarget() const;
    const Vector3& GetLookDir() const;
    const Vector3& GetUpDir() const;
    const Vector3& GetRightDir() const;

private:
    void Destroy();
    void EnsureFrustumUpdate() const;
    void EnsureProjectionMatrixUpdate() const;
    void EnsureViewMatrixUpdate() const;
    void EnsureViewportUpdate() const;

    void InvalidateFrustum() const;
    void InvalidateProjectionMatrix() const;
    void InvalidateViewMatrix() const;
    void InvalidateViewport() const;

    void UpdateFrustum() const;
    void UpdateProjectionMatrix() const;
    void UpdateViewMatrix() const;
    void UpdateViewport() const;
    void UpdataCameraObject() const;

    ProjectionType m_projectionType;

    // for perspective projection.
    f32 m_fov;
    mutable f32 m_aspectRatio;

    // for orthogonal projection.
    f32 m_left;
    f32 m_right;
    f32 m_bottom;
    f32 m_top;

    f32 m_zNear;
    f32 m_zFar;
    Frustum* m_frustum;

    mutable Matrix4 m_projectionMatrix;
    mutable Matrix4 m_viewMatrix;
    mutable Matrix4 m_preRotation;

    mutable Vector3 m_eyePos;
    mutable Vector3 m_target;
    mutable Vector3 m_lookDir;
    mutable Vector3 m_upDir;
    mutable Vector3 m_rightDir;
    mutable Vector3 m_rotation;
    mutable f32 m_curZoom;

    mutable bool m_frustumUpdated;
    mutable bool m_projectionMatrixUpdated;
    mutable bool m_viewMatrixUpdated;
    mutable bool m_viewportUpdated;
};

NS_CG_END

#endif