package com.akshit.brewkery.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.akshit.brewkery.data.model.Category
import com.akshit.brewkery.data.model.MenuItem
import com.akshit.brewkery.data.model.Order
import com.akshit.brewkery.data.model.StoreMeta
import com.akshit.brewkery.ui.theme.CoffeeBrown
import com.akshit.brewkery.ui.theme.RatingGold
import com.akshit.brewkery.ui.theme.TextPrimary
import com.akshit.brewkery.ui.theme.TextSecondary
import com.akshit.brewkery.ui.viewmodel.MenuUiState
import com.akshit.brewkery.ui.viewmodel.MenuViewModel
import java.util.Locale

val AppBackground = Color(0xFFFBF8F3)
val HeaderTextSecondary = Color(0xFF555555)
val StoreCardBackground = Color(0xFFF7ECE8)
val StoreInfoDotColor = Color(0xFF9E4233)
val SearchBackground = Color(0xFFFFFFFF)
val SearchBorder = Color(0xFFE2DDD7)
val CardBackground = Color(0xFFFFFFFF)
val CardBorder = Color(0xFFEAE5DF)
val TerracottaPriceRed = Color(0xFFC8402A)
val BadgeTanBackground = Color(0xFFF9EED9)
val BadgeTanText = Color(0xFFA36F19)
val BottomCartBlack = Color(0xFF111111)
val CheckoutOrange = Color(0xFFF59E0B)
val ActiveOrderGreen = Color(0xFF00A859)
val StatusBarDividerColor = Color(0xFFCCCCCC)

@Composable
fun HomeScreen(
    viewModel: MenuViewModel,
    onItemClick: (Int) -> Unit,
    onViewCartClick: () -> Unit,
    onViewActiveOrderClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()
    val cartSubtotal by viewModel.cartSubtotal.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = cartItemCount > 0,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                BottomCartBar(
                    itemCount = cartItemCount,
                    subtotal = cartSubtotal,
                    onViewCartClick = onViewCartClick
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBackground)
        ) {
            when (val state = uiState) {
                is MenuUiState.Loading -> {
                    LoadingScreen()
                }

                is MenuUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.loadMenu() }
                    )
                }

                is MenuUiState.Success -> {
                    MenuContent(
                        meta = state.meta,
                        categories = state.categories,
                        filteredItems = state.filteredItems,
                        selectedCategoryId = state.selectedCategoryId,
                        searchQuery = state.searchQuery,
                        cartItemCount = cartItemCount,
                        activeOrder = activeOrder,
                        onCategorySelect = { viewModel.selectCategory(it) },
                        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                        onItemClick = onItemClick,
                        onViewCartClick = onViewCartClick,
                        onViewActiveOrderClick = onViewActiveOrderClick
                    )
                }
            }
        }
    }
}

@Composable
fun MenuContent(
    meta: StoreMeta,
    categories: List<Category>,
    filteredItems: List<MenuItem>,
    selectedCategoryId: String,
    searchQuery: String,
    cartItemCount: Int,
    activeOrder: Order?,
    onCategorySelect: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onItemClick: (Int) -> Unit,
    onViewCartClick: () -> Unit,
    onViewActiveOrderClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top App Bar Header
        item {
            TopStoreHeader(
                meta = meta,
                cartItemCount = cartItemCount,
                onCartClick = onViewCartClick
            )
        }

        // Active Order Card if present, else Store Info Card
        item {
            if (activeOrder != null) {
                ActiveOrderCard(
                    order = activeOrder,
                    onClick = onViewActiveOrderClick
                )
            } else {
                StoreInfoCard(meta = meta)
            }
        }

        // Search Bar
        item {
            SearchBarSection(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange
            )
        }

        // Categories Row
        item {
            CategorySelectionSection(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelect = onCategorySelect
            )
        }

        // Menu Items List
        if (filteredItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items match your search",
                        color = TextSecondary,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            items(
                items = filteredItems,
                key = { it.id }
            ) { item ->
                MenuItemCard(
                    menuItem = item,
                    currencySymbol = meta.currencySymbol,
                    onClick = { onItemClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun TopStoreHeader(
    meta: StoreMeta,
    cartItemCount: Int,
    onCartClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        // Status bar area + divider at its bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            HorizontalDivider(
                color = StatusBarDividerColor,
                thickness = 1.dp,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Header content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black,
                    shadowElevation = 0.dp,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "BK",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Fresh Roast & Bakes",
                        fontSize = 12.sp,
                        color = HeaderTextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = meta.app + " Artisans",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }

            // Cart
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) {
                        onCartClick()
                    },
                contentAlignment = Alignment.BottomStart
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(
                        1.dp,
                        Color(0xFFE2DDD7)
                    ),
                    shadowElevation = 0.dp,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Cart",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (cartItemCount > 0) {
                    Surface(
                        shape = CircleShape,
                        color = TerracottaPriceRed,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$cartItemCount",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoreInfoCard(meta: StoreMeta) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = StoreCardBackground),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Scooter Icon
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorder),
                shadowElevation = 0.dp,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🛵",
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STORE INFO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StoreInfoDotColor,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(StoreInfoDotColor)
                    )
                }

                Text(
                    text = "Delivery in ${meta.estimatedDeliveryTime}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E)
                )

                Text(
                    text = "${meta.currencySymbol}${String.format(Locale.US, "%.2f", meta.deliveryFee)} flat fee",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Open Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFECE4DE)),
                shadowElevation = 0.dp
            ) {
                Text(
                    text = "Open",
                    color = Color(0xFF1E1E1E),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ActiveOrderCard(
    order: Order,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { onClick() },
        colors = CardDefaults.cardColors(containerColor = StoreCardBackground),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Rounded Square Icon Container
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 0.dp,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(ActiveOrderGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Middle Column: ACTIVE ORDER header directly above Active Order No.
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE ORDER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ActiveOrderGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(TerracottaPriceRed)
                    )
                }

                Text(
                    text = "Active Order ${order.ticketId}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E)
                )

                Text(
                    text = "Preparing (Arriving in ${order.estimatedWaitTime})",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Track Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ActiveOrderGreen,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) { onClick() }
            ) {
                Text(
                    text = "Track",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBarSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(17.dp),
        color = SearchBackground,
        border = BorderStroke(1.dp, SearchBorder),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF555555),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search roast, cold brew, pastry...",
                        color = Color(0xFF777777),
                        fontSize = 13.sp
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        color = Color(0xFF181818),
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(CoffeeBrown),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (searchQuery.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource
                        ) { onSearchQueryChange("") }
                )
            }
        }
    }
}

@Composable
fun CategorySelectionSection(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelect: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = categories,
            key = { it.id }
        ) { category ->
            val isSelected = category.id == selectedCategoryId
            val interactionSource = remember { MutableInteractionSource() }
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) { onCategorySelect(category.id) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) Color.Black else Color.White,
                border = if (isSelected) null else BorderStroke(1.dp, SearchBorder),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (category.icon.isNotEmpty() && category.id != "ALL") {
                        Text(text = category.icon, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = category.name,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF222222)
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    currencySymbol: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image Thumbnail
            AsyncImage(
                model = menuItem.imageUrl,
                contentDescription = menuItem.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Badge above title
                if (!menuItem.badge.isNullOrEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BadgeTanBackground,
                        shadowElevation = 0.dp
                    ) {
                        Text(
                            text = menuItem.badge,
                            color = BadgeTanText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 12.sp,
                            modifier = Modifier.padding(
                                horizontal = 5.dp,
                                vertical = 1.dp
                            )
                        )
                    }

                }

                // Title
                Text(
                    text = menuItem.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF181818),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (menuItem.rating > 0) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = RatingGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${menuItem.rating}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF222222)
                        )
                        if (menuItem.reviewCount > 0) {
                            Text(
                                text = " (${menuItem.reviewCount})",
                                fontSize = 11.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }

                // Bottom Row with Price and + Customize Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", menuItem.basePrice)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TerracottaPriceRed
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = TerracottaPriceRed,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .height(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(
                                indication = null,
                                interactionSource = interactionSource
                            ) { onClick() }
                    ) {
                        Text(
                            text = "+ Customize",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomCartBar(
    itemCount: Int,
    subtotal: Double,
    onViewCartClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { onViewCartClick() },
        shape = RoundedCornerShape(22.dp),
        color = BottomCartBlack,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Red Circle Badge Count
                Surface(
                    shape = CircleShape,
                    color = TerracottaPriceRed,
                    shadowElevation = 0.dp,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$itemCount",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "View Your Cart",
                        color = Color(0xFFAAAAAA),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", subtotal)}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Proceed to Checkout",
                    color = CheckoutOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Checkout",
                    tint = CheckoutOrange,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = CoffeeBrown,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Brewing menu details...",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "☕",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Unable to connect",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again", color = Color.White)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    com.akshit.brewkery.ui.theme.BrewkeryTheme {
        val sampleMeta = StoreMeta(
            app = "Brewkery",
            tagline = "Artisanal Coffee & Fresh Oven Bakes",
            currencySymbol = "$",
            deliveryFee = 2.50,
            estimatedDeliveryTime = "20 – 30 mins"
        )
        val sampleCategories = listOf(
            Category("ALL", "All Items", "☕", 6),
            Category("cat_hot_coffee", "Hot Coffee", "☕", 2),
            Category("cat_cold_brews", "Cold Brews", "🧊", 2),
            Category("cat_bakery", "Artisan Bakery", "🥐", 2)
        )
        val sampleItem = MenuItem(
            id = 1,
            categoryId = "cat_hot_coffee",
            name = "Toasted Caramel Macchiato",
            tagline = "Double shot espresso with velvety oat milk & salted caramel",
            description = "Rich espresso roast layered with velvety steamed milk.",
            basePrice = 4.85,
            rating = 4.9,
            reviewCount = 312,
            prepTime = "5-7 mins",
            imageUrl = "",
            badge = "BESTSELLER"
        )
        MenuContent(
            meta = sampleMeta,
            categories = sampleCategories,
            filteredItems = listOf(sampleItem),
            selectedCategoryId = "ALL",
            searchQuery = "",
            cartItemCount = 2,
            activeOrder = null,
            onCategorySelect = {},
            onSearchQueryChange = {},
            onItemClick = {},
            onViewCartClick = {},
            onViewActiveOrderClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun HomeScreenWithActiveOrderPreview() {
    com.akshit.brewkery.ui.theme.BrewkeryTheme {
        val sampleMeta = StoreMeta(
            app = "Brewkery",
            tagline = "Artisanal Coffee & Fresh Oven Bakes",
            currencySymbol = "$",
            deliveryFee = 2.50,
            estimatedDeliveryTime = "20 – 30 mins"
        )
        val sampleCategories = listOf(
            Category("ALL", "All Items", "☕", 6)
        )
        val sampleItem = MenuItem(
            id = 1,
            categoryId = "cat_hot_coffee",
            name = "Toasted Caramel Macchiato",
            basePrice = 4.85,
            imageUrl = ""
        )
        val sampleOrder = Order(
            ticketId = "#BK-25887",
            items = emptyList(),
            subtotal = 4.85,
            deliveryFee = 2.50,
            tax = 0.39,
            total = 7.74,
            estimatedWaitTime = "25 mins"
        )
        MenuContent(
            meta = sampleMeta,
            categories = sampleCategories,
            filteredItems = listOf(sampleItem),
            selectedCategoryId = "ALL",
            searchQuery = "",
            cartItemCount = 0,
            activeOrder = sampleOrder,
            onCategorySelect = {},
            onSearchQueryChange = {},
            onItemClick = {},
            onViewCartClick = {},
            onViewActiveOrderClick = {}
        )
    }
}
