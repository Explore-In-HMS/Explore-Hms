/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description:
 * Author: xuhan
 * Create: 2020-3-1
 */
#ifndef MATERIAL_H
#define MATERIAL_H

#include "Rendering/RenderCommon.h"
#include "Resource/IResource.h"

NS_CG_BEGIN

class RenderPipeline;
class Shader;
class Texture;
class SubMesh;
class ResourceManager;
struct PipelineState;
struct TexInfo;

class CGKIT_EXPORT Material : public IResource {
    friend ResourceManager;
/**/
public:
    Material(const String& filePath);
    virtual ~Material();
    Material& operator=(const Material& material) = delete;

public:
    void Init();
    void SetSubMesh(const SubMesh* subMesh);
    void AttachShaderStage(ShaderStageType stage, const String& filePath);
    void SetTexture(TextureType type, const String& filePath);
    void SetSamplerParam(
        TextureType type,
        SamplerFilter filterMin,
        SamplerFilter filterMag,
        SamplerMipmapMode filterMipmap,
        SamplerAddress addressMode);

    bool HasTexture(const TextureType type) const;

    const RenderPipeline* GetRenderPipeline() const;
    const String& GetTexturePathByType(TextureType type) const;
    StringVector GetTexturePaths();
    const Texture* GetTexture(TextureType type) const;
    u32 GetTextureSet(TextureType type) const;
    u32 GetTextureBinding(TextureType type) const;

    void SetCullMode(CullMode cullMode);
    CullMode GetCullMode() const;

    void SetFillMode(FillMode fillMode);
    FillMode GetFillMode() const;

    void SetFrontFace(FrontFace frontFace);
    FrontFace GetFrontFace() const;

    void SetDepthTestEnable(bool depthTestEnable);
    bool GetDepthTestEnable() const;

    void SetDepthWriteEnable(bool depthWriteEnable);
    bool GetDepthWriteEnable() const;

    void SetDepthCompareOp(CompareOperation op);
    CompareOperation GetDepthCompareOp() const;

    void SetBlendingEnable(u32 index, bool bEnable);
    bool GetBlendingEnable(u32 index) const;

    void SetSrcColorBlend(u32 index, BlendFactor srcColorBlend);
    BlendFactor GetSrcColorBlend(u32 index) const;

    void SetDstColorBlend(u32 index, BlendFactor dstColorBlend);
    BlendFactor GetDstColorBlend(u32 index) const;

    void SetColorBlendOperation(u32 index, BlendOperation colorBlendOp);
    BlendOperation GetColorBlendOperation(u32 index) const;

    void SetSrcAlphaBlend(u32 index, BlendFactor srcAlphaBlend);
    BlendFactor GetSrcAlphaBlend(u32 index) const;

    void SetDstAlphaBlend(u32 index, BlendFactor dstAlphaBlend);
    BlendFactor GetDstAlphaBlend(u32 index) const;

    void SetAlphaBlendOperation(u32 index, BlendOperation alphaBlendOp);
    BlendOperation GetAlphaBlendOperation(u32 index) const;

    void Create();

    virtual bool Resume() override;
    virtual void Pause() override;

private:
    Material(const Material& rhs);
    void Destroy();

    static TextureType TextureTypeFromString(const String& type);

    void SetTextureSlot(TextureType type, Texture* texture);
    void SetTextureSlot(TextureType type, u32 set, u32 binding);
    void AddTextureInfo(TextureType type, TexInfo* texture);
    void DeleteTexture(Texture* texture);
    bool HasTexture(const String& path) const;
    void GenerateSetBinding();

private:
    using TexInfoMap = std::map<TextureType, TexInfo*>;
    using TexInfoMapIt = TexInfoMap::iterator;

    TexInfoMap m_textures;
    PipelineState* m_state = nullptr;
    RenderPipeline* m_pipeline = nullptr;
};

NS_CG_END

#endif