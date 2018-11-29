package org.nikok.jumpnrun.core

import kotlin.reflect.KClass

abstract class GameResult
    (val gameCls: KClass<out Game<*>>, val credits: Int = 0)