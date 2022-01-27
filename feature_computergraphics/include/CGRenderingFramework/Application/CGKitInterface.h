/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description:
 * Author: xuhan
 *
 * Create: 2020-4-28
 */

#ifndef CG_KIT_INTERFACE_H
#define CG_KIT_INTERFACE_H

#include "Core/Types.h"

NS_CG_BEGIN

class BaseApplication;
class SceneManager;

class CGKIT_EXPORT CGKitInterface {
public:
    void SetApplication(BaseApplication* application);
    const BaseApplication* GetApplication() const;

    f32 GetAspectRadio() const;
    u32 GetScreenWidth() const;
    u32 GetScreenHeight() const;
    bool IsPlatformSupported() const;

    static CGKitInterface& GetInstance()
    {
        static CGKitInterface m_instance;
        return m_instance;
    }

private:
    BaseApplication* m_application;
};

#define gCGKitInterface CGKitInterface::GetInstance()

NS_CG_END

#endif