package com.amaiyorov.multysample.realm

import io.realm.RealmObject

open class Dog() : RealmObject() {
    var name: String? = null
    var age: Int? = 0
}