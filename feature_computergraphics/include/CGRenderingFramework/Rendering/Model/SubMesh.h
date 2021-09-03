/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: SubMesh.
 */

#ifndef SUB_MESH_H
#define SUB_MESH_H

#include "Math/Vector2.h"
#include "Math/AABB.h"
#include "Rendering/RenderCommon.h"

NS_CG_BEGIN

class AABB;
class Mesh;
class VertexBuffer;
class IndexBuffer;
class Material;

class CGKIT_EXPORT SubMesh {
public:
    SubMesh();
    ~SubMesh();
    SubMesh(const SubMesh& rhs) = delete;
    SubMesh& operator=(const SubMesh& rhs) = delete;

    void SetVertexBuffer(const VertexBuffer* vertexBuffer);
    const VertexBuffer* GetVertexBuffer() const;

    void SetIndexBuffer(const IndexBuffer* indexBuffer);
    const IndexBuffer* GetIndexBuffer() const;

    void SetPrimitiveMode(PrimitiveMode mode);
    PrimitiveMode GetPrimitiveMode() const;

    u32 GetTriangleCount() const;
    u32 GetVertexCount() const;

    void SetAABB(const AABB& aabb);
    const AABB& GetAABB() const;

    bool Resume();
    void Pause();

private:
    void Destroy();

private:
    IndexBuffer* m_indexBuffer = nullptr;
    VertexBuffer* m_vertexBuffer = nullptr;
    PrimitiveMode m_primitiveMode = PrimitiveMode::PRIMITIVE_MODE_TRIANGLE_LIST;
    AABB m_aabb;
};

NS_CG_END

#endif