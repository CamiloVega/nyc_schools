package cvdevelopers.takehome.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface IErrorMessagesSender {
    fun sentErrorMessage(errorMessage: ErrorMessage)
}

interface IErrorMessagesListener {
    fun observeErrorMessages(): Observable<ErrorMessage>
}

data class ErrorMessage(val errorMessage: String, val error: Throwable)

class ErrorMessagesDispatcher : IErrorMessagesSender, IErrorMessagesListener {
    private val messagesSubject = PublishSubject.create<ErrorMessage>()
    override fun sentErrorMessage(errorMessage: ErrorMessage) {
        messagesSubject.onNext(errorMessage)
    }

    override fun observeErrorMessages(): Observable<ErrorMessage> = messagesSubject.hide()

}