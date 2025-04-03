package com.example.akirax.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    onBackPressed: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.Companion.padding(start = 8.dp)
            )
        },
        navigationIcon = {
            onBackPressed?.let {
                Box(
                    modifier = Modifier.Companion
                        .padding(start = 8.dp)
                        .size(32.dp)
                        .background(
                            Color.Companion.White.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .clickable(onClick = it),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        modifier = Modifier.Companion.size(20.dp)
                    )
                }
            }
        },
        actions = {
            actions?.invoke(this)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
    )
}