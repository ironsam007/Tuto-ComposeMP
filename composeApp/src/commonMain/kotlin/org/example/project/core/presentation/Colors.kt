package org.example.project.core.presentation

import androidx.compose.ui.graphics.Color

val DarkBlue = Color(0xFF0B405E)
val DesertWhite = Color(0xFFF7F7F7)
val SandYellow = Color(0xFFFFBD64)
val LightBlue = Color(0xFF9AD9FF)



//color in Compose (ARGB) has 4 components:
//A (Alpha) = transparency
//R (Red)
//G (Green)
//B (Blue)
// so the following change on alpha is possible:
val color1 = LightBlue.copy(alpha = 0.2f)