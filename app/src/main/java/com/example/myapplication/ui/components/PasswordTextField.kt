package com.example.myapplication.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.example.myapplication.ui.theme.LilUrlTheme

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    supportingText: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    var visible by remember { mutableStateOf(false) }
    LilUrlTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        leadingIcon = Icons.Filled.Lock,
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (visible) "Hide password" else "Show password",
                )
            }
        },
        errorMessage = errorMessage,
        supportingText = supportingText,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        imeAction = imeAction,
        onImeAction = onImeAction,
        enabled = enabled,
    )
}

@Preview(showBackground = true)
@Composable
private fun PasswordTextFieldPreview(
    @PreviewParameter(PasswordFieldParams::class) config: PasswordFieldPreviewConfig,
) {
    LilUrlTheme(darkTheme = false, dynamicColor = false) {
        var value by remember { mutableStateOf(config.value) }
        PasswordTextField(
            value = value,
            onValueChange = { value = it },
            label = config.label,
            errorMessage = config.error,
            enabled = config.enabled,
        )
    }
}
