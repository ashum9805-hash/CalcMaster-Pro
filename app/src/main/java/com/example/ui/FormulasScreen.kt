package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FormulaCategory(
    val title: String,
    val items: List<FormulaItem>
)

data class FormulaItem(
    val name: String,
    val formula: String,
    val note: String
)

@Composable
fun FormulasScreen(
    onOpenHistory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        FormulaCategory(
            title = "Order of Operations & Hierarchy",
            items = listOf(
                FormulaItem("PEMDAS / BODMAS", "P → E → MD → AS", "Parentheses / Brackets, Exponents & Roots, Multiplication & Division (left to right), Addition & Subtraction (left to right)."),
                FormulaItem("Negative Signs", "-(a) = (-1) × a", "Unary minus has high precedence when attached directly to numbers, but in -3^2, exponentiation is evaluated first: -(3^2) = -9.")
            )
        ),
        FormulaCategory(
            title = "Trigonometric Identities",
            items = listOf(
                FormulaItem("Pythagorean Identity", "sin²(θ) + cos²(θ) = 1", "Fundamental relationship between sine and cosine for any angle θ."),
                FormulaItem("Tangent Definition", "tan(θ) = sin(θ) / cos(θ)", "Valid where cos(θ) ≠ 0 (undefined at 90°, 270°, etc.)."),
                FormulaItem("Double Angle (Sine)", "sin(2θ) = 2 sin(θ) cos(θ)", "Expresses sine of double angle in terms of single angle."),
                FormulaItem("Double Angle (Cosine)", "cos(2θ) = cos²(θ) - sin²(θ)", "Equivalent to 2cos²(θ) - 1 or 1 - 2sin²(θ)."),
                FormulaItem("Degree to Radian", "rad = deg × (π / 180°)", "360° corresponds to 2π radians. 1 radian ≈ 57.2958°.")
            )
        ),
        FormulaCategory(
            title = "Logarithm & Exponent Laws",
            items = listOf(
                FormulaItem("Product Rule", "log(a × b) = log(a) + log(b)", "The logarithm of a product is the sum of logarithms."),
                FormulaItem("Quotient Rule", "log(a / b) = log(a) - log(b)", "The logarithm of a quotient is the difference of logarithms."),
                FormulaItem("Power Rule", "log(aᵇ) = b × log(a)", "Exponents inside logarithm can be factored outside."),
                FormulaItem("Change of Base", "log_b(a) = ln(a) / ln(b)", "Used to calculate any base logarithm using natural log."),
                FormulaItem("Negative Exponent", "a⁻ⁿ = 1 / aⁿ", "Negative powers represent reciprocal."),
                FormulaItem("Fractional Exponent", "a^(1/n) = ⁿ√a", "Power of 1/2 is square root, 1/3 is cube root.")
            )
        ),
        FormulaCategory(
            title = "Fundamental Constants",
            items = listOf(
                FormulaItem("Pi (π)", "3.14159 26535 89793", "Ratio of a circle's circumference to its diameter."),
                FormulaItem("Euler's Number (e)", "2.71828 18284 59045", "Base of the natural logarithm, limit of (1 + 1/n)ⁿ."),
                FormulaItem("Golden Ratio (φ)", "(1 + √5) / 2 ≈ 1.61803", "Famous divine proportion found throughout nature and geometry.")
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "Mathematical Formulas & Notes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Quick reference guide for scientific calculations",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier.size(36.dp).testTag("formulas_history_button")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "View calculation history",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(categories) { category ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    category.items.forEach { item ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.formula,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
