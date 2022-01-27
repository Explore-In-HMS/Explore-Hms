/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: The class used to report key and button events. Defined the constants input key code,
 * and the interface for get key code or key action.
 */

#ifndef KEY_INPUT_EVENT_H
#define KEY_INPUT_EVENT_H

#include "Application/Event/InputEvent.h"

NS_CG_BEGIN

enum KeyCode {
    KEY_CODE_UNKNOWN,    // 未知
    KEY_CODE_SPACE,      // 空格键
    KEY_CODE_APOSTROPHE, // ’(撇号)
    KEY_CODE_COMMA,      // ，（逗号）
    KEY_CODE_MINUS,      // -（减号）
    KEY_CODE_PERIOD,     // 。（句号）
    KEY_CODE_SLASH,      // /（正斜线）
    KEY_CODE_0,          // 0键
    KEY_CODE_1,          // 1键
    KEY_CODE_2,          // 2键
    KEY_CODE_3,          // 3键
    KEY_CODE_4,          // 4键
    KEY_CODE_5,          // 5键
    KEY_CODE_6,          // 6键
    KEY_CODE_7,          // 7键
    KEY_CODE_8,          // 8键
    KEY_CODE_9,          // 9键
    KEY_CODE_SEMICOLON,  // ；（分号）
    KEY_CODE_EQUAL,      // =（等号）
    KEY_CODE_A,
    KEY_CODE_B,
    KEY_CODE_C,
    KEY_CODE_D,
    KEY_CODE_E,
    KEY_CODE_F,
    KEY_CODE_G,
    KEY_CODE_H,
    KEY_CODE_I,
    KEY_CODE_J,
    KEY_CODE_K,
    KEY_CODE_L,
    KEY_CODE_M,
    KEY_CODE_N,
    KEY_CODE_O,
    KEY_CODE_P,
    KEY_CODE_Q,
    KEY_CODE_R,
    KEY_CODE_S,
    KEY_CODE_T,
    KEY_CODE_U,
    KEY_CODE_V,
    KEY_CODE_W,
    KEY_CODE_X,
    KEY_CODE_Y,
    KEY_CODE_Z,
    KEY_CODE_LEFT_BRACKET,  // [ （左中括号）
    KEY_CODE_BACKSLASH,     // \ (反斜线)
    KEY_CODE_RIGHT_BRACKET, // ] （右中括号）
    KEY_CODE_GRAVE_ACCENT,  // ` （反引号）
    KEY_CODE_ESCAPE,        // 退出键
    KEY_CODE_ENTER,         // enter键
    KEY_CODE_TAB,           // tab键
    KEY_CODE_BACKSPACE,     // 退格键
    KEY_CODE_INSERT,        // 插入键
    KEY_CODE_DELETE,        // 删除键
    KEY_CODE_RIGHT,         // 右键
    KEY_CODE_LEFT,          // 左键
    KEY_CODE_DOWN,          // 下键
    KEY_CODE_UP,            // 上建
    KEY_CODE_PAGE_UP,       // 上翻页键
    KEY_CODE_PAGE_DOWN,     // 下翻页键
    KEY_CODE_HOME,          // home键
    KEY_CODE_END,           // end键
    KEY_CODE_BACK,          // back键
    KEY_CODE_CAPS_LOCK,     // 大写锁定键
    KEY_CODE_SCROLL_LOCK,   // scroll lock键
    KEY_CODE_NUM_LOCK,      // num lock键
    KEY_CODE_PRINT_SCREEN,  // print screen键
    KEY_CODE_PAUSE,         // 暂停键
    KEY_CODE_F1,
    KEY_CODE_F2,
    KEY_CODE_F3,
    KEY_CODE_F4,
    KEY_CODE_F5,
    KEY_CODE_F6,
    KEY_CODE_F7,
    KEY_CODE_F8,
    KEY_CODE_F9,
    KEY_CODE_F10,
    KEY_CODE_F11,
    KEY_CODE_F12,
    KEY_CODE_KP_0,          // 数字键盘0
    KEY_CODE_KP_1,          // 数字键盘1
    KEY_CODE_KP_2,          // 数字键盘2
    KEY_CODE_KP_3,          // 数字键盘3
    KEY_CODE_KP_4,          // 数字键盘4
    KEY_CODE_KP_5,          // 数字键盘5
    KEY_CODE_KP_6,          // 数字键盘6
    KEY_CODE_KP_7,          // 数字键盘7
    KEY_CODE_KP_8,          // 数字键盘8
    KEY_CODE_KP_9,          // 数字键盘9
    KEY_CODE_KP_DECIMAL,    // 数字键盘 .
    KEY_CODE_KP_DIVIDE,     // 数字键盘 /
    KEY_CODE_KP_MULTIPLY,   // 数字键盘 *
    KEY_CODE_KP_SUBTRACT,   // 数字键盘 –
    KEY_CODE_KP_ADD,        // 数字键盘 +
    KEY_CODE_KP_ENTER,      // 数字键盘 enter
    KEY_CODE_KP_EQUAL,      // 数字键盘 =
    KEY_CODE_LEFT_SHIFT,    // 左shift
    KEY_CODE_LEFT_CONTROL,  // 左ctrl
    KEY_CODE_LEFT_ALT,      // 左 alt
    KEY_CODE_RIGHT_SHIFT,   // 右 shift
    KEY_CODE_RIGHT_CONTROL, // 右 ctrl
    KEY_CODE_RIGHT_ALT,     // 右 alt
    KEY_CODE_MAX = KEY_CODE_RIGHT_ALT
};

enum KeyAction {
    KEY_ACTION_DOWN,    // 按下
    KEY_ACTION_UP,      // 抬起
    KEY_ACTION_REPEAT,  // 重复（长按）
    KEY_ACTION_UNKNOWN, // 未知
    KEY_ACTION_MAX = KEY_ACTION_UNKNOWN
};

class CGKIT_EXPORT KeyInputEvent : public InputEvent {
public:
    KeyInputEvent(KeyCode code, KeyAction action);
    ~KeyInputEvent();
    KeyCode GetCode() const;
    KeyAction GetAction() const;

private:
    KeyCode m_code;
    KeyAction m_action;
};

NS_CG_END

#endif // KEY_INPUT_EVENT_H
