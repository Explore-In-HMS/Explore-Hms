/*
 * Copyright (c) Hisilicon Technologies Co.. 2020-2020. All rights reserved.
 * Description: The entry point class in sample, use different defined to distinct platform handle.
 */

#include "MainApplication.h"
using namespace CGKit;


void android_main(android_app* state)
{
    auto app = CreateMainApplication();
    if (app == nullptr) {
        return;
    }
    app->Start((void*)(state));
    app->MainLoop();
    CG_SAFE_DELETE(app);
}

