package me.tbsten.cream.test.copyTo

import me.tbsten.cream.CopyTo

// data object -> data object copy
@CopyTo(EmptyTarget::class)
data object EmptySource

data object EmptyTarget
