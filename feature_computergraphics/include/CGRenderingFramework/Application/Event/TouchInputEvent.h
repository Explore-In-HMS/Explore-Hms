/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: The class used to report touch input events. Defined the constants touch action,
 * the interface get touch pointer info, action, posX and posY.
 */

#ifndef TOUCH_INPUT_EVENT_H
#define TOUCH_INPUT_EVENT_H

#include "Core/Types.h"
#include "Application/Event/InputEvent.h"

NS_CG_BEGIN

enum TouchAction {
    TOUCH_ACTION_UNKNOWN,       // 未知动作
    TOUCH_ACTION_DOWN,          // 触摸按下动作
    TOUCH_ACTION_UP,            // 触摸抬起动作
    TOUCH_ACTION_MOVE,          // 触摸移动动作
    TOUCH_ACTION_CANCEL,        // 触摸取消动作
    TOUCH_ACTION_POINTER_DOWN,  // 多指触摸按下动作 
    TOUCH_ACTION_POINTER_UP,    // 多指触摸抬起动作
    TOUCH_ACTION_MAX,
};

struct TouchCoord {
    f32 x = 0.0f;
    f32 y = 0.0f;

    TouchCoord(f32 posX, f32 posY)
    {
        x = posX;
        y = posY;
    }
};

class CGKIT_EXPORT TouchInputEvent : public InputEvent {
public:
    TouchInputEvent(u32 touchIndex, const vector<u32>& pointers, const vector<TouchCoord>& touchCoords, TouchAction action,
        u64 eventTime, u64 downTime);
    ~TouchInputEvent();
    TouchAction GetAction() const;
    u32 GetTouchCount() const;
    u32 GetTouchIndex() const;
    s32 GetTouchId(u32 touchIndex = 0) const;
    f32 GetPosX(u32 touchIndex = 0) const;
    f32 GetPosY(u32 touchIndex = 0) const;
    u64 GetEventTime() const;
    u64 GetDownTime() const;
    const vector<TouchCoord>& GetTouchCoords() const;
    const vector<u32>& GetTouchPointers() const;

private:
    vector<TouchCoord> m_touchCoords;
    vector<u32> m_pointers;
    TouchAction m_action = TOUCH_ACTION_MAX;
    u32 m_touchIndex = 0;
    u32 m_touchCount = 0;
    u64 m_eventTime = 0;
    u64 m_downTime = 0;
};

NS_CG_END

#endif