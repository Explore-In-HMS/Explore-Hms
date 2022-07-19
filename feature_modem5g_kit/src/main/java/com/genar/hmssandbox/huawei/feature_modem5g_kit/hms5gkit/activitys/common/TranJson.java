package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TranJson
{
    public static long maxTime = 4294967295L;
    private String ParamName;
    private String ModemSlice;
    private String JsonStr;
    private List<String> JsonKeyValue;
    private List<String> TimeStamp;

    public TranJson(){
        JsonKeyValue = new ArrayList<>();
        TimeStamp = new ArrayList<>();
    }

    public void setParamAndJsonStr(String paramName, String jsonStr){
        JsonStr = jsonStr;
        ParamName = paramName;
    }

    public String getParamName(){ return ParamName; }

    public void setModemSlice(String modemSlice) throws JSONException {
        try {
            JSONObject jsonObj = new JSONObject(modemSlice);
            ModemSlice = jsonObj.get("curTimeStamp").toString();
        } catch (JSONException e) {
            e.printStackTrace();
            ModemSlice = modemSlice.toString();
        }
    }

    public void clean(){
        ModemSlice = null;
        JsonStr = null;
        JsonKeyValue.clear();
        TimeStamp.clear();
    }

    public String TimeTran() throws JSONException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        if(JsonStr == "") //当Json字符串为空时,不进行转换
        {
            return "";
        }
        iteraJson(JsonStr,JsonKeyValue);
        for (String jsonKeyValue : JsonKeyValue) {
            if(jsonKeyValue.contains("timestamp")){
                TimeStamp.add(jsonKeyValue.split(":")[1]); //jsonKeyValue中数据形式为key:value,这里将data中的所有时间戳全部取出
            }
        }
        for (String timestamp : TimeStamp)
        {
            int sec;
            if(Long.parseLong(timestamp) > maxTime) {
                sec = (int)((Long.parseLong(timestamp)- Long.parseLong(ModemSlice)-maxTime)/32768L);
            }else {
                sec = (int)((Long.parseLong(timestamp)- Long.parseLong(ModemSlice))/32768L);
            }
            c.add(Calendar.SECOND, sec);
            Date d = c.getTime();
            String day = format.format(d);
            JsonStr = JsonStr.replace(timestamp,day);
        }
        return JsonStr;
    }

    public static boolean iteraJson(String str, List<String> res) throws JSONException {
        if(str.indexOf(":") == -1){ //Json字符串中没有":"证明无值,则不进行递归解析
            return true;
        }
        JSONObject Json = new JSONObject(str);
        Iterator keys = Json.keys();
        while(keys.hasNext()){
            String key = keys.next().toString();
            Object value = Json.get(key);
            String val = value.toString();
            if(val.indexOf("[{") == -1){
                if(val.indexOf(":") == -1){
                    res.add(key+":"+val);
                }else{
                    iteraJson(val,res);
                }
            }else if(val.indexOf("[{") != -1){
                if(val.indexOf("[{") == 0){
                    String jsons = val.substring(1, val.lastIndexOf("]"));//得到数据格式为：{...},{...},{...}
                    jsons = jsons.replaceAll("\\}\\s?,\\s?\\{", "}~{");
                    String[] split = jsons.split("~");
                    for(int i = 0; i < split.length;i++){
                        iteraJson(split[i],res);
                    }
                }else{
                    iteraJson(val,res);//value仍然可能是一个json且这个json中包含数组。
                }
            }
        }
        return false;
    }

}

