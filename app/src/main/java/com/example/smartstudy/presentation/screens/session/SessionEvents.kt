package com.example.smartstudy.presentation.screens.session

import com.example.smartstudy.domain.model.Session
import com.example.smartstudy.domain.model.Subject

sealed class SessionEvents {
    data class OnRelatedSubjectChange(val subject: Subject) : SessionEvents()
    data class SaveSession(val duration: Long) : SessionEvents()
    data class OnDeleteSession(val session: Session) : SessionEvents()
    data object DeleteSession : SessionEvents()
    data object NotifyToUpdateSubject : SessionEvents()
    data class UpdateSubject(
        val subjectId: Int?,
        val relatedToSubject: String?
    ) : SessionEvents()
}