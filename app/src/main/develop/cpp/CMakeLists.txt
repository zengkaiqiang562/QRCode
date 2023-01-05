cmake_minimum_required(VERSION 3.18.1)
project("app")

# TODO 需要改成正式环境的全局配置
add_definitions(-DPROFILE_CACHE="1f8b0800000000000000cd91c16bc32014c6eff92b8ae784c568dab95b6130063b14721ca358e356c11a5193d296fcef7bcdd2926c0decd877d2f7fddec7f3f314cda0102fdf940fe869f6deddcf75ba9e7aa229f62a882d40c1d5321ecbca18e996e5c8e1b6d3c0716d3517126690e009b736b1f526218ca6593e4f196334cb297d2014b34792678ca278d2291c6c67e40377618a13baf2b250c733994e7a356b2fb8ee983f483bea7cc4bf335a5d1e54ec600fe97ed6b9526d7c7ff1e29410bc200b9cfd235e65ee285c51412a62186f341844207faaaf570364c335cccdfbb5908760fcf6b9763ca8ca80822f8a347ca3e54bad4ad97f44d47e030efd476823030000")
add_definitions(-DSIGN="9e58e69uagp2a15iuhbcyzr7w9ydfetf")
add_definitions(-DCKEY="4Si0C6dEyMfRkx7c")
add_definitions(-DSKEY="zVpK0LqCJ1GdBX8P")
add_definitions(-DTAG="CryptoGuard")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SRC_LIST
        cryptoguard.cpp
)

add_library(cryptoguard SHARED ${SRC_LIST})

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(
    cryptoguard
    ${log-lib}
    ${android-lib}
)