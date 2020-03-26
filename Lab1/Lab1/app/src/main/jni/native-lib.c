#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_lab1_MainActivity_stringFromJni(JNIEnv *env, jobject thiz) {
    // TODO: implement stringFromJni()
    __android_log_print(ANDROID_LOG_INFO, "Example", "This is a log message");
}

JNIEXPORT void JNICALL
Java_com_example_lab1_MainActivity_sayHello(JNIEnv *env, jobject thiz) {
    // TODO: implement sayHello()
    __android_log_print(ANDROID_LOG_INFO, "Example", "This is say hello function call");
}

JNIEXPORT void JNICALL
Java_com_example_lab1_MainActivity_surfaceDraw(JNIEnv *env, jobject thiz, jobject surface) {
    // TODO: implement surfaceDraw()
    ANativeWindow *wnd = ANativeWindow_fromSurface(env,surface);

    ANativeWindow_setBuffersGeometry(wnd,0,0,WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer buffer;

    ANativeWindow_lock(wnd, &buffer, NULL);

    uint32_t* image = buffer.bits;
    float step = 255.f / buffer.height;
    for(int y = 0; y < buffer.height; y++) {
        uint8_t curVal = step*y;
        for(int x = 0; x < buffer.width; x++) {
            //we a setting R G to the same value which produces different
            //shades of yellow
            image[y*buffer.stride + x] = curVal << 8 | curVal;
        }
    }
    ANativeWindow_unlockAndPost(wnd);
    ANativeWindow_release(wnd);
}
