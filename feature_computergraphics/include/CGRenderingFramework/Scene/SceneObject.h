/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Scene object utils.
 */

#ifndef SCENE_OBJECT_H
#define SCENE_OBJECT_H

#include "Math/Vector3.h"
#include "Scene/IComponent.h"
#include "Log/Log.h"

NS_CG_BEGIN

class Transform;

using SceneObjectList = list<SceneObject*>;
using SceneObjectListIt = list<SceneObject*>::iterator;
using SceneObjectListCIt = list<SceneObject*>::const_iterator;

class CGKIT_EXPORT SceneObject {
    friend class SceneManager;

public:
    SceneObject(SceneObject* parent);
    virtual ~SceneObject();

    virtual void PreUpdate(f32 deltaTime);
    virtual void Update(f32 deltaTime);
    virtual void PostUpdate(f32 deltaTime);

    bool RemoveComponent(IComponent* component);

    template <typename T> T* AddComponent()
    {
        T* component = CG_NEW T(this);
        if (component == nullptr){
            LOG_ALLOC_NULL("New typename T failed.");
            return nullptr;
        }
        component->Start();
        m_components.push_back(component);
        return component;
    }

    template <typename T> T* GetComponent() const
    {
        for (IComponent* component : m_components) {
            T* t = dynamic_cast<T*>(component);
            if (t != nullptr) {
                return t;
            }
        }
        return nullptr;
    }

    template <typename T> vector<T*> GetComponents()
    {
        vector<T*> ret;
        for (IComponent* component : m_components) {
            T* t = dynamic_cast<T*>(component);
            if (t != nullptr) {
                ret.push_back(t);
            }
        }
        return ret;
    }

    const SceneObject* GetParent() const;
    void SetParent(SceneObject* newParent);
    bool HasParent() const;
    bool IsParentOf(const SceneObject* object) const;
 
    const list<SceneObject*>& GetChildren() const;
    u32 GetChildrenCount() const;
  
    void AddChild(SceneObject* child);
    bool RemoveChild(SceneObject* child);
    bool IsChildOf(const SceneObject* object) const;
   
    u32 GetID() const {
        return m_id;
    }
    void SetVisible(bool visible);
    bool IsVisible() const {
        return m_visible;
    }

    void SetPosition(const Vector3& position);
    const Vector3& GetPosition() const;
    void SetScale(const Vector3& scale);
    const Vector3& GetScale() const;
    void SetRotation(const Vector3& rotation);
    const Vector3& GetRotation() const;

    const Transform* GetTransform() const;
    const Transform* GetParentTransform() const;

private:
    void UpdateChild(SceneObject* child);

    static u32 GenerateId()
    {
        static u32 g_id = 0;
        return ++g_id;
    }

private:
    Transform* m_transform;
    SceneObject* m_parent;
    u32 m_id;
    SceneObjectList m_children;
    ComponentList m_components;
    bool m_visible;
};

NS_CG_END

#endif