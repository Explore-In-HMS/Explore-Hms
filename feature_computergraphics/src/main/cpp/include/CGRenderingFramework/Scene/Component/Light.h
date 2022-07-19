/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: xuhan
 * Create: 2020-3-1
 */

#ifndef LIGHT_H
#define LIGHT_H

#include "Math/Vector4.h"
#include "Math/Vector3.h"
#include "Rendering/RenderCommon.h"
#include "Scene/IComponent.h"

NS_CG_BEGIN

struct LightInfo;

class CGKIT_EXPORT Light : public IComponent {
    friend class SceneObject;

protected:
    Light(SceneObject* sceneObject = nullptr);
    virtual ~Light();
    Light& operator=(const Light& light) = delete;
    Light(const Light& light) = delete;

public:
    virtual void Start() override;
    virtual void Update(f32 deltaTime) override;

    const LightInfo* GetLightInfo() const;

    void SetLightType(LightType type);
    void SetColor(const Vector3& color);
    void SetIntensity(f32 intensity);
    void SetPositon(const Vector3& position);
    void SetDirection(const Vector3& direction);

    LightType GetLightType() const;
    Vector3 GetColor() const;
    f32 GetIntensity() const;
    Vector3 GetPosition() const;
    Vector3 GetDirection() const;

private:
    void Destroy();
    void UpdataLightObject();

private:
    LightInfo* m_lightInfo = nullptr;
};

NS_CG_END

#endif
