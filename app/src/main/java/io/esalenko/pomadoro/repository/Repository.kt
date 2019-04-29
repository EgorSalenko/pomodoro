package io.esalenko.pomadoro.repository

import io.reactivex.Maybe
import io.reactivex.Single


interface Repository<Item> {

    fun getAll(): Maybe<List<Item>>

    fun get(id: Long): Single<Item>

    fun get(item: Item): Single<Item>

    fun add(item : Item)

    fun delete(id : Long)

    fun delete(item : Item)

    fun deleteAll()



}