#include "cn_wjdiankong_dexfiledynamicload_NativeTool.h"
#include "common.h"
#include <stdlib.h>
#include <dlfcn.h>
#include <stdio.h>

#include <android/log.h>

#define  LOG_TAG    "mlog"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

JNINativeMethod *dvm_dalvik_system_DexFile;
void (*openDexFile)(const u4* args, union JValue* pResult);


///////////////////////////////////////////////////
//
// 函数名称:		get_env
// 函数功能:		获取 jni 句柄			
// 函数返回值:	    dalvik  句柄
//
//////////////////////////////////////////////////
JNIEnv* get_env()
{
	typedef JNIEnv* (*pFn_getJNIEnv)();
	static pFn_getJNIEnv getJNIEnv = NULL;
	if(getJNIEnv)
	{
		return getJNIEnv();
	}
	void* handle = dlopen("/system/lib/libandroid_runtime.so", RTLD_NOW);
	if(NULL == handle)
	{
		return 0;
	}
	getJNIEnv = (pFn_getJNIEnv)dlsym(handle, "_ZN7android14AndroidRuntime9getJNIEnvEv");
	if(getJNIEnv == NULL)
	{
		return 0;
	}
	return getJNIEnv();
}


int lookup(JNINativeMethod *table, const char *name, const char *sig,
           void (**fnPtrout)(u4 const *, union JValue *)) {
    int i = 0;
    while (table[i].name != NULL)
    {
        LOGI("lookup %d %s" ,i,table[i].name);
        if ((strcmp(name, table[i].name) == 0)
            && (strcmp(sig, table[i].signature) == 0))
        {
            *fnPtrout = table[i].fnPtr;
            return 1;
        }
        i++;
    }
    return 0;
}

/* This function will be call when the library first be load.
 * You can do some init in the libray. return which version jni it support.
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {

    void *ldvm = (void*) dlopen("libdvm.so", RTLD_LAZY);
    dvm_dalvik_system_DexFile = (JNINativeMethod*) dlsym(ldvm, "dvm_dalvik_system_DexFile");

    //openDexFile
    if(0 == lookup(dvm_dalvik_system_DexFile, "openDexFile", "([B)I",&openDexFile)) {
        openDexFile = NULL;
        LOGI("openDexFile method does not found ");
    }else{
        LOGI("openDexFile method found ! HAVE_BIG_ENDIAN");
    }

    LOGI("ENDIANNESS is %c" ,ENDIANNESS );
    void *venv;
    LOGI("dufresne----->JNI_OnLoad!");
    if ((*vm)->GetEnv(vm, (void**) &venv, JNI_VERSION_1_4) != JNI_OK) {
        LOGI("dufresne--->ERROR: GetEnv failed");
        return -1;
    }
    return JNI_VERSION_1_4;
}

JNIEXPORT jint JNICALL Java_com_example_memload_NativeTool_loadDex(JNIEnv* env, jclass jv, jbyteArray dexArray, jlong dexLen)
{
    // header+dex content
    u1 * olddata = (u1*)(*env)-> GetByteArrayElements(env,dexArray,NULL);
    char* arr;
    arr = (char*)malloc(16 + dexLen);
    ArrayObject *ao=(ArrayObject*)arr;
    ao->length = dexLen;
    memcpy(arr+16,olddata,dexLen);
    u4 args[] = { (u4) ao };
    union JValue pResult;
    jint result;
    if(openDexFile != NULL) {
        openDexFile(args,&pResult);
    }else{
        result = -1;
    }

    result = (jint) pResult.l;
    LOGI("Java_cn_wjdiankong_dexfiledynamicload_NativeTool_loadDex %d" , result);
    return result;
}

jint loadDex(char *pbuf, u4 dexLen)
{
	void *ldvm = (void*) dlopen("libdvm.so", RTLD_LAZY);
	if(openDexFile == NULL)
	{
		dvm_dalvik_system_DexFile = (JNINativeMethod*) dlsym(ldvm, "dvm_dalvik_system_DexFile");
		//openDexFile
		if(0 == lookup(dvm_dalvik_system_DexFile, "openDexFile", "([B)I",&openDexFile)) {
			openDexFile = NULL;
			LOGI("openDexFile method does not found ");
		}else{
			LOGI("openDexFile method found ! HAVE_BIG_ENDIAN");
		}
    }
    char* arr;
    arr = (char*)malloc(16 + dexLen);
    ArrayObject *ao=(ArrayObject*)arr;
    ao->length = dexLen;
    memcpy(arr+16, pbuf, dexLen);
    u4 args[] = { (u4) ao };
    union JValue pResult;
    jint result;
    if(openDexFile != NULL) {
        openDexFile(args,&pResult);
    }else{
        result = -1;
    }

    result = (jint) pResult.l;
    LOGI("Java_cn_wjdiankong_dexfiledynamicload_NativeTool_loadDex %d" , result);
    return result;
}
