package com.demo.newedgedemo.domain.repository

import com.demo.newedgedemo.domain.model.GrammarError

interface GrammarRepository {
    suspend fun checkGrammar(text: String): List<GrammarError>
}
