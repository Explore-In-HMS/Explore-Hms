/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Overview of the Application. Defining the application using steps.
 * contains initialization, update, render etc.
 */

#ifndef BASE_APPLICATION_H
#define BASE_APPLICATION_H

#include "Core/Types.h"

NS_CG_BEGIN

class InputEvent;
class Platform;

class CGKIT_EXPORT BaseApplication {
public:
    BaseApplication();
    virtual ~BaseApplication();
    BaseApplication operator=(const BaseApplication& baseApplication) = delete;
    BaseApplication(const BaseApplication& baseApplication) = delete;

    virtual void Start(void* param);
    virtual void Initialize(const void* winHandle, u32 width, u32 height);
    virtual void UnInitialize();
    virtual bool Resume(const void* winHandle, u32 width, u32 height);
    virtual void Pause();
    virtual void InitScene();
    virtual void Update(f32 deltaTime);
    virtual void Render();
    virtual void Resize(u32 width, u32 height);
    virtual void Run();
    virtual void ProcessInputEvent(const InputEvent* inputEvent);

    void MainLoop();
    void SetFocus(bool flag);
    bool IsFocused() const;

protected:
    virtual void HandleKeyboardEvent(const InputEvent* inputEvent);
    virtual void HandleTouchEvent(const InputEvent* inputEvent);

    f32 GetFps() const;
    void SetConfigDataSize(u32 width, u32 height);

protected:
    Platform* m_platform = nullptr;
    f32 m_fps = 0;
    f32 m_frameTime = 0;
    u32 m_frameCount = 0;
    bool m_focus = true;
};

NS_CG_END

#endif
