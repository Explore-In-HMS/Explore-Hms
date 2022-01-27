/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Resource manager utils.
 */

#ifndef RESOURCE_MANAGER_H
#define RESOURCE_MANAGER_H

#include "Core/Singleton.h"
#include "Resource/IResource.h"

NS_CG_BEGIN

class CGKIT_EXPORT ResourceManager : public Singleton<ResourceManager> {
    friend class Singleton<ResourceManager>;

private:
    ResourceManager();
    virtual ~ResourceManager();

public:
    /* Resource create from file, resource type can recgonized by file type */
    IResource* Get(const String& filePath);

    /* Material object donot create from file, it cannot be reuse.
     * when we Get Material object, we create a new object and return it.
     * the name of Material object is auto generated, the input name is unuesd.
     */
    IResource* Get(ResourceType type, const String& name = "");

    /* user who called Get(xxx) should call Delete(xxx) after using the resource */
    void Delete(const String& filePath);
    void Delete(IResource* resource);
    void DeleteAll();

    bool Resume();
    void Pause();

    void Update(f32 deltaTime);

private:
    IResource* Add(const String& filePath);
    IResource* FindByName(const String& resourceName);
    IResource* FindByName(const String& resourceName, ResourceType type);
    void DeleteAll(ResourceType type);
    void Destroy();
    void Destroy(ResourceType type);

private:
    std::unordered_map<ResourceType, std::unordered_map<String, IResource*>> m_resourceMap;
    f32 m_time = 0.0f;
};

#define gResourceManager ResourceManager::GetSingleton()

NS_CG_END

#endif
