/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description:
 * Author: xuhan
 *
 * Create: 2020-5-27
 */

#ifndef LOG_COMMON_H
#define LOG_COMMON_H

#include "Core/Types.h"

NS_CG_BEGIN

enum LogPathType {
    LOG_PATH_CONSOLE = 0x01,
};

enum LogType {
    LOG_VERBOSE,
    LOG_DEBUG,
    LOG_INFO,
    LOG_WARNING,
    LOG_ERROR
};

NS_CG_END

#endif