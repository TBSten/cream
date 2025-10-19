package me.tbsten.cream.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommonCard(
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    OutlinedCard(
        shape = RoundedCornerShape(size = 20.dp),
        elevation = CardDefaults.outlinedCardElevation(4.dp),
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = verticalArrangement,
            modifier = Modifier.padding(20.dp),
        ) {
            content()
        }
    }
}
