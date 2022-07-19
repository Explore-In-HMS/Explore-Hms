/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Input event interface. Defining the interface for get input event source.
 */

#ifndef INPUT_EVENT_H
#define INPUT_EVENT_H

#include "Core/Global.h"

NS_CG_BEGIN

enum EventSource {
    EVENT_SOURCE_KEYBOARD,
    EVENT_SOURCE_TOUCHSCREEN,
    EVENT_SOURCE_MAX,
};

class CGKIT_EXPORT InputEvent {
public:
    InputEvent(EventSource source);
    virtual ~InputEvent();
    EventSource GetSource() const;

private:
    EventSource m_source;
};

NS_CG_END

#endif
