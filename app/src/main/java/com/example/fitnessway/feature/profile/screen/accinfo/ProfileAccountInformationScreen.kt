package com.example.fitnessway.feature.profile.screen.accinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessway.data.model.user.User
import com.example.fitnessway.feature.profile.viewmodel.ProfileViewModel
import com.example.fitnessway.ui.shared.Header
import com.example.fitnessway.ui.shared.Messages.NotFoundMessage
import com.example.fitnessway.ui.shared.Screen
import com.example.fitnessway.ui.theme.AppModifiers.areaContainer
import com.example.fitnessway.ui.theme.AppModifiers.AreaContainerSize
import org.koin.androidx.compose.koinViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ProfileAccountInformationScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val user = viewModel.user

    Screen(
        header = {
            Header(
                onBackClick = onBackClick,
                title = "Account"
            )
        },

        content = {
            if (user == null) {
                NotFoundMessage(message = "No user found")
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .areaContainer(size = AreaContainerSize.SMALL),
                    content = {
                        val userInformation = getUserAccountInformation(user)

                        userInformation.forEach {
                            ProfileAccInfoRow(key = it.first, value = it.second)
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun ProfileAccInfoRow(key: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        content = {
            val textStyle = MaterialTheme.typography.bodyLarge

            Text(
                text = key,
                style = textStyle
            )

            Text(
                text = value,
                style = textStyle,
                color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
            )
        }
    )
}

private fun getUserAccountInformation(user: User): List<Pair<String, String>> {
    return listOf(
        Pair("Email", user.email),
        Pair("Date Joined", formatDate(user.createdAt))
    )
}


private fun formatDate(dateString: String): String {
    val offsetDateTime = OffsetDateTime.parse(dateString)
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
    return offsetDateTime.format(formatter)
}