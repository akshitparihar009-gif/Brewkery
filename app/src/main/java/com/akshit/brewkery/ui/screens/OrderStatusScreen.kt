package com.akshit.brewkery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akshit.brewkery.data.model.Order
import com.akshit.brewkery.ui.theme.CoffeeBrown
import com.akshit.brewkery.ui.theme.LightCoffeeCream
import com.akshit.brewkery.ui.theme.TextPrimary
import com.akshit.brewkery.ui.theme.TextSecondary
import com.akshit.brewkery.ui.viewmodel.OrderViewModel

val OrderStatusBackground = Color(0xFFFBF8F3)
val DispatchedRed = Color(0xFFC8402A)
val CardBorderColor = Color(0xFFE5DDD5)
val PreparingBadgeBg = Color(0xFFF9EED9)
val PreparingBadgeText = Color(0xFFA36F19)
val StatusAcceptedGreen = Color(0xFF128848)
val DarkMenuButtonBg = Color(0xFF111111)

@Composable
fun OrderStatusScreen(
    viewModel: OrderViewModel,
    onReturnToMenuClick: () -> Unit
) {
    val activeOrder by viewModel.activeOrder.collectAsState()

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = OrderStatusBackground,
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = onReturnToMenuClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkMenuButtonBg),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "Back to Menu",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(OrderStatusBackground)
        ) {
            val order = activeOrder
            if (order == null) {
                NoActiveOrderView(onReturnToMenuClick = onReturnToMenuClick)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Coffee Avatar Illustration
                    item {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF9EDE8),
                            border = androidx.compose.foundation.BorderStroke(2.dp, DispatchedRed),
                            modifier = Modifier.size(90.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "☕", fontSize = 38.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "ORDER DISPATCHED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DispatchedRed,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Brewing in Progress!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF111111),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Your ticket was dispatched to our barista.",
                            fontSize = 14.sp,
                            color = Color(0xFF777777),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(140.dp))
                    }

                    // Order Ticket Card
                    item {
                        OrderTicketCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTicketCard(order: Order) {
    val totalItemCount = order.items.sumOf { it.quantity }

    Card(
        modifier = Modifier.fillMaxWidth()
            .height(210.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            // Header Row (Ticket ID + Preparing Badge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "ORDER TICKET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF888888),
                        letterSpacing = 0.5.sp
                    )
//                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.ticketId,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111111)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PreparingBadgeBg
                ) {
                    Text(
                        text = order.status.name,
                        color = PreparingBadgeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = CardBorderColor)
            Spacer(modifier = Modifier.height(8.dp))

            // Estimated Wait
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Estimated Wait:",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Text(
                    text = order.estimatedWaitTime,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DispatchedRed
                )
            }

//            Spacer(modifier = Modifier.height(10.dp))

            // Items Ordered Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Items Ordered:",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "$totalItemCount Item${if (totalItemCount > 1) "s" else ""}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = CardBorderColor)
            Spacer(modifier = Modifier.height(8.dp))

            // Status message
            Column {
                Text(
                    text = "Status:",
                    fontSize = 13.sp,
                    color = Color(0xFF666666)
                )
//                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Barista accepted your order!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusAcceptedGreen
                )
            }
        }
    }
}

@Composable
fun NoActiveOrderView(onReturnToMenuClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = LightCoffeeCream,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "No Order",
                        tint = CoffeeBrown,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active Order",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Place an order from the menu to track status.",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onReturnToMenuClick,
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Go to Menu", color = Color.White)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun OrderStatusTicketPreview() {
    com.akshit.brewkery.ui.theme.BrewkeryTheme {
        val sampleItem = com.akshit.brewkery.data.model.MenuItem(
            id = 1,
            categoryId = "cat_hot_coffee",
            name = "Toasted Caramel Macchiato",
            basePrice = 4.85,
            imageUrl = ""
        )
        val sampleCartItem = com.akshit.brewkery.data.model.CartItem(
            cartItemId = "1-sz_small-m_oat-0",
            menuItem = sampleItem,
            selectedSize = com.akshit.brewkery.data.model.SizeOption("sz_small", "Tall (8 oz)", 0.0),
            selectedMilk = com.akshit.brewkery.data.model.MilkOption("m_oat", "Oat Milk (Barista Blend)", 0.0),
            selectedSugar = "0% Unsweetened",
            quantity = 2,
            unitPrice = 4.85,
            totalPrice = 9.70
        )
        val sampleOrder = Order(
            ticketId = "#BK-74921",
            items = listOf(sampleCartItem),
            subtotal = 4.85,
            deliveryFee = 2.50,
            tax = 0.39,
            total = 7.74,
            status = com.akshit.brewkery.data.model.OrderStatus.PREPARING,
            estimatedWaitTime = "20 – 30 minutes"
        )
        OrderTicketCard(order = sampleOrder)
    }
}
