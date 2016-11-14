package com.example.yoshida_makoto.kotlintest

import com.example.yoshida_makoto.kotlintest.messages.Message
import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject

/**
 * Created by yoshida_makoto on 2016/11/14.
 */
class Messenger {
    private val bus = SerializedSubject<Message, Message>(PublishSubject.create<Message>())

    fun send(message: Message) {
        bus.onNext(message)
    }

    fun <T : Message> register(messageClazz: Class<out T>): Observable<T> {
        return bus
                .ofType(messageClazz)
                .map({ message -> message as T })
    }
}