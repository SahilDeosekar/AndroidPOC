package net.srijan.swiko.ui.actor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ActorScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import net.srijan.swiko.stream.AppStream.messages
import net.srijan.swiko.stream.Message
import kotlin.reflect.KSuspendFunction2

typealias ActorFunction<S> = KSuspendFunction2<ActorScope<Message>,S, Unit>

typealias TheActorScope = ActorScope<Message>

@OptIn(ObsoleteCoroutinesApi::class)
data class TheActor<S> private constructor(val actorFunction:ActorFunction<S>, val initialState: S){

    companion object{

        fun <S> ActorFunction<S>.toActor(state:S): TheActor<S> {
            return TheActor(this, state)
        }

    }

    fun start(scope:CoroutineScope) = scope.launch {
            val actor = actor<Message>(this.coroutineContext) {
                actorFunction(this, initialState)
            }
            messages.collect(actor::send)
        }

}

class ScopeReference(private val scope:CoroutineScope){

    fun <S> TheActor<S>.start() = this.start(scope)

}
