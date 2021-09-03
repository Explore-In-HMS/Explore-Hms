/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Model.
 */

#ifndef MODEL_H
#define MODEL_H

#include "Resource/IResource.h"

NS_CG_BEGIN

class Mesh;

class CGKIT_EXPORT Model : public IResource {
public:
    Model(const String& filePath);
    Model& operator=(const Model& rhs) = delete;
    Model(const Model& rhs) = delete;

    virtual ~Model();

    virtual bool Resume() override;
    virtual void Pause() override;

    Mesh* Create();
    const Mesh* GetMesh() const;

private:
    void Destroy();

private:
    Mesh* m_mesh = nullptr;
};

NS_CG_END

#endif