package com.cvte.maxhub.mvvmsample.models.data

enum class FunctionType(var type : Int) {
    realtime(0x00000002),
    anonymous(0x00000004),
    location(0x00000008),
    default(0)
}