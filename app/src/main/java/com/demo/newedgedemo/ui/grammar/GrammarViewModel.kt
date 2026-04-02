package com.demo.newedgedemo.ui.grammar

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.newedgedemo.domain.model.GrammarError
import com.demo.newedgedemo.domain.repository.GrammarRepository
import com.demo.newedgedemo.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GrammarUiState(
    val textFieldValue: TextFieldValue = TextFieldValue(""),
    val errors: List<GrammarError> = emptyList(),
    val correctedText: AnnotatedString? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isInitialState: Boolean = true
)

@HiltViewModel
class GrammarViewModel @Inject constructor(
    private val repository: GrammarRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _state = mutableStateOf(GrammarUiState())
    val state: State<GrammarUiState> = _state

    fun onTextChanged(newValue: TextFieldValue) {
        val textChanged = newValue.text != _state.value.textFieldValue.text
        _state.value = _state.value.copy(
            textFieldValue = newValue,
            errors = if (textChanged) emptyList() else _state.value.errors,
            correctedText = if (textChanged) null else _state.value.correctedText
        )
    }

    fun setInitialState(isInitial: Boolean) {
        _state.value = _state.value.copy(isInitialState = isInitial)
    }

    fun appendText(newText: String) {
        val currentText = _state.value.textFieldValue.text
        val updatedText = if (currentText.isEmpty()) newText else "$currentText $newText"
        onTextChanged(TextFieldValue(updatedText))
    }

    fun checkGrammar() {
        val currentText = _state.value.textFieldValue.text
        if (currentText.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, correctedText = null, successMessage = null)
            try {
                val errors = repository.checkGrammar(currentText)
                if (errors.isEmpty()) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "It is already grammatically correct"
                    )
                } else {
                    val annotatedString = highlightErrors(currentText, errors)
                    
                    _state.value = _state.value.copy(
                        errors = errors,
                        textFieldValue = _state.value.textFieldValue.copy(
                            annotatedString = annotatedString
                        ),
                        isLoading = false,
                        isInitialState = false // Switch to results flow
                    )
                    generateCorrectedText(currentText, errors)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to check grammar"
                )
            }
        }
    }

    private fun highlightErrors(text: String, errors: List<GrammarError>): AnnotatedString {
        return buildAnnotatedString {
            append(text)
            errors.forEach { error ->
                if (error.offset + error.length <= text.length) {
                    addStyle(
                        style = SpanStyle(color = Color.Red, fontWeight = FontWeight.SemiBold),
                        start = error.offset,
                        end = error.offset + error.length
                    )
                }
            }
        }
    }

    private fun generateCorrectedText(text: String, errors: List<GrammarError>) {
        val sortedErrors = errors.sortedBy { it.offset }
        val finalBuilder = buildAnnotatedString {
            var lastIdx = 0
            for (error in sortedErrors) {
                if (error.suggestions.isNotEmpty()) {
                    append(text.substring(lastIdx, error.offset))
                    
                    val replacement = error.suggestions.first()
                    val highlightStart = this.length
                    append(replacement)
                    addStyle(
                        style = SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold),
                        start = highlightStart,
                        end = this.length
                    )
                    lastIdx = error.offset + error.length
                }
            }
            if (lastIdx < text.length) {
                append(text.substring(lastIdx))
            }
        }
        _state.value = _state.value.copy(correctedText = finalBuilder)
    }

    fun clearText() {
        _state.value = _state.value.copy(
            textFieldValue = TextFieldValue(""),
            errors = emptyList(),
            correctedText = null,
            isInitialState = true,
            successMessage = null,
            error = null
        )
    }

    fun consumeSuccessMessage() {
        _state.value = _state.value.copy(successMessage = null)
    }

    fun checkInternetStatus(): Boolean {
        return networkUtils.isInternetAvailable()
    }
}
