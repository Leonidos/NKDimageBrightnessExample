LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
 
LOCAL_MODULE    := ImageProcessor
LOCAL_SRC_FILES := ImageProcessor.c

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)