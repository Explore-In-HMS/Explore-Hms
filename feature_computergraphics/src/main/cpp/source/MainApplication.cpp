/*
 * Copyright (c) Hisilicon Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: A sample that illustrates the rendering of a demo scene.
 */

#include "MainApplication.h"

MainApplication::MainApplication() {}

MainApplication::~MainApplication() {}

void MainApplication::Start(void *param)
{
    BaseApplication::Start(param);
}

void MainApplication::Initialize(void *winHandle, u32 width, u32 height)
{
    BaseApplication::Initialize(winHandle, width, height);
}

void MainApplication::UnInitialize()
{
    gSceneManager.DeleteObject(m_sceneObject);
    BaseApplication::UnInitialize();
}

void MainApplication::InitScene()
{
    LOGINFO("MainApplication InitScene.");
    BaseApplication::InitScene();
    // add camera
    LOGINFO("Enter init main camera.");
    SceneObject *cameraObj = CG_NEW SceneObject(nullptr);
    if (cameraObj == nullptr) {
        LOGERROR("Failed to create camera object.");
        return;
    }
    Camera *mainCamera = cameraObj->AddComponent<Camera>();
    if (mainCamera == nullptr) {
        CG_SAFE_DELETE(cameraObj);
        LOGERROR("Failed to create main camera.");
        return;
    }
    const f32 FOV = 60.f;
    const f32 NEAR = 0.1f;
    const f32 FAR = 500.0f;
    const Vector3 EYE_POSITION(0.0f, 0.0f, 0.0f);
    cameraObj->SetPosition(EYE_POSITION);
    mainCamera->SetProjectionType(ProjectionType::PROJECTION_TYPE_PERSPECTIVE);
    mainCamera->SetProjection(FOV, gCGKitInterface.GetAspectRadio(), NEAR, FAR);
    mainCamera->SetViewport(0, 0, gCGKitInterface.GetScreenWidth(), gCGKitInterface.GetScreenHeight());
    gSceneManager.SetMainCamera(mainCamera);
    LOGINFO("Left init main camera.");

    // Load default model
    String modelName = "models/black_smith/black_smith.obj";
    Model *model = static_cast<Model *>(gResourceManager.Get(modelName));

    // Add to scene
    MeshRenderer *meshRenderer = nullptr;
    SceneObject *object = gSceneManager.CreateSceneObject();
    if (object != nullptr) {
        meshRenderer = object->AddComponent<MeshRenderer>();
        if (meshRenderer != nullptr && model != nullptr && model->GetMesh() != nullptr) {
            meshRenderer->SetMesh(model->GetMesh());
        } else {
            LOGERROR("Failed to add mesh renderer.");
        }
    } else {
        LOGERROR("Failed to create scene object.");
    }
    if (model != nullptr) {
        const Mesh *mesh = model->GetMesh();
        if (mesh != nullptr) {
            LOGINFO("Model submesh count %d.", mesh->GetSubMeshCount());
            LOGINFO("Model vertex count %d.", mesh->GetVertexCount());

            // load Texture
            String texAlbedo = "models/black_smith/texture/black_smith.png";
            //String texNormal = "models/Avatar/Normal_01.png";
            //String texPbr = "models/Avatar/Pbr_01.png";
            String texEmissive = "shaders/pbr_brdf.png";
            u32 subMeshCnt = mesh->GetSubMeshCount();
            for (u32 i = 0; i < subMeshCnt; ++i) {
                SubMesh *subMesh = mesh->GetSubMesh(i);
                if (subMesh == nullptr) {
                    LOGERROR("Failed to get submesh.");
                    continue;
                }
                Material *material =
                    dynamic_cast<Material *>(gResourceManager.Get(ResourceType::RESOURCE_TYPE_MATERIAL));
                if (material == nullptr) {
                    LOGERROR("Failed to create new material.");
                    return;
                }
                material->Init();
                material->SetSubMesh(subMesh);
                material->SetTexture(TextureType::TEXTURE_TYPE_ALBEDO, texAlbedo);
                material->SetSamplerParam(TextureType::TEXTURE_TYPE_ALBEDO,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_MIPMAP_BILINEAR,
                                          SAMPLER_ADDRESS_CLAMP);
                /*material->SetTexture(TextureType::TEXTURE_TYPE_NORMAL, texNormal);
                material->SetSamplerParam(TextureType::TEXTURE_TYPE_NORMAL,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_MIPMAP_BILINEAR,
                                          SAMPLER_ADDRESS_CLAMP);
                material->SetTexture(TextureType::TEXTURE_TYPE_PBRTEXTURE, texPbr);
                material->SetSamplerParam(TextureType::TEXTURE_TYPE_PBRTEXTURE,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_MIPMAP_BILINEAR,
                                          SAMPLER_ADDRESS_CLAMP);*/
                material->SetTexture(TextureType::TEXTURE_TYPE_EMISSION, texEmissive);
                material->SetSamplerParam(TextureType::TEXTURE_TYPE_EMISSION,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_MIPMAP_BILINEAR,
                                          SAMPLER_ADDRESS_CLAMP);
                material->SetTexture(TextureType::TEXTURE_TYPE_ENVIRONMENTMAP, m_envMap);
                material->SetSamplerParam(TextureType::TEXTURE_TYPE_ENVIRONMENTMAP,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_FILTER_BILINEAR,
                                          SAMPLER_MIPMAP_BILINEAR,
                                          SAMPLER_ADDRESS_CLAMP);
                material->AttachShaderStage(ShaderStageType::SHADER_STAGE_TYPE_VERTEX, "shaders/pbr_vert.spv");
                material->AttachShaderStage(ShaderStageType::SHADER_STAGE_TYPE_FRAGMENT, "shaders/pbr_frag.spv");
                material->SetCullMode(CULL_MODE_NONE);
                material->SetDepthTestEnable(true);
                material->SetDepthWriteEnable(true);
                material->Create();
                meshRenderer->SetMaterial(i, material);
            }
        } else {
            LOGERROR("Failed to get mesh.");
        }
    } else {
        LOGERROR("Failed to load model.");
    }

    m_sceneObject = object;
    if (m_sceneObject != nullptr) {
        m_sceneObject->SetPosition(SCENE_OBJECT_POSITION);
        m_sceneObject->SetScale(SCENE_OBJECT_SCALE);
    }
    m_objectRotation = Math::PI;

    // create sky box
    SceneObject *skyboxObj = CreateSkybox();
    if (skyboxObj != nullptr) {
        skyboxObj->SetScale(Vector3(100.f, 100.f, 100.f));
    }

    // add light
    LOGINFO("Enter init light.");
    SceneObject *lightObject = CG_NEW SceneObject(nullptr);
    if (lightObject != nullptr) {
        Light *lightCom = lightObject->AddComponent<Light>();
        if (lightCom != nullptr) {
            lightCom->SetColor(Vector3::ONE);
            const Vector3 DIRECTION_LIGHT_DIR(0.1f, 0.2f, 1.0f);
            lightCom->SetDirection(DIRECTION_LIGHT_DIR);
            lightCom->SetLightType(LIGHT_TYPE_DIRECTIONAL);
            LOGINFO("Left init light.");
        } else {
            LOGERROR("Failed to add component light.");
        }
    } else {
        LOG_ALLOC_ERROR("New light object failed.");
    }

    SceneObject *pointLightObject = CG_NEW SceneObject(nullptr);
    if (pointLightObject != nullptr) {
        m_pointLightObject = pointLightObject;
        Light *lightCom = pointLightObject->AddComponent<Light>();
        if (lightCom != nullptr) {
            const Vector3 POINT_LIGHT_COLOR(0.0, 10000.0f, 10000.0f);
            lightCom->SetColor(POINT_LIGHT_COLOR);
            lightCom->SetLightType(LIGHT_TYPE_POINT);
        } else {
            LOGERROR("Failed to add component light.");
        }
    } else {
        LOG_ALLOC_ERROR("New light object failed.");
    }
}

void MainApplication::Update(float deltaTime)
{
    LOGINFO("Update %f.", deltaTime);
    m_deltaTime = deltaTime;
    m_deltaAccumulate += m_deltaTime;

    if (m_sceneObject != nullptr) {
        m_sceneObject->SetRotation(Vector3(0.0, m_objectRotation, 0.0));
        m_sceneObject->SetScale(SCENE_OBJECT_SCALE * m_objectScale * 2);
    }

    const float POINT_HZ_X = 0.2f;
    const float POINT_HZ_Y = 0.5f;
    const float POINT_LIGHT_CIRCLE = 50.f;
    if (m_pointLightObject) {
        m_pointLightObject->SetPosition(
            Vector3(sin(m_deltaAccumulate * POINT_HZ_X) * POINT_LIGHT_CIRCLE,
                    sin(m_deltaAccumulate * POINT_HZ_Y) * POINT_LIGHT_CIRCLE + POINT_LIGHT_CIRCLE,
                    cos(m_deltaAccumulate * POINT_HZ_X) * POINT_LIGHT_CIRCLE));
    }
    BaseApplication::Update(deltaTime);
}

void MainApplication::Resize(u32 width, u32 height)
{
    BaseApplication::Resize(width, height);
}

void MainApplication::ProcessInputEvent(const InputEvent *inputEvent)
{
    BaseApplication::ProcessInputEvent(inputEvent);
    LOGINFO("MainApplication ProcessInputEvent.");
    EventSource source = inputEvent->GetSource();
    if (source == EVENT_SOURCE_TOUCHSCREEN) {
        const TouchInputEvent *touchEvent = reinterpret_cast<const TouchInputEvent *>(inputEvent);
        if (touchEvent == nullptr) {
            LOGERROR("Failed to get touch event.");
            return;
        }
        if (touchEvent->GetAction() == TOUCH_ACTION_DOWN) {
            LOGINFO("Action move start.");
            m_touchBegin = true;
        } else if (touchEvent->GetAction() == TOUCH_ACTION_MOVE) {
            float touchPosDeltaX = touchEvent->GetPosX(touchEvent->GetTouchIndex()) - m_touchPosX;
            float touchPosDeltaY = touchEvent->GetPosY(touchEvent->GetTouchIndex()) - m_touchPosY;
            if (m_touchBegin) {
                if (fabs(touchPosDeltaX) > 2.f) {
                    if (touchPosDeltaX > 0.f) {
                        m_objectRotation -= 2.f * m_deltaTime;
                    } else {
                        m_objectRotation += 2.f * m_deltaTime;
                    }
                    LOGINFO("Set rotation start.");
                }
                if (fabs(touchPosDeltaY) > 3.f) {
                    if (touchPosDeltaY > 0.f) {
                        m_objectScale -= 0.25f * m_deltaTime;
                    } else {
                        m_objectScale += 0.25f * m_deltaTime;
                    }
                    m_objectScale = min(1.25f, max(0.75f, m_objectScale));
                    LOGINFO("Set scale start.");
                }
            }
        } else if (touchEvent->GetAction() == TOUCH_ACTION_UP) {
            LOGINFO("Action up.");
            m_touchBegin = false;
        } else if (touchEvent->GetAction() == TOUCH_ACTION_CANCEL) {
            LOGINFO("Action cancel.");
            m_touchBegin = false;
        }
        m_touchPosX = touchEvent->GetPosX(touchEvent->GetTouchIndex());
        m_touchPosY = touchEvent->GetPosY(touchEvent->GetTouchIndex());
    }
}

SceneObject *MainApplication::CreateSkybox()
{
    String modelName = "models/cube/cube.obj";
    Model *model = static_cast<Model *>(gResourceManager.Get(modelName));
    const Mesh *mesh = model->GetMesh();
    // load Texture
    u32 subMeshCnt = mesh->GetSubMeshCount();
    // Add to scene
    SceneObject *sceneObj = gSceneManager.CreateSceneObject();
    MeshRenderer *meshRenderer = sceneObj->AddComponent<MeshRenderer>();
    meshRenderer->SetMesh(model->GetMesh());

    for (u32 i = 0; i < subMeshCnt; ++i) {
        SubMesh *subMesh = mesh->GetSubMesh(i);
        // add Material
        Material *material = dynamic_cast<Material *>(gResourceManager.Get(ResourceType::RESOURCE_TYPE_MATERIAL));
        material->Init();
        material->SetSubMesh(subMesh);
        material->SetTexture(TextureType::TEXTURE_TYPE_ENVIRONMENTMAP, m_envMap);
        material->SetSamplerParam(TextureType::TEXTURE_TYPE_ENVIRONMENTMAP,
                                  SAMPLER_FILTER_BILINEAR,
                                  SAMPLER_FILTER_BILINEAR,
                                  SAMPLER_MIPMAP_BILINEAR,
                                  SAMPLER_ADDRESS_CLAMP);
        material->AttachShaderStage(ShaderStageType::SHADER_STAGE_TYPE_VERTEX, "shaders/sky_vert.spv");
        material->AttachShaderStage(ShaderStageType::SHADER_STAGE_TYPE_FRAGMENT, "shaders/sky_frag.spv");
        material->SetCullMode(CULL_MODE_NONE);
        material->SetDepthTestEnable(true);
        material->SetDepthWriteEnable(true);
        material->Create();
        meshRenderer->SetMaterial(i, material);
    }

    sceneObj->SetScale(Vector3(1.f, 1.f, 1.f));
    sceneObj->SetPosition(Vector3(0.0f, 0.0f, 0.0f));
    sceneObj->SetRotation(Vector3(0.0f, 0.0, 0.0));
    return sceneObj;
}

BaseApplication *CreateMainApplication()
{
    return new MainApplication();
}
