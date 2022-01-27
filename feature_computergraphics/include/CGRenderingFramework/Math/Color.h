/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Define Color type.
 */

#ifndef COLOR_H
#define COLOR_H

#include "Core/Types.h"

NS_CG_BEGIN

class CGKIT_EXPORT Color {
public:
    Color() = default;
    ~Color() = default;

    Color(u32 red, u32 green, u32 blue, u32 alpha = 255)
        : color(((red & 0xff) << 24) | ((green & 0xff) << 16) | ((blue & 0xff) << 8) | (alpha & 0xff))
    {}

    Color& operator=(const Color& other);
    bool operator==(const Color& other) const;
    bool operator!=(const Color& other) const;

    String ToString() const;

public:
    union {
        u32 color;
        struct {
            u8 r;
            u8 g;
            u8 b;
            u8 a;
        };
    };

    static const Color WHITE;
    static const Color BLACK;
    static const Color RED;
    static const Color GREEN;
    static const Color BLUE;
    static const Color YELLOW;
    static const Color ORANGE;
    static const Color CYAN;
    static const Color MAGENTA;
};

NS_CG_END

#endif