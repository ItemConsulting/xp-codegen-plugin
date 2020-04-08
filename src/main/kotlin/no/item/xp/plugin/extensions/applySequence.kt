package no.item.xp.plugin.extensions

import arrow.core.*
import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.k

fun <A> applySequence(seq: Sequence<Either<Throwable, A>>): Either<Throwable, Sequence<A>> {
  return seq
    .k()
    .sequence(Either.applicative())
    .fix()
    .map { it.fix() }
}
