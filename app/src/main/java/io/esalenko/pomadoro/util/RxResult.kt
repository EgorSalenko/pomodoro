package io.esalenko.pomadoro.util


data class RxResult<out T>(val status: RxStatus, val data: T?, val msg: String?) {

    companion object {
        fun <T> success(data: T?): RxResult<T> {
            return RxResult(RxStatus.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): RxResult<T> {
            return RxResult(RxStatus.ERROR, data, msg)
        }

        fun <T> loading(data: T?): RxResult<T> {
            return RxResult(RxStatus.LOADING, data, null)
        }
    }
}