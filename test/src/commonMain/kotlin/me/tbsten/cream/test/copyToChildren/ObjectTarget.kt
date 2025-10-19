package me.tbsten.cream.test.copyToChildren

import me.tbsten.cream.CopyToChildren

// data object -> data object copy
@CopyToChildren
sealed class EmptyParent

data object EmptyChild1 : EmptyParent()

data object EmptyChild2 : EmptyParent()
