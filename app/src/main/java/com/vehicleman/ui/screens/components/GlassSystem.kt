package com.vehicleman.ui.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: Int? = null,
    onClick: (() -> Unit)? = null,
    isNightMode: Boolean,
    glassColor: Color,
    glassBorderColor: Color,
    padding: androidx.compose.ui.unit.Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        color = glassColor,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, glassBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(padding)
        ) {
            if (label != null || icon != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (icon != null) {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (label != null) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = (if (isNightMode) Color.White else Color.Black).copy(alpha = 0.65f),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
            content()
        }
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    isNightMode: Boolean,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    val contentColor = if (isNightMode) Color.White else Color.Black
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = contentColor.copy(alpha = 0.4f), fontSize = 14.sp) },
        modifier = modifier.onFocusChanged { onFocusChanged(it.isFocused) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = contentColor,
            unfocusedTextColor = contentColor,
            cursorColor = contentColor
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = contentColor)
    )
}

@Composable
fun GlassTextFieldWithSelection(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    isNightMode: Boolean,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    val contentColor = if (isNightMode) Color.White else Color.Black
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = contentColor.copy(alpha = 0.4f), fontSize = 14.sp) },
        modifier = modifier.onFocusChanged { onFocusChanged(it.isFocused) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = contentColor,
            unfocusedTextColor = contentColor,
            cursorColor = contentColor
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = contentColor)
    )
}
