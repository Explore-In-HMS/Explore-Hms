/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Define custom types.
 */

#ifndef TYPES_H
#define TYPES_H

#include "Core/Global.h"

NS_CG_BEGIN

#define HashMap ::std::unordered_map
#define HashMultiMap ::std::unordered_multimap
#define HashSet ::std::unordered_set
#define HashMultiSet ::std::unordered_multiset

using u8 = uint8_t;
using s8 = int8_t;

using u16 = uint16_t;
using s16 = int16_t;

using u32 = uint32_t;
using s32 = int32_t;

using u64 = uint64_t;
using s64 = int64_t;

using f32 = float;
using f64 = double;
using c8 = char;

NS_CG_END

#include "Core/STDHeaders.h"

#ifdef CG_WINDOWS_PLATFORM
#include <io.h>
#include <windows.h>
#endif

#ifdef CG_ANDROID_PLATFORM
#include <unistd.h>
#include <android/log.h>
#include <android_native_app_glue.h>
#endif

using namespace std;
using String = string;
using StringStream = basic_stringstream<char, char_traits<char>, allocator<char>>;
using StringVector = vector<String>;

NS_CG_BEGIN
template <typename T>
using BindingMap = std::map<u32, std::map<u32, T>>;
NS_CG_END

#if defined(API_GRAPHICS_VULKAN)
#include <vulkan/vulkan.h>
#ifdef CG_WINDOWS_PLATFORM
#define VK_USE_PLATFORM_WIN32_KHR
#include <stdint.h>
#pragma comment(lib, "vulkan-1.lib")
#pragma comment(lib, "VkLayer_utils.lib")
#elif defined(CG_ANDROID_PLATFORM)
#define VK_USE_PLATFORM_ANDROID_KHR
#include <vulkan/vulkan_android.h>
#endif
#endif

#endif
