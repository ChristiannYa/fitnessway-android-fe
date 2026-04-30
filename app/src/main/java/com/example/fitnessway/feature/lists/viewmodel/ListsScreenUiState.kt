package com.example.fitnessway.feature.lists.viewmodel

import com.example.fitnessway.data.model.m_26.EdibleType
import com.example.fitnessway.util.UiState

private typealias EdibleStateMap = Map<EdibleType, UiState<Unit>>

data class ListsScreenUiState(
    val edibleRequestAddState: EdibleStateMap = edibleStateMap(),
    val edibleAddState: EdibleStateMap = edibleStateMap(),

    val foodUpdateState: UiState<Unit> = UiState.Idle,
    val foodDeleteState: UiState<Unit> = UiState.Idle,
    val foodReviewDismissState: UiState<Unit> = UiState.Idle
) {
    companion object {
        fun edibleStateMap(): EdibleStateMap = EdibleType.entries.associateWith { UiState.Idle }
    }
}