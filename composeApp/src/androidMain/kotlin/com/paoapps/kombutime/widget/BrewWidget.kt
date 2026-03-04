package com.paoapps.kombutime.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.paoapps.kombutime.MainActivity
import com.paoapps.kombutime.domain.Brew
import com.paoapps.kombutime.domain.BrewState
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max

/**
 * Home screen widget showing active kombucha brews
 */
class BrewWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            BrewWidgetSizes.Small,
            BrewWidgetSizes.Medium,
            BrewWidgetSizes.Large
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val brews = BrewWidgetDataProvider.getBrews(context)
                WidgetContent(brews)
            }
        }
    }

    @Composable
    private fun WidgetContent(brews: List<Brew>) {
        val size = androidx.glance.LocalSize.current

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .clickable(actionStartActivity<MainActivity>())
                .padding(16.dp)
        ) {
            if (brews.isEmpty()) {
                EmptyState()
            } else {
                when {
                    size.height < 100.dp -> SmallWidget(brews.first())
                    size.height < 200.dp -> MediumWidget(brews.take(2))
                    else -> LargeWidget(brews.take(4))
                }
            }
        }
    }

    @Composable
    private fun EmptyState() {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🫙",
                style = TextStyle(fontSize = 32.sp)
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(
                text = "No active brews",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = GlanceTheme.colors.onBackground
                )
            )
            Text(
                text = "Tap to start brewing!",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }
    }

    @Composable
    private fun SmallWidget(brew: Brew) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrewItem(brew, isCompact = true)
        }
    }

    @Composable
    private fun MediumWidget(brews: List<Brew>) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            WidgetHeader()
            Spacer(modifier = GlanceModifier.height(8.dp))
            brews.forEach { brew ->
                BrewItem(brew, isCompact = false)
                Spacer(modifier = GlanceModifier.height(8.dp))
            }
        }
    }

    @Composable
    private fun LargeWidget(brews: List<Brew>) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            WidgetHeader()
            Spacer(modifier = GlanceModifier.height(12.dp))
            brews.forEach { brew ->
                BrewItem(brew, isCompact = false)
                Spacer(modifier = GlanceModifier.height(12.dp))
            }
        }
    }

    @Composable
    private fun WidgetHeader() {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🫙",
                style = TextStyle(fontSize = 20.sp)
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = "KombuTime",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onBackground
                )
            )
        }
    }

    @Composable
    private fun BrewItem(brew: Brew, isCompact: Boolean) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val fermentationDays = when(brew.state) {
            BrewState.FirstFermentation -> brew.settings.firstFermentationDays
            is BrewState.SecondFermentation -> brew.settings.secondFermentationDays
        }
        val remainingDays = fermentationDays - (today - brew.startDate).days
        val progress = max(0f, ((today - brew.startDate).days.toFloat() / fermentationDays).coerceIn(0f, 1f))

        val icon = when(brew.state) {
            is BrewState.FirstFermentation -> "🫙"
            is BrewState.SecondFermentation -> "🍾"
        }

        val title = when(val state = brew.state) {
            is BrewState.FirstFermentation -> {
                if (state.teaType.isNotBlank()) {
                    "${brew.settings.name} - ${state.teaType}"
                } else {
                    brew.settings.name
                }
            }
            is BrewState.SecondFermentation -> {
                if (state.flavor.isNotBlank()) {
                    "${brew.settings.name} - ${state.flavor}"
                } else {
                    brew.settings.name
                }
            }
        }

        val subtitle = when(brew.state) {
            is BrewState.FirstFermentation -> "First Fermentation"
            is BrewState.SecondFermentation -> "Second Fermentation"
        }

        val daysText = when {
            remainingDays < 0 -> "${-remainingDays} days overdue"
            remainingDays == 0 -> "Ready today!"
            remainingDays == 1 -> "1 day left"
            else -> "$remainingDays days left"
        }

        val textColor = if (remainingDays < 0) {
            androidx.glance.unit.ColorProvider(Color.Red)
        } else if (remainingDays == 0) {
            androidx.glance.unit.ColorProvider(Color(0xFF2E7D32)) // Green
        } else {
            GlanceTheme.colors.onBackground
        }

        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            // Header row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    style = TextStyle(fontSize = if (isCompact) 16.sp else 20.sp)
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = if (isCompact) 12.sp else 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = GlanceTheme.colors.onBackground
                        )
                    )
                    if (!isCompact) {
                        Text(
                            text = subtitle,
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = GlanceTheme.colors.onSurfaceVariant
                            )
                        )
                    }
                }
                Text(
                    text = daysText,
                    style = TextStyle(
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
            }

            // Progress bar
            if (!isCompact) {
                Spacer(modifier = GlanceModifier.height(6.dp))
                ProgressBar(progress, brew.state)
            }
        }
    }

    @Composable
    private fun ProgressBar(progress: Float, state: BrewState) {
        val backgroundColor = when(state) {
            is BrewState.FirstFermentation -> androidx.glance.unit.ColorProvider(Color(0xFFFFE0B2))
            is BrewState.SecondFermentation -> androidx.glance.unit.ColorProvider(Color(0xFFC5E1A5))
        }

        val progressColor = when(state) {
            is BrewState.FirstFermentation -> androidx.glance.unit.ColorProvider(Color(0xFFFF9800))
            is BrewState.SecondFermentation -> androidx.glance.unit.ColorProvider(Color(0xFF8BC34A))
        }

        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(8.dp)
                .background(backgroundColor)
        ) {
            if (progress > 0) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .background(progressColor)
                )
            }
        }
    }
}

/**
 * Widget receiver
 */
class BrewWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BrewWidget()
}
