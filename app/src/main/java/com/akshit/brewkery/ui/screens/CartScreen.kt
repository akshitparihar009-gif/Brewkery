package com.akshit.brewkery.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akshit.brewkery.data.model.CartItem
import com.akshit.brewkery.data.model.Order
import com.akshit.brewkery.ui.theme.CoffeeBrown
import com.akshit.brewkery.ui.theme.LightCoffeeCream
import com.akshit.brewkery.ui.theme.TextPrimary
import com.akshit.brewkery.ui.theme.TextSecondary
import com.akshit.brewkery.ui.viewmodel.CartViewModel
import java.util.Locale


// ---------------------------------------------------------
// COLORS
// ---------------------------------------------------------

val CartBackground = Color(0xFFFBF8F3)
val CartTitleColor = Color(0xFF181818)
val CartClearRed = Color(0xFFC8402A)
val CartItemBorder = Color(0xFFE5DDD5)
val CartStepperBackground = Color(0xFFF5EAE6)


// ---------------------------------------------------------
// CART SCREEN
// ---------------------------------------------------------

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBackClick: () -> Unit,
    onOrderPlaced: (Order) -> Unit
) {

    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val deliveryFee by viewModel.deliveryFee.collectAsState()
    val tax by viewModel.tax.collectAsState()
    val total by viewModel.total.collectAsState()


    Scaffold(

        // -------------------------------------------------
        // FIXED BOTTOM SECTION
        // -------------------------------------------------

        bottomBar = {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CartBackground,
                shadowElevation = 8.dp
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 10.dp,
                            bottom = 10.dp
                        )
                ) {

                    // -----------------------------------------
                    // SUMMARY CARD
                    // -----------------------------------------

                    CostSummaryCard(
                        subtotal = subtotal,
                        deliveryFee = deliveryFee,
                        tax = tax,
                        total = total
                    )


                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    val context = LocalContext.current
                    // -----------------------------------------
                    // PLACE ORDER BUTTON
                    // -----------------------------------------

                    Button(
                        onClick = {

                            if (cartItems.isNotEmpty()) {
                                val order = viewModel.placeOrder()

                                Toast.makeText(
                                    context,
                                    "Order placed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                onOrderPlaced(order)
                            }

                        },

                        enabled = cartItems.isNotEmpty(),

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),

                        shape = RoundedCornerShape(18.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = CartClearRed,
                            disabledContainerColor = CartClearRed,
                            disabledContentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Secure Checkout",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Place Order Now",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "•",
                                fontSize = 16.sp,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "$${
                                    String.format(
                                        Locale.US,
                                        "%.2f",
                                        total
                                    )
                                }",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->


        // -------------------------------------------------
        // MAIN SCREEN
        // -------------------------------------------------

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CartBackground)
        ) {


            // -------------------------------------------------
            // TOP HEADER
            // -------------------------------------------------

            CartHeader(
                onBackClick = onBackClick,
                onClearCart = {
                    viewModel.clearCart()
                }
            )


            // -------------------------------------------------
            // CONTENT
            // -------------------------------------------------

            if (cartItems.isEmpty()) {

                // EMPTY CART UI
                EmptyCartContent()

            } else {

                // NORMAL CART UI
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),

                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 16.dp
                    )
                ) {

                    items(
                        items = cartItems,
                        key = { it.cartItemId }
                    ) { item ->

                        CartItemCard(
                            cartItem = item,

                            onIncrement = {
                                viewModel.incrementQuantity(
                                    item.cartItemId
                                )
                            },

                            onDecrement = {
                                viewModel.decrementQuantity(
                                    item.cartItemId
                                )
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }
                }
            }
        }
    }
}


// ---------------------------------------------------------
// CART HEADER
// ---------------------------------------------------------

@Composable
fun CartHeader(
    onBackClick: () -> Unit,
    onClearCart: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        // Status bar area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),

                color = CartItemBorder,
                thickness = 1.dp
            )
        }


        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),

            horizontalArrangement = Arrangement.SpaceBetween,

            verticalAlignment = Alignment.CenterVertically
        ) {


            // Back button
            Surface(
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(
                    1.dp,
                    CartItemBorder
                ),
                shadowElevation = 0.dp,

                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onBackClick()
                    }
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,

                        contentDescription = "Back",

                        tint = Color.Black,

                        modifier = Modifier.size(19.dp)
                    )
                }
            }


            // Title
            Text(
                text = "YOUR CART",

                fontSize = 15.sp,

                fontWeight = FontWeight.ExtraBold,

                color = CartTitleColor,

                letterSpacing = 0.5.sp
            )


            // Clear Cart
            Text(
                text = "Clear Cart",

                fontSize = 14.sp,

                        textDecoration = TextDecoration.Underline,

                fontWeight = FontWeight.Bold,

                color = CartClearRed,

                modifier = Modifier.clickable {
                    onClearCart()
                }
            )
        }
    }
}


// ---------------------------------------------------------
// EMPTY CART CONTENT
// ---------------------------------------------------------

@Composable
fun EmptyCartContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {


        // -------------------------------------------------
        // EMPTY CART CARD
        // -------------------------------------------------

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            border = BorderStroke(
                1.dp,
                CartItemBorder
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(198.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {


                // Empty cart icon
                Icon(
                    imageVector = Icons.Default.ShoppingBag,

                    contentDescription = "Empty Cart",

                    tint = Color(0xFFE8D1C3),

                    modifier = Modifier.size(44.dp)
                )


                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                Text(
                    text = "Your in-memory cart is empty.",

                    fontSize = 18.sp,

                    fontWeight = FontWeight.Normal,

                    color = Color(0xFF817B76)
                )
            }
        }


        // The remaining space is automatically left empty.
        // Bottom summary is handled by Scaffold.
        Spacer(
            modifier = Modifier.weight(1f)
        )
    }
}


// ---------------------------------------------------------
// CART ITEM CARD
// ---------------------------------------------------------

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        border = BorderStroke(
            1.dp,
            CartItemBorder
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = cartItem.menuItem.name,

                    fontSize = 16.sp,

                    fontWeight = FontWeight.Bold,

                    color = CartTitleColor
                )


                // Options
                val options =
                    mutableListOf<String>()

                cartItem.selectedSize?.let {
                    options.add(it.label)
                }

                cartItem.selectedMilk?.let {
                    options.add(it.name)
                }

                cartItem.selectedSugar?.let {
                    options.add(it)
                }


                if (options.isNotEmpty()) {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = options.joinToString(" • "),

                        fontSize = 13.sp,

                        color = Color(0xFF888888)
                    )
                }


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                Text(
                    text = "$${
                        String.format(
                            Locale.US,
                            "%.2f",
                            cartItem.totalPrice
                        )
                    }",

                    fontSize = 15.sp,

                    fontWeight = FontWeight.Bold,

                    color = CartClearRed
                )
            }


            Spacer(
                modifier = Modifier.width(12.dp)
            )


            // Quantity stepper
            Surface(
                shape = RoundedCornerShape(12.dp),

                color = CartStepperBackground
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,

                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 2.dp
                    )
                ) {

                    IconButton(
                        onClick = onDecrement,

                        modifier = Modifier.size(32.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Remove,

                            contentDescription =
                                "Decrease",

                            tint = Color.Black,

                            modifier = Modifier.size(16.dp)
                        )
                    }


                    Text(
                        text = "${cartItem.quantity}",

                        fontSize = 15.sp,

                        fontWeight = FontWeight.Bold,

                        color = Color.Black,

                        modifier = Modifier.padding(
                            horizontal = 6.dp
                        )
                    )


                    IconButton(
                        onClick = onIncrement,

                        modifier = Modifier.size(32.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Add,

                            contentDescription =
                                "Increase",

                            tint = Color.Black,

                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}


// ---------------------------------------------------------
// COST SUMMARY CARD
// ---------------------------------------------------------

@Composable
fun CostSummaryCard(
    subtotal: Double,
    deliveryFee: Double,
    tax: Double,
    total: Double
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        border = BorderStroke(
            1.dp,
            CartItemBorder
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {


            SummaryRow(
                label = "Subtotal",
                amount = subtotal
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            SummaryRow(
                label = "Delivery Fee",
                amount = deliveryFee
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            SummaryRow(
                label = "Est. Tax (8.0%)",
                amount = tax
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            HorizontalDivider(
                color = CartItemBorder
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "Total Payable",

                    fontSize = 16.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = CartTitleColor
                )


                Text(
                    text = "$${
                        String.format(
                            Locale.US,
                            "%.2f",
                            total
                        )
                    }",

                    fontSize = 20.sp,

                    fontWeight = FontWeight.ExtraBold,

                    color = CartClearRed
                )
            }
        }
    }
}


// ---------------------------------------------------------
// SUMMARY ROW
// ---------------------------------------------------------

@Composable
fun SummaryRow(
    label: String,
    amount: Double
) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = label,

            fontSize = 14.sp,

            color = Color(0xFF666666)
        )


        Text(
            text = "$${
                String.format(
                    Locale.US,
                    "%.2f",
                    amount
                )
            }",

            fontSize = 14.sp,

            fontWeight = FontWeight.Bold,

            color = CartTitleColor
        )
    }
}