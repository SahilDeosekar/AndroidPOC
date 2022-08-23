package net.srijan.swiko.stream

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface Message
interface State : Message
interface Action : Message

object AppStream {


    private val stream: MutableSharedFlow<Message> = MutableSharedFlow(extraBufferCapacity = 100)

    suspend fun send(event: Message) {
        stream.emit(event)
    }

    val messages: Flow<Message>
        get() = stream

}