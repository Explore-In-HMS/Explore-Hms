/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Reference count of resoruce.
 * Author: liuyue
 * Create: 2020-5-25
 */

#ifndef RESOURCE_RERCOUNT_H
#define RESOURCE_RERCOUNT_H

#include "Core/Types.h"

NS_CG_BEGIN

class RefCount
{
public:
    RefCount() {}
    virtual ~RefCount() {}

    void IncRef() { m_count++; }
    void DecRef() { m_count--; }
    bool CanDestroy() { return m_count <= 0; }
    s32 GetRef() { return m_count; }

private:
    s32 m_count = 1; // when create, the reference count is 1
};

NS_CG_END

#endif // RESOURCE_RERCOUNT_H