/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: IResource utils.
 */

#ifndef RESOURCE_H
#define RESOURCE_H

#include "Core/Types.h"
#include "RefCount.h"

NS_CG_BEGIN

enum ResourceType {
    RESOURCE_TYPE_MODEL,
    RESOURCE_TYPE_TEXTURE,
    RESOURCE_TYPE_MATERIAL,
    RESOURCE_TYPE_MAX
};

enum LoadState {
    LOAD_STATE_IDLE,
    LOAD_STATE_STARTED,
    LOAD_STATE_COMPLETED,
    LOAD_STATE_FAILED,

    LOAD_STATE_MAX = LOAD_STATE_FAILED
};

class CGKIT_EXPORT IResource : public RefCount {
public:
    IResource(const String& filePath, ResourceType resourceType);
    virtual ~IResource();

    const String& GetFilePath() const;
    void SetFilePath(const String& filePath);

    u32 GetId() const;
    void SetId(u32 id);

    LoadState GetLoadState() const;
    void SetLoadState(LoadState loadState);

    ResourceType GetResourceType() const;

    // Re-Create gpu resoruce when the app come to forground
    virtual bool Resume() { return true; }

    // destory gpu resoruce when the app goes to background
    virtual void Pause() {}

private:
    void Destroy() {}
    static u32 GenerateId();

private:
    static u32 m_gId;
    String m_filePath;
    ResourceType m_resourceType;
    LoadState m_loadState;
    u32 m_id;
};

NS_CG_END

#endif
