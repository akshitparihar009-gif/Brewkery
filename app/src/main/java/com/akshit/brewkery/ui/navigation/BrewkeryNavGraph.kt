package com.akshit.brewkery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.akshit.brewkery.ui.screens.CartScreen
import com.akshit.brewkery.ui.screens.HomeScreen
import com.akshit.brewkery.ui.screens.OrderStatusScreen
import com.akshit.brewkery.ui.screens.ProductDetailsScreen
import com.akshit.brewkery.ui.viewmodel.CartViewModel
import com.akshit.brewkery.ui.viewmodel.ItemDetailViewModel
import com.akshit.brewkery.ui.viewmodel.MenuViewModel
import com.akshit.brewkery.ui.viewmodel.OrderViewModel

object Destinations {
    const val HOME = "home"
    const val PRODUCT_DETAILS = "product_details/{itemId}"
    const val CART = "cart"
    const val ORDER_STATUS = "order_status"

    fun productDetailsRoute(itemId: Int) = "product_details/$itemId"
}

@Composable
fun BrewkeryNavGraph(
    navController: NavHostController = rememberNavController(),
    menuViewModel: MenuViewModel = viewModel(),
    itemDetailViewModel: ItemDetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HOME
    ) {
        composable(Destinations.HOME) {
            HomeScreen(
                viewModel = menuViewModel,
                onItemClick = { itemId ->
                    navController.navigate(Destinations.productDetailsRoute(itemId))
                },
                onViewCartClick = {
                    navController.navigate(Destinations.CART)
                },
                onViewActiveOrderClick = {
                    navController.navigate(Destinations.ORDER_STATUS)
                }
            )
        }

        composable(
            route = Destinations.PRODUCT_DETAILS,
            arguments = listOf(
                navArgument("itemId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 1
            ProductDetailsScreen(
                itemId = itemId,
                viewModel = itemDetailViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onCartClick = {
                    navController.navigate(Destinations.CART)
                }
            )
        }

        composable(Destinations.CART) {
            CartScreen(
                viewModel = cartViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderPlaced = {
                    navController.navigate(Destinations.ORDER_STATUS) {
                        popUpTo(Destinations.HOME)
                    }
                }
            )
        }

        composable(Destinations.ORDER_STATUS) {
            OrderStatusScreen(
                viewModel = orderViewModel,
                onReturnToMenuClick = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
