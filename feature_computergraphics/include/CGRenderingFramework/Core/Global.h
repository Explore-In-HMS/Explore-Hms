/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Define global macros.
 */

#ifndef GLOBAL_H
#define GLOBAL_H

#include "Core/Macro.h"

#ifndef CG_DEPRECATED
#define CG_DEPRECATED __declspec(deprecated)
#endif

#define CG_NORETURN __declspec(noreturn)

#if defined(_STDCALL_SUPPORTED)
#define CGCALLCONV __stdcall
#else
#define CGCALLCONV __cdecl
#endif

#ifdef __cplusplus
#define NS_CG_BEGIN namespace CGKit {
#define NS_CG_END }
#define USING_NS_CG using namespace CGKit;
#define NS_CG CGKit
#else
#define NS_CG_BEGIN
#define NS_CG_END
#define USING_CG_DL
#define NS_CG
#endif

#if defined(_WIN32) || defined(_WIN64) || defined(WIN32) || defined(WIN64)
#define CG_WINDOWS_PLATFORM
#elif defined(__ANDROID__)
#define CG_ANDROID_PLATFORM
#endif

#ifndef BUILD_STATIC
#if defined(CGKIT_LIB)
#ifdef CG_WINDOWS_PLATFORM
#define CGKIT_EXPORT __declspec(dllexport)
#else
#define CGKIT_EXPORT __attribute__((visibility("default")))
#endif
#else
#ifdef CG_WINDOWS_PLATFORM
#define CGKIT_EXPORT __declspec(dllimport)
#else
#define CGKIT_EXPORT
#endif
#endif
#else
#define CGKIT_EXPORT
#endif

#define API_GRAPHICS_VULKAN
//#define CGKIT_SHOW_AABB

#endif
