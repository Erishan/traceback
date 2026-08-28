package com.erishan.traceback.opportunity.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.erishan.traceback.core.enums.PipelineStage
import com.erishan.traceback.ui.theme.TracebackTheme

@Composable
private fun ListScreenPreview(darkTheme: Boolean, uiState: OpportunityListUiState) {
    TracebackTheme(darkTheme = darkTheme) {
        OpportunityListScreen(
            uiState = uiState,
            onAddClick = {},
            onFilterSelected = {},
            onOpenOpportunity = {},
            onOpenMe = {},
        )
    }
}

@Preview(name = "dark", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListDarkPreview() = ListScreenShowcase(darkTheme = true)

@Preview(name = "light", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListLightPreview() = ListScreenShowcase(darkTheme = false)

@Preview(name = "empty dark", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListEmptyDarkPreview() {
    ListScreenPreview(darkTheme = true, uiState = OpportunityListUiState(isLoading = false))
}

@Preview(name = "empty light", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListEmptyLightPreview() {
    ListScreenPreview(darkTheme = false, uiState = OpportunityListUiState(isLoading = false))
}

/** Filter matched nothing, but the pipeline is not empty - the strip still has something to say. */
@Preview(name = "filtered empty", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListFilteredEmptyPreview() {
    ListScreenPreview(
        darkTheme = true,
        uiState = OpportunityListUiState(
            selectedFilter = OpportunityFilter.Won,
            distribution = StageDistribution(
                mapOf(
                    PipelineStage.DRAFT to 1,
                    PipelineStage.APPLIED to 5,
                    PipelineStage.IN_CONVERSATION to 3,
                    PipelineStage.INTERVIEW to 2,
                    PipelineStage.HIRED to 1,
                    PipelineStage.LOST to 4,
                )
            ),
            isLoading = false,
        ),
    )
}

@Preview(name = "loading", widthDp = 400, heightDp = 880)
@Composable
private fun OpportunityListLoadingPreview() {
    ListScreenPreview(darkTheme = true, uiState = OpportunityListUiState())
}
