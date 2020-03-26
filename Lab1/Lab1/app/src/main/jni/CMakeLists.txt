cmake_minimum_required(VERSION 3.4.1)

add_library( native-lib
             SHARED
             native-lib.c)

find_library(
        log-lib
        log )

find_library(
        android-lib android
)

target_link_libraries(native-lib ${log-lib} ${android-lib})
