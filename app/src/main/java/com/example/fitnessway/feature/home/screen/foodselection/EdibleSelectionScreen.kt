package com.example.fitnessway.feature.home.screen.foodselection

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.mappers.toEdibleType
import com.example.fitnessway.data.mappers.toSuccessOrNull
import com.example.fitnessway.data.mappers.toTitleCase
import com.example.fitnessway.data.model.m_26.EdibleScope
import com.example.fitnessway.data.model.m_26.EdibleSource
import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.data.model.m_26.FoodLogCategory
import com.example.fitnessway.data.model.m_26.FoodLogListFilter
import com.example.fitnessway.data.model.m_26.FoodToLogSearchCriteria
import com.example.fitnessway.feature.home.screen.foodselection.composables.AppFoodResultsPagination
import com.example.fitnessway.feature.home.screen.foodselection.composables.AppFoodSearchBar
import com.example.fitnessway.feature.home.screen.foodselection.composables.RecentlyLoggedFoods
import com.example.fitnessway.feature.home.screen.foodselection.foodlog.FoundEdibleByBarcode
import com.example.fitnessway.feature.home.viewmodel.HomeViewModel
import com.example.fitnessway.feature.profile.screen.main.composables.UpgradePromptDialog
import com.example.fitnessway.ui.edible.EdibleListSelectionTextButton
import com.example.fitnessway.ui.edible.FoodPreviewList
import com.example.fitnessway.ui.shared.Clickables
import com.example.fitnessway.ui.shared.DarkOverlay
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.PremiumIcon
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure
import com.example.fitnessway.util.UiState
import com.example.fitnessway.util.logcat
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.koin.compose.viewmodel.koinViewModel
import com.example.fitnessway.R as AppR

@Composable
fun EdibleSelectionScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToSelectedFood: () -> Unit,
    onPopBackStack: () -> Unit,
) {
    val userRepoUiState by viewModel.userRepoUiState.collectAsState()
    val userFoodRepoUiState by viewModel.userFoodRepoUiState.collectAsState()
    val appFoodRepoUiState by viewModel.appFoodRepoUiState.collectAsState()
    val userSupplementRepoUiState by viewModel.userSupplementRepoUiState.collectAsState()
    val foodRecentLogRepoUiState by viewModel.foodRecentLogRepoUiState.collectAsState()
    val supplementRecentLogRepoUiState by viewModel.supplementRecentLogRepoUiState.collectAsState()

    val foodList by viewModel.foodList.collectAsState()
    val appFoodSearchQuery by viewModel.appFoodSearchQuery.collectAsState()

    val logCategory = viewModel.foodLogCategory.collectAsState().value

    val user = userRepoUiState.userUiState.toSuccessOrNull()
    val appFoodsUiStatePager = appFoodRepoUiState.appEdiblesUiStatePager
    val foodRecentLogRepoUiStatePager = foodRecentLogRepoUiState.uiStatePager
    val supplementRecentLogRepoUiStatePager = supplementRecentLogRepoUiState.uiStatePager
    val userFoodsUiStatePager = userFoodRepoUiState.uiStatePager
    val userSupplementsUiStatePager = userSupplementRepoUiState.uiStatePager

    var isUpgradePromptDialogVisible by remember { mutableStateOf(false) }
    var isByBarcodePopupVisible by remember { mutableStateOf(false) }
    var scannedBarcode by remember { mutableStateOf("") }

    fun onBackClick() {
        viewModel.onResetFoodSelectionScreen()
        onPopBackStack()
    }

    BackHandler { onBackClick() }

    LaunchedEffect(foodList) {
        foodList?.let {
            when (it) {
                FoodLogListFilter.RECENTLY_LOGGED_FOODS -> {
                    if (logCategory != FoodLogCategory.SUPPLEMENT) viewModel.getRecentlyLoggedFoods()
                }

                FoodLogListFilter.RECENTLY_LOGGED_SUPPLEMENTS -> {
                    if (logCategory == FoodLogCategory.SUPPLEMENT) viewModel.getRecentlyLoggedSupplements()
                }

                FoodLogListFilter.USER_FOODS -> viewModel.getUserFoods()

                FoodLogListFilter.USER_SUPPLEMENTS -> viewModel.getUserSupplements()
            }
        }
    }

    LaunchedEffect(scannedBarcode) {
        if (scannedBarcode.isNotBlank()) viewModel.getAppEdibleByBarcode(scannedBarcode)
    }

    if (logCategory != null) {
        val context = LocalContext.current
        val barcodeScanner = GmsBarcodeScanning.getClient(context)

        val categoryString = logCategory.name.toTitleCase()

        Screen(
            header = {
                Header(
                    onBackClick = ::onBackClick,
                    title = "$categoryString selection"
                ) {
                    Box {
                        Clickables.AppPngIconButton(
                            icon = Structure.AppIconSource.Resource(AppR.drawable.barcode),
                            contentDescription = "Scan ${logCategory.toEdibleType()}",
                            onClick = {
                                if (user?.isPremium == false) {
                                    isUpgradePromptDialogVisible = true
                                    return@AppPngIconButton
                                }

                                barcodeScanner
                                    .startScan()
                                    .addOnSuccessListener {
                                        it.rawValue?.let { sb ->
                                            isByBarcodePopupVisible = true
                                            scannedBarcode = sb
                                        }
                                    }
                                    .addOnFailureListener { ex ->
                                        logcat("barcode exception: ${ex.message}")
                                    }
                            }
                        )

                        if (user?.isPremium == false) {
                            PremiumIcon(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(MaterialTheme.colorScheme.background)
                            )
                        }
                    }
                }
            },
        ) { focusManager ->
            Box(Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Column {
                        var isTyping by remember { mutableStateOf(false) }
                        val isLoading = isTyping || appFoodsUiStatePager.uiState is UiState.Loading

                        AppFoodSearchBar(
                            query = appFoodSearchQuery,
                            onQueryChange = { q ->
                                isTyping = q.isNotBlank()
                                viewModel.getAppFoods(q, logCategory.toEdibleType())
                            },
                            focusManager = focusManager
                        )

                        AppFoodResultsPagination(
                            appFoodsUiStatePager = appFoodsUiStatePager,
                            isLoading = isLoading,
                            isUserPremium = user?.isPremium ?: false,
                            onTypingConsumed = { isTyping = false },
                            onLoadMore = {
                                viewModel.getMoreAppEdibles(
                                    appFoodSearchQuery,
                                    logCategory.toEdibleType()
                                )
                            },
                            onFoodClick = { id ->
                                viewModel.setSearchCriteria(
                                    FoodToLogSearchCriteria(
                                        scope = EdibleScope.Id(id),
                                        source = EdibleSource.APP,
                                        edibleType = logCategory.toEdibleType()
                                    )
                                )
                                onNavigateToSelectedFood()
                            },
                            modifier = Modifier
                                .animateContentSize()
                                .then(
                                    if (isTyping || appFoodsUiStatePager.uiState !is UiState.Idle) {
                                        Modifier.fillMaxHeight()
                                    } else Modifier
                                )
                        )
                    }

                    val isRecentlyLoggedFoodsVisible = foodList == FoodLogListFilter.RECENTLY_LOGGED_FOODS &&
                            logCategory != FoodLogCategory.SUPPLEMENT

                    val isRecentlyLoggedSupplementsVisible =
                        foodList == FoodLogListFilter.RECENTLY_LOGGED_SUPPLEMENTS &&
                                logCategory == FoodLogCategory.SUPPLEMENT

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        EdibleListSelectionTextButton(
                            text = "Recently Logged",
                            isSelected = when (logCategory) {
                                FoodLogCategory.SUPPLEMENT -> isRecentlyLoggedSupplementsVisible
                                else -> isRecentlyLoggedFoodsVisible
                            },
                            onClick = {
                                when (logCategory) {
                                    FoodLogCategory.SUPPLEMENT -> viewModel.setFoodList(FoodLogListFilter.RECENTLY_LOGGED_SUPPLEMENTS)
                                    else -> viewModel.setFoodList(FoodLogListFilter.RECENTLY_LOGGED_FOODS)
                                }
                            }
                        )

                        when (logCategory) {
                            FoodLogCategory.SUPPLEMENT -> EdibleListSelectionTextButton(
                                text = "My Supplements",
                                isSelected = foodList == FoodLogListFilter.USER_SUPPLEMENTS,
                                onClick = { viewModel.setFoodList(FoodLogListFilter.USER_SUPPLEMENTS) }
                            )

                            else -> EdibleListSelectionTextButton(
                                text = "My Foods",
                                isSelected = foodList == FoodLogListFilter.USER_FOODS,
                                onClick = { viewModel.setFoodList(FoodLogListFilter.USER_FOODS) }
                            )
                        }
                    }

                    RecentlyLoggedFoods(
                        uiStatePager = foodRecentLogRepoUiStatePager,
                        edibleType = EdibleType.FOOD,
                        isVisible = isRecentlyLoggedFoodsVisible,
                        isUserPremium = user?.isPremium ?: false,
                        onLoadMore = viewModel::getMoreRecentlyLoggedFoods,
                        onFoodClick = { id, source ->
                            viewModel.setSearchCriteria(
                                FoodToLogSearchCriteria(
                                    scope = EdibleScope.Id(id),
                                    source = source,
                                    edibleType = EdibleType.FOOD
                                )
                            )
                            onNavigateToSelectedFood()
                        }
                    )

                    RecentlyLoggedFoods(
                        uiStatePager = supplementRecentLogRepoUiStatePager,
                        edibleType = EdibleType.SUPPLEMENT,
                        isVisible = isRecentlyLoggedSupplementsVisible,
                        isUserPremium = user?.isPremium ?: false,
                        onLoadMore = viewModel::getMoreRecentlyLoggedSupplements,
                        onFoodClick = { id, source ->
                            viewModel.setSearchCriteria(
                                FoodToLogSearchCriteria(
                                    scope = EdibleScope.Id(id),
                                    source = source,
                                    edibleType = EdibleType.SUPPLEMENT
                                )
                            )
                            onNavigateToSelectedFood()
                        }
                    )

                    FoodPreviewList(
                        uiStatePager = userFoodsUiStatePager,
                        isVisible = foodList == FoodLogListFilter.USER_FOODS &&
                                logCategory != FoodLogCategory.SUPPLEMENT,
                        isUserPremium = user?.isPremium ?: false,
                        onLoadMore = viewModel::getMoreUserFoods,
                        onFoodClick = { food ->
                            viewModel.setSearchCriteria(
                                FoodToLogSearchCriteria(
                                    scope = EdibleScope.Id(food.id),
                                    source = EdibleSource.USER,
                                    edibleType = EdibleType.FOOD
                                )
                            )
                            onNavigateToSelectedFood()
                        }
                    )

                    FoodPreviewList(
                        uiStatePager = userSupplementsUiStatePager,
                        isVisible = foodList == FoodLogListFilter.USER_SUPPLEMENTS &&
                                logCategory == FoodLogCategory.SUPPLEMENT,
                        isUserPremium = user?.isPremium ?: false,
                        onLoadMore = viewModel::getMoreUserSupplements,
                        onFoodClick = { food ->
                            viewModel.setSearchCriteria(
                                FoodToLogSearchCriteria(
                                    scope = EdibleScope.Id(food.id),
                                    source = EdibleSource.USER,
                                    edibleType = EdibleType.SUPPLEMENT
                                )
                            )
                            onNavigateToSelectedFood()
                        }
                    )
                }

                DarkOverlay(
                    isVisible = isByBarcodePopupVisible,
                    onClick = { isByBarcodePopupVisible = false }
                )

                FoundEdibleByBarcode(
                    isVisible = isByBarcodePopupVisible,
                    isUserPremium = user?.isPremium ?: false,
                    scannedBarcode = scannedBarcode,
                    edibleDataState = appFoodRepoUiState.appEdible,
                    onDismiss = { isByBarcodePopupVisible = false },
                    onLog = { searchCriteria ->
                        viewModel.setSearchCriteria(searchCriteria)
                        onNavigateToSelectedFood()
                    }
                )

                if (isUpgradePromptDialogVisible) {
                    UpgradePromptDialog(
                        onDismiss = { isUpgradePromptDialogVisible = false },
                        onUpgradeClick = {}
                    )
                }
            }
        }
    }
}