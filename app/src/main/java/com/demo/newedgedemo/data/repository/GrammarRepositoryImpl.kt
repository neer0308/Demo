package com.demo.newedgedemo.data.repository

import com.demo.newedgedemo.data.remote.GrammarApiService
import com.demo.newedgedemo.domain.model.GrammarError
import com.demo.newedgedemo.domain.repository.GrammarRepository
import javax.inject.Inject

class GrammarRepositoryImpl @Inject constructor(
    private val api: GrammarApiService
) : GrammarRepository {
    override suspend fun checkGrammar(text: String): List<GrammarError> {
        return api.checkGrammar(text).matches.map {
            GrammarError(
                message = it.message,
                offset = it.offset,
                length = it.length,
                suggestions = it.replacements.map { r -> r.value }
            )
        }
    }
}
