package uk.co.imshowcase.imshowcase.util

// The actual current state of the operation
enum class OperationState {
    LOADING,
    SUCCESS,
    ERROR
}

// Holds information about the result of an operation. Modelled after the inbuilt Result class which I didn't know how to use.
data class OperationResult<T>(val status: OperationState, val data: T? = null) {
    companion object {
        fun error(err: Exception): OperationResult<Exception> {
            return OperationResult(OperationState.ERROR, err)
        }
        fun <T> success(data: T? = null): OperationResult<T> {
            return OperationResult(OperationState.SUCCESS, data)
        }
        fun loading(what: String? = null): OperationResult<String?> {
            return OperationResult(OperationState.LOADING, what)
        }
    }
}