package com.example.akirax.presentation.ui.components.moviedetailsscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableText(text: String, maxLength: Int) {
    var expanded by remember { mutableStateOf(false) }
    val displayText =
        if (expanded || text.length <= maxLength) text else "${text.take(maxLength)}..."

    Column {
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp),
            textAlign = TextAlign.Justify
        )
        if (text.length > maxLength) {
            Text(
                text = if (expanded) "Show Less" else "Show More",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}