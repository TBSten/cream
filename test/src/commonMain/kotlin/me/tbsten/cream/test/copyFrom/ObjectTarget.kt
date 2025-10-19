package me.tbsten.cream.test.copyFrom

import me.tbsten.cream.CopyFrom

// data object -> data object copy
@CopyFrom(EmptySource::class)
data object EmptyTarget

data object EmptySource
