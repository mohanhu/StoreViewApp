package com.codeloop.storeviewapp.core.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.codeloop.storeviewapp.core.main.nav.NavigationItem
import com.codeloop.storeviewapp.features.PreviewScreen
import com.codeloop.storeviewapp.features.docs.presentation.DocumentScreen
import com.codeloop.storeviewapp.features.docs.presentation.DocumentViewModel
import com.codeloop.storeviewapp.features.music.presentation.MusicListScreen
import com.codeloop.storeviewapp.features.music.presentation.MusicListViewmodel
import com.codeloop.storeviewapp.features.music.presentation.MusicScreen
import com.codeloop.storeviewapp.features.music.presentation.MusicViewModel
import com.codeloop.storeviewapp.features.phone.presentation.PhoneScreen
import com.codeloop.storeviewapp.features.phone.presentation.PhoneViewModel
import com.codeloop.storeviewapp.features.photo.presentation.PhotoListScreen
import com.codeloop.storeviewapp.features.photo.presentation.PhotoListViewModel
import com.codeloop.storeviewapp.features.photo.presentation.PhotoScreen
import com.codeloop.storeviewapp.features.photo.presentation.PhotoViewModel
import com.codeloop.storeviewapp.features.video.presentation.VideoListScreen
import com.codeloop.storeviewapp.features.video.presentation.VideoListViewModel
import com.codeloop.storeviewapp.features.video.presentation.VideoScreen
import com.codeloop.storeviewapp.features.video.presentation.VideoViewModel
import com.codeloop.storeviewapp.ui.theme.StoreViewAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContent {

            StoreViewAppTheme {

               val navController = rememberNavController()
               val scope = rememberCoroutineScope()
               val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
               val context = LocalContext.current.applicationContext

               val bottomNavigationItem = remember { BottomNavigationItem.BottomNavItem.create() }
               val navBackStackEntry by navController.currentBackStackEntryAsState()
               val currentDestination = navBackStackEntry?.destination

               var selectedIndex  by rememberSaveable { mutableIntStateOf(0) }

               ModalNavigationDrawer(
                   drawerState = drawerState,
                   gesturesEnabled = true,
                   modifier = Modifier.fillMaxHeight(),
                   drawerContent = {
                       Spacer(modifier = Modifier.height(16.dp))
                       ModalDrawerSheet(
                           modifier = Modifier.width(300.dp)
                       ) {
                           Column(
                               modifier = Modifier.fillMaxSize(),
                               verticalArrangement = Arrangement.SpaceBetween
                           ) {

                               Column(
                                   modifier = Modifier
                                       .fillMaxWidth(),
                                   horizontalAlignment = Alignment.CenterHorizontally,
                                   verticalArrangement = Arrangement.Center
                               ) {
                                   Text(
                                       text = "Storeview App",
                                       modifier = Modifier
                                           .fillMaxWidth()
                                           .padding(20.dp),
                                       textAlign = TextAlign.Center,
                                       fontSize = 20.sp,
                                       fontWeight = FontWeight.Bold
                                   )
                                   HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                               }

                               Column(
                                   modifier = Modifier.fillMaxWidth()
                               ) {
                                   HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                                   BottomNavigationItem.BottomNavItem.create().forEachIndexed { index, bottomNavigationItem ->
                                       NavigationDrawerItem(
                                           label = {
                                               Text(text = bottomNavigationItem.title)
                                           },
                                           selected = selectedIndex == index ,
                                           onClick = {
                                               navController.navigate(bottomNavigationItem.navigationItem.route)
                                               scope.launch { drawerState.close() }
                                               selectedIndex = index
                                           },
                                           icon = {
                                               Icon(
                                                   imageVector = if (selectedIndex==index) bottomNavigationItem.selectionVector else bottomNavigationItem.unSelectionVector,
                                                   contentDescription = bottomNavigationItem.navigationItem.route
                                               )
                                           },
                                           badge = {
                                               if (bottomNavigationItem.badges>0){
                                                   Text(text = bottomNavigationItem.badges.toString(), textAlign = TextAlign.End)
                                               }
                                           },
                                           modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                       )
                                   }
                               }
                           }
                       }
                   }
               ) {
                   Scaffold(
                       contentWindowInsets = WindowInsets.safeContent,
                       topBar = {
                           val isUnderTopBar : Boolean = bottomNavigationItem.any { showTopBar -> showTopBar.navigationItem.route == currentDestination?.route }
                           AnimatedVisibility(
                               visible = isUnderTopBar,
                               enter = slideInVertically(initialOffsetY = { -it })  + fadeIn() ,
                               exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                           ) {
                               TopAppVarContent(
                                   modifier = Modifier.fillMaxWidth(),
                                   openDrawer = {
                                       scope.launch { drawerState.open() }
                                   }
                               )
                           }
                       },
                       bottomBar = {

                           val isUnderBottomNav : Boolean = bottomNavigationItem.any { showBottomBar ->
                               showBottomBar.navigationItem.route == currentDestination?.route
                           }

                           AnimatedVisibility(
                               visible = isUnderBottomNav,
                               enter = slideInVertically(initialOffsetY = { it })  + fadeIn() ,
                               exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                           ) {
                               BottomNavigationBar(
                                   bottomNavigationItem = bottomNavigationItem,
                                   navController = navController,
                                   onItemClick = {
                                       selectedIndex = bottomNavigationItem.indexOf(it)
                                       navController.navigate(it.navigationItem.route) {
                                           popUpTo(navController.graph.startDestinationId) {
                                               saveState = true
                                           }
                                           launchSingleTop = true
                                           restoreState = true
                                       }
                                   }
                               )
                           }
                       }
                   ) { padding ->
                       Column(modifier = Modifier
                           .fillMaxSize()
                           .padding(padding) // respect scaffold padding
                       ) {
                           NavController(navController, modifier = Modifier)
//                           PreviewScreen()
                       }
                   }
               }
           }
        }
    }
}

@Composable
fun BottomNavigationBar(
    bottomNavigationItem: List<BottomNavigationItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavigationItem) -> Unit
) {

    NavigationBar(
        modifier = modifier,
        tonalElevation = 10.dp
    ) {
        bottomNavigationItem.forEachIndexed { index, bottomNavigationItem ->
            val selected =  bottomNavigationItem.navigationItem.route == navController.currentDestination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick.invoke(bottomNavigationItem) },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (bottomNavigationItem.badges > 0) {
                            BadgedBox(
                                badge = {
                                    Badge{
                                        Text(text = bottomNavigationItem.badges.toString())
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = bottomNavigationItem.selectionVector,
                                    contentDescription = bottomNavigationItem.navigationItem.toString()
                                )
                            }
                        }
                        else{
                            Icon(
                                imageVector = bottomNavigationItem.selectionVector,
                                contentDescription = bottomNavigationItem.navigationItem.toString()
                            )
                        }
                        Text(
                            text = bottomNavigationItem.title,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun NavController(navController: NavHostController,modifier: Modifier) {

    NavHost(navController = navController, modifier = modifier, startDestination = NavigationItem.Photo.route) {

        /**START Bottom Nav Item END */

        composable(NavigationItem.Photo.route) {
            val viewModel: PhotoViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            PhotoScreen(
                title = "Photo",
                uiState = uiState,
                accept = viewModel.accept,
                onItemClick = {
                    navController.navigate(NavigationItem.PhotoList(folderName = it))
                }
            )
//            PreviewScreen()
        }
        composable(NavigationItem.Video.route) {
            val viewModel: VideoViewModel = hiltViewModel()
            VideoScreen(
                title = "Video",
                uiState = viewModel.uiState.collectAsState(),
                accept = viewModel.accept,
                onItemClick = {
                    navController.navigate(NavigationItem.VideoList(folderName = it))
                }
            )
        }
        composable(NavigationItem.Music.route) {
            val viewModel : MusicViewModel = hiltViewModel()
            MusicScreen(
                title = "Music",
                uiState = viewModel.uiState.collectAsState(),
                accept = viewModel.accept,
                onItemClick = {
                    navController.navigate(NavigationItem.MusicList(folderName = it))
                }
            )
        }
        composable(NavigationItem.Documents.route) {
            val viewModel : DocumentViewModel = hiltViewModel()
            DocumentScreen(
                title = "Documents",
                uiState = viewModel.uiState.collectAsState(),
                accept = viewModel.accept
            )
        }
        composable(NavigationItem.Contact.route) {
            val viewModel : PhoneViewModel = hiltViewModel()
            PhoneScreen(
                title = "Contact",
                uiState = viewModel.uiState.collectAsState(),
                accept = viewModel.accept
            )
        }
        /**Bottom Nav Item END */

        composable<NavigationItem.PhotoList> {
            val viewModel : PhotoListViewModel = hiltViewModel()
            PhotoListScreen(
                title = "PhotoList",
                uiState = viewModel.uiState.collectAsState(),
                accept = viewModel.accept,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        composable<NavigationItem.VideoList> {
            val viewModel : VideoListViewModel = hiltViewModel()
            VideoListScreen (
                title = "VideoList",
                uiState = viewModel.uiState.collectAsState(),
                accept = viewModel.accept,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable<NavigationItem.MusicList> {
            val viewModel : MusicListViewmodel = hiltViewModel()
            MusicListScreen(
                title = "MusicListScreen",
                uiState = viewModel.uiState.collectAsState(),
                accept = viewModel.accept,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppVarContent(
    modifier: Modifier=Modifier,
    openDrawer:()->Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier,
        title = { Text(text = "StoreViewApp") },
        navigationIcon = {
            Icon(imageVector = Icons.Default.Menu , contentDescription = "Menu",
                modifier = Modifier
                    .padding(10.dp)
                    .size(28.dp)
                    .clickable { openDrawer() }
            )
        }, actions = {
            Icon(imageVector = Icons.Default.AccountCircle , contentDescription = "Profile",
                modifier = Modifier
                    .padding(10.dp)
                    .size(28.dp))
            Icon(imageVector = Icons.Default.Notifications , contentDescription = "Notifications",
                modifier = Modifier
                    .padding(10.dp)
                    .size(28.dp))
        })

}

data class BottomNavigationItem(
    val title:String,
    val navigationItem: NavigationItem,
    val selectionVector: ImageVector,
    val unSelectionVector: ImageVector,
    val hasCount: Boolean,
    val badges: Int,
){
    object BottomNavItem {
        fun create():List<BottomNavigationItem>{
            return listOf(
                BottomNavigationItem(
                    title = "Photo",
                    navigationItem = NavigationItem.Photo,
                    selectionVector = Icons.Filled.Home,
                    unSelectionVector = Icons.Outlined.Home,
                    hasCount = false,
                    badges = 3
                ),
                BottomNavigationItem(
                    title = "Video",
                    navigationItem = NavigationItem.Video,
                    selectionVector = Icons.Filled.Add,
                    unSelectionVector = Icons.Outlined.Add,
                    hasCount = false,
                    badges = 3
                ),
                BottomNavigationItem(
                    title = "Music",
                    navigationItem = NavigationItem.Music,
                    selectionVector = Icons.Filled.Settings,
                    unSelectionVector = Icons.Outlined.Settings,
                    hasCount = true,
                    badges = 0
                ),
            /*    BottomNavigationItem(
                    title = "Document",
                    navigationItem = NavigationItem.Documents,
                    selectionVector = Icons.Filled.Settings,
                    unSelectionVector = Icons.Outlined.Settings,
                    hasCount = true,
                    badges = 0
                ),*/
                BottomNavigationItem(
                    title = "Contact",
                    navigationItem = NavigationItem.Contact,
                    selectionVector = Icons.Filled.Settings,
                    unSelectionVector = Icons.Outlined.Settings,
                    hasCount = true,
                    badges = 4
                )
            )
        }
    }
}