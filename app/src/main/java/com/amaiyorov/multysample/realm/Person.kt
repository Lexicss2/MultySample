package com.amaiyorov.multysample.realm

import io.realm.RealmList
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

class Person(@PrimaryKey var id: Long = 0,
             var name: String = "",
             var age: Int = 0,
             var dog: Dog? = null,
             var cats: RealmList<Cat> = RealmList(),
             @Ignore var tempReference: Int = 0
) {
}