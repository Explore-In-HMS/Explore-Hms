/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Singleton for class operations.
 */

#ifndef SINGLETON_H
#define SINGLETON_H

#include "Core/Types.h"
#include "Log/Log.h"

NS_CG_BEGIN

template <typename T>
class Singleton {
public:
    static T& GetSingleton()
    {
        static T m_singleton;
        return m_singleton;
    }

 protected:
    Singleton() {}
    virtual ~Singleton() {}
    Singleton(const Singleton&) {}
    Singleton& operator = (const Singleton&) {}
};

NS_CG_END

#endif