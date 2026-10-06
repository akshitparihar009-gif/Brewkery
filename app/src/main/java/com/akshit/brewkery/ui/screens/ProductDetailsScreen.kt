package com.akshit.brewkery.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.akshit.brewkery.data.model.MilkOption
import com.akshit.brewkery.data.model.SizeOption
import com.akshit.brewkery.ui.viewmodel.ItemDetailUiState
import com.akshit.brewkery.ui.viewmodel.ItemDetailViewModel
import java.util.Locale

// Exact Color Palette matching the design image
val ProductDetailBackground = Color(0xFFFAF7F2)
val CustomizerTitleColor = Color(0xFF1A1715)
val TerracottaRed = Color(0xFFBA4225)
val LightPeachSelected = Color(0xFFFBF1EB)
val UnselectedBorderColor = Color(0xFFEDE7E1)
val IngredientsTitleColor = Color(0xFF7A7067)
val DescriptionTextColor = Color(0xFF7C7267)
val ItemTextColor = Color(0xFF3E3A36)
val ItemSubTextColor = Color(0xFF888078)
val DarkSelectedBackground = Color(0xFF1A1715)
val BadgeGoldColor = Color(0xFFFFA000)

@Composable
fun ProductDetailsScreen(
    itemId: Int,
    viewModel: ItemDetailViewModel,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(itemId) {
        viewModel.loadItemDetail(itemId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            if (uiState is ItemDetailUiState.Success) {
                val state = uiState as ItemDetailUiState.Success

                BottomActionBar(
                    quantity = state.quantity,
                    totalPrice = state.totalPrice,
                    onIncrement = { viewModel.incrementQuantity() },
                    onDecrement = { viewModel.decrementQuantity() },

                    onAddToCart = {
                        if (viewModel.addToCart()) {

                            Toast.makeText(
                                context,
                                "Successfully added to cart",
                                Toast.LENGTH_SHORT
                            ).show()

                            onCartClick()
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ProductDetailBackground)
        ) {
            when (val state = uiState) {
                is ItemDetailUiState.Loading -> {
                    LoadingScreen()
                }

                is ItemDetailUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.loadItemDetail(itemId) }
                    )
                }

                is ItemDetailUiState.Success -> {
                    ProductDetailsContent(
                        state = state,
                        onBackClick = onBackClick,
                        onSizeSelect = { viewModel.selectSize(it) },
                        onMilkSelect = { viewModel.selectMilk(it) },
                        onSugarSelect = { viewModel.selectSugar(it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailsContent(
    state: ItemDetailUiState.Success,
    onBackClick: () -> Unit,
    onSizeSelect: (SizeOption) -> Unit,
    onMilkSelect: (MilkOption) -> Unit,
    onSugarSelect: (String) -> Unit
) {
    val item = state.menuItem
    var isFavorite by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Top Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 1.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, UnselectedBorderColor),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onBackClick() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1715),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "ITEM CUSTOMIZER",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CustomizerTitleColor,
                    letterSpacing = 0.5.sp
                )

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(
                        1.dp,
                        UnselectedBorderColor
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            isFavorite = !isFavorite

                            if (isFavorite) {
                                Toast.makeText(
                                    context,
                                    "Item saved to wishlist",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Item removed from wishlist",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) {
                                Color(0xFFE53935)
                            } else {
                                Color.Gray
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }}}

        // Hero Image Card with BESTSELLER Badge
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(190.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (!item.badge.isNullOrEmpty()) {
                    Surface(
                        modifier = Modifier
                            .padding(14.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = item.badge,
                            color = BadgeGoldColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Title, Price & Description
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustomizerTitleColor,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", item.basePrice)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TerracottaRed
                    )
                }

//                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = DescriptionTextColor,
                    lineHeight = 19.sp
                )

                // Key Ingredients Chips
                if (!item.ingredients.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "KEY INGREDIENTS",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = IngredientsTitleColor,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.ingredients.forEach { ingredient ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, UnselectedBorderColor)
                            ) {
                                Text(
                                    text = ingredient,
                                    fontSize = 11.5.sp,
                                    color = IngredientsTitleColor,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Customizations Sections Inside Clean White Cards
        item {
            val customizations = item.customizations
            if (customizations != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Size Selection Card
                    if (!customizations.sizes.isNullOrEmpty()) {
                        CustomizerCard(title = "Size Selection") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                customizations.sizes.forEach { size ->
                                    val isSelected = state.selectedSize?.id == size.id
                                    SizeSelectionItem(
                                        size = size,
                                        isSelected = isSelected,
                                        onClick = { onSizeSelect(size) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 2. Milk Options / Spreads Card
                    if (!customizations.milkOptions.isNullOrEmpty()) {
                        CustomizerCard(title = "Milk Options / Spreads") {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                customizations.milkOptions.forEach { milk ->
                                    val isSelected = state.selectedMilk?.id == milk.id
                                    MilkSelectionItem(
                                        milk = milk,
                                        isSelected = isSelected,
                                        onClick = { onMilkSelect(milk) }
                                    )
                                }
                            }
                        }
                    }

                    // 3. Sugar Levels / Serving Card
                    if (!customizations.sugarLevels.isNullOrEmpty()) {
                        CustomizerCard(title = "Sugar Levels / Serving") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                customizations.sugarLevels.forEach { sugar ->
                                    val isSelected = state.selectedSugar == sugar
                                    // Proportional weight allows longer text ("100% Standard Sweet")
                                    // and shorter text ("50% Mild") to fit naturally on one line
                                    val weight = when {
                                        sugar.length <= 9 -> 1.0f
                                        sugar.length <= 15 -> 1.4f
                                        else -> 2.0f
                                    }
                                    SugarSelectionItem(
                                        sugar = sugar,
                                        isSelected = isSelected,
                                        onClick = { onSugarSelect(sugar) },
                                        modifier = Modifier.weight(weight)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reusable Outer White Card Wrapper
 */
@Composable
fun CustomizerCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, UnselectedBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = CustomizerTitleColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
fun SizeSelectionItem(
    size: SizeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) LightPeachSelected else Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) TerracottaRed else UnselectedBorderColor
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = size.label,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) TerracottaRed else ItemTextColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
//            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "+$${String.format(Locale.US, "%.2f", size.extraPrice)}",
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) TerracottaRed else ItemSubTextColor
            )
        }
    }
}

@Composable
fun MilkSelectionItem(
    milk: MilkOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) LightPeachSelected else Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) TerracottaRed else UnselectedBorderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = milk.name,
                fontSize = 13.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TerracottaRed else ItemTextColor
            )
            Text(
                text = "+$${String.format(Locale.US, "%.2f", milk.extraPrice)}",
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) TerracottaRed else ItemSubTextColor
            )
        }
    }
}

@Composable
fun SugarSelectionItem(
    sugar: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) DarkSelectedBackground else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, UnselectedBorderColor)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = sugar,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else ItemTextColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun BottomActionBar(
    quantity: Int,
    totalPrice: Double,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ProductDetailBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stepper Pill Container
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, UnselectedBorderColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(48.dp).padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDecrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = Color(0xFF1A1715),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Text(
                        text = "$quantity",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1715),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onIncrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Color(0xFF1A1715),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Add To Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaRed),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Add to Cart  •  $${String.format(Locale.US, "%.2f", totalPrice)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ProductDetailsScreenPreview() {
    com.akshit.brewkery.ui.theme.BrewkeryTheme {
        val sampleItem = com.akshit.brewkery.data.model.MenuItem(
            id = 1,
            categoryId = "cat_hot_coffee",
            name = "Toasted Caramel Macchiato",
            tagline = "Double shot espresso with velvety oat milk & salted caramel",
            description = "Rich espresso roast layered with velvety steamed milk, infused with house-made vanilla bean syrup and marked with a buttery toasted caramel drizzle.",
            basePrice = 4.85,
            rating = 4.9,
            reviewCount = 312,
            prepTime = "5-7 mins",
            imageUrl = "",
            badge = "BESTSELLER",
            ingredients = listOf("Signature Espresso", "Steamed Oat Milk", "Salted Caramel", "Bourbon Vanilla"),
            customizations = com.akshit.brewkery.data.model.Customizations(
                sizes = listOf(
                    SizeOption("sz_small", "Tall (8 oz)", 0.0),
                    SizeOption("sz_medium", "Grande (12 oz)", 0.65),
                    SizeOption("sz_large", "Venti (16 oz)", 1.25)
                ),
                milkOptions = listOf(
                    MilkOption("m_oat", "Oat Milk (Barista Blend)", 0.0),
                    MilkOption("m_almond", "Roasted Almond Milk", 0.50),
                    MilkOption("m_whole", "Organic Whole Milk", 0.0)
                ),
                sugarLevels = listOf("0% Unsweetened", "50% Mild", "100% Standard Sweet")
            )
        )
        val state = com.akshit.brewkery.ui.viewmodel.ItemDetailUiState.Success(
            menuItem = sampleItem,
            selectedSize = sampleItem.customizations?.sizes?.get(0),
            selectedMilk = sampleItem.customizations?.milkOptions?.get(0),
            selectedSugar = "0% Unsweetened",
            quantity = 1,
            unitPrice = 4.85,
            totalPrice = 4.85
        )
        ProductDetailsContent(
            state = state,
            onBackClick = {},
            onSizeSelect = {},
            onMilkSelect = {},
            onSugarSelect = {}
        )
    }
}