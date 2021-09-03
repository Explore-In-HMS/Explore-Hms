/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: xuhan
 *
 * Create: 2020-5-6
 */

#ifndef CG_KIT_HEADERS_H
#define CG_KIT_HEADERS_H

#include "Application/CGKitInterface.h"
#include "Application/BaseApplication.h"
#include "Application/Event/InputEvent.h"
#include "Application/Event/TouchInputEvent.h"
#include "Application/Event/KeyInputEvent.h"

#include "Core/Global.h"
#include "Core/Macro.h"
#include "Core/STDHeaders.h"
#include "Core/Types.h"
#include "Core/Singleton.h"

#include "Log/LogCommon.h"
#include "Log/Log.h"

#include "Math/Math.h"
#include "Math/AABB.h"
#include "Math/Color.h"
#include "Math/Matrix4.h"
#include "Math/Plane.h"
#include "Math/Quaternion.h"
#include "Math/Vector2.h"
#include "Math/Vector3.h"
#include "Math/Vector4.h"

#include "Rendering/Model/Model.h"
#include "Rendering/Model/Mesh.h"
#include "Rendering/Model/SubMesh.h"
#include "Rendering/Material.h"
#include "Rendering/RenderCommon.h"

#include "Resource/IResource.h"
#include "Resource/ResourceManager.h"
#include "Resource/RefCount.h"

#include "Scene/IComponent.h"
#include "Scene/Component/Renderable.h"
#include "Scene/Component/Transform.h"
#include "Scene/Component/MeshRenderer.h"
#include "Scene/Component/Light.h"
#include "Scene/Component/Camera.h"
#include "Scene/SceneObject.h"
#include "Scene/SceneManager.h"

#endif