/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description:
 * Author: xuhan
 * Create: 2020-3-1
 */
#ifndef MESH_RENDERER_H
#define MESH_RENDERER_H

#include "Scene/Component/Renderable.h"

NS_CG_BEGIN

class RenderData;

class CGKIT_EXPORT MeshRenderer : public Renderable {
    friend class SceneObject;

protected:
    MeshRenderer(SceneObject* pSceneObject = nullptr);
    virtual ~MeshRenderer();

public:
    virtual void Render(const Matrix4& transMat) override;

    // destory gpu resoruce when the app goes to background
    void Pause();

    // Re-Create gpu resoruce when the app come to forground
    bool Resume();

private:
    void Destroy();
    vector<RenderData*> m_renderdata;
};

NS_CG_END

#endif