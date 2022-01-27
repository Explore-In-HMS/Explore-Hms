/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: IComponent utils.
 */

#ifndef ICOMPONENT_H
#define ICOMPONENT_H

#include "Core/Types.h"

NS_CG_BEGIN

class SceneObject;

class CGKIT_EXPORT IComponent {
    friend class SceneObject;

protected:
    IComponent(SceneObject* object);
    virtual ~IComponent();

public:
    virtual void Start();
    virtual void Update(f32 deltaTime);

private:
    void Destroy();

protected:
    SceneObject* m_sceneObject;
};

using ComponentList = list<IComponent*>;
using ComponentListIt = list<IComponent*>::iterator;

NS_CG_END

#endif