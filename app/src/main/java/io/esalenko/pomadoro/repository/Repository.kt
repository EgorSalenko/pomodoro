package io.esalenko.pomadoro.repository

import io.reactivex.Maybe


interface Repository<Item> {

    fun getAll(): Maybe<List<Item>>

    fun get(id: Long): Item

    fun get(item: Item): Item

    fun add(item : Item)

    fun delete(id : Long)

    fun delete(item : Item)

    fun deleteAll()



}