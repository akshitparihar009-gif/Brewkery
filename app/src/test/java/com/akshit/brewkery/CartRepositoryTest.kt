package com.akshit.brewkery

import com.akshit.brewkery.data.model.Customizations
import com.akshit.brewkery.data.model.MenuItem
import com.akshit.brewkery.data.model.MilkOption
import com.akshit.brewkery.data.model.SizeOption
import com.akshit.brewkery.data.repository.CartRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CartRepositoryTest {

    private val sampleItem = MenuItem(
        id = 1,
        categoryId = "cat_hot_coffee",
        name = "Toasted Caramel Macchiato",
        tagline = "Double shot espresso",
        description = "Rich espresso roast",
        basePrice = 4.85,
        rating = 4.9,
        reviewCount = 312,
        prepTime = "5-7 mins",
        calories = 240,
        imageUrl = "https://example.com/image.jpg",
        badge = "BESTSELLER",
        ingredients = listOf("Espresso", "Oat Milk"),
        customizations = Customizations(
            sizes = listOf(SizeOption("sz_medium", "Grande (12 oz)", 0.65)),
            milkOptions = listOf(MilkOption("m_almond", "Roasted Almond Milk", 0.50)),
            sugarLevels = listOf("50% Mild")
        )
    )

    @Before
    fun setUp() {
        CartRepository.clearCart()
        CartRepository.clearActiveOrder()
    }

    @Test
    fun addToCart_calculatesUnitPriceAndTotalPriceCorrectly() {
        val size = SizeOption("sz_medium", "Grande (12 oz)", 0.65)
        val milk = MilkOption("m_almond", "Roasted Almond Milk", 0.50)
        val sugar = "50% Mild"

        CartRepository.addToCart(sampleItem, size, milk, sugar, quantity = 2)

        val items = CartRepository.cartItems.value
        assertEquals(1, items.size)

        val cartItem = items[0]
        val expectedUnitPrice = 4.85 + 0.65 + 0.50 // 6.00
        val expectedTotalPrice = expectedUnitPrice * 2 // 12.00

        assertEquals(expectedUnitPrice, cartItem.unitPrice, 0.001)
        assertEquals(expectedTotalPrice, cartItem.totalPrice, 0.001)
        assertEquals(2, cartItem.quantity)
    }

    @Test
    fun incrementAndDecrementQuantity_updatesCartState() {
        val size = SizeOption("sz_medium", "Grande (12 oz)", 0.65)
        CartRepository.addToCart(sampleItem, size, null, null, quantity = 1)

        val itemId = CartRepository.cartItems.value[0].cartItemId

        CartRepository.incrementQuantity(itemId)
        assertEquals(2, CartRepository.cartItems.value[0].quantity)

        CartRepository.decrementQuantity(itemId)
        assertEquals(1, CartRepository.cartItems.value[0].quantity)

        CartRepository.decrementQuantity(itemId)
        assertTrue(CartRepository.cartItems.value.isEmpty())
    }

    @Test
    fun placeOrder_createsTicketAndClearsCart() {
        val size = SizeOption("sz_medium", "Grande (12 oz)", 0.65)
        CartRepository.addToCart(sampleItem, size, null, null, quantity = 1)

        val order = CartRepository.placeOrder()

        assertNotNull(order)
        assertTrue(order.ticketId.startsWith("#BK-"))
        assertEquals(1, order.items.size)
        assertTrue(CartRepository.cartItems.value.isEmpty())
        assertNotNull(CartRepository.activeOrder.value)
    }
}
