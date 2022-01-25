/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description:
 * Author: xuhan
 * Create: 2020-3-1
 */
#ifndef SCENE_MANAGER_H
#define SCENE_MANAGER_H

#include "Core/Singleton.h"
#include "Rendering/RenderCommon.h"
#include "Scene/SceneObject.h"
#include "Math/Matrix4.h"

NS_CG_BEGIN

class Camera;
class RenderPath;
class Renderable;
struct LightInfos;

class CGKIT_EXPORT SceneManager : public Singleton<SceneManager> {
    friend class Singleton<SceneManager>;

private:
    SceneManager();
    virtual ~SceneManager();

public:
    void Initialize();
    void Uninitialize();
    bool Resume();
    void Pause();
    void Update(f32 deltaTime);
    void Render();
    void Resize(s32 width, s32 height);

    SceneObject* CreateSceneObject(SceneObject* parent = nullptr);
    void DeleteObject(SceneObject* object);
    void AddSceneObject(SceneObject* object);
    void RemoveSceneObject(SceneObject* object);

    const SceneObject* GetRoot() const
    {
        return m_root;
    }
    const Camera* GetMainCamera() const
    {
        return m_camera;
    }
    void SetMainCamera(Camera* camera);

private:
    RenderPath* CreateRenderPath();
    void InitRoot();
    void InitLightsInfo();
    void UpdateCamera(f32 deltaTime);
    void UpdateLight();
    void CollectLightsInfo(const SceneObject* object, LightInfos* lightInfos);
    void UpdateSceneObjectRecursive(SceneObject* object, f32 deltaTime);
    void RenderSceneObjectRecursive(SceneObject* object);
    bool Cull(Renderable* render, const Matrix4& transMat);
    bool Resume(const SceneObject* object);
    void Pause(const SceneObject* object);

private:
    SceneObject* m_root = nullptr;
    SceneObjectList m_sceneObjects;
    Camera* m_camera = nullptr;
    RenderPath* m_renderPath = nullptr;
    LightInfos* m_globalLightsCpu = nullptr;
};

#define gSceneManager SceneManager::GetSingleton()

NS_CG_END

#endif
