package br.gohan.videofeed.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LoginRoute) {
        authGraph(
            navController = navController,
            onNavigateToFeed = {
                navController.navigate(FeedRoute) {
                    popUpTo(LoginRoute) { inclusive = true }
                }
            }
        )
        composable<FeedRoute> {
            // feedGraph() added in Phase 3
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Feed — coming in Phase 3")
            }
        }
    }
}
