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
            title = "Circles, Arcs & Sectors",
            items = listOf(
                FormulaItem("Circumference & Diameter", "C = 2πr = πd", "Distance around a circle of radius r and diameter d = 2r."),
                FormulaItem("Area of a Circle", "A = πr²", "Total enclosed area inside a circle of radius r."),
                FormulaItem("Arc Length & Sector Area", "s = rθ,  A_sec = ½r²θ", "Central angle θ measured in radians (θ_rad = θ° × π/180)."),
                FormulaItem("Chord Length", "c = 2r·sin(θ / 2)", "Straight-line distance connecting the endpoints of a circular arc."),
                FormulaItem("Circle Cartesian Equation", "(x - h)² + (y - k)² = r²", "Circle centered at (h, k) with radius r; centered at origin: x² + y² = r².")
            )
        ),
        FormulaCategory(
            title = "Trigonometric Identities & Triangles",
            items = listOf(
                FormulaItem("SOH - CAH - TOA", "sin = O/H, cos = A/H, tan = O/A", "Right triangle trigonometric ratios relative to an acute angle."),
                FormulaItem("Pythagorean Theorem & Identity", "a² + b² = c²,  sin²(θ) + cos²(θ) = 1", "Relates legs and hypotenuse of right triangles, and sine/cosine on the unit circle."),
                FormulaItem("Law of Sines", "a / sin(A) = b / sin(B) = c / sin(C)", "Relates side lengths to sines of opposite angles in any triangle."),
                FormulaItem("Law of Cosines", "c² = a² + b² - 2ab·cos(C)", "Generalizes the Pythagorean theorem to any SSS or SAS triangle."),
                FormulaItem("Tangent & Reciprocals", "tan = sin/cos, csc = 1/sin, sec = 1/cos, cot = 1/tan", "All six fundamental trigonometric functions."),
                FormulaItem("Double Angle Formulas", "sin(2θ) = 2sin(θ)cos(θ), cos(2θ) = cos²(θ) - sin²(θ)", "Useful for simplifying trigonometric expressions."),
                FormulaItem("Degree to Radian", "rad = deg × (π / 180°)", "360° corresponds to 2π radians. 1 radian ≈ 57.2958°.")
            )
        ),
        FormulaCategory(
            title = "3D Solids Geometry",
            items = listOf(
                FormulaItem("Sphere", "V = (4/3)πr³,  A = 4πr²", "Volume and surface area of a sphere with radius r."),
                FormulaItem("Right Circular Cylinder", "V = πr²h,  A = 2πr(r + h)", "Volume and total surface area of a cylinder with radius r and height h."),
                FormulaItem("Right Circular Cone", "V = ⅓πr²h,  A = πr(r + √(r²+h²))", "Cone volume is one-third of the enclosing cylinder.")
            )
        ),
        FormulaCategory(
            title = "Physics Laws & Physical Quantities",
            items = listOf(
                FormulaItem("Uniform Circular Motion", "a_c = v²/r = ω²r,  F_c = mv²/r,  T = 2πr/v", "Centripetal acceleration & force always point radially inward; angular velocity ω = v/r."),
                FormulaItem("Newton's 2nd Law, Work & Power", "F = ma,  W_earth = mg,  W = Fd,  P = W/t", "1 Newton = 1 kg·m/s², 1 Joule = 1 N·m, 1 Watt = 1 J/s."),
                FormulaItem("Momentum & Impulse", "p = mv,  J = Δp = m(v - u) = F_avg·Δt", "Impulse equals change in linear momentum; conserved in closed collisions."),
                FormulaItem("Kinematics (Uniform Accel)", "v = u + at,  s = ut + ½at²,  v² = u² + 2as", "Relates initial velocity u, final velocity v, acceleration a, time t, and displacement s."),
                FormulaItem("Projectile Motion", "R = v₀²sin(2θ)/g,  H_max = (v₀sinθ)²/(2g)", "Parabolic flight under uniform gravity; horizontal speed v₀cosθ stays constant."),
                FormulaItem("Mechanical Energy", "E_k = ½mv²,  E_p = mgh,  E_tot = E_k + E_p", "Total mechanical energy is conserved without non-conservative work."),
                FormulaItem("Universal Gravitation & Orbits", "F_g = GM₁M₂/r²,  v_esc = √(2GM/r),  v_orb = √(GM/r)", "Newtonian gravity with G = 6.6743×10⁻¹¹ m³/(kg·s²)."),
                FormulaItem("Oscillations (SHM)", "T_pend = 2π√(L/g),  T_spring = 2π√(m/k)", "Period & frequency f = 1/T of simple pendulums and Hookean springs."),
                FormulaItem("Density & Fluid Pressure", "ρ = m/V,  P = F/A,  P_h = ρgh", "Pascal's hydrostatic pressure increases linearly with depth h."),
                FormulaItem("Ohm's Law & DC Power", "V = I·R,  P = V·I = I²R", "Relates voltage (V), current (A), resistance (Ω), and power (W)."),
                FormulaItem("Coulomb's Law (Electrostatics)", "F_e = k_e·q₁q₂/r²,  E = k_e·q₁/r²", "Electrostatic force and field with k_e ≈ 8.988×10⁹ N·m²/C²."),
                FormulaItem("Planck-Einstein & Wave", "E = h·f = hc / λ,  p_photon = h / λ", "Photon energy with h = 6.626×10⁻³⁴ J·s and c = 2.998×10⁸ m/s.")
            )
        ),
        FormulaCategory(
            title = "Chemistry Equilibrium & Gas Laws",
            items = listOf(
                FormulaItem("pH & pOH Scale (25°C)", "pH = -log₁₀[H⁺],  pH + pOH = 14", "Water ion product K_w = [H⁺][OH⁻] = 1.0 × 10⁻¹⁴ M²."),
                FormulaItem("Ideal Gas Law", "PV = nRT", "Universal gas constant R = 0.08206 L·atm/(mol·K) = 8.3145 J/(mol·K)."),
                FormulaItem("Solution Dilution", "M₁V₁ = M₂V₂", "Conservation of solute moles during solvent dilution.")
            )
        ),
        FormulaCategory(
            title = "Order of Operations & Hierarchy",
            items = listOf(
                FormulaItem("PEMDAS / BODMAS", "P → E → MD → AS", "Parentheses / Brackets, Exponents & Roots, Multiplication & Division (left to right), Addition & Subtraction (left to right)."),
                FormulaItem("Negative Signs", "-(a) = (-1) × a", "Unary minus has high precedence when attached directly to numbers, but in -3^2, exponentiation is evaluated first: -(3^2) = -9.")
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
