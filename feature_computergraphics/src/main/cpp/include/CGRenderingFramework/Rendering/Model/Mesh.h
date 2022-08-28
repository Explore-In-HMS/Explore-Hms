/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Mesh.
 */

#ifndef MESH_H
#define MESH_H

#include "Rendering/Model/SubMesh.h"

NS_CG_BEGIN

class Material;

class CGKIT_EXPORT Mesh {
public:
    Mesh();
    ~Mesh();

    SubMesh* AddSubMesh(Material* material);
    SubMesh* GetSubMesh(u32 index) const;
    u32 GetSubMeshCount() const;
    void RemoveSubMesh(u32 index);
    bool HasSubMesh(u32 index) const;

    u32 GetTriangleCount() const;
    u32 GetVertexCount() const;

    const AABB& GetAABB();
    std::vector<SubMesh*> m_subMeshes;

    bool Resume();
    void Pause();

private:
    void Destroy();

private:
    AABB m_aabb;
};

NS_CG_END

#endif