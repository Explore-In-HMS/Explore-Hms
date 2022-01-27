/*
 * Copyright (c) Explore in HMS. 2020-2020. All rights reserved.
 * Description: Log utils.
 */

#ifndef LOG_H
#define LOG_H

#include "Log/LogCommon.h"

NS_CG_BEGIN

#ifdef CGKIT_LOG
#define LOGVERBOSE(...)        Log::WriteLog(LogType::LOG_VERBOSE, __FUNCTION__, __FILE__, __LINE__, __VA_ARGS__)
#define LOGDEBUG(...)          Log::WriteLog(LogType::LOG_DEBUG, __FUNCTION__, __FILE__, __LINE__, __VA_ARGS__)
#define LOGINFO(...)           Log::WriteLog(LogType::LOG_INFO, __FUNCTION__, __FILE__, __LINE__, __VA_ARGS__)
#define LOGWARNING(...)        Log::WriteLog(LogType::LOG_WARNING, __FUNCTION__, __FILE__, __LINE__, __VA_ARGS__)
#define LOGERROR(...)          Log::WriteLog(LogType::LOG_ERROR, __FUNCTION__, __FILE__, __LINE__, __VA_ARGS__)
#define LOG_ALLOC_ERROR(...)   Log::WriteLog(LogType::LOG_ERROR, __FUNCTION__, __FILE__, __LINE__, __VA_ARGS__)
#define LOG_ALLOC_ABORT(...) { Log::WriteLog(LogType::LOG_ERROR, __FUNCTION__, __FILE__, __LINE__, __VA_ARGS__);  std::abort(); }
#define LOG_ALLOC_NULL(...)    Log::WriteLog(LogType::LOG_ERROR, __FUNCTION__, __FILE__, __LINE__, __VA_ARGS__)
#else
#define LOGVERBOSE(...)
#define LOGDEBUG(...)
#define LOGINFO(...)
#define LOGWARNING(...)
#define LOGERROR(...)
#define LOG_ALLOC_ERROR(...)
#define LOG_ALLOC_ABORT(...)   std::abort()
#define LOG_ALLOC_NULL(...)
#endif

struct LogCmd;
class ILogger;

class CGKIT_EXPORT Log {
public:
    Log() = default;
    ~Log() = default;

    static void Initialize();
    static void Uninitialize();
    static void SetLogLevel(LogType logType);

    static void WriteLog(LogType type, String func, String file, int line, const char* fmt, ...);
    static void Write(LogType type, const char* text);

private:
    static void RegisterLogger(LogPathType logPathType, ILogger* logger);
    static void UnregisterLogger(LogPathType logPathType);
    static void EnableLogger(LogPathType logPathType, bool bEnable);

    static String GetTimestamp();
    static void Flush();
    static bool IsLogLevelEnabled(LogType logType);
    static char* GetFileName(const char* file);

private:
    static std::map<LogPathType, ILogger*> m_loggerMap;
    static vector<LogCmd> m_logBuffer;
    static mutex m_mutex;
    static LogType m_logLevel;
};

NS_CG_END

#endif