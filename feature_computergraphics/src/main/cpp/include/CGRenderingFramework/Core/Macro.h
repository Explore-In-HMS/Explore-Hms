/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Define general macros.
 */

#ifndef MACRO_H
#define MACRO_H

#include <assert.h>
#include <stdio.h>
#include <exception>

#define LEN_OF_ARRAY(array) s32(sizeof(array) / sizeof((array)[0]))
#define FAIL_RET_X(act, x) if (!(act))            \
        return x;
#define FAIL_RET_VOID(act) if (!(act))            \
        return;
#define FAIL_RET(act) FAIL_RET_X(act, false);
#define FAIL_BREAK(act) if (!(act))               \
        break;
#define SUCC_RET_X(act, x) if (act)               \
        return x;
#define SUCC_RET_VOID(act) if (act)               \
        return;
#define ASSERT(expression) assert(expression)
#define ASSERT_MSG(cond, msg) assert((cond) && ("Reason: " msg));
#define ASSERT_MSG_RET(cond, msg) ASSERT_MSG(cond, msg);          \
    FAIL_RET(cond);
#define ASSERT_MSG_RET_X(cond, msg, x) ASSERT_MSG(cond, msg);     \
    FAIL_RET_X(cond, x);
#define ASSERT_MSG_RET_VOID(cond, msg) ASSERT_MSG(cond, msg);     \
    FAIL_RET_VOID(cond);
#define CLR_BIT(var, bit) ((var) &= (~(bit)))
#define SET_BIT(var, bit) ((var) |= (bit))

#define CG_NEW new (std::nothrow)
#define CG_DELETE delete
#define CG_DELETE_ARRAY(p) if ((p)) {          \
        delete[] (p);                          \
        (p) = nullptr;                         \
    }
#define CG_SAFE_DELETE(p) if ((p)) {           \
        delete (p);                            \
        (p) = nullptr;                         \
    }
#define CG_DEBUG_BREAK_IF(cond) if (cond) {    \
    }

#define CG_NEW_EXCEPTION(ptr, type) do {       \
        try {                                  \
            ptr = new type(this);              \
        } catch (...) {                        \
            std::abort();                      \
        }                                      \
    } while (0);

#define CG_UNUSED(a) (void)a

#endif