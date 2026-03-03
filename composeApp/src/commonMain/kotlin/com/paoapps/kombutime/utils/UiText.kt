package com.paoapps.kombutime.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * A sealed interface representing text that can be resolved in a composable context.
 * Replaces the moko-resources StringDesc pattern with Compose Multiplatform resources.
 */
sealed interface UiText {
    data class Plain(val text: String) : UiText
    data class Resource(val res: StringResource, val args: List<Any> = emptyList()) : UiText
    data class Plural(val res: PluralStringResource, val quantity: Int, val args: List<Any> = emptyList()) : UiText
    data class Combined(val parts: List<UiText>) : UiText

    operator fun plus(other: UiText): UiText = Combined(
        when {
            this is Combined && other is Combined -> this.parts + other.parts
            this is Combined -> this.parts + other
            other is Combined -> listOf(this) + other.parts
            else -> listOf(this, other)
        }
    )
}

fun String.toUiText(): UiText = UiText.Plain(this)

fun StringResource.toUiText(vararg args: Any): UiText = UiText.Resource(this, args.toList())

fun PluralStringResource.toUiText(quantity: Int, vararg args: Any): UiText = UiText.Plural(this, quantity, args.toList())

@Composable
fun UiText.resolve(): String = when (this) {
    is UiText.Plain -> text
    is UiText.Resource -> if (args.isEmpty()) stringResource(res) else stringResource(res, *args.toTypedArray())
    is UiText.Plural -> pluralStringResource(res, quantity, *args.toTypedArray())
    is UiText.Combined -> {
        val resolved = parts.map { it.resolve() }
        resolved.joinToString("")
    }
}
