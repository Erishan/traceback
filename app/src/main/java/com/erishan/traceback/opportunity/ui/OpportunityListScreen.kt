package com.erishan.traceback.opportunity.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun OpportunityListScreen(uiState: OpportunityListUiState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()){
        if(uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(items = uiState.opportunities, key = { opportunity -> opportunity.id }) { opportunity ->
                    Text(opportunity.title)
                }
            }
        }
    }
}
