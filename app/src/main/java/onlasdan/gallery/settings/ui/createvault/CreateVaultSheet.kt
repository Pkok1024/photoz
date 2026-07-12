/*
 *   Copyright 2020–2026 PhotoZ
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package onlasdan.gallery.settings.ui.createvault

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import onlasdan.gallery.R
import onlasdan.gallery.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVaultSheet(
	show: Boolean,
	isDecoy: Boolean = false,
	onDismissRequest: () -> Unit,
	onCreateVault: (String) -> Unit,
) {
	val sheetState = rememberModalBottomSheetState()

	if (show) {
		ModalBottomSheet(
			onDismissRequest = onDismissRequest,
			sheetState = sheetState,
		) {
			AppTheme {
				var password by remember { mutableStateOf("") }

				Column(
					modifier =
						Modifier
							.fillMaxWidth()
							.padding(horizontal = 24.dp)
							.padding(bottom = 48.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(20.dp),
				) {
					Text(
						text = if (isDecoy) "Create Decoy Vault" else stringResource(R.string.settings_security_create_vault_title),
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Bold,
					)

					Text(
						text = if (isDecoy) {
							"A decoy vault opens when a specific secondary password is entered. It looks like a normal vault but contains different data."
						} else {
							stringResource(R.string.settings_security_create_vault_summary)
						},
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)

					OutlinedTextField(
						value = password,
						onValueChange = { password = it },
						label = { Text(stringResource(R.string.setup_enter_password)) },
						visualTransformation = PasswordVisualTransformation(),
						modifier = Modifier.fillMaxWidth(),
					)

					Button(
						onClick = { onCreateVault(password) },
						enabled = password.isNotEmpty(),
						modifier = Modifier.fillMaxWidth(),
					) {
						Text(text = stringResource(R.string.setup_button))
					}
				}
			}
		}
	}
}
