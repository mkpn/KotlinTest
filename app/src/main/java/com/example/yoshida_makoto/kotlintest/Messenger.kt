package com.example.yoshida_makoto.kotlintest

import com.example.yoshida_makoto.kotlintest.messages.Message
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
class Messenger {
    private val bus = PublishSubject.create<Message>()

    fun send(message: Message) {
        bus.onNext(message)
    }

    fun <T : Message> register(messageClazz: Class<out T>): Observable<T> {
        return bus
                .ofType(messageClazz)
                .map({ message -> message })
    }
}