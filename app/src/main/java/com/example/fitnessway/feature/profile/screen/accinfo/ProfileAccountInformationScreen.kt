package com.example.fitnessway.feature.profile.screen.accinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.MUser.Model.User
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.shared.Structure.NotFoundScreen
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileAccountInformationScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val userFlow by viewModel.userFlow.collectAsState()

    val user = userFlow

    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Account"
            )
        },
    ) {
        if (user != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.areaContainer(size = AreaContainerSize.SMALL),
            ) {
                val userInformation = getUserAccountInformation(user)

                userInformation.forEach {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val textStyle = MaterialTheme.typography.bodyMedium

                        Text(
                            text = it.first,
                            style = textStyle
                        )

                        Text(
                            text = it.second,
                            style = textStyle,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                        )
                    }
                }
            }
        } else NotFoundScreen(message = "No user found")
    }
}

private fun getUserAccountInformation(user: User): List<Pair<String, String>> {
    return listOf(
        Pair("Email", user.email),
        Pair("Date Joined", user.createdAt)
    )
}