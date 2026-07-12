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

package onlasdan.gallery.uicomponnets.base.processdialogs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Abstract base for all processing view models.
 * Replaced DataBinding with Flow.
 *
 * @param T Type of elements to be processed
 *
 * @since 1.0.0
 * @author PhotoZ
 */
abstract class BaseProcessViewModel<T>(
	app: Application,
) : AndroidViewModel(app) {
	lateinit var items: List<T>

	val processState = MutableStateFlow(ProcessState.INITIALIZING)
	val progressPercent = MutableStateFlow(0)
	val current = MutableStateFlow(0)
	val elementsToProcess = MutableStateFlow(0)

	var failuresOccurred = false

	fun runProcessing() =
		viewModelScope.launch(Dispatchers.IO) {
			preProcess()
			processLoop()
			postProcess()
		}

	open suspend fun preProcess() {
		processState.value = ProcessState.PROCESSING
		updateProgress()
	}

	private suspend fun processLoop() {
		for (item in items) {
			if (processState.value == ProcessState.ABORTED) {
				return
			}

			processItem(item)
			itemProcessed()
		}
	}

	abstract suspend fun processItem(item: T)

	open suspend fun postProcess() {
		if (processState.value != ProcessState.ABORTED) {
			processState.value = ProcessState.FINISHED
		}
	}

	open fun cancel() {
		processState.value = ProcessState.ABORTED
	}

	private fun itemProcessed() {
		current.value++
		updateProgress()
	}

	private fun updateProgress() {
		if (elementsToProcess.value == 0) {
			return
		}

		progressPercent.value = (current.value * 100) / elementsToProcess.value
	}

	fun failuresOccurred() {
		failuresOccurred = true
	}
}
